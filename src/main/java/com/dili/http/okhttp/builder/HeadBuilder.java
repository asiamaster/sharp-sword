package com.dili.http.okhttp.builder;

import com.dili.http.okhttp.OkHttpUtils;
import com.dili.http.okhttp.request.OtherRequest;
import com.dili.http.okhttp.request.RequestCall;

/**
 * Created by wm on 17/3/9.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
