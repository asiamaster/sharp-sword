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
	Label,
	//按钮
	Button,
	// 普通文本
	Text,
	// 普通下拉
	Combo,
	//下拉树
	TreeComboBox,
	// 多选框
	CheckBox,
	// 日期编辑
	DateText,
	// 备注
	Notes,
	// 带多选框的对话框
	TextCheckBoxDialog,
	//图片控件
	Picturebox,

	TextWithButton,
	//多选下拉框
	MultiSelectField,

	SelectDialogField,
	//多行本文
	TextArea;
}
