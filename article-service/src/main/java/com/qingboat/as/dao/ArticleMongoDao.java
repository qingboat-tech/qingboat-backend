package com.qingboat.as.dao;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMongoDao extends MongoRepository<ArticleEntity, String> {

    @Query(value ="{}" ,fields = "{title : 1 ,desc : 1 ,imgUrl : 1,createdTime  : 1 }",sort = "{createdTime : -1 }")
    List<ArticleEntity> findByAuthorId(String authorId);

    List<ArticleEntity> findByParentId(String parentId);

    ArticleEntity findArticleById(String articleId);


}
