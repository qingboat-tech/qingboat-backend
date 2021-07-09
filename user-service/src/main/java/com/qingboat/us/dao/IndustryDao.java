package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IndustryDao {

    @Select("select apps_industry.name from apps_industry,apps_userprofile " +
            "where apps_userprofile.user_id = #{userId} and apps_userprofile.industry_id = apps_industry.id")
    public String getIndustryByUserId(@Param("userId") Integer userId);
}
