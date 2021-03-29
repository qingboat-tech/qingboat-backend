package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleService {

    ArticleEntity findArticleById(String articleId);

    ArticleEntity saveArticle(ArticleEntity articleEntity);

    Page<ArticleEntity> findByAuthorId(String authorId,int pageIndex);

    List<ArticleEntity> findAllByParentId(String parentId);

    void removeArticleById(String articleId);

}
