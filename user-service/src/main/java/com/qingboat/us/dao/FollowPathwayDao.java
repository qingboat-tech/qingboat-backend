package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface FollowPathwayDao {

    @Select("select count(*) from apps_followpathway where pathway_id = #{pathwayId} and user_id = #{userId} and expire_date > NOW()")
    Integer judgeUserIsFollowSomePathway(@Param("pathwayId")Integer pathwayId,@Param("userId")Integer userId);


    @Select("select count(*) from apps_followpathway where user_id = #{userId}")
    int countSubscriptionPathwayNum(@Param("userId")Integer userId);

    Date getNewestDate(@Param("userId")Integer userId, @Param("pathwayIds")List<Integer> pathwaysIds);

}
