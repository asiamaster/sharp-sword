package com.dili.ss.mbg;

import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asiam on 2017/4/10 0007.
 */
public class MyControllerPlugin extends PluginAdapter {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    public MyControllerPlugin() {
    }
    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetProject"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyControllerPlugin", "targetProject"));
            valid = false;
        }
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetPackage"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyControllerPlugin", "targetPackage"));
            valid = false;
        }
        return valid;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String controllerSuperClass = properties.getProperty("controllerSuperClass");
        String controllerSuperInterface = properties.getProperty("controllerSuperInterface");
        String controllerTargetDir = properties.getProperty("targetProject");
        String controllerTargetPackage = properties.getProperty("targetPackage");

        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            CompilationUnit unit = javaFile.getCompilationUnit();
            //实体Bean java类型
            FullyQualifiedJavaType baseModelJavaType = unit.getType();
            String shortName = baseModelJavaType.getShortName();
            if (shortName.endsWith("Mapper")) {
                continue;
            }
            GeneratedJavaFile controllerJavafile = null;
            if (!shortName.endsWith("Example")) {
                //生成controller类
                TopLevelClass clazz = new TopLevelClass(controllerTargetPackage + "." + shortName + "Controller");
                clazz.setVisibility(JavaVisibility.PUBLIC);
                addJavaDocLine(clazz);
                addSuperInterface(clazz, controllerSuperInterface);
                addSuperClass(clazz, controllerSuperClass);
                addAutowiredService(clazz, controllerTargetPackage, shortName);

                //添加和引入注解
                clazz.addImportedType(Api.class.getName());
                clazz.addAnnotation("@Api(\"/"+StringUtils.uncapitalize(shortName)+"\")");
                clazz.addImportedType("org.springframework.stereotype.Controller");
                clazz.addAnnotation("@Controller");
                clazz.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
                clazz.addAnnotation("@RequestMapping(\"/"+StringUtils.uncapitalize(shortName)+"\")");

                //import实体类
                clazz.addImportedType(baseModelJavaType);

                addCRUDMethod(clazz, baseModelJavaType);

                //添加当前controller类
                controllerJavafile = new GeneratedJavaFile(clazz, controllerTargetDir, javaFormatter);
                mapperJavaFiles.add(controllerJavafile);
            }
        }
        return mapperJavaFiles;
    }

    private void addSuperInterface(TopLevelClass clazz, String controllerSuperInterface){
        //如果不配置controllerSuperInterface，则不会在接口上继承
        if (StringUtility.stringHasValue(controllerSuperInterface)) {
            //定义controllerSuperInterface类型
            FullyQualifiedJavaType controllerSuperInterfaceType = new FullyQualifiedJavaType(controllerSuperInterface);
            //import controllerSuperInterface接口
            clazz.addImportedType(controllerSuperInterfaceType);
            //添加controllerSuperInterface接口
            clazz.addSuperInterface(controllerSuperInterfaceType);
        }
    }

    private void addSuperClass(TopLevelClass clazz, String controllerSuperClass){
        //如果不配置controllerSuperClass，则不会在接口上继承
        if (StringUtility.stringHasValue(controllerSuperClass)) {
            FullyQualifiedJavaType controllerSuperType = new FullyQualifiedJavaType(controllerSuperClass);
            //import controllerSuperType类
            clazz.addImportedType(controllerSuperType);
            clazz.setSuperClass(controllerSuperType);
        }
    }

    private void addAutowiredService(TopLevelClass clazz, String controllerTargetPackage, String shortName){
        String servicePackage = controllerTargetPackage.substring(0, controllerTargetPackage.lastIndexOf('.'))+".service";
        String serviceClass = servicePackage+"."+shortName+"Service";
        clazz.addImportedType(serviceClass);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(serviceClass);
        Field field = new Field(StringUtils.uncapitalize(shortName)+"Service", serviceType);
        field.addAnnotation("@Autowired");
        clazz.addField(field);
        clazz.addImportedType(Autowired.class.getName());
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

    //添加增删改查方法
    private void addCRUDMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        clazz.addImportedType(ModelAttribute.class.getName());
        clazz.addImportedType(ModelMap.class.getName());
        clazz.addImportedType(BaseOutput.class.getName());
        clazz.addImportedType(ResponseBody.class.getName());
        clazz.addImportedType(List.class.getName());
        clazz.addImportedType(RequestMethod.class.getName());
        clazz.addImportedType(ApiOperation.class.getName());
        clazz.addImportedType(ApiImplicitParams.class.getName());
        clazz.addImportedType(ApiImplicitParam.class.getName());

        addIndexMethod(clazz, baseModelJavaType);
        addListMethod(clazz, baseModelJavaType);
        addListPageMethod(clazz, baseModelJavaType);
        addInsertMethod(clazz, baseModelJavaType);
        addUpdateMethod(clazz, baseModelJavaType);
        addDeleteMethod(clazz, baseModelJavaType);
    }

    //添加index方法
    private void addIndexMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listMethod = new Method("index");
        FullyQualifiedJavaType modelMapType = new FullyQualifiedJavaType("org.springframework.ui.ModelMap");
        listMethod.addParameter(0, new Parameter(modelMapType, "modelMap"));
        listMethod.setReturnType(new FullyQualifiedJavaType("java.lang.String"));
        listMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String returnLine = "return \""+StringUtils.uncapitalize(baseModelJavaType.getShortName())+"/index\";";
        bodyLines.add(returnLine);
        listMethod.addBodyLines(bodyLines);

        listMethod.addAnnotation("@ApiOperation(\"跳转到" + baseModelJavaType.getShortName() + "页面\")");
        listMethod.addAnnotation("@RequestMapping(value=\"/index\", method = RequestMethod.GET)");
        clazz.addMethod(listMethod);
    }

    //添加list方法
    private void addListMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listMethod = new Method("list");
        Parameter entityParameter = new Parameter(baseModelJavaType, StringUtils.uncapitalize(baseModelJavaType.getShortName()));
        entityParameter.addAnnotation("@ModelAttribute");
        listMethod.addParameter(0, entityParameter);
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("@ResponseBody List");
        returnType.addTypeArgument(baseModelJavaType);
        listMethod.setReturnType(returnType);
        listMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
