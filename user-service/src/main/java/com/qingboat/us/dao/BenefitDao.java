package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BenefitDao {

    @Select("select count(*) from apps_benefit where creator_id = #{creatorId}")
    public int countBenefitByCreator(@Param("creatorId")Integer creatorId);
}
