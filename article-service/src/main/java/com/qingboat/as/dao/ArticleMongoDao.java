package com.qingboat.as.dao;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMongoDao extends MongoRepository<ArticleEntity, String> {

    List<ArticleEntity> findByAuthorId(String authorId);

    List<ArticleEntity> findByParentId(String parentId);


}
