package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NodeDao {

    @Select("select referrence_id from apps_node where pathway_id = #{pathwayId} and referrence_type = 1")
    public List<Integer> getAllArticleIdByPathwayId(@Param("pathwayId")Integer pathwayId);
}
