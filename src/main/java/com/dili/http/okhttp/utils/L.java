package com.dili.http.okhttp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wm on 17/3/9.
 */
public class L
{
    private static boolean debug = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(L.class);
    public static void e(String msg)
    {
        if (debug)
        {
            LOGGER.debug("OkHttp", msg);
        }
    }

}

