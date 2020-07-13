package com.superSign.utils;

import cn.hutool.core.codec.Base64;
import com.superSign.entity.pojo.Result;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName FileManager
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
@Component
public class FileManager {
    /**
     * base64转文件
     * @param base64
     * @param fileName
     * @return
     */
    public File base64ToFile(String base64, String fileName) {
        File file = null;
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.decode(base64);
            file=new File(fileName);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }



}
