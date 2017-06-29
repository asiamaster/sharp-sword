package com.dili.ss.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Created by asiamastor on 2017/1/11.
 */
@Configuration
@ConditionalOnExpression("'${web.enable}'=='true'")
public class WebConfig {

//    Spring MVC 的 CharacterEncodingFilter
//    @Bean
//    public CharacterEncodingFilter characterEncodingFilter() {
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        return filter;
//    }

    @Bean
    public Converter<String, Date> addDateConvert() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(source);
                } catch (ParseException e) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = sdf.parse(source);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                return date;
            }
        };
    }

    @Bean
    public Converter<String, LocalDate> addLocalDateConvert() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        };
    }

    @Bean
    public Converter<String, LocalDateTime> addLocalDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        };
    }

    @Bean
    public Converter<String, Instant> addInstantConvert() {
        return new Converter<String, Instant>() {
            @Override
            public Instant convert(String source) {
                try {
                    //毫秒数转为Instant
                    return Instant.ofEpochMilli(Long.parseLong(source));
                } catch (NumberFormatException e) {
                    //转换失败直接抛运行时异常
                    return Instant.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).parse(source));
                }
            }
        };
    }

}
