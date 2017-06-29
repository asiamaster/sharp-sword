package com.dili.ss.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by asiamaster on 2017/5/26 0026.
 */
public class MapUtils {
    public static <T> T get(Map params, String key, Class<T> returnType){
        Object retval = null;
        if (params.containsKey(key)) {
            retval = params.get(key);
            // 如果返回值要求是枚举，但是结果却是字符串是需在此进行转换
            if (returnType.isEnum() && retval instanceof String) {
                retval = Enum.valueOf((Class<? extends Enum>) returnType, (String) retval);
                // 如果是基本类型
            } else if (returnType.isPrimitive()) {
                // 如果当前没有值,则取出初始值
                if (retval == null) {
                    retval = POJOUtils.getPrimitiveDefault(returnType);
                    // 如果返回值却不是该类型,则需要对基进行转换
                } else if (!returnType.equals(retval.getClass())) {
                    retval = POJOUtils.getPrimitiveValue(returnType, retval);
                }
                // 如果是日期型
            } else if (Date.class.isAssignableFrom(returnType)) {
                // 并且当前字段的值不是日期型
                if (retval != null) {
                    // 转换返回值，并且将新的返回值填入委托对象中
                    if(String.class.equals(retval.getClass())){
                        retval = toDate(retval.toString());
                    }
                }
            }//如果是Integer型
            else if(Integer.class.equals(returnType)){
                retval = retval == null ?   null : new Integer(retval.toString());
            }
            //如果是Float型
            else if(Float.class.equals(returnType)){
                retval = retval == null ?   null :  new Float(retval.toString());
            }
            //如果是Double型
            else if(Double.class.equals(returnType)){
                retval = retval == null ?   null : new Double(retval.toString());
            }
            //如果是Boolean型
            else if(Boolean.class.equals(returnType)){
                if(retval != null)
                {
                    retval = new Boolean(retval.toString());
                }
            }
            // 否则需要返回缺省值
        } else if (returnType.isPrimitive()) {
            throw new RuntimeException("不支持基础类型!");
        }
        return (T) retval;
    }

    /**
     * 将字符串转成日期
     *
     * @param str
     * @return
     */
    private static Date toDate(String str) {
        assert (str != null);
        // 日期的格式器
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormater.parse(str);
        } catch (ParseException e) {
        }
        return null;
    }
}
