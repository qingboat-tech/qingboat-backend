package com.qingboat.as.service;

import com.qingboat.as.entity.ArticleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepositoryService extends MongoRepository<ArticleEntity, String> {
    // This interface comes with many operations,
    // including standard CRUD operations (create, read, update, and delete).
    ArticleEntity findArticleById(String Id);

}

