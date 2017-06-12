package com.dili.utils;


import com.dili.utils.domain.BasePage;
import com.dili.utils.domain.BaseQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by asiamastor on 2017/1/9.
 */
public class BeanConver {
    private final static Logger LOG = LoggerFactory.getLogger(BeanConver.class);

    /**
     * 实例类转换
     * @param source 源对象
     * @param target 目标对象
     * @param <T> 源对象
     * @param <K> 目标对象
     */
    public static<T,K> K copeBean(T source,Class<K> target ){
        if (source == null) {
            return null;
        }
        BeanCopier beanCopier = BeanCopier.create(source.getClass(),target,false);
        K result = null;
        try {
            result = (K)target.newInstance();
        } catch (Exception e) {
            LOG.error("实例转换出错");
        }
        beanCopier.copy(source,result,null);
        return result;
    }

    /**
     * 拷贝Map到Bean
     * @param map
     * @param target
     * @param <T>
     * @param <K>
     * @return
     */
    public static<T, K> K copyMap(Map map, Class<K> target){
        try {
            K result = (K)target.newInstance();
            BeanUtils.copyProperties(result, map);
            return result;
        } catch (Exception e) {
            LOG.error("实例转换出错");
            return null;
        }
    }

    private static boolean hasMethod(Class clazz, String methodName, Class<?>... parameterTypes ){
        try {
            clazz.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝继承BaseQuery的bean
     * @param source
     * @param target
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T,K> K copeBaseQueryBean(T source,Class<K> target ){
        K k = copeBean(source, target);
        if(BaseQuery.class.isAssignableFrom(target)){
            BasePage p = (BasePage)k;
            try {
                if(hasMethod(source.getClass(),"getPage" )) {
                    Method getPageIndex = source.getClass().getMethod("getPage");
                    Object pageIndex = getPageIndex.invoke(source);
                    if (pageIndex != null && (pageIndex instanceof Integer || pageIndex.getClass().getName().equals("int"))) {
                        p.setPage((Integer) pageIndex);
                    }
                }

                if(hasMethod(source.getClass(),"getRows" )) {
                    Method getPageSize = source.getClass().getMethod("getRows");
                    Object pageSize = getPageSize.invoke(source);
                    if (pageSize != null && (pageSize instanceof Integer || pageSize.getClass().getName().equals("int"))) {
                        p.setRows((Integer) pageSize);
                    } else {
                        p.setRows(BasePage.DEFAULT_PAGE_SIZE);
                    }
                }

                if(hasMethod(source.getClass(),"getOrderFieldType" )) {
                    Method getOrderFieldType = source.getClass().getMethod("getOrderFieldType");
                    Object orderFieldType = getOrderFieldType.invoke(source);
                    if (orderFieldType != null && (orderFieldType instanceof String)) {
                        p.setOrderFieldType((String) orderFieldType);
                    }
                }

                if(hasMethod(source.getClass(),"getOrderField" )) {
                    Method getOrderField = source.getClass().getMethod("getOrderField");
                    Object orderField = getOrderField.invoke(source);
                    if (orderField != null && (orderField instanceof String)) {
                        p.setOrderField((String) orderField);
                    }
                }
            } catch (NoSuchMethodException e) {
                LOG.info(target.getName()+"类没有分页方法:getPageIndex或getPageSize");
            } catch (IllegalAccessException e) {
                LOG.info(source.getClass().getName()+"类的getPageIndex或getPageSize方法调用参数不对!");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                LOG.info(source.getClass().getName()+"类的getPageIndex或getPageSize方法调用对象不对!");
                e.printStackTrace();
            }
        }
        return k;
    }

    /**
     * page实例类转换，将BasePage里面的data转换为target类型
     * @param source 源对象
     * @param target 目标对象
     * @param <T> 源对象
     * @param <K> 目标对象
     * @return
     */
    public static <T,K> BasePage<K> copePage(BasePage<T> source, Class<K> target){
        List<K> list = new ArrayList<K>();
        BasePage<K> result = new BasePage<K>();
        List<T> sourceList = source.getDatas();
        for(T af : sourceList){
            BeanCopier beanCopier = BeanCopier.create(af.getClass(),target,false);
            K af1 = null;
            try {
                af1 = (K)target.newInstance();
            } catch (Exception e) {
                LOG.error("实例转换出错");
            }
            beanCopier.copy(af,af1,null);
            list.add(af1);
        }
        result.setDatas(list);
        result.setPage(source.getPage());
        result.setRows(source.getRows());
        result.setTotalItem(source.getTotalItem());
        return result;
    }

    /**
     * page实例类转换
     * @param <T> 源对象
     * @param <K> 目标对象
     * @param source 源对象
     * @param target 目标对象
     * @return
     */
    public static <T,K> List<K> copeList(List<T> source, Class<K> target){
        List<K> list = new ArrayList<K>();
        for(T af : source){
            BeanCopier beanCopier = BeanCopier.create(af.getClass(),target,false);
            K af1 = null;
            try {
                af1 = (K)target.newInstance();
            } catch (Exception e) {
                LOG.error("实例转换出错");
            }
            beanCopier.copy(af,af1,null);
            list.add(af1);
        }
        return list;
    }

    /**
     * 将com.github.pagehelper.Page对象转换为我们自已的BasePage对象
     * @param page
     * @param <T>
     * @return
     */
    public static <T> BasePage<T> convertPage(Page<T> page){
        BasePage<T> result = new BasePage<T>();
        result.setDatas(page.getResult());
        result.setPage(page.getPageNum());
        result.setRows(page.getPageSize());
        result.setTotalItem(Integer.parseInt(String.valueOf(page.getTotal())));
        result.setTotalPage(page.getPages());
        result.setStartIndex(page.getStartRow());
        return result;
    }

    /**
     * 将com.github.pagehelper.Page对象转换为我们自已的BasePage对象
     * @param page
     * @param <T>
     * @return
     */
    public static <T> BasePage<T> convertPage(PageInfo<T> page){
        BasePage<T> result = new BasePage<T>();
        result.setDatas(page.getList());
        result.setPage(page.getPageNum());
        result.setRows(page.getPageSize());
        result.setTotalItem(Integer.parseInt(String.valueOf(page.getTotal())));
        result.setTotalPage(page.getPages());
        result.setStartIndex(page.getStartRow());
        return result;
    }

    /**
     * 将org.springframework.data.domain.Page对象转换为我们自已的BasePage对象
     * @param page
     * @param <T>
     * @return
     */
    public static <T> BasePage<T> convertPage(org.springframework.data.domain.Page<T> page){
        BasePage<T> result = new BasePage<T>();
        result.setDatas(page.getContent());
        result.setPage(page.getNumber()+1);
        result.setRows(page.getSize());
        result.setTotalItem(Integer.parseInt(String.valueOf(page.getTotalElements())));
        result.setTotalPage(page.getTotalPages());
        Integer startIndex = (result.getPage() - 1)*result.getRows()+1; //计算出页开始行数
        if(startIndex<1){
            startIndex = 1;
        }
        if(startIndex>result.getTotalItem()){
            startIndex = result.getTotalItem();
        }
        result.setStartIndex(startIndex);
        return result;
    }
}
