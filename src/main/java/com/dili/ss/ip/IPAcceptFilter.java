package com.dili.ss.ip;

/**
 * Created by asiam on 2017/4/6 0006.
 */
public interface IPAcceptFilter {
    public String IPv6KeyWord = ":";
    public boolean accept(String ipAddress);
}
