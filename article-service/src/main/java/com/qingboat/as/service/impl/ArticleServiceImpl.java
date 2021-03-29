package com.qingboat.as.service.impl;

import com.qingboat.as.dao.ArticleMongoDao;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMongoDao articleMongoDao;

    @Override
    public ArticleEntity findArticleById(String articleId) {
        return  articleMongoDao.findArticleEntityById(articleId);
    }

    @Override
    public ArticleEntity saveArticle(ArticleEntity articleEntity) {
        if (articleEntity.getId() == null){
            articleEntity.setId(ObjectId.get().toString());
        }
        if (articleEntity.getCreatedTime() == null){
            articleEntity.setCreatedTime(new Date());
        }
        articleEntity.setUpdatedTime(new Date());

        return articleMongoDao.save(articleEntity);
    }

    @Override
    public Page<ArticleEntity> findByAuthorId(String authorId ,int pageIndex) {
        if (pageIndex<0){
            pageIndex = 0;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(pageIndex, 10, sort);
        return articleMongoDao.findByAuthorId(authorId,pageable);

//        ArticleEntity articleEntity = new ArticleEntity();
//        articleEntity.setAuthorId(authorId);
//        Example<ArticleEntity> articleExample =Example.of(articleEntity);
//        return  articleMongoDao.findAll(articleExample);
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
