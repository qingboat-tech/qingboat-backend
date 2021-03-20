package com.qingboat.ts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.ts.entity.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GoodsDao extends BaseMapper<GoodsEntity> {

}
