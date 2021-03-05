package com.qingboat.as.controller;


import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.service.ArticleRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.bson.types.ObjectId;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/articles")
public class ArticleController {

    @Autowired
    final private ArticleRepositoryService repository;

    public ArticleController(ArticleRepositoryService repository) {
        this.repository = repository;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<ArticleEntity> getAllArticles() {
        // get article list
        return repository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ArticleEntity getArticleById(@PathVariable("id") String id) {
        // get a single article
        return repository.findArticleById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ArticleEntity updateArticleById(@PathVariable("id") ObjectId id, @Valid @RequestBody ArticleEntity article) {
        // update an article
        article.setId(id.toString());
        repository.save(article);
        return article;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ArticleEntity addNewArticle(@Valid @RequestBody ArticleEntity article) {
        // create an article
        article.setId(ObjectId.get().toString());
        repository.save(article);
        return article;
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteArticleByID(@PathVariable("id") String id) {
        // delete articles
        repository.delete(repository.findArticleById(id));
    }

}
