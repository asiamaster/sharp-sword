package com.dili.ss.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 普通Java的助手类
 *
 * @author asiamaster
 * @create 2008-11-15
 */
public final class POJOUtils {
    // 日志对象
    private static final Logger log = LoggerFactory.getLogger(POJOUtils.class);

    private static final String INVALID_METHOD_NAME = "{0}不符合POJO规范要求!无法根据方法名计算Bean字段的名称";

    private static final String NO_SUPPORT_PRIMITIVE = "当前还没实现对基本类型{0}缺省值的支持!";

    private static final String IS = "is";

    private static final String SET = "set";

    private static final String GET = "get";

    /**
     * 是否为规范的普通JavaBean方法<br>
     * 按JavaBean的规范为Bean的方法名只能是以get/set/is开头
     *
     * @param method
     *          当前方法
     * @return 符合要求的则返回true,否则返回false
     */
    public final static boolean isBeanMethod(Method method) {
        return isBeanMethod(method.getName());
    }

    /**
     * 是否为规范的普通JavaBean方法<br>
     * 按JavaBean的规范为Bean的方法名只能是以get/set/is开头
     *
     * @param methodName
     *          方法名
     * @return 符合要求的则返回true,否则返回false
     */
    public final static boolean isBeanMethod(String methodName) {
        return methodName.startsWith(GET) || methodName.startsWith(SET) || methodName.startsWith(IS);
    }

    /**
     * 是否为Get或Set方法
     *
     * @param methodName
     *          方法名
     * @return 符合要求的则返回true,否则返回false
     */
    public final static boolean isGetOrSetMethod(String methodName) {
        return methodName.startsWith(GET) || methodName.startsWith(SET);
    }

    /**
     * 是否为Get方法
     *
     * @param method
     *          方法名
     * @return
     */
    public final static boolean isGetMethod(Method method) {
        return method.getName().startsWith(GET) || method.getName().startsWith(IS);
    }

    /**
     * 是否为Get方法
     *
     * @param methodName
     *          方法名
     * @return
     */
    public final static boolean isGetMethod(String methodName) {
        return methodName.startsWith(GET) || methodName.startsWith(IS);
    }

    /**
     * 是否为Set方法
     *
     * @param method
     *          方法名
     * @return
     */
    public final static boolean isSetMethod(Method method) {
        return method.getName().startsWith(SET);
    }

    /**
     * 是否为Set方法
     *
     * @param methodName
     *          方法名
     * @return
     */
    public final static boolean isSetMethod(String methodName) {
        return methodName.startsWith(SET);
    }

    /**
     * 是否IS方法
     *
     * @param method
     *          方法名
     * @return 符合要求的则返回true,否则返回false
     */
    public final static boolean isISMethod(Method method) {
        return method.getName().startsWith(IS);
    }

    /**
     * 是否IS方法
     *
     * @param methodName
     *          方法名
     * @return 符合要求的则返回true,否则返回false
     */
    public final static boolean isISMethod(String methodName) {
        return methodName.startsWith(IS);
    }

    /**
     * 根据方法名取对应的字段名<br>
     *
     * @param method
     * @return
     */
    public final static String getBeanField(Method method) {
        return getBeanField(method.getName());
    }

    /**
     * 取在Bean中的字段属性名<br>
     * 注意：只能是符合Get/Set/Is的方法名
     *
     * @param methodName
     *          方法名
     * @return 原来加了断言，现取消，并不再记日志，因为，意义不大．
     */
    public final static String getBeanField(String methodName) {
        if (isGetOrSetMethod(methodName)) {
            return toBeanFieldName(methodName.substring(3));
        } else if (isISMethod(methodName)) {
            return toBeanFieldName(methodName.substring(2));
        } else {
            throw new RuntimeException(MessageFormat.format(INVALID_METHOD_NAME, methodName));
        }
    }

    /**
     * 转成Bean规范的名称<br>
     * 首字母小写，其它字母不便
     *
     * @param name
     *          随意的名称
     * @return
     */
    public final static String toBeanFieldName(String name) {
        return Introspector.decapitalize(name);
    }

    /**
     * 取基本类型的值
     *
     * @param primitiveClz
     *          基本类型
     * @param value
     *          值
     * @return
     */
    public final static Object getPrimitiveValue(Class<?> primitiveClz, Object value) {
        assert (primitiveClz != null);
        assert (primitiveClz.isPrimitive());

        if (value == null) {
            return getPrimitiveDefault(primitiveClz);
        }
        return ConvertUtils.convert(value.toString(), primitiveClz);
    }

