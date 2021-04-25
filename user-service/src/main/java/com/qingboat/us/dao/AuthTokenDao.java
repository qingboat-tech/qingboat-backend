package com.qingboat.us.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.qingboat.us.entity.AuthTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthTokenDao extends BaseMapper<AuthTokenEntity> {

}