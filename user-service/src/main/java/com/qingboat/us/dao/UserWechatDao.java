package com.qingboat.us.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.us.entity.UserWechatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserWechatDao extends BaseMapper<UserWechatEntity> {

}