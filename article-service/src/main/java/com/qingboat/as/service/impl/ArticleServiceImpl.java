package com.qingboat.as.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mongodb.client.result.UpdateResult;
import com.qingboat.as.dao.ArticleMongoDao;
import com.qingboat.as.entity.*;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.TierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.utils.RedisUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMongoDao articleMongoDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TierService tierService;

    @Autowired
    private FeishuService feishuService;

    private static final String USER_STAR_PRE ="USER_STAR_";

    @Override
    public ArticleEntity findArticleById(String articleId) {
        return  articleMongoDao.findArticleEntityById(articleId);
    }

    @Override
    public ArticleEntity saveArticle(ArticleEntity articleEntity ,String operatorId) {


        if (articleEntity.getId() == null){
            articleEntity.setId(ObjectId.get().toString());
            articleEntity.setCreatedTime(LocalDateTime.now());
            articleEntity.setUpdatedTime(LocalDateTime.now());
            articleEntity.setStatus(0);// 0:草稿；1：审核中；2：审核驳回；3：审核通过；4：已发布
            articleEntity.setScope(0);//0:表示免费；1：收费
            articleEntity.setType(0); // 0:newsLetter；1：learnPathway
            articleEntity.setCategoryName("");
            articleEntity.setStarCount(0l);
            articleEntity.setCommentCount(0l);
            articleEntity.setReadCount(0l);

            UserEntity userEntity = userService.findByUserId(Long.parseLong(operatorId));

            if (userEntity!=null){
                if (userEntity.getRole() ==2){
                    throw new BaseException(500,"userId="+operatorId +" is reader");
                }
                articleEntity.setAuthorId(operatorId);
                articleEntity.setAuthorNickName(userEntity.getNickname());
                articleEntity.setAuthorImgUrl(userEntity.getHeadimgUrl());
            }else{
                throw new BaseException(500,"user is null");
            }

            return articleMongoDao.save(articleEntity);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleEntity.getId()));
        query.addCriteria(Criteria.where("authorId").is(operatorId));

        Update update = new Update();
        update.set("status",0);
        update.set("suggestion","");

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
        update.set("updatedTime",LocalDateTime.now());
        articleEntity.setUpdatedTime(LocalDateTime.now());

        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }

        return articleEntity;
    }

    @Override
    public Page<ArticleEntity> findByAuthorId(String authorId ,Integer pageIndex,Integer pageSize,boolean needInit) {
        if (pageIndex ==null || pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize ==null || pageSize<1){
            pageSize =10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorId(authorId,pageable);
        if (page != null && page.isEmpty() && needInit && pageIndex ==0){
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
    public Boolean removeArticleById(String articleId,String authorId) {
        ArticleEntity articleEntity = articleMongoDao.findBaseInfoById(articleId);

        if (articleEntity!=null && authorId.equals(articleEntity.getAuthorId())){
            if (articleEntity.getStatus() == 4){
                //已发布状态，禁止删除
                return Boolean.FALSE;
            }
            articleMongoDao.deleteById(articleId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Page<ArticleEntity> findDraftListByAuthorId(String authorId, Integer pageIndex,Integer pageSize) {
        if (pageIndex ==null || pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize ==null || pageSize<1){
            pageSize =10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedTime");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdAndStatus(authorId,0,pageable);
        return page;
    }

    @Override
    public Page<ArticleEntity> findReviewListByAuthorId(String authorId, Integer pageIndex,Integer pageSize) {
        if (pageIndex ==null || pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize ==null || pageSize<1){
            pageSize =10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedTime");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdAndStatus(authorId,1,pageable);
        return page;
    }

    @Override
    public Page<ArticleEntity> findRefuseListByAuthorId(String authorId, Integer pageIndex,Integer pageSize) {

        if (pageIndex ==null || pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize ==null || pageSize<1){
            pageSize =10;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedTime");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdAndStatus(authorId,2,pageable);
        return page;
    }

    @Override
    public Page<ArticleEntity> findPublishListByAuthorId(String authorId, Integer pageIndex,Integer pageSize,Boolean orderByHot) {

        if (pageIndex ==null || pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize ==null || pageSize<1){
            pageSize =10;
        }
        Sort sort = null;
        if (orderByHot!=null && orderByHot){
            sort = Sort.by(Sort.Direction.DESC, "readCount","updatedTime");
        }else {
            sort = Sort.by(Sort.Direction.DESC, "top","updatedTime");
        }

        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdAndStatus(authorId,4,pageable);
        return page;
    }

    @Override
    public List<ArticleEntity> findByAuthorIdByReadCountDesc(String authorId) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdByReadCountDesc(authorId,pageable);
        if (page != null ){
            return page.getContent();
        }
        return null;
    }

    @Override
    public List<ArticleEntity> findByAuthorIdByUpdateTimeDesc(String authorId) {

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedTime");
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<ArticleEntity>  page =  articleMongoDao.findByAuthorIdAndStatus(authorId,4,pageable);
        if (page!=null){
            return page.getContent();
        }
        return null;
    }

    @Override
    public boolean increaseCommentCountByArticleId(String articleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        Update update = new Update();
        update.inc("commentCount");
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }
    @Override
    public boolean decreaseCommentCountByArticleId(String articleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        Update update = new Update();
        update.inc("commentCount",-1);
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }

    @Override
    public boolean increaseStarCountByArticleId(String articleId,int numble) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        Update update = new Update();
        update.inc("starCount",numble);
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }

    @Override
    public Long handleStarCountByArticleId(String articleId, Long userId) {
        if (hasStar(articleId,userId)){
            boolean rst = increaseStarCountByArticleId(articleId,-1);
            if (rst){
                redisUtil.remove(USER_STAR_PRE+userId,articleId);
            }
        }else {
            boolean rst =increaseStarCountByArticleId(articleId,1);
            if (rst){
                redisUtil.sSet(USER_STAR_PRE+userId,articleId);
            }
        }
        return  redisUtil.size(USER_STAR_PRE+userId);
    }

    @Override
    public boolean hasStar(String articleId, Long userId) {
        return redisUtil.sHasKey(USER_STAR_PRE+userId,articleId);
    }

    @Override
    public boolean increaseReadCountByArticleId(String articleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        Update update = new Update();
        update.inc("readCount");
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }

    @Override
    public boolean submitReviewByArticleId(String articleId,String operatorId, Set<Long> tierIdSet) {
        if (tierIdSet == null || tierIdSet.isEmpty()){
            throw new BaseException(500,"操作失败：发布的文章没有选择套餐范围");
        }
        Set<String> benefitKeySet = new HashSet<>();
        Long creatorId = Long.valueOf(operatorId);

        List<TierEntity> tierList = tierService.listByIds(tierIdSet);
        for (TierEntity tier:tierList) {
            if (tier.getCreatorId().equals(creatorId)){
                if (tier.getBenefitList() == null){
                    throw new BaseException(500,"操作失败：发布的套餐里没有添加会员权益");
                }
                for (BenefitEntity benefit:tier.getBenefitList() ) {
                    benefitKeySet.add(benefit.getKey());
                }
            }
        }
        if (benefitKeySet.isEmpty()){
            throw new BaseException(500,"操作失败：发布的文章没有选择套餐范围");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        query.addCriteria(Criteria.where("authorId").is(operatorId));

        Update update = new Update();
        update.set("status",1);
        update.set("benefit",benefitKeySet);
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }

    @Override
    public boolean reviewByArticleId(String articleId, int status) {
        if (status!=2 && status!=4){
            throw new BaseException(500,"ArticleEntity_status_error");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        Update update = new Update();
        update.set("status",status);
        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        if (result.getModifiedCount() <=0){
            throw new BaseException(500,"ArticleEntity_is_not_exist");
        }
        return true;
    }

    @Override
    @Async
    public void asyncReviewByArticleId(String articleId) {
        ArticleEntity articleEntity = articleMongoDao.findArticleEntityById(articleId);
        if (articleEntity!= null){
            SensitiveFilter sensitiveFilter = SensitiveFilter.DEFAULT;
            String title = articleEntity.getTitle();
            String desc = articleEntity.getDesc();
            String data = articleEntity.getData().toString();

            boolean pass = false;

            String filter = sensitiveFilter.filter(title,'*');
            if (!title.equals(filter)){
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(articleId));

                Update update = new Update();
                update.set("status",2);
                update.set("suggestion","文章标题有敏感词");

                UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
                //发飞书通知给客服
                FeishuService.TextBody textBody = new FeishuService.TextBody(
                        new StringBuilder().append("文章审核驳回").append("\n")
                                .append("文章《").append(title).append("》\n")
                                .append("标题包含敏感词\n").toString());
                feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

                return;
            }
            filter = sensitiveFilter.filter(desc,'*');
            if (!desc.equals(filter)){
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(articleId));

                Update update = new Update();
                update.set("status",2);
                update.set("suggestion","文章描述有敏感词");

                UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);

                //发飞书通知给客服
                FeishuService.TextBody textBody = new FeishuService.TextBody(
                        new StringBuilder().append("文章审核驳回").append("\n")
                                .append("文章《").append(title).append("》\n")
                                .append("文章描述包含敏感词\n").toString());
                feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

                //发送消息
                return;
            }
            filter = sensitiveFilter.filter(data,'*');
            if (!data.equals(filter)){
                Query query = new Query();
                query.addCriteria(Criteria.where("id").is(articleId));

                Update update = new Update();
                update.set("status",2);
                update.set("suggestion","文章内容有敏感词");

                UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);

                //发飞书通知给客服
                FeishuService.TextBody textBody = new FeishuService.TextBody(
                        new StringBuilder().append("文章审核驳回").append("\n")
                                .append("文章《").append(title).append("》\n")
                                .append("文章内容包含敏感词\n").toString());
                feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

                //发送消息
                return;
            }
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(articleId));

            Update update = new Update();
            update.set("status",4);

            UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);

            FeishuService.TextBody textBody = new FeishuService.TextBody(
                    new StringBuilder().append("文章审核通过").append("\n")
                            .append("创作者：").append(articleEntity.getAuthorNickName()).append("\n")
                            .append("文章《").append(title).append("》\n").toString());
            feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);
            //发送消息


        }

    }

    @Override
    public Page<ArticleEntity> findByAuthorIdsAndScope(List<UserSubscriptionEntity> subscriptionEntityList, Integer pageIndex, Integer pageSize){
        Query query = new Query();
        if (subscriptionEntityList == null || subscriptionEntityList.isEmpty()){
            return null;
        }
        if (pageIndex<0){
            pageIndex = 0;
        }
        if (pageSize<1){
            pageSize = 10;
        }

        Criteria[] criteriaList = new Criteria[subscriptionEntityList.size()];
        for(int i=0;i<subscriptionEntityList.size();i++){
            Set<String> benefitSet = new HashSet<>();
            UserSubscriptionEntity entity = subscriptionEntityList.get(i);

            for (BenefitEntity benefitEntity:entity.getBenefitList()) {
                if (benefitEntity.getKey()!=null && !benefitEntity.getKey().isEmpty())
                benefitSet.add(benefitEntity.getKey());
            }
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("authorId").is(String.valueOf(entity.getCreatorId())),
                    Criteria.where("benefit").in(benefitSet)
            );
            criteriaList[i]= criteria;
        }
        query.addCriteria(new Criteria().orOperator(criteriaList));

        Sort sort = Sort.by(Sort.Direction.DESC, "top","updatedTime");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        int skip = pageable.getPageNumber()  * pageable.getPageSize();
        query.with(sort);
        query.skip(skip);
        query.limit(pageable.getPageSize());

        long total = mongoTemplate.count(query, ArticleEntity.class);
        List articleEntityList = mongoTemplate.find(query,ArticleEntity.class);

        Page studentPage = new PageImpl(articleEntityList, pageable, total);

        return studentPage;
    }
//
//    @Override
//    public Page<ArticleEntity> findByAuthorIdsAndScope(List<String> authorIds, Integer pageIndex,Integer pageSize, Integer scope) {
//        if ( authorIds == null || authorIds.isEmpty()){
//            return  null;
//        }
//        List<Integer> scopeList = new ArrayList<>();
//        if (scope ==0){
//            scopeList.add(0);
//        }else if (scope ==1){
//            scopeList.add(1);
//        }else if (scope == null){
//            scopeList.add(0);
//            scopeList.add(1);
//        }else {
//            throw new BaseException(500,"ArticleEntity_scope_error");
//        }
//        if (pageIndex<0){
//            pageIndex = 0;
//        }
//        Sort sort = Sort.by(Sort.Direction.DESC, "top","updatedTime");
//
//        Pageable pageable = PageRequest.of(pageIndex, 10, sort);
//
//        Page<ArticleEntity> page = articleMongoDao.findByAuthorIdsAndScopeAndStatus(authorIds,scopeList,4,pageable);
//        return page;
//    }

    @Override
    public ArticleEntity findBaseInfoById(String articleId){
        return this.articleMongoDao.findBaseInfoById(articleId);
    }

    @Override
    public boolean topArticle(String articleId, Long userId) {
        //1、取消置顶
        Query cancelQuery = new Query();
        cancelQuery.addCriteria(Criteria.where("top").is(1));
        cancelQuery.addCriteria(Criteria.where("authorId").is(String.valueOf(userId)));
        Update cancelTopUpdate = new Update();
        cancelTopUpdate.set("top",0);
        mongoTemplate.updateFirst(cancelQuery, cancelTopUpdate, ArticleEntity.class);

        //2、置顶
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleId));
        query.addCriteria(Criteria.where("authorId").is(String.valueOf(userId)));

        Update update = new Update();
        update.set("top",1);
        update.set("updatedTime",LocalDateTime.now());

        UpdateResult result= mongoTemplate.updateFirst(query, update, ArticleEntity.class);
        return true;
    }


    private void initArticle(String authorId){
        log.info(" ======initArticle====== authorId:"+authorId);
        ArticleEntity articleEntity = new ArticleEntity();
        articleEntity.setId(ObjectId.get().toString());
        articleEntity.setAuthorId(authorId);
        articleEntity.setImgUrl("");
        articleEntity.setDesc("");
        articleEntity.setParentId("");
        articleEntity.setCreatedTime(LocalDateTime.now());
        articleEntity.setUpdatedTime(LocalDateTime.now());
        articleEntity.setTitle("氢舟文档范文");
        articleEntity.setData(JSON.parseArray(demoData));
        articleMongoDao.save(articleEntity);
    }

    private String demoData = "[\n" +
            "            {\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"html\": \"一、如何添加标题、段落\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7xxg2hhqam7vub3\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"方法一：\",\n" +
            "                \"tag\": \"h4\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq1du2aszewumz0aw\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"点击页面底部 <b>快捷按钮</b>，即可快捷创建一个<b>内容块</b>，例如，点击“Img 图片”，即刻快速在文章内容末端插入一个上传图片的组件，点击或直接拖拽图片到组件上即刻实现图片的添加。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knipwu2d6jimv1t86v5\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"img\",\n" +
            "                \"placeholder\": \"点击此处添加图片\",\n" +
            "                \"imageUrl\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/202104/1168cd2a-86f1-49cc-a7e9-2f1493da9825.png\",\n" +
            "                \"blockType\": \"img\",\n" +
            "                \"attribute\": \"template\",\n" +
            "                \"id\": \"kniqeaad8ctzdhra3ts\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"方法二：\",\n" +
            "                \"tag\": \"h4\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq1pr6du10cgftk4q\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"使用<b>斜杠命令</b>（在输入状态下键入“/”）唤醒<b>tag菜单</b>，然后想要插入的标签就可以啦。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq237sme40usb6ef\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"二、如何创建一个Link书签\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq3g3p7a9pq8dgh6q\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"data\": {\n" +
            "                    \"id\": 79,\n" +
            "                    \"website\": {\n" +
            "                        \"domain\": \"mp.weixin.qq.com\",\n" +
            "                        \"name_cn\": \"微信\",\n" +
            "                        \"icon\": \"https://qingboat-azb.oss-cn-beijing.aliyuncs.com/media/NTI4MWU5.ico\"\n" +
            "                    },\n" +
            "                    \"created_at\": \"1612251827000\",\n" +
            "                    \"skills_input\": [\n" +
            "                        \"文章\"\n" +
            "                    ],\n" +
            "                    \"is_saved\": true,\n" +
            "                    \"saved\": {\n" +
            "                        \"id\": 291,\n" +
            "                        \"article_id\": 79\n" +
            "                    },\n" +
            "                    \"title\": \"什么是内容策展(Content curation)\",\n" +
            "                    \"content\": \"<div id=\\\"js_content\\\"> </div>\",\n" +
            "                    \"is_private\": false,\n" +
            "                    \"skills\": [],\n" +
            "                    \"collect_user_info\": {\n" +
            "                        \"id\": 1,\n" +
            "                        \"nickname\": \"nickname1\",\n" +
            "                        \"position\": \"职场人士\",\n" +
            "                        \"headimg_url\": \"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKcOJZkwl1ZicVXEgnwlNogkTEkrenGiaYHUkvk4k4jtgEiaiaToneicpcjKTVQVrgWsv0BSMKZhpGBxOw/132\",\n" +
            "                        \"role\": 1\n" +
            "                    },\n" +
            "                    \"entry_url\": \"https://mp.weixin.qq.com/s/XgyziaryQNS11ZkCA03vPg\",\n" +
            "                    \"summary\": \"Curation一词翻译成中文是：策划、筛选并展示。在最早的时候，这个词指的是艺术展览活动中的构思、组织和管理工作。\\\\x0a\\\\x0a非正式学习发生在有意义的动作与经验里，这些经验建立在之前的经验与已存在的知识构造之上，从而促进了在过去经验，知识，与技能上的延续。\",\n" +
            "                    \"lead_image_url\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/mmbiz_jpgicp2L3NE48urCeBwKrXdERBuBpEym4U5qepebiaDc9V3GYGUezicttSjT0EM0zMKVgiax1QvcVrmKaZ22WglnhKibkw0.jpg\",\n" +
            "                    \"domain\": \"mp.weixin.qq.com\",\n" +
            "                    \"word_count\": 0,\n" +
            "                    \"language\": \"zh-cn\",\n" +
            "                    \"accessible\": true,\n" +
            "                    \"duration_type\": \"分钟\",\n" +
            "                    \"duration_units\": 1,\n" +
            "                    \"summary_cnt\": 0,\n" +
            "                    \"todoblock_cnt\": 0,\n" +
            "                    \"highlightnode_cnt\": 0,\n" +
            "                    \"is_collector\": false,\n" +
            "                    \"highlight_users\": [],\n" +
            "                    \"highlight_user_cnt\": 0,\n" +
            "                    \"one_summary\": {}\n" +
            "                },\n" +
            "                \"tag\": \"link\",\n" +
            "                \"placeholder\": \"\",\n" +
            "                \"blockType\": \"card\",\n" +
            "                \"attribute\": \"template\",\n" +
            "                \"id\": \"knir6z0ozqpreeotuk8\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"方法一：\",\n" +
            "                \"tag\": \"h4\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq6i05zt38s1b07db\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"点击底部“Link书签” <b>快捷按钮</b>，会插入一个Link输入框，将想要添加的书签网址完整的复制到输入框，单击“创建”按钮，即可创建一个<b>Link书签</b>。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniq6s5zs1bxvryxurs\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"img\",\n" +
            "                \"placeholder\": \"点击此处添加图片\",\n" +
            "                \"imageUrl\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/202104/e6764e8b-c542-4953-99d4-008d2a99b6ae.png\",\n" +
            "                \"blockType\": \"img\",\n" +
            "                \"attribute\": \"template\",\n" +
            "                \"id\": \"kniqn7moseuknfjc0m\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<h4 placeholder=\\\"Heading 4\\\" data-position=\\\"8\\\" data-tag=\\\"h4\\\" class=\\\"block___2kida blockSelected___1oLUV  \\\" style=\\\"max-width: 100%;\\\">方法二：</h4>\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniqkb5f9m55ye2lmxb\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"从<b>素材库</b>进行快捷添加，点击页面右上角双箭头按钮开启<b>素材库</b>，点击想添加的一个书签即可在文章末端快捷添加一个Link书签。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniqqukvemn1mtuxh6q\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"img\",\n" +
            "                \"placeholder\": \"点击此处添加图片\",\n" +
            "                \"imageUrl\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/202104/c94a97c8-527f-46e4-b5a5-e3072082e69a.png\",\n" +
            "                \"blockType\": \"img\",\n" +
            "                \"attribute\": \"template\",\n" +
            "                \"id\": \"knir2gtgg97ajjkkekk\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"三、如何修改文字样式。\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knir0wpac0geru2kp5v\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"通过鼠标选中文字，可唤起<b>文本菜单</b>，然后点击选中自己想要的样式即可实现。<span style=\\\"font-size: 1rem;\\\">以下是一段包含</span><span style=\\\"font-size: 1rem; font-style: italic;\\\">斜 体（I）</span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem; font-weight: 600;\\\">加 粗（B）</span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem;\\\"><u>下划线（U）</u></span><span style=\\\"font-size: 1rem;\\\">、</span><span style=\\\"font-size: 1rem;\\\">删 除（S）</span><span style=\\\"font-size: 1rem;\\\">的示例文本。</span>\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knirdbn31qva84rsz3z\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"img\",\n" +
            "                \"placeholder\": \"点击此处添加图片\",\n" +
            "                \"imageUrl\": \"https://qingboat.oss-cn-beijing.aliyuncs.com/media/202104/06893925-61f2-46c7-85b0-a4bc65b0907b.png\",\n" +
            "                \"blockType\": \"img\",\n" +
            "                \"attribute\": \"template\",\n" +
            "                \"id\": \"knirhl5uz2hmnmtowjt\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"blockType\": \"blockquote\",\n" +
            "                \"html\": \"<span style=\\\"color: rgb(85, 85, 85);\\\">什么是Curation？Curation一词翻译成中文是：策划、筛选并展示。在最早的时候，这个词指的是艺术展览活动中的构思、组织和管理工作。</span>\",\n" +
            "                \"tag\": \"blockquote\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne7vm51f4f880yq3k7\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"通过<b>文本菜单</b>可以实现样式有：\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knirice1pmry2lwjkfp\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<ol><li>加粗</li><li>斜体</li><li>下划线</li><li>删除文字</li><li>有序列表</li><li>无序列表</li><li>引用块</li></ol>\",\n" +
            "                \"tag\": \"div\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"orderedList\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knirjf9n4d3p3qy8udd\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"html\": \"四、如何删除内容块\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne897i9ydlpdp6145i\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"<ul><li>当删除文本内容后继续按删除键，即可删除当前<b>内容块</b></li><li>点击内容块左侧箭头按钮，可唤醒编辑菜单，再点击“删除”，即可删除当前<b>内容块</b></li></ul>\",\n" +
            "                \"tag\": \"div\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"unorderedList\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knipjuktnin5cwr9i2\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"html\": \"五、如何调整文章内容顺序\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne8118w4l1a4x9mabu\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"点击并按住左侧箭头按钮，然后便可以上下自由拖拽内容块来调整顺序。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knirqksorslx3q4gtb\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"六、如何预览当前创作的文章\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniqnf4rrr1dcsivsja\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"点击页面右上角双箭头按钮下放的\uD83D\uDC41样式的按钮，即可预览。\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knirwkpjh225njbjmn\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knisgsjisn0jkkrdr8\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"blockType\": \"header\",\n" +
            "                \"html\": \"更多功能即将开放，敬请期待。。。\",\n" +
            "                \"tag\": \"h2\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kne834sco1pnhbph81n\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"\",\n" +
            "                \"tag\": \"p\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"text\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"knishxx2svn0x01sww\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"html\": \"您在体验的过程中遇到任何问题，随时在群里@我们，感谢您的参与\",\n" +
            "                \"tag\": \"blockquote\",\n" +
            "                \"placeholder\": \"键入 “/” 唤醒菜单\",\n" +
            "                \"blockType\": \"blockquote\",\n" +
            "                \"attribute\": \"content\",\n" +
            "                \"id\": \"kniqnfdvhyhcb04ybau\"\n" +
            "            }\n" +
            "        ]";

}
