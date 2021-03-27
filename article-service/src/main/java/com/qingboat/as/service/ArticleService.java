package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleEntity;

import java.util.List;

public interface ArticleService {

    ArticleEntity findArticleById(String articleId);

    ArticleEntity saveArticle(ArticleEntity articleEntity);

    List<ArticleEntity> findAllByAuthorId(String authorId);

    List<ArticleEntity> findAllByParentId(String parentId);

    void removeArticleById(String articleId);

}
