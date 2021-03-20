package com.qingboat.ts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.ts.entity.GoodsSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GoodsSkuDao extends BaseMapper<GoodsSkuEntity> {

}
