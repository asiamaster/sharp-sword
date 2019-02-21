package com.dili.ss.mbg.beetl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 支持按beetl的模板文件生成代码
 * Created by asiam on 2017/4/12
 */
public class BeetlTemplatesPlugin extends PluginAdapter {
    private String targetDir;
    private String templateRootDir;
    private boolean firstTime =true;
    private ShellCallback shellCallback = null;
    private boolean overwrite = false;
    private GroupTemplate groupTemplate;
    //被替换的分隔
    private static final String replaceFromSeparator;
    //替换的分隔
    private static final String replaceToSeparator;

    static {
        //如果是windows
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            replaceFromSeparator = "/";
            replaceToSeparator = "\\\\";
        }else{
            replaceFromSeparator = "\\\\";
            replaceToSeparator = "/";
        }

    }
    public BeetlTemplatesPlugin() {}

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String overwriteStr = this.properties.getProperty("overwrite");
        overwrite = StringUtils.isBlank(overwriteStr)?false:Boolean.parseBoolean(overwriteStr);
        shellCallback = new DefaultShellCallback(overwrite);
    }

    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;
        if (!StringUtility.stringHasValue(this.properties.getProperty("targetDir"))) {
            warnings.add(Messages.getString("ValidationError.18", "MapperConfigPlugin", "targetDir"));
            valid = false;
        }
        if (!StringUtility.stringHasValue(this.properties.getProperty("templateRootDir"))) {
            warnings.add(Messages.getString("ValidationError.18", "MapperConfigPlugin", "templateRootDir"));
            valid = false;
        }
        targetDir = this.properties.getProperty("targetDir");
        templateRootDir = this.properties.getProperty("templateRootDir");
        return valid;
    }

    //生成文件主方法
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        //生成额外的文件
        generateFile(introspectedTable);
        ArrayList answer1 = new ArrayList(0);
        return answer1;
    }

    //具体生成文件类
    private void generateFile(IntrospectedTable introspectedTable) {
        try {
            List<File> files = unzipIfTemplateRootDirIsZipFile(Lists.newArrayList(new File(templateRootDir)));
            List<File> childFiles = Lists.newArrayList();
            childFiles = ergodic(files.get(0),childFiles);
            //找不到模板文件就停
            if(childFiles == null || childFiles.isEmpty()){
                return;
            }
            for(File childFile : childFiles){
                //有数据库相关变量的File则每次循环都生成
                //没有数据库相关变量的File则只在第一次生成
                if(childFile.getCanonicalPath().contains("${classNameFirstLower}") || childFile.getCanonicalPath().contains("${className}") || firstTime){
                    generateFile(childFile, introspectedTable);
                }
            }
            firstTime=false;
        } catch (ShellException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成文件
    private void generateFile(File childFile, IntrospectedTable introspectedTable) throws IOException, ShellException {
        if(childFile.isDirectory()){
            File dir = new File(getDirRelativePath(childFile.getCanonicalPath(), introspectedTable));
            if(!dir.exists()) {
                dir.mkdirs();
            }
            return;
        }
        String className = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String classNameFirstLower = StringUtils.uncapitalize(className);
        Template bodyVM = getFileBeetlGroupTemplate().getTemplate(getFileRelativePath(childFile.getCanonicalPath()));
        //文件夹没加载到就到classpath下找
        if(bodyVM == null){
            bodyVM = getClasspathBeetlGroupTemplate().getTemplate(getFileRelativePath(childFile.getCanonicalPath()));
        }
        StringWriter screenContent = new StringWriter();
        bodyVM.binding(properties);
        bodyVM.binding(TemplateConstants.className, className);
        bodyVM.binding(TemplateConstants.classNameFirstLower, classNameFirstLower);
        bodyVM.binding(TemplateConstants.table, introspectedTable);
        bodyVM.renderTo(screenContent);
        write(getFileRelativePath(childFile.getCanonicalPath(), introspectedTable), screenContent.toString());
    }

    //根据模板File和IntrospectedTable,替换变量后获取模板目录相对路径
    private String getDirRelativePath(String fileName, IntrospectedTable introspectedTable){
        String className = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String classNameFirstLower = StringUtils.uncapitalize(className);
        String finalName = targetDir.replaceAll(replaceFromSeparator, replaceToSeparator)+fileName.substring(fileName.indexOf(templateRootDir.replaceAll(replaceFromSeparator, replaceToSeparator))+templateRootDir.length());
        finalName = finalName.replaceAll("\\$\\{classNameFirstLower\\}", classNameFirstLower).replaceAll("\\$\\{className\\}", className);
        return replaceAllProperties(finalName);
    }

    //根据模板File和IntrospectedTable,替换变量后获取模板文件相对路径
    private String getFileRelativePath(String fileName, IntrospectedTable introspectedTable){
        String className = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String classNameFirstLower = StringUtils.uncapitalize(className);
        String finalName = fileName.substring(fileName.indexOf(templateRootDir.replaceAll(replaceFromSeparator, replaceToSeparator))+templateRootDir.length(), fileName.lastIndexOf(".btl"));
        finalName = finalName.replaceAll("\\$\\{classNameFirstLower\\}", classNameFirstLower).replaceAll("\\$\\{className\\}", className);
        return replaceAllProperties(finalName);
    }

    //替换文件路径中所有属性变量
    private String replaceAllProperties(String fileName){
        for(String key : this.properties.stringPropertyNames()){
            fileName = fileName.replaceAll("\\$\\{"+key+"\\}",properties.getProperty(key));
        }
        return fileName;
    }

    //getFileBeetlGroupTemplate().getTemplate时获取模板文件相对路径
    @org.jetbrains.annotations.NotNull
    private String getFileRelativePath(String fileName){
        return fileName.substring(fileName.indexOf(templateRootDir.replaceAll(replaceFromSeparator, replaceToSeparator))+templateRootDir.length());
    }

    //根据字符串写入文件
    private void write(String relativeFileName, String content) throws ShellException, IOException {
        //根据绝对路径+com.dili.xxx包名生成文件
//        File parent = new DefaultShellCallback(true).getDirectory(targetProject, targetPackage);
        File targetFile = new File(targetDir, relativeFileName);
        //如果文件存在，并且不能覆盖就直接return了
        if(targetFile.exists() && !overwrite){
            return;
        }
        if(!targetFile.exists()) {
            File parent = new File(targetFile.getParent());
            if(!parent.exists()) {
                new File(targetFile.getParent()).mkdirs();
            }
            targetFile.createNewFile();
        }
        this.writeFile(targetFile, content, "UTF-8");
    }

    //写入文件
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    //递归获取子文件和目录
    private List<File> ergodic(File file,List<File> resultFile){
        File[] files = file.listFiles();
        if(files==null)return resultFile;// 判断目录下是不是空的
        for (File f : files) {
            if(f.isDirectory()){// 判断是否文件夹
                resultFile.add(f);
                ergodic(f,resultFile);// 调用自身,查找子目录
            }else
                resultFile.add(f);
        }
        return resultFile;
    }

    /**
     * 解压模板目录,如果模板目录是一个zip,jar文件 . 并且支持指定 zip文件的子目录作为模板目录,通过 !号分隔 指定zip文件:
     * c:\\some.zip 指定zip文件子目录: c:\some.zip!/folder/
     *
     * @throws MalformedURLException
     **/
    private List<File> unzipIfTemplateRootDirIsZipFile(ArrayList<File>     templateRootDirs) throws MalformedURLException {
        List<File> unzipIfTemplateRootDirIsZipFile = new ArrayList<File>();
        for (int i = 0; i < templateRootDirs.size(); i++) {
            File file = templateRootDirs.get(i);
            String templateRootDir = FileHelper.toFilePathIfIsURL(file);

            String subFolder = "";
            int zipFileSeperatorIndexOf = templateRootDir.indexOf("!");
            if (zipFileSeperatorIndexOf >= 0) {
                subFolder = templateRootDir.substring(zipFileSeperatorIndexOf + 1);
                templateRootDir = templateRootDir.substring(0, zipFileSeperatorIndexOf);
            }

            if (new File(templateRootDir).isFile()) {
                File tempDir = ZipUtils.unzip2TempDir(new File(templateRootDir),
                        "tmp_generator_template_folder_for_zipfile");
                unzipIfTemplateRootDirIsZipFile.add(new File(tempDir, subFolder));
            } else {
                unzipIfTemplateRootDirIsZipFile.add(new File(templateRootDir, subFolder));
            }
        }
        return unzipIfTemplateRootDirIsZipFile;
    }

    //获取文件夹下beetl模板
    private GroupTemplate getFileBeetlGroupTemplate() throws IOException {
        if(groupTemplate == null) {
//          ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
            FileResourceLoader resourceLoader = new FileResourceLoader(templateRootDir,"utf-8");
            Configuration cfg = Configuration.defaultConfiguration();
            cfg.add("/mbg-templates/btl/beetl.properties");
            groupTemplate = new GroupTemplate(resourceLoader, cfg);
            return groupTemplate;
        }
        return groupTemplate;
    }

    //获取classpath下beetl模板
    private GroupTemplate getClasspathBeetlGroupTemplate() throws IOException {
        if(groupTemplate == null) {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(templateRootDir, "utf-8");
//        FileResourceLoader resourceLoader = new FileResourceLoader(templateRootDir,"utf-8");
            Configuration cfg = Configuration.defaultConfiguration();
            cfg.add("/mbg-templates/btl/beetl.properties");
            groupTemplate = new GroupTemplate(resourceLoader, cfg);
            return groupTemplate;
        }
        return groupTemplate;
    }


}
