package com.qingboat.as.dao.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.AuthTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthTokenDao extends BaseMapper<AuthTokenEntity> {

}