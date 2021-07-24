package com.qingboat.as.dao;

import com.qingboat.as.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface ArticleDao {
    @Select("select id,title,summary from apps_article where id = #{articleId} and apps_article.accessible = 1 and is_private = 0  ")
    public ArticleEntity selectArticleById(@Param("articleId")Integer articleId);
}
