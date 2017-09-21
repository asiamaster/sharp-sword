package com.dili.ss.mbg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.mbg.beetl.BeetlTemplateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提供者生成插件
 * Created by asiam on 2017/4/7 0007.
 */
public class MyProviderPlugin extends PluginAdapter {

    private String targetPackage = null;
    private String targetProject = null;

    public MyProviderPlugin() {
    }
    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetProject"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyProviderPlugin", "targetProject"));
            valid = false;
        }
        if(!StringUtility.stringHasValue(this.properties.getProperty("targetPackage"))) {
            warnings.add(Messages.getString("ValidationError.18", "MyProviderPlugin", "targetPackage"));
            valid = false;
        }
	    targetProject = this.properties.getProperty("targetProject");
        targetPackage = this.properties.getProperty("targetPackage");
        return valid;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String providerSuperInterface = "com.dili.ss.metadata.ValueProvider";
        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
        	//注解不为空
			if(StringUtils.isBlank(column.getRemarks())){
				continue;
			}
	        JSONObject jsonObject = BeetlTemplateUtil.getJsonObject(column.getRemarks());
			//必须要有provider属性和data属性才生成provider
			if(jsonObject == null || !jsonObject.containsKey("provider") || !jsonObject.containsKey("data")){
				continue;
			}
	        String provider = jsonObject.getString("provider");
	        JSONArray data = jsonObject.getJSONArray("data");
	        GeneratedJavaFile providerJavafile = null;

            //生成provider实现类----------------------------------------------------------------
            TopLevelClass clazz = new TopLevelClass(targetPackage + "." + StringUtils.capitalize(provider));
            clazz.setVisibility(JavaVisibility.PUBLIC);
            addJavaDocLine(clazz);

            //添加注解
            clazz.addAnnotation("@Component");
            //添加当前provider类接口
            FullyQualifiedJavaType providerSuperInterfaceType = new FullyQualifiedJavaType(providerSuperInterface);
            clazz.addSuperInterface(providerSuperInterfaceType);

			//添加导入
	        addImport(clazz);
	        //添加字段
	        addFields(clazz);
	        //添加初始化块
	        addInitializationBlock(clazz, data);
            //添加getLookupList方法
	        addGetLookupListMethod(clazz);
			//添加addGetDisplayText方法
	        addGetDisplayTextMethod(clazz);
	        //添加provider类到java生成文件
	        providerJavafile = new GeneratedJavaFile(clazz, targetProject, javaFormatter);
	        mapperJavaFiles.add(providerJavafile);
        }
        return mapperJavaFiles;
    }

    //添加静态初始化块
    private void addInitializationBlock(TopLevelClass clazz, JSONArray data){
	    InitializationBlock initializationBlock = new InitializationBlock(true);
	    initializationBlock.addBodyLine("buffer = new ArrayList<ValuePair<?>>();");
	    for(Object obj : data){
		    JSONObject jo = (JSONObject)obj;
		    initializationBlock.addBodyLine("buffer.add(new ValuePairImpl(\""+jo.get("text")+"\", \""+jo.get("value")+"\"));");
	    }
	    clazz.addInitializationBlock(initializationBlock);
    }
	//添加字段
	private void addFields(TopLevelClass clazz){
		//添加字段
		Field buffer = new Field();
		buffer.setName("buffer");
		buffer.setType(new FullyQualifiedJavaType("List<ValuePair<?>>"));
		buffer.setFinal(true);
		buffer.setStatic(true);
		buffer.setVisibility(JavaVisibility.PRIVATE);
		clazz.addField(buffer);
	}

	//添加import
    private void addImport(TopLevelClass clazz){
	    clazz.addImportedType(new FullyQualifiedJavaType("java.util.ArrayList"));
	    clazz.addImportedType(new FullyQualifiedJavaType("java.util.List"));
	    clazz.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
	    clazz.addImportedType(new FullyQualifiedJavaType("com.dili.ss.metadata.FieldMeta"));
	    clazz.addImportedType(new FullyQualifiedJavaType("com.dili.ss.metadata.ValuePair"));
	    clazz.addImportedType(new FullyQualifiedJavaType("com.dili.ss.metadata.ValuePairImpl"));
	    clazz.addImportedType(new FullyQualifiedJavaType("com.dili.ss.metadata.ValueProvider"));
	    clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Component"));
    }

    //添加类注释
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

    //添加getLookupList方法
    private void addGetLookupListMethod(TopLevelClass clazz){
        Method getLookupListMethod = new Method("getLookupList");
        getLookupListMethod.setReturnType(new FullyQualifiedJavaType("List<ValuePair<?>>"));
        getLookupListMethod.setVisibility(JavaVisibility.PUBLIC);
	    getLookupListMethod.addAnnotation("@Override");
        getLookupListMethod.addBodyLines(Lists.newArrayList("return buffer;"));
	    Parameter objParameter = new Parameter(new FullyQualifiedJavaType("Object"), "obj");
	    getLookupListMethod.addParameter(objParameter);
	    Parameter metaMapParameter = new Parameter(new FullyQualifiedJavaType("Map"), "metaMap");
	    getLookupListMethod.addParameter(metaMapParameter);
	    Parameter fieldMetaParameter = new Parameter(new FullyQualifiedJavaType("FieldMeta"), "fieldMeta");
	    getLookupListMethod.addParameter(fieldMetaParameter);
        clazz.addMethod(getLookupListMethod);
    }

	//添加getDisplayText方法
	private void addGetDisplayTextMethod(TopLevelClass clazz){
		Method getDisplayTextMethod = new Method("getDisplayText");
		getDisplayTextMethod.setReturnType(new FullyQualifiedJavaType("String"));
		getDisplayTextMethod.setVisibility(JavaVisibility.PUBLIC);
		getDisplayTextMethod.addAnnotation("@Override");
		Parameter objParameter = new Parameter(new FullyQualifiedJavaType("Object"), "obj");
		getDisplayTextMethod.addParameter(objParameter);
		Parameter metaMapParameter = new Parameter(new FullyQualifiedJavaType("Map"), "metaMap");
		getDisplayTextMethod.addParameter(metaMapParameter);
		Parameter fieldMetaParameter = new Parameter(new FullyQualifiedJavaType("FieldMeta"), "fieldMeta");
		getDisplayTextMethod.addParameter(fieldMetaParameter);

		String bodyline = "if(obj == null || obj.equals(\"\")) return null;\n" +
				"        for(ValuePair<?> valuePair : buffer){\n" +
				"            if(obj.toString().equals(valuePair.getValue())){\n" +
				"                return valuePair.getText();\n" +
				"            }\n" +
				"        }\n" +
				"        return null;";
		getDisplayTextMethod.addBodyLines(Lists.newArrayList(bodyline));
		clazz.addMethod(getDisplayTextMethod);
	}
}
