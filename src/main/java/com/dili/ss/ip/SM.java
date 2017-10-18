package com.dili.ss.ip;

import org.apache.commons.mail.EmailException;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

/**
 * Created by asiam on 2017/4/6 0006.
 */
//@Component
public class SM {

    @PostConstruct
    public void postConstruct() throws UnknownHostException, EmailException {
        //普通邮件
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

        //html邮件带附件
//        EmailAttachment attachment = new EmailAttachment();
//        attachment.setPath("d:\\215787.jpg");
//        attachment.setDisposition(EmailAttachment.ATTACHMENT);
//        attachment.setDescription("a picture");
//
//        HtmlEmail email = new HtmlEmail ();
////smtp host
//        email.setHostName("smtp.exmail.qq.com");
////登陆邮件服务器的用户名和密码
//        email.setAuthentication("wangmi@diligrp.com", "Asdf1qaz");
////接收人
//        email.addTo("asiamastor@vip.qq.com", "asiamaster");
////发送人
//        email.setFrom("wangmi@diligrp.com", "Me");
////标题
//        email.setSubject("Test message");
////邮件内容
//        email.setHtmlMsg("<html><body>This is a simple test of commons-email<br/>sub title<br/>content...</body></html>");
////添加附件
//		email.attach(attachment);
////发送
//        email.send();
    }
}
