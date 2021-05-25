package com.qingboat.us.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.us.entity.AuthUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthUserDao extends BaseMapper<AuthUserEntity> {

}