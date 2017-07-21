package com.dili.ss.beetl;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.MetadataUtils;
import com.dili.ss.metadata.ObjectMeta;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Tag;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * fieldMeta数据标签
 * Created by asiamaster on 2017/7/11 0011.
 */
@Component("fieldMeta")
public class FieldMetaTag extends Tag {
	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final String TAB = "    ";

	//标签自定义属性
	private final String DTO_CLASS_FIELD = "dtoClass";

	@Override
	public void render() {
		try {
//			BodyContent content = getBodyContent(); // 标签体内容，暂存
			Map<String, Object> argsMap = (Map)this.args[1];
			String dtoClass = (String) argsMap.get(DTO_CLASS_FIELD);
//			dtoClass参数必填
			if(StringUtils.isBlank(dtoClass)) {
				return;
			}
			Class clazz = Class.forName(dtoClass);
			ObjectMeta objectMeta = MetadataUtils.getDTOMeta(clazz);
			JSONObject jsonObject = new JSONObject();
			for(FieldMeta fieldMeta : objectMeta){
				jsonObject.put(fieldMeta.getName(), JSONObject.toJSON(fieldMeta));
			}
			ctx.byteWriter.writeString("var "+clazz.getSimpleName()+" = " + jsonObject.toJSONString()+";"+LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


}
