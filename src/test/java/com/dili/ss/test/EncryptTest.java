package com.dili.ss.test;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

/**
 * Created by asiamaster on 2017/6/29 0029.
 */
public class EncryptTest {

	public static final String PASSWORD = "security";

	/**
	 * 根据code加密，返回并打印加密后的串
	 * @return
	 */
	@Test
	public void encrypt(){
		BasicTextEncryptor encryptor = new BasicTextEncryptor();
		encryptor.setPassword(PASSWORD);
		String encrypted = encryptor.encrypt("123456");
		System.out.println(encrypted);
	}
}
