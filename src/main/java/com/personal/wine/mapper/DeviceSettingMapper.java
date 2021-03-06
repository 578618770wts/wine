package com.personal.wine.mapper;

import com.personal.wine.model.DeviceSetting;
import com.personal.wine.model.DeviceSettingExample;
import java.util.List;

public interface DeviceSettingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    long countByExample(DeviceSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    int insert(DeviceSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    int insertSelective(DeviceSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    List<DeviceSetting> selectByExample(DeviceSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    DeviceSetting selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(DeviceSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(DeviceSetting record);
}