package com.dili.ss.metadata;

/**
 * 值对的实殃
 *
 * @author WangMi
 * @create 2017-05-30
 */
public class ValuePairImpl<T> implements ValuePair<T> {
	private static final long serialVersionUID = -7249602894129329260L;

	// 缺省的值对
	public static final ValuePairImpl<Object> EMPTY = new ValuePairImpl<Object>(null, null);

	// 显示名称
	private String text;

	// 值
	private T value;

	public ValuePairImpl() {
	}

	/**
	 * 根据名字和值进行构造
	 *
	 * @param name
	 *          名称
	 * @param value
	 *          值
	 */
	public ValuePairImpl(String name, T value) {
		this.text = name;
		this.value = value;
	}
	@Override
	public String getText() {
		return text;
	}
	@Override
	public T getValue() {
		return value;
	}
	@Override
	public void setText(String name) {
		this.text = name;
	}
	@Override
	public void setValue(T value) {
		this.value = value;
	}

}

