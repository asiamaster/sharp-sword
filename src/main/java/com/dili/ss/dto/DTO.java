package com.dili.ss.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO对象的统一基类<br>
 * 此对象不支持直接构造
 *
 * @author WangMi
 * @create 2017-7-30
 */
public class DTO extends HashMap<String, Object> {
	private static final long serialVersionUID = -514229978937800587L;
	// 附属信息
	// 缺省情况下均没有,只有在有属性要求的情况下才创建
	private Map<String, Object> attached;

	// 是否监听修改
	private boolean listenModify;

	// 是否已经被修改
	// 只要调用了beginMonitorModify后，调过put方法都认为是进行修改
	private boolean modified;

	/**
	 * 开始监听修改
	 */
	public void beginMonitorModify() {
		listenModify = true;
		modified = false;
	}

	/**
	 * 结束修改监听 修改状态仍然保留上一次的值
	 */
	public void endMonitorModify() {
		listenModify = false;
	}

	/**
	 * 是否已处于修改状态
	 *
	 * @return modified
	 */
	public boolean isModified() {
		return modified;
	}

	@Override
	public Object put(String key, Object value) {
		Object retval = super.put(key, value);
		if (listenModify) {
			modified = true;
		}
		return retval;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (this == o) {
			return true;
		} else if (o instanceof DTO) {
			return DTOUtils.isEquals(this, o);
		} else if (DTOUtils.isProxy(o)) {
			DTO tmp = DTOUtils.go(o);
			if (this == tmp) {
				return true;
			} else {
				return DTOUtils.isEquals(this, tmp);
			}
		}
		return false;
	}

	/**
	 * 无参数构造
	 */
	public DTO() {
		super();
	}

	/**
	 * 带初始大小的构造
	 *
	 * @param initialCapacity
	 */
	public DTO(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 通过Map填充来构造
	 *
	 * @param m
	 */
	@SuppressWarnings("unchecked")
	public DTO(Map m) {
		super(m);
	}

	/**
	 * 附加属性中是否存在
	 * @param key
	 * @return
	 */
	public boolean isAttach(String key) {
		if(attached != null)
			return attached.containsKey(key);
		return false;
	}

	/**
	 * 取指定的附加属性
	 * @param key
	 * @return
	 */
	public Object getAttach(String key) {
		if(attached != null)
			return attached.get(key);
		return null;
	}

	/**
	 * 附加属性<br>
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Object attach(String key, Object value) {
		if (attached == null)
			// 由于附加属性均比较少,因此,此处初始创建3个
			attached = new HashMap<String, Object>(3);
		return attached.put(key, value);
	}

	/**
	 * 删除某个附加属性
	 * @param key
	 * @return
	 */
	public Object unattach(String key) {
		if (attached != null)
			return attached.remove(key);
		return null;
	}

	/**
	 * 目前由于只是在客户端时会使用到，为简化实现暂时使用序列化和反序列化的方式
	 *
	 * @see HashMap#clone()
	 */
	@Override
	public Object clone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception ex) {
		}
		return super.clone();
	}
}