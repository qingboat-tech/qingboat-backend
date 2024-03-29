package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.vo.UserProfileInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserProfileDao extends BaseMapper<UserEntity> {


    @Select("select role from apps_userprofile where user_id = #{userId}")
    public byte getRoleByUserId(Integer userId);

    @Select("select * from apps_userprofile where user_id = #{userId}")
    public UserProfileInfoVo getUserProfileInfoById(@Param("userId")Integer userId);

}