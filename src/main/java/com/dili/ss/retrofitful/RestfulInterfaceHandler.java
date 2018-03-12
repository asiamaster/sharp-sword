package com.dili.ss.retrofitful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.*;
import com.dili.ss.util.SystemConfigUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by asiamastor on 2016/11/28.
 */
public class RestfulInterfaceHandler<T> implements InvocationHandler, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RestfulInterfaceHandler.class);

    private static final long serialVersionUID = -109085454821564L;
    // 代理的类名
    private Class<T> proxyClazz;
    /**
     * 约定的构造器
     *
     * @param proxyClazz
     */
    public RestfulInterfaceHandler(Class<T> proxyClazz) {
        this.proxyClazz = proxyClazz;
    }

    /**
     * 进行代理调用
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Restful restful = proxyClazz.getAnnotation(Restful.class);
        POST post = method.getAnnotation(POST.class);
        GET get = method.getAnnotation(GET.class);
        //如果有POST注解
        if(post != null){
            return doPost(method, args, restful, post);
        }else if(get != null){
            return doGet(method, args, restful, get);
        }
        return null;
    }

    private String getBaseUrl(Restful restful){
        String baseUrl = StringUtils.isBlank(restful.baseUrl()) ? restful.value().trim() : restful.baseUrl().trim();
        if(baseUrl.startsWith("${") && baseUrl.endsWith("}")){
            String key = baseUrl.substring(2, baseUrl.length()-1).trim();
            return SystemConfigUtils.getProperty(key);
        }else{
            return baseUrl;
        }
    }

    private Object doPost(Method method, Object[] args, Restful restful, POST post){
        //验证POST的value不为空
        if(StringUtils.isBlank(post.value())){
            throw new RuntimeException(method.getName()+"方法的POST注解参数值为空!");
        }
        String baseUrl = getBaseUrl(restful);
        if(StringUtils.isBlank(baseUrl)){
            throw new RuntimeException("Restful注解参数值["+restful.baseUrl()+"]为空或属性不存在!");
        }
        String url = baseUrl+post.value();
        DelegateService delegate = new DelegateService();
        //如果只有一个参数，并且该参数有VOBody注解，多个参数则不考虑VOBody注入
        if(args.length == 1 && method.getParameterAnnotations()[0][0].annotationType().equals(VOBody.class)){
            return delegate.httpPost(url, args[0], method.getGenericReturnType());
        }
        //如果只有一个参数，并且该参数有VOSingleParameter注解，多个参数则不考虑VOSingleParameter注入
        if(args.length == 1 && method.getParameterAnnotations()[0][0].annotationType().equals(VOSingleParameter.class)){
            return delegate.httpPost(url, args[0], method.getGenericReturnType());
        }
        //扫描VOField注解
        JSONObject jo = new JSONObject();
        Annotation[][] ass = method.getParameterAnnotations();
        for(int i=0; i<ass.length; i++){
            for(int j=0; j<ass[i].length; j++){
                if(VOField.class.isAssignableFrom(ass[i][j].getClass())) {
                    jo.put(((VOField) ass[i][j]).value(), args[i]);
                }
            }
        }
        return delegate.httpPost(url, jo, method.getGenericReturnType());
    }

    private Object doGet(Method method, Object[] args, Restful restful, GET get){
        //验证POST的value不为空
        if(StringUtils.isBlank(get.value())){
            throw new RuntimeException(method.getName()+"方法的POST注解参数值为空!");
        }
        String baseUrl = getBaseUrl(restful);
        if(StringUtils.isBlank(baseUrl)){
            throw new RuntimeException("Restful注解参数值["+restful.baseUrl()+"]为空或属性不存在!");
        }
        String url = baseUrl+get.value();
        DelegateService delegate = new DelegateService();
        if(args == null)
            return delegate.httpGet(url, method.getGenericReturnType());

        //用于拼装URL参数
        JSONObject jo = new JSONObject();
        //如果只有一个参数，并且该参数有VOBody注解，多个参数则不考虑VOBody注入
        if(args.length == 1 && method.getParameterAnnotations()[0][0].annotationType().equals(VOBody.class)){
            jo = (JSONObject)JSON.toJSON(args[0]);
        }
        //如果只有一个参数，并且该参数有VOSingleParameter注解，多个参数则不考虑VOSingleParameter注入
        else if(args.length == 1 && method.getParameterAnnotations()[0][0].annotationType().equals(VOSingleParameter.class)){
            jo.put(method.getParameters()[0].getName(), args[0]);
        }else {
            //扫描VOField注解
            Annotation[][] ass = method.getParameterAnnotations();
            for (int i = 0; i < ass.length; i++) {
                for (int j = 0; j < ass[i].length; j++) {
                    if (VOField.class.isAssignableFrom(ass[i][j].getClass())) {
                        jo.put(((VOField) ass[i][j]).value(), args[i]);
                    }
                }
            }
        }
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(url);
            if (jo != null) {
                Iterator i$ = jo.entrySet().iterator();
                while (i$.hasNext()) {
                    Map.Entry entry = (Map.Entry) i$.next();
                    String value = entry.getValue() == null? null: entry.getValue().toString();
                    builder.addParameter((String) entry.getKey(), value);
                }
            }
            url = builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return delegate.httpGet(url, method.getGenericReturnType());
    }

    /**
     * 当前的代理接口
     *
     * @return proxyClazz
     */
    public Class<T> getProxyClazz() {
        return proxyClazz;
    }

    /**
     * 设置委托的接口
     * @param proxyClazz
     */
    void setProxyClazz(Class<T> proxyClazz) {
        this.proxyClazz = proxyClazz;
    }

    /**
     * 委托服务
     */
    private static class DelegateService {

        protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        static OkHttpClient okHttpClient;
        static OkHttpUtils okHttpUtils;
        static {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
//                .addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    //其他配置
                    .build();
            okHttpUtils = OkHttpUtils.initClient(okHttpClient);
        }
        DelegateService(){
        }

        protected static <T extends BaseOutput<?>> T httpGet(String url, Type retType){
            return execute(url, null, retType, "GET");
        }
        protected static <T extends BaseOutput<?>>  T httpPost(String url, Object paramObj, Type retType){
            return execute(url, paramObj, retType, "POST");
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static <T extends BaseOutput>  T execute(String url, Object paramObj, Type type, String httpMethod){
            T output=  (T) new BaseOutput();
//            HttpResponse httpResponse = null;
            Response resp = null;
            try{
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json;charset=utf-8");

                if("POST".equalsIgnoreCase(httpMethod)){
                    String json = paramObj instanceof String ? (String)paramObj : JSON.toJSONString(paramObj);
//                    logger.info("RestfulInterfaceHandler.DelegateService.execute, url:"+url+", 返回类型type:+"+type+",json:" + json);
                    resp = okHttpUtils
                            .postString().headers(headersMap)
                            .url(url).content(json)
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute();
//                    httpResponse = HttpRequester.sendPost(Constants.FUNDS_BASE_URL + url, null, JSON.toJSONString(paramObj));
                }else{
                    resp = okHttpUtils
                            .get()
                            .url(url).params((Map)JSON.toJSON(paramObj))
                            .build()
                            .execute();
//                    httpResponse = HttpRequester.sendGet(Constants.FUNDS_BASE_URL + url, null);
                }

                if(resp.isSuccessful()){
                    String result = resp.body().string();
//                    logger.info("RestfulInterfaceHandler.DelegateService.execute调用成功,结果:"+result);
                    return (T)JSON.parseObject(result, type);
                }else{
                    logger.info("RestfulInterfaceHandler.DelegateService.execute调用失败,code:"+resp.code()+", message:"+resp.message());
                    output.setCode(ResultCode.APP_ERROR);
                    output.setResult(String.format("远程调用发生异常code:[%s], message:[%s]", resp.code(),resp.message()));
                    output = JSON.parseObject(JSON.toJSONString(output), type);
                }
//                if (httpResponse == null) {
//                    output.setCode(ResultCode.APP_ERROR);
//                    output.setResult("[FUNDS]远程调用失败，调用Url之后无返回值");
//                    //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
//                    return JSON.parseObject(JSON.toJSONString(output), type);
//                }
//                int code = httpResponse.getStatusCode();
//                if (code != 200) {
//                    output.setCode(ResultCode.APP_ERROR);
//                    output.setResult("http code:"+code +";"+String.format("[FUNDS]远程调用失败，返回状态码为%s", code));
//                    //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
//                    output = JSON.parseObject(JSON.toJSONString(output), type);
//                }else if (StringUtils.isBlank(httpResponse.getBody())){
//                    output.setCode(ResultCode.APP_ERROR);
//                    output.setResult("http code:"+code +";"+String.format("[FUNDS]远程调用失败，调用Url后返回结果为空,返回状态码为%s", code));
//                    //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
//                    output = JSON.parseObject(JSON.toJSONString(output), type);
//                }else {
//                    output = JSON.parseObject(httpResponse.getBody(), type);
//                }
            } catch (Exception e) {
                logger.info("RestfulInterfaceHandler.DelegateService.execute调用异常,message:"+e.getMessage());
                output.setCode(ResultCode.APP_ERROR);
                output.setResult(String.format("远程调用发生异常[%s]", e.getMessage()));
                //解决：java.lang.ClassCastException:BaseOutput cannot be cast to PageOutput
                output = JSON.parseObject(JSON.toJSONString(output), type);
            }
            return output;
        }
    }

}
