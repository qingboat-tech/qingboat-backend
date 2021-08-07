package com.qingboat.us.dao;

import com.qingboat.us.entity.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GoodsDao {

    @Select("select * from apps_goods where product_id = #{pathwayId} and  product_type = 1")
    GoodsEntity getGoodsEntityByPathwayId(@Param("pathwayId")Integer pathwayId);
}
