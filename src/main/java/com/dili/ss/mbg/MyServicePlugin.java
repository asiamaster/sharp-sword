package com.dili.ss.mbg;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asiam on 2017/4/7 0007.
 */
public class MyServicePlugin extends PluginAdapter {

    private ShellCallback shellCallback = null;
    private String targetPackage = null;

    public MyServicePlugin() {
        shellCallback = new DefaultShellCallback(false);
    }
    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetProject"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyServicePlugin", "targetProject"));
            valid = false;
        }
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetPackage"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyServicePlugin", "targetPackage"));
            valid = false;
        }
        targetPackage = this.properties.getProperty("targetPackage");
        return valid;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String serviceSuperClass = properties.getProperty("serviceSuperClass");
        String serviceSuperInterface = properties.getProperty("serviceSuperInterface");
        String serviceTargetDir = properties.getProperty("targetProject");
        String serviceTargetPackage = properties.getProperty("targetPackage");

        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            CompilationUnit unit = javaFile.getCompilationUnit();
            FullyQualifiedJavaType baseModelJavaType = unit.getType();
            String shortName = baseModelJavaType.getShortName();
            GeneratedJavaFile serviceJavafile = null;
            if (shortName.endsWith("Mapper")) { // 扩展Mapper
                continue;
//                if (StringUtility.stringHasValue(expandServiceTargetPackage)) {
//                    Interface mapperInterface = new Interface(
//                            expandServiceTargetPackage + "." + shortName.replace("Mapper", "ExpandMapper"));
//                    mapperInterface.setVisibility(JavaVisibility.PUBLIC);
//                    mapperInterface.addJavaDocLine("/**");
//                    mapperInterface.addJavaDocLine(" * " + shortName + "扩展");
//                    mapperInterface.addJavaDocLine(" */");
//
//                    FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(expandServiceSuperClass);
//                    mapperInterface.addImportedType(daoSuperType);
//                    mapperInterface.addSuperInterface(daoSuperType);
//
//                    serviceJavafile = new GeneratedJavaFile(mapperInterface, serviceTargetDir, javaFormatter);
//                    try {
//                        File mapperDir = shellCallback.getDirectory(serviceTargetDir, serviceTargetPackage);
//                        File mapperFile = new File(mapperDir, serviceJavafile.getFileName());
//                        // 文件不存在
//                        if (!mapperFile.exists()) {
//                            mapperJavaFiles.add(serviceJavafile);
//                        }
//                    } catch (ShellException e) {
//                        e.printStackTrace();
//                    }
//                }
            } else if (!shortName.endsWith("Example")) {
                //生成service接口----------------------------------------------------------------
                Interface mapperInterface = new Interface(serviceTargetPackage + "." + shortName + "Service");

                mapperInterface.setVisibility(JavaVisibility.PUBLIC);
                addJavaDocLine(mapperInterface);
                addSuperInterface(mapperInterface, serviceSuperInterface, baseModelJavaType);

                //生成javaFile
                serviceJavafile = new GeneratedJavaFile(mapperInterface, serviceTargetDir, javaFormatter);
                mapperJavaFiles.add(serviceJavafile);

                //生成service实现类----------------------------------------------------------------
                TopLevelClass clazz = new TopLevelClass(serviceTargetPackage + ".impl." + shortName + "ServiceImpl");
                clazz.setVisibility(JavaVisibility.PUBLIC);
                addJavaDocLine(clazz);
                addSuperClass(clazz, serviceSuperClass, baseModelJavaType);

                //添加注解
                clazz.addImportedType("org.springframework.stereotype.Service");
                clazz.addAnnotation("@Service");
                //添加当前service类接口
                clazz.addImportedType(mapperInterface.getType());
                clazz.addSuperInterface(mapperInterface.getType());
                serviceJavafile = new GeneratedJavaFile(clazz, serviceTargetDir, javaFormatter);
                mapperJavaFiles.add(serviceJavafile);

                //添加getActualDao方法
                addGetActualDaoMethod(clazz, baseModelJavaType);
            }
        }
        return mapperJavaFiles;
    }

    private void addJavaDocLine(TopLevelClass clazz){
        clazz.addJavaDocLine("/**");
        clazz.addJavaDocLine(" * 由MyBatis Generator工具自动生成");
        StringBuilder sb = new StringBuilder();
        sb.append(" * This file was generated on ");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        sb.append('.');
        clazz.addJavaDocLine(sb.toString());
        clazz.addJavaDocLine(" */");
    }

    private void addJavaDocLine(Interface mapperInterface){
        mapperInterface.addJavaDocLine("/**");
        mapperInterface.addJavaDocLine(" * 由MyBatis Generator工具自动生成");
        StringBuilder sb = new StringBuilder();
        sb.append(" * This file was generated on ");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        sb.append('.');
        mapperInterface.addJavaDocLine(sb.toString());
        mapperInterface.addJavaDocLine(" */");
    }


    private void addSuperClass(TopLevelClass clazz, String serviceSuperClass, FullyQualifiedJavaType baseModelJavaType){
        //如果不配置serviceSuperClass，则不会在接口上继承
        if (StringUtility.stringHasValue(serviceSuperClass)) {
            FullyQualifiedJavaType serviceSuperType = new FullyQualifiedJavaType(serviceSuperClass);
            FullyQualifiedJavaType simpleServiceSuperType = new FullyQualifiedJavaType(serviceSuperClass.substring(serviceSuperClass.lastIndexOf(".")+1));
            // 添加泛型支持
            simpleServiceSuperType.addTypeArgument(baseModelJavaType);
            simpleServiceSuperType.addTypeArgument(new FullyQualifiedJavaType("java.lang.Long"));
            clazz.addImportedType(baseModelJavaType);
            clazz.addImportedType(serviceSuperType);
            clazz.setSuperClass(simpleServiceSuperType);
        }
    }

    private void addSuperInterface(Interface mapperInterface, String serviceSuperInterface, FullyQualifiedJavaType baseModelJavaType){
        //              如果不配置serviceSuperInterface，则不会在接口上继承
        if (StringUtility.stringHasValue(serviceSuperInterface)) {
            //定义BaseService类型
            FullyQualifiedJavaType serviceSuperInterfaceType = new FullyQualifiedJavaType(serviceSuperInterface);
            FullyQualifiedJavaType simpleServiceSuperInterfaceType = new FullyQualifiedJavaType(serviceSuperInterface.substring(serviceSuperInterface.lastIndexOf(".")+1));
            // 添加第一个泛型参数(实体)支持
            simpleServiceSuperInterfaceType.addTypeArgument(baseModelJavaType);
            // 添加第二个泛型参数(主键类型,这里写死为Long)支持
            simpleServiceSuperInterfaceType.addTypeArgument(new FullyQualifiedJavaType("java.lang.Long"));
            //import实体类
            mapperInterface.addImportedType(baseModelJavaType);
            //import serviceSuperInterface接口
            mapperInterface.addImportedType(serviceSuperInterfaceType);
            //添加 serviceSuperInterface接口
            mapperInterface.addSuperInterface(simpleServiceSuperInterfaceType);
        }
    }

    //添加getActualDao方法
    private void addGetActualDaoMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method getActualDaoMethod = new Method("getActualDao");
        getActualDaoMethod.setReturnType(new FullyQualifiedJavaType(baseModelJavaType.getShortName()+"Mapper"));
        getActualDaoMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String returnLine = "return ("+baseModelJavaType.getShortName()+"Mapper)getDao();";
        bodyLines.add(returnLine);
        getActualDaoMethod.addBodyLines(bodyLines);
        clazz.addMethod(getActualDaoMethod);
        String javaClientTargetPackage = null;
        if(getContext().getJavaClientGeneratorConfiguration() != null) {
            javaClientTargetPackage = getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        }
        if(properties.getProperty("javaClientTargetPackage")!= null){
            javaClientTargetPackage = properties.getProperty("javaClientTargetPackage");
        }else {
            String mapperTargetPackage = targetPackage.substring(0, targetPackage.lastIndexOf(".")) + ".dao";
            javaClientTargetPackage = StringUtils.isBlank(javaClientTargetPackage) ? mapperTargetPackage : javaClientTargetPackage;
        }
        clazz.addImportedType(javaClientTargetPackage+"."+baseModelJavaType.getShortName()+"Mapper");
    }
}
