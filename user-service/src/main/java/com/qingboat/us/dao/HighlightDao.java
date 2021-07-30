package com.qingboat.us.dao;

import com.qingboat.us.entity.HighlightEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HighlightDao {
    //得到一系列文章中最新的重点
    public HighlightEntity getNewestHighlightEntity(@Param("articleIds") List<Integer> articleIds,@Param("userId")Integer userId);
    //得到一系列文章中所有的重点的Id
    public List<Integer> getAllHighlightIds(@Param("articleIds") List<Integer> articleIds,@Param("userId")Integer userId);

    @Select("select count(*) from apps_highlight where highlight_user_id = #{userId}")
    public int countHighlight(@Param("userId")Integer userId);

}
