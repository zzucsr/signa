package com.superSign.service;


import com.superSign.entity.AppleEntity;

public interface AppleService {

    AppleEntity queryNewApple();

    void updateDeviceCountById(long id);

    boolean updateAppleIsUse(long id);

    AppleEntity queryUserNewAppleAccount();

    AppleEntity queryAppleAccountById(long appId);
}
