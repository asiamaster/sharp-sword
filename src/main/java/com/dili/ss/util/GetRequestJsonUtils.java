package com.dili.ss.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by asiamaster on 2017/5/26 0026.
 * request 对象的相关操作
 * @author WANGMI
 * @created 2017年5月26日
 */
public class GetRequestJsonUtils {

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return : <code>byte[]</code>
     * @throws IOException
     */
    public static String getRequestJsonString(HttpServletRequest request)
            throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if ("GET".equals(submitMehtod)) {
            return new String(request.getQueryString().getBytes("iso-8859-1"),"utf-8").replaceAll("%22", "\"");
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 获取 post 请求的 byte[] 数组
     * @param request
     * @return
     * @throws IOException
     */
    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if(contentLength<0){
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 获取 post 请求内容
     * @param request
     * @return
     * @throws IOException
     */
    public static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }

    /**
     * 获取 post 请求json内容中key对应的value
     * @param request
     * @param key
     * @return
     * @throws IOException
     */
    public static Object getRequestPostJsonValueByKey(HttpServletRequest request, String key)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        JSONObject jo = JSONObject.parseObject(new String(buffer, charEncoding));
        return jo.get(key);
    }

    /**
     * 获取 post 请求json内容中key对应的value
     * @param request
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T getRequestPostJsonValueByKey(HttpServletRequest request, String key, Class<T> clazz)
            throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        JSONObject jo = JSONObject.parseObject(new String(buffer, charEncoding));
        return (T)jo.get(key);
    }

}