    /**
     * 取基本类型的缺省值
     *
     * @param primitiveClz
     * @return
     */
    public final static Object getPrimitiveDefault(Class<?> primitiveClz) {
        assert (primitiveClz != null);
        assert (primitiveClz.isPrimitive());
        if (int.class == primitiveClz)
            return 0;
        else if (long.class == primitiveClz)
            return 0;
        else if (double.class == primitiveClz)
            return 0.00;
        else if (float.class == primitiveClz)
            return 0.00f;
        else if (boolean.class == primitiveClz)
            return false;

        else {
            String message = MessageFormat.format(NO_SUPPORT_PRIMITIVE, primitiveClz);
            log.debug(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 对象中是否存在某属性<br>
     * <li>目前是以是否能读取该属性来检查</li>
     * <li>1.能读，则表示存在该属性</li>
     * <li>2.不能读，则表示不存在该属性</li>
     *
     * @param object
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static boolean hasProperty(Object object, String name) {
        if (object instanceof Map) {
            return ((Map) object).containsKey(name);
        }
        return PropertyUtils.isReadable(object, name);
    }

    /**
     * 取属性的值
     *
     * @param object
     * @param name
     * @return
     */
    public final static Object getProperty(Object object, String name) {
        assert (name != null);

        if (object != null) {
            try {
                return PropertyUtils.getProperty(object, name);
            } catch (Exception e) {
                log.debug(MessageFormat.format("取{0}属性的值时出错,错误消息:{1}", name, e.getMessage()));
            }
        }
        return null;
    }

    /**
     * 设置属性的值
     *
     * @param object
     * @param name
     * @param value
     */
    public static void setProperty(Object object, String name, Object value) {
        assert (name != null);
        if (object != null) {
            try {
                PropertyUtils.setProperty(object, name, value);
            } catch (Exception e) {
                log.debug(MessageFormat.format("设置{0}属性的值时出错，错误消息:{1}", name, e.getMessage()));
            }
        }
    }

    /**
     * 取静态字段的值
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object getStaticField(Class clazz, String fieldName) {
        Field[] fields = clazz.getFields();
        if (fields != null) {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(fieldName)) {
                    try {
                        return field.get(null);
                    } catch (Exception ex) {
                        log.debug(MessageFormat.format("取静态字段{0}时出错，错误消息:{1}", fieldName, ex.getMessage()));
                    }
                }
            }
        }
        return null;
    }

    /**
     * clazz2是否为clazz1类或其子类<br>
     * 注意:会检查此接口
     * <li>即class1是否可以从class2中得到</li>
     *
     * @param clazz1
     * @param clazz2Name
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static boolean isAssignableFrom(Class clazz1, String clazz2Name) {
        if (clazz1 == null || clazz2Name == null)
            return false;
        try {
            return isAssignableFrom(clazz1, ClassUtils.getClass(clazz2Name));
        } catch (Exception e) {
            log.error("检查两个类的继承和实现关系时出错,错误消息：", e);
        }
        return false;
    }

    /**
     * clazz2是否为clazz1类或其子类<br>
     * 注意:会检查此接口
     * <li>即class1是否可以从class2中得到</li>
     *
     * @param clazz1Name
     * @param clazz2
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isAssignableFrom(String clazz1Name, Class clazz2) {
        if (clazz1Name == null || clazz2 == null)
            return false;
        try {
            return isAssignableFrom(ClassUtils.getClass(clazz1Name), clazz2);
        } catch (Exception e) {
            log.debug(MessageFormat.format("检查两个类的继承和实现关系时出错,错误消息：", e.getMessage()));
        }
        return false;
    }

    /**
     * clazz2是否为clazz1类或其子类<br>
     * 注意:会检查此接口
     * <li>即class1是否可以从class2中得到</li>
     *
     * @param clazz1Name
     * @param clazz2Name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isAssignableFrom(String clazz1Name, String clazz2Name) {
        if (clazz1Name == null || clazz2Name == null)
            return false;
        try {
            return isAssignableFrom(ClassUtils.getClass(clazz1Name), ClassUtils.getClass(clazz2Name));
        } catch (Exception e) {
            log.debug(MessageFormat.format("检查两个类的继承和实现关系时出错,错误消息：", e.getMessage()));
        }
        return false;
    }

    /**
     * clazz2是否为clazz1类或其子类<br>
     * 注意:会检查此接口
     * <li>即class1是否可以从class2中得到</li>
     *
     * @param clazz1
     * @param clazz2
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static boolean isAssignableFrom(Class clazz1, Class clazz2) {
        return clazz1 == null || clazz2 == null ? false : clazz1.isAssignableFrom(clazz2);
    }

    /**
     * 是否为基本类的Clazz<br>
     * 注意：字符串/日期,也认为是基本类型
     * <li>1.检查类本身是否为PrimitiveClass</li>
     * <li>2.检查类中是否有TYPE声明</li>
     * <li>3.是否为字符串类</li>
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public final static boolean isBaseTypeClass(Class clazz) {
        if (clazz == String.class || clazz == Date.class)
            return true;
        if (clazz.isPrimitive())
            return true;
        return clazz.getPackage() != null && "java.lang".equals(clazz.getPackage().getName());
    }

    // ===========================  下划线与驼峰的相互转换  ===========================

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    /**
     * 驼峰转下划线(简单写法，效率低于{@link #humpToLineFast(String)})
     */
    public static String humpToLine(String str){
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    /**
     * 驼峰转下划线,效率比humpToLine高
     */
    public static String humpToLineFast(String str){
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.indexOf("_") == 0 ? sb.substring(1) : sb.toString();
    }

}
