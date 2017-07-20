package com.dili.ss.metadata;


import java.io.Serializable;

/**
 * 字段元数据<br>
 * 作为结果提供使用时,防止被修改
 *
 * @author WangMi
 * @create 2010-6-2
 */
public class FieldMeta implements Comparable<FieldMeta>, Serializable, Cloneable {
	private static final long serialVersionUID = -2710206113310782917L;

	String name; // 名称
	String label; // 显示标签
	int length; // 最大的长度
	String defValue; // 缺省值
	Class<?> type; // DTO中的数据类型

	Class<? extends ValueProvider> provider; // 编辑值的提供者
	String txtField; // 显示文本的字段

	boolean required; // 是否必填
	boolean visible = true; // 是否可见
	boolean readonly; // 是否只读
	FieldEditor editor; // 编辑器类型

	boolean sortable; // 是否将作为排序字段
	boolean formable; // 是否允许显示在form中
	boolean gridable; // 是否允许显示在gird中

	int index = Integer.MAX_VALUE; // 编辑列的顺序

	public FieldMeta(String name) {
		this.name = name;
		this.label = name;
	}

	/**
	 * 排序比较
	 */
	@Override
	public int compareTo(FieldMeta o) {
		return index - o.getIndex();
	}

	@Override
	public Object clone() {
		try {
			return (FieldMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<? extends ValueProvider> getProvider() {
		return provider;
	}

	public void setProvider(Class<? extends ValueProvider> provider) {
		this.provider = provider;
	}

	public String getTxtField() {
		return txtField;
	}

	public void setTxtField(String txtField) {
		this.txtField = txtField;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public FieldEditor getEditor() {
		return editor;
	}

	public void setEditor(FieldEditor editor) {
		this.editor = editor;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isFormable() {
		return formable;
	}

	public void setFormable(boolean formable) {
		this.formable = formable;
	}

	public boolean isGridable() {
		return gridable;
	}

	public void setGridable(boolean gridable) {
		this.gridable = gridable;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
