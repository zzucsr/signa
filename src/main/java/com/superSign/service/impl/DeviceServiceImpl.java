package com.superSign.service.impl;

import com.superSign.dao.DeviceDao;
import com.superSign.entity.DeviceEntity;
import com.superSign.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName DeviceServiceImpl
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;
    @Override
    public DeviceEntity queryDeviceByUdid(String udid) {
        DeviceEntity device = deviceDao.queryDeviceByUdid(udid);
        return device;
    }

    @Override
    public boolean insertDevice(String udid, long id, String devId) {
        //boolean isSuccess = deviceDao.insertDevice(udid,id,devId);
        return true;
    }

    @Override
    public void updateDeviceIsUseByAppleId(long id, boolean isUse) {
        deviceDao.updateDeviceIsUseByAppleId(id, isUse);

    }
}
