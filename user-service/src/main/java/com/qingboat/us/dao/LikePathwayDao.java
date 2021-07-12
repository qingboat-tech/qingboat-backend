package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikePathwayDao {

    @Select("select count(*) from apps_likepathway where target_id = #{targetId}")
    public Integer likeCountByPathwayId(@Param("targetId")Integer targetId);

    //用于判断 某个用户是否已经点赞
    @Select("select count(*) from apps_likepathway where action_user_id = #{userId} and target_id = #{pathwayId}")
    public Integer judgeSomeUserIsLiked(@Param("userId")Integer userId,@Param("pathwayId")Integer pathwayId);
}
