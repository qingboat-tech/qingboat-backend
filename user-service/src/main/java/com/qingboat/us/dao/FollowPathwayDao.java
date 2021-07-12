package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FollowPathwayDao {

    @Select("select count(*) from apps_followpathway where pathway_id = #{pathwayId} and user_id = #{userId} and expire_date > NOW()")
    public Integer judgeUserIsFollowSomePathway(@Param("pathwayId")Integer pathwayId,@Param("userId")Integer userId);
}
