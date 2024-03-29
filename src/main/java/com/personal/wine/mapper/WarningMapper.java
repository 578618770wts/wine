package com.personal.wine.mapper;

import com.personal.wine.model.Warning;
import com.personal.wine.model.WarningExample;
import java.util.List;

public interface WarningMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    long countByExample(WarningExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    int insert(Warning record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    int insertSelective(Warning record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    List<Warning> selectByExample(WarningExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    Warning selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Warning record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table w_warning
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Warning record);
}