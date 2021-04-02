package com.qingboat.as.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.AuthTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMongoDao extends MongoRepository<ArticleEntity, String> {

    @Query(value = "{'authorId':?0 , 'parentId': {'$eq':''} }" ,fields = "{title : 1 ,desc : 1 ,imgUrl : 1, authorId:1, createdTime  : 1 }",sort = "{createdTime : -1 }")
    List<ArticleEntity> findByAuthorId(String authorId);

    @Query(value = "{'authorId':?0 , 'parentId': {'$eq':''} }" ,fields = "{title : 1 ,desc : 1 ,imgUrl : 1, authorId:1, createdTime  : 1 }")
    Page<ArticleEntity> findByAuthorId(String authorId, Pageable pageable);

    List<ArticleEntity> findByParentId(String parentId);

    ArticleEntity findArticleEntityById(String articleId);

}
