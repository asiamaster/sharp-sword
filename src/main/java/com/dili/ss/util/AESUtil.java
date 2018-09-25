package com.dili.ss.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * @desc AES 加密工具类
 * @author wangmi
 */
public class AESUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param password 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return Base64.encodeBase64String(result);//通过Base64转码返回
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param password
     * @return
     */
    public static String decrypt(String content, String password) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            //执行操作
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            kg.init(128, secureRandom);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main1(String[] args) {
        String s = "{\n" +
                "    \"type\": \"json\",\n" +
                "    \"from\": \"settlement\",\n" +
                "    \"sendTime\": \"2018-09-07 15:02:03\",\n" +
                "    \"data\": {\n" +
                "        \"certificateType\": \"id\",\n" +
                "        \"certificateNumber\": \"510100201202021111\",\n" +
                "        \"sourceSystem\": \"settlement\",\n" +
                "        \"market\" : \"hd\", \n" +
                "        \"type\" : \"purchase\", \n" +
                "        \"organizationType\": \"individuals\",\n" +
                "        \"name\": \"test090701\",\n" +
                "        \"sex\": \"male\",\n" +
                "        \"phone\": \"13000000001\",\n" +
                "        \"created\": \"2018-03-25 17:25:36\",\n" +
                "        \"address\": [\n" +
                "            \"四川省成都市锦江区人民东路6号\",\n" +
                "            \"成都市人民北路8号\"\n" +
                "        ],\n" +
                "        \"extensions\": [\n" +
                "            {\n" +
                "                \"acctId\": \"1001\",\n" +
                "                \"notes\": \"卡号:10315265\",\n" +
                "                \"acctType\": \"masterCard\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"acctId\": \"1001\",\n" +
                "                \"notes\": \"卡号:10315265\",\n" +
                "                \"acctType\": \"masterCard\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        System.out.println("s:" + s);
        Long start = System.currentTimeMillis();
        String s1 = AESUtil.encrypt(s, "12345678");
        System.out.println("s1:" + s1);
        System.out.println("aes encrypt cost:"+ (System.currentTimeMillis()-start)+"ms");
        start = System.currentTimeMillis();
        System.out.println("s2:"+AESUtil.decrypt(s1, "12345678"));
        System.out.println("aes decrypt cost:"+ (System.currentTimeMillis()-start)+"ms");


    }

}