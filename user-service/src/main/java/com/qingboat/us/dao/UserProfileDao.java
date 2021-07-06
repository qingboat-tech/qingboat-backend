package com.qingboat.us.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.vo.UserProfileVO1;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserProfileDao extends BaseMapper<UserProfileEntity> {

    UserProfileEntity findByUserId(Long userId);

    /**
     * 获取所有的创作者id
     * @return
     */
    @Select("select user_id from apps_userprofile where role = 1")
    List<Integer> getCreatorIds();


    List<UserProfileVO1> getUserProfileByIds(@Param("ids")List<Integer> ids);



}
