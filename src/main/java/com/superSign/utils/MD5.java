package com.superSign.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * MD5通用类
 */
public class MD5 {
    /**
     * MD5方法
     *
     * @param text 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) {
        //加密后的字符串
        // 确定计算方法
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder base64Encoder = Base64.getEncoder();
            md5.digest("dsdsfda".getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
public static  void main(String[] str) throws Exception {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    Base64.Encoder base64Encoder = Base64.getEncoder();
    //System.out.println(GetMD5Code("1234"));
}
    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key  密钥
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if (md5Text.equals(md5)) {
            return true;
        }

        return false;
    }
    private static final String KEY_MD5 = "MD5";
    // 全局数组
    private static final String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    /**
     * MD5加密
     * @param strObj
     * @return
     * @throws Exception
     */
    public static String GetMD5Code(String strObj, String key) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(KEY_MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // md.digest() 该函数返回值为存放哈希值结果的byte数组
        return byteToString(md.digest((strObj + "-" +key).getBytes()));
    }
}