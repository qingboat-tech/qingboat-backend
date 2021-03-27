package com.qingboat.as.controller;


import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @RequestMapping(value = "/author/{authorId}", method = RequestMethod.GET)
    @ResponseBody
    public List<ArticleEntity> findAllByAuthorId(@PathVariable("authorId") String authorId) {
        return articleService.findAllByAuthorId(authorId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ArticleEntity findByArticleId(@PathVariable("id") String id) {
        return articleService.findArticleById(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public ArticleEntity saveArticle(@Valid @RequestBody ArticleEntity article) {
        return articleService.saveArticle(article);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteArticleById(@PathVariable("id") String id) {
       articleService.removeArticleById(id);
    }

}
