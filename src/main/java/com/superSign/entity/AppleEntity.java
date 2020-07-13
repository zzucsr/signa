package com.superSign.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName Apple 开发者账号
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/

public class AppleEntity {
    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private int count ; //已有设备数
    @Getter
    @Setter
    private String p8; //私钥
    @Getter
    @Setter
    private String iss; //issId
    @Getter
    @Setter
    private String kid ; //key ID;
    @Getter
    @Setter
    private String p12; // p12 文件存放地址
    @Getter
    @Setter
    private String csr; //创建开发者证书时需要的本地证书
    @Getter
    @Setter
    private String cerId; //授权证书id
    @Getter
    @Setter
    private String bundleIds;//开发者后台的配置证书id
    @Getter
    @Setter
    private long ctime;//开始时间
    @Getter
    @Setter
    private long mtime; //修改时间
    @Getter
    @Setter
    private boolean isUse; //是否可用
}
