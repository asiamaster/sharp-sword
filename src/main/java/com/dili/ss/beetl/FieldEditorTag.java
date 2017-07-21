package com.dili.ss.beetl;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.metadata.FieldEditor;
import org.beetl.core.Tag;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by asiamaster on 2017/7/21 0021.
 */
@Component("fieldEditor")
public class FieldEditorTag extends Tag {
	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final String TAB = "    ";

	//标签自定义属性

	@Override
	public void render() {
		try {
//			BodyContent content = getBodyContent(); // 标签体内容，暂存
			Map<String, Object> argsMap = (Map) this.args[1];
			JSONObject jsonObject = new JSONObject();
			for(FieldEditor fieldEditor : FieldEditor.values()){
				jsonObject.put(fieldEditor.name(), fieldEditor.getEditor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
