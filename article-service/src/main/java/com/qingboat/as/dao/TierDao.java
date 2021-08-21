package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.TierEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TierDao extends BaseMapper<TierEntity> {


    @Select("select * from apps_tier where creator_id = #{creatorId} and status = 1 order by month_price desc limit 0,1")
    TierEntity getMAXPriceByCreatorId(@Param("creatorId")String creatorId);
}