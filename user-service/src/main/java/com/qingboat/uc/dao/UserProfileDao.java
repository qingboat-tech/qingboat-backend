package com.qingboat.uc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.uc.entity.UserProfileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserProfileDao extends BaseMapper<UserProfileEntity> {


}
