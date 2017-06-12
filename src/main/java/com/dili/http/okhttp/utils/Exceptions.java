package com.dili.http.okhttp.utils;

/**
 * Created by wm on 17/3/9.
 */
public class Exceptions
{
    public static void illegalArgument(String msg, Object... params)
    {
        throw new IllegalArgumentException(String.format(msg, params));
    }


}
