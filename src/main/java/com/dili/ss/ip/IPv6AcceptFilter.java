package com.dili.ss.ip;

/**
 * Created by asiam on 2017/4/6 0006.
 */
public class IPv6AcceptFilter implements IPAcceptFilter{
    private static IPAcceptFilter instance = null;

    /**
     * Access Control
     */
    private IPv6AcceptFilter(){};

    /**
     * Ignore multiple thread sync problem in extreme case
     */
    public static IPAcceptFilter getInstance(){
        if(instance == null){
            instance = new IPv6AcceptFilter();
        }
        return instance;
    }

    @Override
    public boolean accept(String ipAddress) {
        return ipAddress != null && ipAddress.indexOf(IPv6KeyWord) > -1;
    }


}
