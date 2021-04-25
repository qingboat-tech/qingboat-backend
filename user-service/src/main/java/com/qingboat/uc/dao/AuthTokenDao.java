package com.qingboat.uc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.qingboat.uc.entity.AuthTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthTokenDao extends BaseMapper<AuthTokenEntity> {

}