package com.qingboat.us.dao;

import com.qingboat.us.entity.ArticlePrefaceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//推荐语
@Mapper
public interface ArticlePrefaceDao {

    @Select("select * from apps_articlepreface where pathway_id = #{pathwayId} order by updated_at desc limit 0,1")
    public ArticlePrefaceEntity getNewestArticlePrefaceByPathwayId(@Param("pathwayId")Integer pathwayId);
}
