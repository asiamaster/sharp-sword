package com.dili.ss.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 返回值类型转换工厂，用于解决DTOHandler过多的if判断
 */
public class ReturnTypeHandlerFactory {

    static final Map<Class<?>, Strategy> cache = new HashMap<>();

    static {
        cache.put(Long.class, new LongStrategy());
        cache.put(Integer.class, new IntegerStrategy());
        cache.put(Float.class, new FloatStrategy());
        cache.put(Double.class, new DoubleStrategy());
        cache.put(Date.class, new DateStrategy());
        cache.put(Boolean.class, new BooleanStrategy());
        cache.put(Byte.class, new ByteStrategy());
        cache.put(BigDecimal.class, new BigDecimalStrategy());
        cache.put(Clob.class, new ClobStrategy());
        cache.put(Instant.class, new InstantStrategy());
        cache.put(LocalDateTime.class, new LocalDateTimeStrategy());
    }

    /**
     * 根据类型，选择对应的策略器执行
     * @param type  要转换的类型
     * @param value 要转换的值
     * @return
     */
    public static Object convertValue(Class<?> type, Object value) {
        Strategy strategy = cache.get(type);
        if(strategy == null){
            return null;
        }
        return strategy.convert(value);
    }

    /**
     * 策略接口
     */
    public interface Strategy {
        /**
         * 类型转换
         * @param value
         * @return
         */
        Object convert(Object value);
    }

    /**
     * 长整型转换策略
     */
    private static class LongStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Long.parseLong(value.toString());
        }
    }

    /**
     * 整型转换策略
     */
    private static class IntegerStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Integer.parseInt(value.toString());
        }
    }

    /**
     * Float转换策略
     */
    private static class FloatStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Float.parseFloat(value.toString());
        }
    }

    /**
     * Double转换策略
     */
    private static class DoubleStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Double.parseDouble(value.toString());
        }
    }

    /**
     * Byte转换策略
     */
    private static class ByteStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Byte.parseByte(value.toString());
        }
    }

    /**
     * Boolean转换策略
     */
    private static class BooleanStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return Boolean.parseBoolean(value.toString());
        }
    }

    /**
     * BigDecimal转换策略
     */
    private static class BigDecimalStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return new BigDecimal(value.toString());
        }
    }

    /**
     * Clob转换策略
     */
    private static class ClobStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            return getClobString((java.sql.Clob)value);
        }
    }

    /**
     * Instant转换策略
     */
    private static class InstantStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            if(String.class.equals(value.getClass())){
                return Instant.from(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.systemDefault()).parse((String)value));
            } else if(Long.class.equals(value.getClass())){
                return Instant.ofEpochMilli((Long) value);
            }
            return null;
        }
    }

    /**
     * LocalDateTime转换策略
     */
    private static class LocalDateTimeStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            if(String.class.equals(value.getClass())){
                return LocalDateTime.parse((String)value, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneId.systemDefault()));
            }else if(Long.class.equals(value.getClass())){
                return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZoneId.systemDefault());
            }
            return null;
        }
    }

    /**
     * Date转换策略
     */
    private static class DateStrategy implements Strategy{

        @Override
        public Object convert(Object value) {
            // 如果当前字段的值不是日期型, 转换返回值，并且将新的返回值填入委托对象中
            if(String.class.equals(value.getClass())){
                try {
                    return StringUtils.isNumeric(value.toString()) ? new Date(Long.parseLong(value.toString())) : DateUtils.parseDate(value.toString(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (Long.class.equals(value.getClass())) {
                return new Date((Long)value);
            }
            return null;
        }
    }

    private static String getClobString(java.sql.Clob c) {
        try {
            Reader reader=c.getCharacterStream();
            if (reader == null) {
                return null;
            }
            StringBuffer sb = new StringBuffer();
            char[] charbuf = new char[4096];
            for (int i = reader.read(charbuf); i > 0; i = reader.read(charbuf)) {
                sb.append(charbuf, 0, i);
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

}
