package com.dili.http.okhttp.builder;

import java.util.Map;

/**
 * Created by wm on 17/3/9.
 */
public interface HasParamsable
{
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
