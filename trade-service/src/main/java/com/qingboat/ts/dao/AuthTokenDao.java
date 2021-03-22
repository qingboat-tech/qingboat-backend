package com.qingboat.ts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.ts.entity.AuthToken;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuthTokenDao extends BaseMapper<AuthToken> {

}
