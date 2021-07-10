package com.qingboat.us.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PathwayDao {

    @Select("select author_id  from apps_pathway where id = #{targetId}")
    public Integer getCreatorIdByPathwayId(@Param("targetId")Integer targetId);
}
