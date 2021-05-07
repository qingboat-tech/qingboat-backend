package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.UserSubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Set;

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

    ArticleEntity saveArticle(ArticleEntity articleEntity ,String operatorId);

    Page<ArticleEntity> findByAuthorId(String authorId,Integer pageIndex,Integer pageSize,boolean needInit);

    List<ArticleEntity> findAllByParentId(String parentId);

    Boolean removeArticleById(String articleId,String autherId);

    /**
     * 查询草稿箱列表
     */
    Page<ArticleEntity> findDraftListByAuthorId(String authorId,Integer pageIndex,Integer pageSize);

    /**
     * 查询审核中列表
     */
    Page<ArticleEntity> findReviewListByAuthorId(String authorId,Integer pageIndex,Integer pageSize);

    /**
     * 查询审核不通过列表
     */
    Page<ArticleEntity> findRefuseListByAuthorId(String authorId,Integer pageIndex,Integer pageSize);

    /**
     * 查询已发布列表
     */
    Page<ArticleEntity> findPublishListByAuthorId(String authorId,Integer pageIndex,Integer pageSize ,Boolean orderByHot);

    /**
     * 查询最热列表 ，size =10
     */
    @Deprecated
    List<ArticleEntity> findByAuthorIdByReadCountDesc(String authorId);

    /**
     * 查询最新列表 ，size =10
     */
    @Deprecated
    List<ArticleEntity> findByAuthorIdByUpdateTimeDesc(String authorId);

    /**
     * 增加评论数
     */
    boolean increaseCommentCountByArticleId(String articleId);

    /**
     * 减少评论数
     */
    boolean decreaseCommentCountByArticleId(String articleId);


    /**
     * 增加点赞数
     */
    boolean increaseStarCountByArticleId(String articleId,int numble);
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
    boolean submitReviewByArticleId(String articleId,String operatorId, Set<Long> tierIdSet);

    /**
     * 文章审核
     */
    boolean reviewByArticleId(String articleId,int status);

    /**
     * 异步文章审核
     */
    void asyncReviewByArticleId(String articleId);

//    @Deprecated
//    Page<ArticleEntity> findByAuthorIdsAndScope(List<String> authorIds,Integer pageIndex,Integer pageSize,Integer  scope);

    Page<ArticleEntity> findByAuthorIdsAndScope(List<UserSubscriptionEntity> subscriptionEntityList,Integer pageIndex,Integer pageSize);


    ArticleEntity findBaseInfoById(String articleId);

    /**
     * 文章置顶
     */
    boolean topArticle(String articleId ,Long userId);


}
