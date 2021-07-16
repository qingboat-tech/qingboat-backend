package com.qingboat.as.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleMarkCompletedDao {

    @Select("select count(*) from apps_articlemarkcompleted where user_id = #{userId} and article_id = #{articleId} and completed = 1")
    public Integer judgeMarkCompleted(@Param("userId")Integer userId,@Param("articleId")Integer articleId);
}
