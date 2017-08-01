package com.dili.http.okhttp.log;

import com.dili.http.okhttp.utils.L;
import okhttp3.*;
import okio.Buffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by wm on 17/3/9.
 */
public class LoggerInterceptor implements Interceptor
{
    public static final String TAG = "OkHttpUtils";
    private String tag;
    private boolean showResponse;
    private static final Logger Log = LoggerFactory.getLogger(L.class);

    public LoggerInterceptor(String tag, boolean showResponse)
    {
        if (StringUtils.isEmpty(tag))
        {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag)
    {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response)
    {
        try
        {
            //===>response logger
            Log.debug("========response'logger=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.debug( "url : " + clone.request().url());
            Log.debug( "code : " + clone.code());
            Log.debug( "protocol : " + clone.protocol());
            if (!StringUtils.isEmpty(clone.message()))
                Log.debug( "message : " + clone.message());

            if (showResponse)
            {
                ResponseBody body = clone.body();
                if (body != null)
                {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null)
                    {
                        Log.debug( "responseBody's contentType : " + mediaType.toString());
                        if (isText(mediaType))
                        {
                            String resp = body.string();
                            Log.debug( "responseBody's content : " + resp);

                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else
                        {
                            Log.debug( "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }

            Log.debug( "========response'logger=======end");
        } catch (Exception e)
        {
//            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request)
    {
        try
        {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.debug( "========request'logger=======");
            Log.debug( "method : " + request.method());
            Log.debug( "url : " + url);
            if (headers != null && headers.size() > 0)
            {
                Log.debug( "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null)
            {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null)
                {
                    Log.debug( "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType))
                    {
                        Log.debug( "requestBody's content : " + bodyToString(request));
                    } else
                    {
                        Log.debug( "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.debug( "========request'logger=======end");
        } catch (Exception e)
        {
//            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request)
    {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e)
        {
            return "something error when show requestBody.";
        }
    }
}
