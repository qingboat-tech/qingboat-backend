package com.qingboat.as.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface PathwayDao {

    @Select("select author_id  from apps_pathway where id = #{targetId}")
    public Integer getCreatorIdByPathwayId(@Param("targetId")Integer targetId);

//


//    public List<NewsUpdateCardVO> pathwayInfoByCreatorIds(@Param("creatorIds")List creatorIds);

    // 根据creator 获取 此creator的最新的pathway的时间
    @Select("select updated_at from apps_pathway where author_id = #{authorId} order by updated_at desc limit 0,1 ")
    public Date getLastUpdateTimeByCreator(@Param("authorId") Integer authorId);
}
