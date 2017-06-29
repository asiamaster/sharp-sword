package com.dili.ss.ip;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

/**
 * Created by asiam on 2017/4/6 0006.
 */
@Component
public class SM {

    @PostConstruct
    public void postConstruct() throws UnknownHostException, EmailException {
//        SimpleEmail email = new SimpleEmail();
//        //smtp host
//        email.setHostName("smtp.126.com");
//        //登陆邮件服务器的用户名和密码
//        email.setAuthentication("xxx@126.com","123456");
//        //接收人
//        email.addTo("xxx@vip.qq.com", "王者之风");
//        //发送人
//        email.setFrom("xxx@126.com", "我");
//        //标题
//        email.setSubject("Test message");
//        //邮件内容
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        email.setMsg("IP:"+ InetAddress.getLocalHost().getHostAddress()+",时间:"+sdf.format(new Date()));
//        //发送
//        email.send();
    }
}
