package com.superSign.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @ClassName DeviceEntity
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
public class DeviceEntity {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String udId; //设备id

    @Getter
    @Setter
    private long appId; //设别对应的账号id

    @Getter
    @Setter
    private String deviceId; //设备在开发者后台的id

    @Getter
    @Setter
    private long ctime; //创建游戏

    @Getter
    @Setter
    private long mtime; //修改游戏

    @Getter
    @Setter
    private boolean isUse; //该设备是否可用
}
