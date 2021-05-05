package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.ArticleCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ArticleCommentDao extends BaseMapper<ArticleCommentEntity> {

    void updateReplyCount(ArticleCommentEntity entity);

}