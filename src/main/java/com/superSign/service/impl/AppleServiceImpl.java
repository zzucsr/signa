package com.superSign.service.impl;


import com.superSign.dao.AppleDao;
import com.superSign.entity.AppleEntity;
import com.superSign.service.AppleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName AppleServiceImpl
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
@Service
public class AppleServiceImpl implements AppleService {

    @Autowired
    private AppleDao appleDao;

    @Override
    public AppleEntity queryNewApple() {
        AppleEntity apple = appleDao.queryNewApple();
        return apple;
    }

    /**
     * 添加设备成功
     * @param id
     */
    @Override
    public void updateDeviceCountById(long id) {
        appleDao.updateDeviceCountById(id);
    }

    @Override
    public boolean updateAppleIsUse(long id) {
        int b =appleDao.updateAppleIsUse(id);
        return b > 0;
    }

    @Override
    public AppleEntity queryUserNewAppleAccount() {
        AppleEntity apple = appleDao.queryUserNewAppleAccount();
        return apple;
    }

    @Override
    public AppleEntity queryAppleAccountById(long appId) {
        AppleEntity apple = appleDao.queryAppleAccountById(appId);
        return apple;
    }
}
