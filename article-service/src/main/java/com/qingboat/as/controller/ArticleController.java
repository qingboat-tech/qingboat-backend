package com.qingboat.as.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.as.utils.RssUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
import com.qingboat.base.exception.BaseException;
import com.rometools.rome.io.FeedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;



    @RequestMapping(value = "/findByAuthorId", method = RequestMethod.GET)
    @ResponseBody
    public Page<ArticleEntity> findByAuthorId(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return articleService.findByAuthorId(uid,pageIndex,true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject findByArticleId(@PathVariable("id") String id) {
        ArticleEntity articleEntity = articleService.findArticleById(id);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(articleEntity);
        if (articleEntity!=null && !StringUtils.isEmpty(articleEntity.getAuthorId())){
            UserEntity user =userService.findByUserId(Long.parseLong(articleEntity.getAuthorId()));
            if (user!=null){
                jsonObject.put("authorNickName",user.getNickname());
                jsonObject.put("authorImgUrl",user.getHeadimgUrl());
            }
        }
        return jsonObject;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public ArticleEntity saveArticle(@Valid @RequestBody ArticleEntity article) {
        String uid = getUId();
        article.setAuthorId(uid);
        return articleService.saveArticle(article);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteArticleById(@PathVariable("id") String id) {
       articleService.removeArticleById(id);
    }

    @RequestMapping(value = "/rss")
    public Boolean readRss(@RequestBody Map<String,String> param) throws IOException, FeedException {
        if (param !=null && param.containsKey("url")){
            String rssUrl = param.get("url");
            log.info( "RSS.url: " +rssUrl);
            RssUtil.readRss(rssUrl);
            return true;
        }

        return false;

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
            String newFileName = new StringBuilder(UUID.randomUUID().toString()).append(suffixName).toString();

            String fileUrl = AliyunOssUtil.upload(file,newFileName);
            rst.put("fileName",fileName);
            rst.put("fileUrl",fileUrl);
            return rst;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(500,e.getMessage());
        }
    }

    @RequestMapping(value = "/sensitive", method = RequestMethod.POST)
    @ResponseBody
    public String sensitive( @RequestBody Map<String,String> param) {

        SensitiveFilter filter = SensitiveFilter.DEFAULT;
        Iterator<String> ite = param.values().iterator();
        if (ite.hasNext()){
            String rst = filter.filter(ite.next(), '*');
            return rst;
        }
        return  null;

    }


    private String getUId(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        Long uid = (Long) request.getAttribute("UID");
        if (uid == null){
            throw new BaseException(401,"AUTH_ERROR");
        }
        return uid +"";
    }



}
