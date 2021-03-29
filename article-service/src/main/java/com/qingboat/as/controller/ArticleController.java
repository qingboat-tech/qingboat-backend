package com.qingboat.as.controller;


import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @RequestMapping(value = "/findByAuthorId", method = RequestMethod.GET)
    @ResponseBody
    public Page<ArticleEntity> findByAuthorId(@RequestParam("authorId") String authorId,@RequestParam("pageIndex") int pageIndex) {
        return articleService.findByAuthorId(authorId,pageIndex);
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


    @PostMapping(value = "/uploadFile")
    public Map<String,String> UploadFile(@RequestParam(value = "file") MultipartFile file) {

        try {
            if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
                log.error("文件为空");
                throw new BaseException(500,"FILE_IS_EMPTY");
            }
            Map<String,String> rst = new HashMap<>();
            String fileName = file.getOriginalFilename();  // 文件名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
            fileName = new StringBuilder(UUID.randomUUID().toString()).append(".").append(suffixName).toString();

            String fileUrl = AliyunOssUtil.upload(file,fileName);
            rst.put("fileName",fileName);
            rst.put("fileUrl",fileUrl);
            return rst;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(500,e.getMessage());
        }
    }

}
