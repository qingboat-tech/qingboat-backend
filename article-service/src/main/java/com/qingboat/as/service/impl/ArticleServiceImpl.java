package com.qingboat.as.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.qingboat.as.dao.ArticleMongoDao;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.service.ArticleService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMongoDao articleMongoDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ArticleEntity findArticleById(String articleId) {
        return  articleMongoDao.findArticleEntityById(articleId);
    }

    @Override
    public ArticleEntity saveArticle(ArticleEntity articleEntity) {

        if (articleEntity.getId() == null){
            articleEntity.setId(ObjectId.get().toString());
            articleEntity.setCreatedTime(new Date());
            articleEntity.setUpdatedTime(new Date());
            return articleMongoDao.save(articleEntity);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleEntity.getId()));
        Update update = new Update();
        if (articleEntity.getData()!=null){
            update.set("data",articleEntity.getData());
        }
        if (articleEntity.getDesc()!=null){
            update.set("desc",articleEntity.getDesc());
        }
        if (articleEntity.getTitle()!=null){
            update.set("title",articleEntity.getTitle());
        }
        if (articleEntity.getImgUrl()!=null){
            update.set("imgUrl",articleEntity.getImgUrl());
        }
        if (articleEntity.getParentId()!=null){
            update.set("parentId",articleEntity.getParentId());
        }
        Date updateTime = new Date();
        update.set("updateTime",updateTime);
        articleEntity.setUpdatedTime(updateTime);

        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }

        return articleEntity;
    }

    @Override
    public Page<ArticleEntity> findByAuthorId(String authorId ,int pageIndex) {
        if (pageIndex<0){
            pageIndex = 0;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(pageIndex, 10, sort);
        return articleMongoDao.findByAuthorId(authorId,pageable);

    }

    @Override
    public List<ArticleEntity> findAllByParentId(String parentId) {
        return articleMongoDao.findByParentId(parentId);
    }

    @Override
    public void removeArticleById(String articleId) {
        articleMongoDao.deleteById(articleId);
    }


}
