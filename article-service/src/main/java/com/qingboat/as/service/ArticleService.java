package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.xml.stream.events.Comment;
import java.util.List;

public interface ArticleService {

    /**
     * 通过文章Id查询
     *
     * @param articleId
     * @return
     */
    ArticleEntity findArticleById(String articleId);

    /**
     * 保存文章
     * @param articleEntity
     * @return
     */

    ArticleEntity saveArticle(ArticleEntity articleEntity);

    Page<ArticleEntity> findByAuthorId(String authorId,int pageIndex,boolean needInit);

    List<ArticleEntity> findAllByParentId(String parentId);

    void removeArticleById(String articleId);

    /**
     * 查询草稿箱列表
     */
    Page<ArticleEntity> findDraftListByAuthorId(String authorId,int pageIndex,boolean needInit);

    /**
     * 查询审核中列表
     */
    Page<ArticleEntity> findReviewListByAuthorId(String authorId,int pageIndex,boolean needInit);

    /**
     * 查询审核不通过列表
     */
    Page<ArticleEntity> findRefuseListByAuthorId(String authorId,int pageIndex,boolean needInit);

    /**
     * 查询已发布列表
     */
    Page<ArticleEntity> findPublishListByAuthorId(String authorId,int pageIndex,boolean needInit);

    /**
     * 查询最热列表 ，size =10
     */
    List<ArticleEntity> findByAuthorIdByReadCountDesc(String authorId);

    /**
     * 查询最新列表 ，size =10
     */
    List<ArticleEntity> findByAuthorIdByUpdateTimeDesc(String authorId);

    /**
     * 增加评论数
     */
    boolean increaseCommentCountByArticleId(String articleId);

    /**
     * 增加点赞数
     */
    boolean increaseStarCountByArticleId(String articleId);
    Long handleStarCountByArticleId(String articleId,Long userId);
    /**
     * 判断该人是否点赞
     */
    boolean hasStar(String articleId,Long userId);

    /**
     * 增加阅读数
     */
    boolean increaseReadCountByArticleId(String articleId);

    /**
     * 提交文章审核
     */
    boolean submitReviewByArticleId(String articleId,int scope);

    /**
     * 文章审核
     */
    boolean reviewByArticleId(String articleId,int status);

}
