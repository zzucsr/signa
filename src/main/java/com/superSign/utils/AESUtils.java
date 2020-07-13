package com.superSign.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.superSign.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author ：iizvv
 * @date ：Created in 2019-09-03 09:35
 * @description：TODO
 * @version: 1.0
 */
public class AESUtils {

    static Logger logger = LoggerFactory.getLogger(AESUtils.class);

    /**
      * create by: iizvv
      * description: 加密
      * create time: 2019-09-03 09:37

      * @return str
      */
    public static String encryptHex(String hex) {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, Constant.aesKey.getBytes());
        //加密为16进制表示
        String encryptHex = aes.encryptHex(hex);
        logger.info("加密钱:{},加密后:{}",hex,encryptHex);
        return encryptHex;
    }

    /**
      * create by: iizvv
      * description: 解密为long
      * create time: 2019-09-03 09:38

      * @return long
      */
    public static long decryptStr(String encryptHex) {
        String hexStr = decryptHexStr(encryptHex);
        if (isNumeric(hexStr)) {
            return Integer.valueOf(hexStr);
        }
        return 0;
    }

    /**
      * create by: iizvv
      * description: 解密为str
      * create time: 2019-09-04 15:18

      * @return str
      */
    public static String decryptHexStr(String encryptHex) {
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, Constant.aesKey.getBytes());
        //解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        logger.info("解密前:{}解密后:{}",encryptHex,decryptStr);
        return decryptStr;
    }

    /**
      * create by: iizvv
      * description: 判断是否为数字
      * create time: 2019-09-03 10:07

      * @return boolean
      */
    static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }


}
