package com.dili.ss.mbg.beetl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * beetl模板取字段注释信息中的名称，描述或JSON参数对象
 * Created by asiamaster on 2017/6/6 0006.
 */
public class BeetlTemplateUtil {

    /**
     * 获得json对象,如 名称##字段名称描述。。。##{json}
     * @param remark
     * @return
     */
    public static JSONObject getJsonObject(String remark){
        if(StringUtils.isBlank(remark) || !remark.contains("##")) return null;
        String jsonStr = remark.substring(remark.lastIndexOf("##")+2).trim();
        if(jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
            try {
                return JSONObject.parseObject(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 获取字段名
     * @param remark
     * @return
     */
    public static String getFieldName(String remark){
        if(remark != null && remark.contains("##")){
            return remark.substring(0, remark.indexOf("##"));
        }else{
            return remark;
        }
    }

    /**
     * 获取描述,如：名称##描述##JSON
     * 只有一个##(两段数据)，则取第二段
     * @param remark
     * @return
     */
    public static String getComment(String remark){
        if(remark != null && remark.contains("##")){
            //只有一个##(两段数据)，则取第二段
            if(remark.indexOf("##") == remark.lastIndexOf("##")){
                return remark.substring(remark.indexOf("##")+2);
            }
            return remark.substring(remark.indexOf("##")+2, remark.lastIndexOf("##"));
        }else{
            return remark;
        }
    }

}
