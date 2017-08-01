package com.dili.ss.metadata;

import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.dili.ss.util.POJOUtils;
import com.dili.ss.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * DTOMeta的管理器
 *
 * @author WangMi
 * @create 2010-6-2
 */
public class MetadataUtils {
	protected static final Logger logger = LoggerFactory.getLogger(MetadataUtils.class);
	// 缓冲,用支持并发的Map以提高性能，减少阻塞
	private static final HashMap<String, ObjectMeta> buffer = new HashMap<String, ObjectMeta>();

	/**
	 * 清除全部的Meta信息
	 */
	public static void clearDTOMetas() {
		buffer.clear();
	}

	/**
	 * 根据DTO的类取其Meta
	 *
	 * @param dtoClazz
	 * @return
	 */
	public static ObjectMeta getDTOMeta(Class<?> dtoClazz) {
		assert (dtoClazz != null);
		ObjectMeta retval = buffer.get(dtoClazz.getName());
		if (retval == null) {
			retval = new ObjectMeta();
			retval.setLoadSuper(true);
			retval.addAll(getDTOMetaByMethod(dtoClazz));
			Collections.sort(retval);
			buffer.put(dtoClazz.getName(), retval);
		}
		if (!retval.isLoadSuper()) {
			loadSuperMeta(dtoClazz, retval);
		}
		return retval;
	}

	/**
	 * 判断FieldMeta是否已在ObjectMeta中
	 *
	 * @param subMeta
	 * @param fieldMeta
	 * @return
	 */
	private static boolean hasFieldMeta(ObjectMeta subMeta, FieldMeta fieldMeta) {
		for (FieldMeta meta : subMeta) {
			if (meta.getName().equals(fieldMeta.getName()))
				return true;
		}
		return false;
	}

	/**
	 * 把父类Meta存到子类Meta中 <br>
	 * 如果子类已存在相同的FieldMea，则不会添加
	 *
	 * @param subMeta
	 * @param superMeta
	 */
	private static void addSuperMeta(ObjectMeta subMeta, ObjectMeta superMeta) {
		for (FieldMeta fieldMeta : superMeta) {
			if (!hasFieldMeta(subMeta, fieldMeta)) {
				subMeta.add(fieldMeta);
			}
		}
	}

	private static void loadMeta(Class<?> dtoClz, ObjectMeta subMeta) {
		ObjectMeta superMeta = buffer.get(dtoClz.getName());
		if (superMeta == null) {
			superMeta = new ObjectMeta();
		}
		if (!superMeta.isLoadSuper()) {// 未加载父Meta
			loadSuperMeta(dtoClz, superMeta);
		}
		addSuperMeta(subMeta, superMeta);
	}

	private static void loadSuperMeta(Class<?> dtoClazz, ObjectMeta objectMeta) {
		// 查找父类
		Class<?> superClz = dtoClazz.getSuperclass();
		if (superClz != null) {
			loadMeta(superClz, objectMeta);
		}
		// 查找接口
		Class<?>[] interfaces = dtoClazz.getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			for (Class<?> superInterface : interfaces) {
				loadMeta(superInterface, objectMeta);
			}
		}
		Collections.sort(objectMeta); // 排序
		objectMeta.setLoadSuper(true);
		buffer.put(dtoClazz.getName(), objectMeta); // 重新加到缓存
	}

	/**
	 * 根据Get方法新建一个字段的Meta
	 *
	 * @param method          方法
	 * @return 有可能返回null
	 */
	@SuppressWarnings("unchecked")
	private static FieldMeta newFieldMetaFromGetMethod(Class<?> dtoClazz, Method method) {
		FieldMeta retval = null;
		FieldDef fieldDef = method.getAnnotation(FieldDef.class);
		if (fieldDef != null) {
			retval = newFieldMetaFromFieldDef(POJOUtils.getBeanField(method
					.getName()), fieldDef);
		}
		// 如果是元数据管理的字段,则检查是否有编辑方式定义
		if (retval != null) {
			retval.setType(method.getReturnType());
			updateFieldMetaByEditMode(retval, method);
			updateFieldMetaFromField(retval, method, dtoClazz);
		}
		return retval;
	}

	private static void updateFieldMetaFromField(FieldMeta fMeta, Method method, Class<?> dtoClazz) {
		String fieldName = POJOUtils.getBeanField(method);
		Field field = null;
		if(!dtoClazz.isInterface()) {
			field = ReflectionUtils.getAccessibleField(dtoClazz, fieldName);
		}
		String dbFieldName = null;
		Column column = null;
        //没找到getter对应的字段,或者dtoClass是接口，则先取getter方法上的Column@javax.persistence.Column注解

        if(field == null){
	        column = method.getAnnotation(Column.class);
        }else{ //找到getter对应的字段则取字段上的@javax.persistence.Column注解
            column = field.getAnnotation(Column.class);
        }
		//字段和getter上都没有Column注解，则直接转换getter的字段名为下线划，以和数据库字段对应
		if(column != null) {
			dbFieldName = column.name();
		}else{
			dbFieldName = POJOUtils.humpToLineFast(fieldName);
		}
		fMeta.setColumn(dbFieldName);
	}

	/**
	 * 根据字段定义创建字段的元数据
	 *
	 * @param field
	 * @param fieldDef
	 * @return
	 */
	private static FieldMeta newFieldMetaFromFieldDef(String field, FieldDef fieldDef) {
		FieldMeta retval = new FieldMeta(field);
		// 如果没有指定标签,则直接用字段名作为标签名
		retval.setLabel((fieldDef.label() == null && fieldDef.label().length() == 0) ? field
				: fieldDef.label());
		retval.setLength(fieldDef.maxLength());
		retval.setDefValue(fieldDef.defValue());
		return retval;
	}

	/**
	 * 根据字段的编辑方式来更新字段的Meta
	 *
	 * @param fMeta
	 * @param method
	 */
	private static void updateFieldMetaByEditMode(FieldMeta fMeta, Method method) {
		EditMode editMode = method.getAnnotation(EditMode.class);
		if (editMode != null) {
			fMeta.setRequired(editMode.required());
			fMeta.setVisible(editMode.visible());
			fMeta.setReadonly(editMode.readOnly());
			fMeta.setEditor(editMode.editor());
			fMeta.setParams(editMode.params());
			fMeta.setIndex(editMode.index());
			fMeta.setSortable(editMode.sortable());
			fMeta.setFormable(editMode.formable());
			fMeta.setGridable(editMode.gridable());
			fMeta.setQueryable(editMode.queryable());
			// 界面显示所用的字段
			if (StringUtils.isNotBlank(editMode.txtField())) {
				fMeta.setTxtField(editMode.txtField());
			}
			// 提供者配置
			fMeta.setProvider(editMode.provider());
		}
	}

	private static List<FieldMeta> getDTOMetaByMethod(Class<?> dtoClazz) {
		List<FieldMeta> retval = new ArrayList<>();
		Method[] methods = dtoClazz.getMethods();
		if (methods != null) {
			for (Method method : methods) {
				if (POJOUtils.isGetMethod(method)
						|| POJOUtils.isISMethod(method)) {
					FieldMeta fieldMeta = newFieldMetaFromGetMethod(dtoClazz, method);
					if (fieldMeta != null) {
						retval.add(fieldMeta);
					}
				}
			}
		}
		return retval;
	}

}
