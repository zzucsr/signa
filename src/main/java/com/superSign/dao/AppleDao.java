package com.superSign.dao;

import com.superSign.entity.AppleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AppleDao {
    @Select("SELECT * FROM apple where isUse = true and count > 0 and p12 is not null order by count desc limit 1 ")
    AppleEntity queryNewApple();

    @Update("update apple set count = count - 1 where id = #{id}")
    int updateDeviceCountById(long id);

    @Update("UPDATE apple SET isUse = false WHERE id = #{id}")
    int updateAppleIsUse(long id);

    @Select("SELECT * FROM apple where isUse = true AND count>0 AND p12 IS NOT NULL ORDER BY count DESC LIMIT 1")
    AppleEntity queryUserNewAppleAccount();

    @Select("SELECT * FROM APPLE WHERE id = #{id} and isUse = true")
    AppleEntity queryAppleAccountById(long appId);
}
