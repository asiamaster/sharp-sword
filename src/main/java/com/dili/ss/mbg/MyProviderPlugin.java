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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提供者生成插件<br/>
 * 后续扩展思路:
 * CREATE TABLE `mbg_provider` (
 * `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 * `provider_name` VARCHAR(50) NOT NULL COMMENT '提供者类名',
 * `value` VARCHAR(50) NULL DEFAULT NULL COMMENT '值',
 * `text` VARCHAR(50) NULL DEFAULT NULL COMMENT '文本',
 * `type` VARCHAR(50) NULL DEFAULT NULL COMMENT '缓存加载类型: \'global\', \'remote\'',
 * `url` VARCHAR(100) NULL DEFAULT NULL COMMENT '远程加载url，返回数据格式:{provider:"YnProvider", data:[{value:10,text:"不可用"},{value:20, text:"可用"}]}',
 * `sql` VARCHAR(255) NULL DEFAULT NULL COMMENT 'SQL脚本获取，要求value和text字段',
 * PRIMARY KEY (`id`)
 * )
 * COMMENT='代码生成器提供者生成列表'
 * COLLATE='utf8_general_ci'
 * ENGINE=InnoDB;
 * Created by asiam on 2017/4/7 0007.
 */
public class MyProviderPlugin extends PluginAdapter {

    private String targetPackage = null;
    private String targetProject = null;
    //只调用一次的标识，用于根据自定义的表生成provider
    private boolean generateOnce = false;
    private String tableName = null;

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
	    tableName = this.properties.getProperty("tableName");
        return valid;
    }

    @Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
        //如果配置了提供者生成表名(字段为id,provider_name,value,text,order_number)，则会根据表来生成全局缓存的提供者，便于使用provider的代码复用
        if(StringUtils.isNotBlank(tableName) && introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime().equals(tableName.trim())){
			Connection connection = DBHelper.getConnection(getContext());
	        try {
		        PreparedStatement preparedStatement = connection.prepareStatement("select provider_name, `value`, `text`, `type`, `order_number` from "+tableName+" order by provider_name, order_number");
//		        preparedStatement.setString(1, "YnProvider");//设置参数从1开始
		        ResultSet resultSet = preparedStatement.executeQuery();
		        List<JSONObject> jsonObjectList = Lists.newArrayList();
		        JSONObject jsonObject = new JSONObject();
		        while (resultSet.next()){
		        	String providerName = resultSet.getString("provider_name");
			        JSONObject dataJo = new JSONObject();
			        dataJo.put("value", resultSet.getString("value"));
			        dataJo.put("text", resultSet.getString("text"));
			        //如果有provider的键(同时肯定会有data键),并且provider和上次一样(因为排了序的)，则往现有的data中加值
		        	if(jsonObject.containsKey("provider") && jsonObject.get("provider").equals(providerName)){
				        jsonObject.getJSONArray("data").add(dataJo);
			        }else{//否则没有provider键，或者是和上次的provider不一样，则认为是一个新的provider，并且需要重置data
				        jsonObject = new JSONObject();
				        jsonObject.put("provider", providerName);
				        JSONArray ja = new JSONArray();
				        ja.add(dataJo);
				        jsonObject.put("data", ja);
				        jsonObjectList.add(jsonObject);
			        }
		        }
		        for(JSONObject jo : jsonObjectList) {
			        generateByJSONObject(jo, mapperJavaFiles);
		        }
	        } catch (SQLException e) {
		        e.printStackTrace();
	        }
        }

        //循环表中的所有字段，注释中有provider和data的字段则生成provider类
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
        	//注解不为空
			if(StringUtils.isBlank(column.getRemarks())){
				continue;
			}
			//获取注释中最后一段的json参数
	        JSONObject jsonObject = BeetlTemplateUtil.getJsonObject(column.getRemarks());
			generateByJSONObject(jsonObject, mapperJavaFiles);
        }
        return mapperJavaFiles;
    }

    private void generateByJSONObject(JSONObject jsonObject, List<GeneratedJavaFile> mapperJavaFiles) {
	    //必须要有provider属性和data属性才生成provider，示例:{provider:"YnProvider", data:[{value:10,text:"不可用"},{value:20, text:"可用"}]}
	    if(jsonObject == null || !jsonObject.containsKey("provider") || !jsonObject.containsKey("data")){
		    return;
	    }
	    String providerSuperInterface = "com.dili.ss.metadata.ValueProvider";
	    JavaFormatter javaFormatter = context.getJavaFormatter();
	    //获取注释的json中的provider属性
	    String provider = jsonObject.getString("provider");
	    //获取注释的json中的data属性
	    JSONArray data = jsonObject.getJSONArray("data");
	    //声明provider实现类
	    TopLevelClass clazz = new TopLevelClass(targetPackage + "." + StringUtils.capitalize(provider));
	    clazz.setVisibility(JavaVisibility.PUBLIC);
	    //添加类注释
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
	    GeneratedJavaFile providerJavafile = new GeneratedJavaFile(clazz, targetProject, javaFormatter);
	    mapperJavaFiles.add(providerJavafile);
    }

    //添加静态初始化块
    private void addInitializationBlock(TopLevelClass clazz, JSONArray data){
	    InitializationBlock initializationBlock = new InitializationBlock(true);
	    initializationBlock.addBodyLine("buffer = new ArrayList<ValuePair<?>>();");
	    for(Object obj : data){
		    JSONObject jo = (JSONObject)obj;
		    String value = jo.get("value") == null ? "" : jo.get("value").toString();
		    initializationBlock.addBodyLine("buffer.add(new ValuePairImpl(\""+jo.get("text")+"\", \""+value+"\"));");
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
