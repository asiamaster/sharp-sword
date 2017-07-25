package com.dili.ss.metadata;

/**
 * Created by asiamaster on 2017/7/20 0020.
 */
/**
 * 字段的编辑方式定义
 *
 * @author WangMi
 * @create 2010-6-2
 */
public enum FieldEditor {
	Label("label"),
	//按钮
	Button("linkbutton"),
	// 普通文本
	Text("textbox"),
	//密码框
	Password("passwordbox"),
	// 数字
	Number("numberbox"),
	// 普通下拉
	Combo("combobox"),
	// 多选框
	CheckBox("check"),
	// 日期编辑
	Date("datebox"),
	// 时间编辑
	Datetime("datetimebox"),
	//多行本文
	Textarea("textarea");

	private String editor;

	FieldEditor(String editor){
		this.editor = editor;
	}

	public String getEditor() {
		return editor;
	}
}
