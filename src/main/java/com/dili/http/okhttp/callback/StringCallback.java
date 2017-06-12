package com.dili.http.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by wm on 17/3/9.
 */
public abstract class StringCallback extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }
}
