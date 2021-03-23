package com.qingboat.ts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.ts.entity.AuthToken;
import com.qingboat.ts.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
