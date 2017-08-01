package com.dili.ss.dto;

import java.io.Serializable;
import java.util.*;

/**
 * DTO的结果集
 *
 * @author wangmi
 * Created by asiamaster on 2017/7/31 0031.
 */
public class DTOList<T extends IDTO> extends AbstractList<T> implements List<T>, IPagingResult, RandomAccess, Cloneable, Serializable {
	private static final long serialVersionUID = 6003648687822621383L;

	@SuppressWarnings("unchecked")
	private List resDatas;

	private Class<T> dtoClazz;

	private int rowCount;

	/**
	 * @param dtoClazz
	 * @param resDatas
	 */
	public DTOList(Class<T> dtoClazz, List<? extends DTO> resDatas) {
		this.resDatas = resDatas;
		this.dtoClazz = dtoClazz;
	}

	/**
	 * @param dtoClazz
	 * @param size
	 */
	public DTOList(Class<T> dtoClazz, int size) {
		this.dtoClazz = dtoClazz;
		if (size == 0)
			this.resDatas = Collections.EMPTY_LIST;
		else
			this.resDatas = new ArrayList<T>(size);
	}

	/**
	 * @param dtoClazz
	 */
	public DTOList(Class<T> dtoClazz) {
		this.dtoClazz = dtoClazz;
		this.resDatas = new ArrayList<T>();
	}

	/**
	 * @return rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount
	 *          set the rowCount
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @see ArrayList#size()
	 */
	@Override
	public int size() {
		return resDatas.size();
	}

	/**
	 * @see ArrayList#get(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		Object obj = resDatas.get(index);
		T retval = null;
		// 如果对象不为空
		if (obj != null) {
			if (obj instanceof DTO) {
				retval = DTOUtils.internalAs(obj, dtoClazz, DTOHandler.class);
				resDatas.set(index, retval);
			}  else {
				retval = (T) obj;
			}
		}
		return retval;
	}

	/**
	 * @see ArrayList#toArray()
	 */
	@Override
	public Object[] toArray() {
		Object[] retval = new Object[resDatas.size()];
		for (int i = 0; i < resDatas.size(); i++) {
			retval[i] = get(i);
		}
		return retval;
	}

	/**
	 * @see ArrayList#toArray(T[])
	 */
	@SuppressWarnings( { "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] a) {
		int size = resDatas.size();
		if (a.length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		Object[] array = toArray();
		System.arraycopy(array, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	/**
	 * @see ArrayList#trimToSize()
	 */
	@SuppressWarnings("unchecked")
	public void trimToSize() {
		if (resDatas instanceof ArrayList)
			((ArrayList) resDatas).trimToSize();
	}

	/**
	 * @see ArrayList#add(int, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, T element) {
		resDatas.add(index, element);
	}

	/**
	 * @see ArrayList#add(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(T o) {
		return resDatas.add(o);
	}

	/**
	 * @see ArrayList#addAll(Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return resDatas.addAll(c);
	}

	/**
	 * @see ArrayList#addAll(int, Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return resDatas.addAll(index, c);
	}

	/**
	 * @see ArrayList#clear()
	 */
	public void clear() {
		resDatas.clear();
	}

	/**
	 * @see ArrayList#remove(int)
	 */
	@SuppressWarnings("unchecked")
	public T remove(int index) {
		return object2T(resDatas.remove(index));
	}

	/**
	 * @see ArrayList#remove(Object)
	 */
	public boolean remove(Object o) {
		return resDatas.remove(o);
	}

	/**
	 * @see ArrayList#set(int, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T element) {
		return object2T(resDatas.set(index, element));
	}

	/**
	 * @see AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		return resDatas.toString();
	}

	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T object2T(Object obj) {
		T retval = null;
		if (obj != null) {
			if (obj instanceof DTO) {
				retval = DTOUtils.internalAs(obj, dtoClazz, DTOHandler.class);
			} else {
				retval = (T) obj;
			}
		}
		return retval;
	}

}
