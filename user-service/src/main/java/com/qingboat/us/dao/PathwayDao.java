package com.qingboat.us.dao;

import com.qingboat.us.vo.NewsUpdateCardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PathwayDao {

    @Select("select author_id  from apps_pathway where id = #{targetId}")
    public Integer getCreatorIdByPathwayId(@Param("targetId")Integer targetId);


    public List<NewsUpdateCardVO> pathwayInfoByCreatorIds(@Param("creatorIds")List creatorIds);
}
