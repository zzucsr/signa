package com.superSign.dao;

import com.superSign.entity.DeviceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DeviceDao {

    /**
     * 根据udid 获取到设备信息
     * @param udid
     * @return
     */
    @Select("select * from device where udid = #{udid} and isUse = true")
    DeviceEntity queryDeviceByUdid(String udid);

    @Insert("insert into device (udid,apple_id,device_id) values (#{udid},#{id},#{devId})")
    boolean insertDevice(String udid, long id, String devId);

    @Update("UPDATE device SET is_use = #{isUse} WHERE apple_id = #{appleId}")
    void updateDeviceIsUseByAppleId(long id, boolean isUse);
}
