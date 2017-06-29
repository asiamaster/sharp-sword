package com.dili.ss.ip;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
/**
 * Helper class the get the external net IP address
 * 获取外网IP,使用方法如下:
 * System.out.println(ServerHost.getInstance().getExtranetIPAddress());
 * System.out.println(ServerHost.getInstance().getExtranetIPv4Address());
 * System.out.println(ServerHost.getInstance().getExtranetIPv6Address());
 * Created by asiam on 2017/4/6 0006.
 */
public class ServerHost {
    /**
     * Singleton instance
     */
    private static final ServerHost instance = new ServerHost();
    /**
     * Access Control
     */
    private ServerHost(){};
    /**
     * @return instance
     */
    public static ServerHost getInstance() {
        return instance;
    }

    public String getExtranetIPv4Address(){
        return searchNetworkInterfaces(IPAcceptFilterFactory.getIPv4AcceptFilter());
    }


    public String getExtranetIPv6Address(){
        return searchNetworkInterfaces(IPAcceptFilterFactory.getIPv6AcceptFilter());
    }


    public String getExtranetIPAddress(){
        return searchNetworkInterfaces(IPAcceptFilterFactory.getIPAllAcceptFilter());
    }

    private String searchNetworkInterfaces(IPAcceptFilter ipFilter){
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Ignore Loop/virtual/Non-started network interface
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = addressEnumeration.nextElement();
                    String address = inetAddress.getHostAddress();
                    if(ipFilter.accept(address)){
                        return address;
                    }
                }
            }
        } catch (SocketException e) {
            //consider log for this exception
        }
        return "";
    }

    public static String getRealIp() throws SocketException {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }

    public static void main1(String[] args) throws SocketException {
        System.out.println(ServerHost.getInstance().getExtranetIPAddress());
        System.out.println(ServerHost.getInstance().getExtranetIPv4Address());
        System.out.println(ServerHost.getInstance().getExtranetIPv6Address());
        //找不到外网IP，就只能返回内网IP了
        System.out.println(ServerHost.getRealIp());
    }
}