//        String modelPutLines = "modelMap.put(\"list\", "+StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.list("+entityParameter.getName()+"));";
//        bodyLines.add(modelPutLines);
        String returnLine = "return "+StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.list("+entityParameter.getName()+");";
        bodyLines.add(returnLine);
        listMethod.addBodyLines(bodyLines);

        listMethod.addAnnotation("@ApiOperation(value=\"查询" + baseModelJavaType.getShortName() + "\", notes = \"查询"+baseModelJavaType.getShortName() + "，返回列表信息\")");
        StringBuilder sb = new StringBuilder();
        sb.append("@ApiImplicitParams({"+LINE_SEPARATOR);
        sb.append("\t\t@ApiImplicitParam(name=\"" + baseModelJavaType.getShortName() + "\", paramType=\"form\", value = \""+ baseModelJavaType.getShortName() +"的form信息\", required = false, dataType = \"string\")"+LINE_SEPARATOR);
        sb.append("\t})");
        listMethod.addAnnotation(sb.toString());
        listMethod.addAnnotation("@RequestMapping(value=\"/list\", method = {RequestMethod.GET, RequestMethod.POST})");
        clazz.addMethod(listMethod);
    }

    //添加listPage方法
    private void addListPageMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listPageMethod = new Method("listPage");
        Parameter entityParameter = new Parameter(baseModelJavaType, StringUtils.uncapitalize(baseModelJavaType.getShortName()));
        entityParameter.addAnnotation("@ModelAttribute");
        listPageMethod.addParameter(0, entityParameter);
        FullyQualifiedJavaType exception = new FullyQualifiedJavaType("Exception");
        listPageMethod.addException(exception);
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("@ResponseBody String");
        listPageMethod.setReturnType(returnType);
        listPageMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String returnLine = "return "+StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.listEasyuiPageByExample("+entityParameter.getName()+", true).toString();";
        bodyLines.add(returnLine);
        listPageMethod.addBodyLines(bodyLines);

        listPageMethod.addAnnotation("@ApiOperation(value=\"分页查询" + baseModelJavaType.getShortName() + "\", notes = \"分页查询"+baseModelJavaType.getShortName() + "，返回easyui分页信息\")");
        StringBuilder sb = new StringBuilder();
        sb.append("@ApiImplicitParams({"+LINE_SEPARATOR);
        sb.append("\t\t@ApiImplicitParam(name=\"" + baseModelJavaType.getShortName() + "\", paramType=\"form\", value = \""+ baseModelJavaType.getShortName() +"的form信息\", required = false, dataType = \"string\")"+LINE_SEPARATOR);
        sb.append("\t})");
        listPageMethod.addAnnotation(sb.toString());
        listPageMethod.addAnnotation("@RequestMapping(value=\"/listPage\", method = {RequestMethod.GET, RequestMethod.POST})");
        clazz.addMethod(listPageMethod);
    }

    //添加insert方法
    private void addInsertMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listMethod = new Method("insert");
        Parameter entityParameter = new Parameter(baseModelJavaType, StringUtils.uncapitalize(baseModelJavaType.getShortName()));
        entityParameter.addAnnotation("@ModelAttribute");
        listMethod.addParameter(0, entityParameter);
        listMethod.setReturnType(new FullyQualifiedJavaType("@ResponseBody BaseOutput"));
        listMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String contentLine1 = StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.insertSelective("+entityParameter.getName()+");";
        String returnLine = "return BaseOutput.success(\"新增成功\");";
        bodyLines.add(contentLine1);
        bodyLines.add(returnLine);
        listMethod.addBodyLines(bodyLines);

        listMethod.addAnnotation("@ApiOperation(\"新增" + baseModelJavaType.getShortName() + "\")");
        StringBuilder sb = new StringBuilder();
        sb.append("@ApiImplicitParams({"+LINE_SEPARATOR);
        sb.append("\t\t@ApiImplicitParam(name=\"" + baseModelJavaType.getShortName() + "\", paramType=\"form\", value = \""+ baseModelJavaType.getShortName() +"的form信息\", required = true, dataType = \"string\")"+LINE_SEPARATOR);
        sb.append("\t})");
        listMethod.addAnnotation(sb.toString());
        listMethod.addAnnotation("@RequestMapping(value=\"/insert\", method = {RequestMethod.GET, RequestMethod.POST})");
        clazz.addMethod(listMethod);
    }

    //添加update方法
    private void addUpdateMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listMethod = new Method("update");
        Parameter entityParameter = new Parameter(baseModelJavaType, StringUtils.uncapitalize(baseModelJavaType.getShortName()));
        entityParameter.addAnnotation("@ModelAttribute");
        listMethod.addParameter(0, entityParameter);
        listMethod.setReturnType(new FullyQualifiedJavaType("@ResponseBody BaseOutput"));
        listMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String contentLine1 = StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.updateSelective("+entityParameter.getName()+");";
        String returnLine = "return BaseOutput.success(\"修改成功\");";
        bodyLines.add(contentLine1);
        bodyLines.add(returnLine);
        listMethod.addBodyLines(bodyLines);

        listMethod.addAnnotation("@ApiOperation(\"修改" + baseModelJavaType.getShortName() + "\")");
        StringBuilder sb = new StringBuilder();
        sb.append("@ApiImplicitParams({"+LINE_SEPARATOR);
        sb.append("\t\t@ApiImplicitParam(name=\"" + baseModelJavaType.getShortName() + "\", paramType=\"form\", value = \""+ baseModelJavaType.getShortName() +"的form信息\", required = true, dataType = \"string\")"+LINE_SEPARATOR);
        sb.append("\t})");
        listMethod.addAnnotation(sb.toString());
        listMethod.addAnnotation("@RequestMapping(value=\"/update\", method = {RequestMethod.GET, RequestMethod.POST})");
        clazz.addMethod(listMethod);
    }

    //添加delete方法
    private void addDeleteMethod(TopLevelClass clazz, FullyQualifiedJavaType baseModelJavaType){
        Method listMethod = new Method("delete");
        Parameter entityParameter = new Parameter(new FullyQualifiedJavaType("Long"), "id");
        listMethod.addParameter(0, entityParameter);
        listMethod.setReturnType(new FullyQualifiedJavaType("@ResponseBody BaseOutput"));
        listMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> bodyLines = new ArrayList<>();
        String contentLine1 = StringUtils.uncapitalize(baseModelJavaType.getShortName())+"Service.delete("+entityParameter.getName()+");";
        String returnLine = "return BaseOutput.success(\"删除成功\");";
        bodyLines.add(contentLine1);
        bodyLines.add(returnLine);
        listMethod.addBodyLines(bodyLines);

        listMethod.addAnnotation("@ApiOperation(\"删除" + baseModelJavaType.getShortName() + "\")");
        StringBuilder sb = new StringBuilder();
        sb.append("@ApiImplicitParams({"+LINE_SEPARATOR);
        sb.append("\t\t@ApiImplicitParam(name=\"id\", paramType=\"form\", value = \""+ baseModelJavaType.getShortName() +"的主键\", required = true, dataType = \"long\")"+LINE_SEPARATOR);
        sb.append("\t})");
        listMethod.addAnnotation(sb.toString());
        listMethod.addAnnotation("@RequestMapping(value=\"/delete\", method = {RequestMethod.GET, RequestMethod.POST})");
        clazz.addMethod(listMethod);
    }

}
