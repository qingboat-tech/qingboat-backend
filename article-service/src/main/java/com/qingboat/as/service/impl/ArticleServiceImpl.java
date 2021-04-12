package com.qingboat.as.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
    public Page<ArticleEntity> findByAuthorId(String authorId ,int pageIndex,boolean needInit) {
        if (pageIndex<0){
            pageIndex = 0;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(pageIndex, 10, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorId(authorId,pageable);
        if (page != null && page.isEmpty() && needInit && pageIndex ==0){
            log.info(" ======initArticle====== authorId:"+authorId);
            initArticle(authorId);
            page =  articleMongoDao.findByAuthorId(authorId,pageable);
        }
        return page;
    }

    @Override
    public List<ArticleEntity> findAllByParentId(String parentId) {
        return articleMongoDao.findByParentId(parentId);
    }

    @Override
    public void removeArticleById(String articleId) {
        articleMongoDao.deleteById(articleId);
    }


    private void initArticle(String authId){
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setAuthorId(authId);
        articleEntity.setId(ObjectId.get().toString());
        articleEntity.setCreatedTime(new Date());
        articleEntity.setUpdatedTime(new Date());
        articleEntity.setTitle("氢舟文档范文");
        articleEntity.setData(JSON.parseArray(demoData));
        articleMongoDao.insert(articleEntity);
    }

    private String demoData = " [\n" +
            "            {\n" +
            "                \"html\": \"<span style=\\\"color: rgb(122, 136, 154);\\\">氢舟文档编辑器可以提供什么?</span>\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne86dz03nbow1qi997\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"图片:\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7xxg2hhqam7vub3\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"kn5dzll741fzjrjzufn\",\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"img\",\n" +
            "                \"placeholder\": \"点击此处添加图片\",\n" +
            "                \"imageUrl\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/image-1.jpg\",\n" +
            "                \"blockType\": \"img\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"标题:\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7vynn0awpu74leo2c\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"h1\",\n" +
            "                \"placeholder\": \"Heading 1\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7z6pypc1ayd360l\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"Heading 2\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne88arh4akxxbc6jte\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"h3\",\n" +
            "                \"placeholder\": \"Heading 3\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne88rwj6ydvo3f9hs9\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<span style=\\\"font-size: 1rem;\\\">这是一段包含</span><span style=\\\"font-size: 1rem; font-style: italic;\\\">斜 体（I）</span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem; font-weight: 600;\\\">加 粗（B）</span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem; text-decoration-line: underline;\\\">下划线（U）</span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem; text-decoration-line: line-through;\\\">删 除（S）</span><span style=\\\"font-size: 1rem;\\\">的示例文本。</span>\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7uh294aila6w4z6x\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"引用:\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7zovfkyjzzv2d3n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<span style=\\\"color: rgb(85, 85, 85);\\\">什么是Curation？Curation一词翻译成中文是：策划、筛选并展示。在最早的时候，这个词指的是艺术展览活动中的构思、组织和管理工作。</span>\",\n" +
            "                \"tag\": \"blockquote\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"blockquote\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7vm51f4f880yq3k7\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"列表：\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne897i9ydlpdp6145i\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<ul><li>无序列表</li><li>无序列表</li><li>...</li></ul><ol><li><span style=\\\"font-size: 1rem;\\\">有序列表</span></li><li>有序列表</li><li>...</li></ol>\",\n" +
            "                \"tag\": \"div\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"orderedList\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne83grz3cjipzvsod\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"快捷菜单：\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne80jyhxe34ewyv8sa\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne89hmfhtvjsn5rejl\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"排序：\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne89rxm8cqlxriwfu6\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"←← 左边的按钮可拖拽调整文章顺序，快来试试吧。【请将鼠标移到此处】\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne8118w4l1a4x9mabu\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne8a65da3753kb94mu\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"更多敬请期待。。。\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne834sco1pnhbph81n\"\n" +
            "            }\n" +
            "        ]";

}
