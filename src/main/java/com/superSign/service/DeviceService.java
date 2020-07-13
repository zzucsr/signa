package com.superSign.service;


import com.superSign.entity.DeviceEntity;

public interface DeviceService {
    DeviceEntity queryDeviceByUdid(String udid);

    boolean insertDevice(String udid, long id, String devId);

    void updateDeviceIsUseByAppleId(long id, boolean b);
}
