package com.qingboat.us.dao;


import com.qingboat.us.entity.TierEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TierDao {

    @Select("select * from apps_tire where creator_id = #{creatorId}")
    public List<TierEntity> listTierEntitiesByCreatorId(@Param("creatorId")Integer creatorId);
}
