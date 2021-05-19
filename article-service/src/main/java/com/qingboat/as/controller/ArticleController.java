package com.qingboat.as.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.as.entity.*;
import com.qingboat.as.service.*;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.as.utils.RedisUtil;
import com.qingboat.as.utils.RssUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
import com.qingboat.as.vo.ArticlePublishVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.BASE64Util;
import com.rometools.rome.io.FeedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/article")
@Slf4j
public class ArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageService messageService;

    //=======================针对 creator 接口=============================

    /**
     * 根据作者分页查询草稿的文章列表
     */
    @GetMapping(value = "/findDraftArticleList")
    @ResponseBody
    public Page<ArticleEntity> findDraftArticleList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize) {
        String uid = getUIdStr();
        return articleService.findDraftListByAuthorId(uid,pageIndex,pageSize);
    }

    /**
     * 根据作者分页查询正在审核的文章列表
     */
    @GetMapping(value = "/findReviewArticleList")
    @ResponseBody
    public Page<ArticleEntity> findReviewingArticleList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize) {
        String uid = getUIdStr();
        return articleService.findReviewListByAuthorId(uid,pageIndex,pageSize);
    }

    /**
     * 根据作者分页查询审核未通过的文章列表
     */
    @GetMapping(value = "/findRefuseArticleList")
    @ResponseBody
    public Page<ArticleEntity> findRefuseArticleList(@RequestParam(value = "pageIndex",required = false)Integer pageIndex,@RequestParam(value = "pageSize",required = false) Integer pageSize ) {
        String uid = getUIdStr();
        return articleService.findRefuseListByAuthorId(uid,pageIndex,pageSize);
    }

    /**
     * 根据作者分页查询已发布文章列表
     */
    @GetMapping(value = "/findPublishArticleList")
    @ResponseBody
    public Page<ArticleEntity> findPublishArticleList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                      @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                      @RequestParam(value = "hot",required = false) Boolean orderByHot ) {
        String uid = getUIdStr();
        return articleService.findPublishListByAuthorId(uid,pageIndex,pageSize,orderByHot,null);
    }

    /**
     * 根据作者查询最新发布的前10篇文章
     */
    @GetMapping(value = "/findByAuthorIdByUpdateTimeDesc")
    @ResponseBody
    public List<ArticleEntity> findByAuthorIdByUpdateTimeDesc() {
        String uid = getUIdStr();
        return articleService.findByAuthorIdByUpdateTimeDesc(uid);
    }

    /**
     * 根据作者查询最热发布的前10篇文章
     */
    @GetMapping(value = "/findByAuthorIdByReadCountDesc")
    @ResponseBody
    public List<ArticleEntity> findByAuthorIdByReadCountDesc() {
        String uid = getUIdStr();
        return articleService.findByAuthorIdByReadCountDesc(uid);
    }

    /**
     * 根据文章Id返回文章内容
     */
    @GetMapping(value = "/preview/{id}")
    @ResponseBody
    public ArticleEntity findByArticleId(@PathVariable("id") String id)  {
        ArticleEntity articleEntity = articleService.findArticleById(id);
        String userId = getUIdStr();
        if (articleEntity !=null && userId.equals(articleEntity.getAuthorId()) ){
            return articleEntity;
        }else {
            throw new BaseException(500,"System_auth_error");
        }
    }

    /**
     * 添加or保存文章内容
     */
    @PostMapping(value = "/")
    @ResponseBody
    public ArticleEntity saveArticle(@Valid @RequestBody ArticleEntity article) {
        String uid = getUIdStr();
        return articleService.saveArticle(article,uid);
    }

    /**
     * 设置置顶
     */
    @PostMapping(value = "/topArticle/{id}")
    @ResponseBody
    public Boolean topArticle(@PathVariable("id") String id) {
        return articleService.topArticle(id,getUId());
    }

    /**
     * 根据文章Id删除文章，状态为4的（已发布）不能删除
     */
    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delArticle(@PathVariable("id") String id) {
        String uid = getUIdStr();
        return articleService.removeArticleById(id,uid);
    }

    /**
     * 提交文章审核
     */
    @PostMapping(value = "/submitReview")
    @ResponseBody
    public Boolean submitReview(@Valid @RequestBody ArticlePublishVo articlePublishVo) {  //TODO 需要修改
        String uid = getUIdStr();
        //TODO  david
        Boolean rst =articleService.submitReviewByArticleId(articlePublishVo.getArticleId(),uid, articlePublishVo.getPublishType(), articlePublishVo.getTags());

        articleService.asyncReviewByArticleId(articlePublishVo.getArticleId());
        return rst;
    }

    /**
     * 文章审核
     */
    @PostMapping(value = "/reviewByArticleId")
    @ResponseBody
    public boolean reviewByArticleId(@RequestBody Map<String,Object> param){  //TODO
        String articleId = (String) param.get("articleId");
        Integer status = (Integer) param.get("status");
        return articleService.reviewByArticleId(articleId,status);
    }


    //=======================针对 reader 接口=============================

    /**
     * 根据作者分页查询已发布文章列表
     */
    @GetMapping(value = "/findArticleListWithStatus")
    @ResponseBody
    public Page<ArticleEntity> findArticleListWithStatus(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                      @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                      @RequestParam(value = "creatorId") String creatorId ) {
        Long userId = getUId();
        return articleService.findPublishListByAuthorId(creatorId,pageIndex,pageSize,Boolean.TRUE,userId);
    }

    /**
     *
     * @param pageIndex
     * @param creatorId  null：表示全部
     * @param paid  null：表示全部  true 表示付费，false 表示免费
     * @return
     */
    @RequestMapping(value = "/subscriptionArticleList", method = RequestMethod.GET)
    @ResponseBody
    public Page<ArticleEntity> subscriptionArticleList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                       @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                       @RequestParam(value = "creatorId",required = false) Long creatorId,
                                                       @RequestParam(value = "paid",required = false) Boolean paid) {
        Long subscriberId = getUId();

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<UserSubscriptionEntity> lambdaQueryWrapper = queryWrapper.lambda();

        lambdaQueryWrapper.eq(UserSubscriptionEntity::getSubscriberId,subscriberId);
        lambdaQueryWrapper.ge(UserSubscriptionEntity::getExpireDate,today);
        if (creatorId!=null){
            lambdaQueryWrapper.eq(UserSubscriptionEntity::getCreatorId,creatorId);
        }
        log.info( "subscriptionArticleList: "+paid);
        if (paid!=null ) {
            if(paid){
                lambdaQueryWrapper.ne(UserSubscriptionEntity::getOrderId,0);
            }else {
                lambdaQueryWrapper.eq(UserSubscriptionEntity::getOrderId,0);
            }
        }

        List<UserSubscriptionEntity> subscriptionEntityList = userSubscriptionService.list(queryWrapper);
        if (subscriptionEntityList == null || subscriptionEntityList.isEmpty()){
            return null;
        }

        return  articleService.findArticleListByUserSubscription(subscriptionEntityList,paid,pageIndex,pageSize);
    }

    @GetMapping(value = "/getInviteKey")
    @ResponseBody
    public Map<String,Object> getInviteKey(@RequestParam("articleId") String articleId) {
        Map<String,Object> rstMap = new HashMap<>();

        ArticleEntity articleEntity = articleService.findBaseInfoById(articleId);
        if (articleEntity  == null){
            throw  new BaseException(500,"推荐试读的文章不存在");
        }
        if (articleEntity.getAuthorId().equals(getUIdStr())){
            //作者邀请试读
        }else {
            //检查该用户是否已订阅
            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserSubscriptionEntity::getSubscriberId,getUId())
                    .eq(UserSubscriptionEntity::getCreatorId,Long.parseLong(articleEntity.getAuthorId()))
                    .ge(UserSubscriptionEntity::getExpireDate,new Date());
            int count = userSubscriptionService.count(queryWrapper);
            if (count == 0){
                throw new BaseException(500,"未订阅用户，禁止推荐试读分享");
            }
        }

        String refKey = BASE64Util.encode(articleId+"#"+ getUIdStr());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,10);

        rstMap.put("ref",refKey);
        rstMap.put("expire",cal.getTime());

        return rstMap;
    }

    @GetMapping(value = "/getInviteUserListByInviteKey")
    @ResponseBody
    public List<UserEntity> getInviteUserListByInviteKey(@RequestParam("inviteKey") String inviteKey) {
        inviteKey = BASE64Util.decode(inviteKey);
        Set<Object> inviteUserIdSet = redisUtil.members("AIK_"+inviteKey);
        Set<Long> inviteUserIdLongSet = new HashSet<>();
        if (inviteUserIdSet!=null && !inviteUserIdSet.isEmpty()){
            Iterator<Object> ite =  inviteUserIdSet.iterator();
            while (ite.hasNext()){
                inviteUserIdLongSet.add(Long.parseLong(ite.next().toString()));
            }
            return userService.findListByUserIds(inviteUserIdLongSet);
        }
       return null;
    }

    /**
     *  领取邀请码
     *
     * @param inviteKey 邀请码
     * @return
     */

    @GetMapping(value = "/takeInviteKey")
    @ResponseBody
    public Boolean takeInviteKey(@RequestParam("inviteKey") String inviteKey) {
        if (StringUtils.isEmpty(inviteKey) ){
            throw new BaseException(500,"操作失败：请求参数缺失");
        }
        String readerId = getUIdStr();
        inviteKey = BASE64Util.decode(inviteKey);
        if (!inviteKey.contains("#")){
            throw new BaseException(500,"非法邀请码，不能领取哦");
        }

        if (redisUtil.isMember("AIK_"+inviteKey,readerId)){
            throw new BaseException(500,"你已经领取邀请码，不能重复领取哦");
        }
        long size = redisUtil.size("AIK_"+inviteKey);
        if (size> 5){
            throw new BaseException(500,"本次分享超出限量阅读，下次要手快哦");
        }
        redisUtil.sSet("AIK_"+inviteKey,readerId);
        return Boolean.FALSE;
    }

    @GetMapping(value = "/hasTakeInviteKey")
    @ResponseBody
    public Boolean hasTakeInviteKey(@RequestParam("inviteKey") String inviteKey) {
        if (StringUtils.isEmpty(inviteKey)){
            throw new BaseException(500,"操作失败：请求参数缺失");
        }
        String readerId = getUIdStr();
        inviteKey = BASE64Util.decode(inviteKey);
        if (!inviteKey.contains("#")){
            throw new BaseException(500,"非法邀请码，不能领取哦");
        }
        if (redisUtil.isMember("AIK_"+inviteKey,readerId)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



    /**
     *
     * @param articleId
     * @param inviteKey  推荐者唯一值
     * @return
     */
    @GetMapping(value = "/{articleId}")
    @ResponseBody
    public ArticleEntity viewArticleByArticleId(@PathVariable("articleId") String articleId,@RequestParam(value = "inviteKey",required = false) String inviteKey ) {
        ArticleEntity articleEntity = articleService.findArticleById(articleId);
        if (articleEntity!=null){
            String readerId = getUIdStr();
            String authorId = articleEntity.getAuthorId();
            articleEntity.setHasStar( articleService.hasStar(articleId,getUId()));
            if (readerId.equals(authorId)){  //创作者自己看自己的文章
                articleEntity.setCanComment(true);
                articleEntity.setReaderRole("author");
                return articleEntity;
            }
            // 处理推荐逻辑，每个订阅者最多分享给5个好友阅读
            if (inviteKey!= null){
                inviteKey = BASE64Util.decode(inviteKey);
                try {
                    String[] refContent = inviteKey.split("#"); //验证其合法性
                    //refContent[0] = articleId; refContent[1] = creatorId;
                    if (refContent!=null && refContent.length ==2
                            &&  articleId.equals(refContent[0])
                            && authorId.equals(refContent[1])){

                        if (redisUtil.isMember("AIK_"+inviteKey,readerId)){
                            articleService.increaseReadCountByArticleId(articleId);//增加该文章阅读数
                            articleEntity.setReaderRole(articleService.getReaderRole(articleEntity,getUId()));
                            return articleEntity;
                        }

                        long size = redisUtil.size("AIK_"+inviteKey);
                        if (size> 5){
                            throw new BaseException(500,"本次分享超出限量阅读，下次要手快哦");
                        }else {
                            redisUtil.sSet("AIK_"+inviteKey,readerId);
                            articleService.increaseReadCountByArticleId(articleId);//增加该文章阅读数
                            articleEntity.setReaderRole(articleService.getReaderRole(articleEntity,getUId()));
                            return articleEntity;
                        }
                    }else {
                        throw new BaseException(500,"非法邀请码");
                    }

                } catch (BaseException e) {
                    throw e;
                }catch (Exception e) {
                    e.printStackTrace();
                    throw new BaseException(500,"邀请码参数无效");
                }
            }

            // 获取订阅信息
            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserSubscriptionEntity::getSubscriberId,getUId())
                    .eq(UserSubscriptionEntity::getCreatorId,Long.parseLong(authorId))
                    .ge(UserSubscriptionEntity::getExpireDate,new Date());
            List<UserSubscriptionEntity> userSubscriptionEntityList = userSubscriptionService.list(queryWrapper);

            if (userSubscriptionEntityList == null || userSubscriptionEntityList.isEmpty()){
                articleEntity.setReaderRole("visitor");
                //没有订阅，查看付费文章（下面处理试读）
            }else {
                Set<String> userBenefitSet = new HashSet<>();
                UserSubscriptionEntity userSubscriptionEntity = userSubscriptionEntityList.get(0);
                // 检查是否有评论权限
                if (userSubscriptionEntity.getBenefitList() !=null ){
                    for (BenefitEntity benefitEntity :  userSubscriptionEntity.getBenefitList()) {
                        userBenefitSet.add(benefitEntity.getKey());
                    }
                }

                if (userBenefitSet.contains("COMMENT")){
                    articleEntity.setCanComment(true);
                }
                if ("free".equalsIgnoreCase(userSubscriptionEntity.getSubscribeDuration())){
                    articleEntity.setReaderRole("free-subscriber");
                }else {
                    articleEntity.setReaderRole("paid-subscriber");
                }

                Set<String> intersectElements = articleEntity.getBenefit().stream()
                        .filter(userBenefitSet :: contains)
                        .collect(Collectors.toSet()); //交集运算

                if ((articleEntity.getBenefit()!=null && articleEntity.getBenefit().contains("FREE"))
                || (!intersectElements.isEmpty())){
                    articleService.increaseReadCountByArticleId(articleId);//增加该文章阅读数
                    return articleEntity;
                }
            }

            //没有订阅，查看付费文章（处理试读）
            articleEntity.setData(subList(articleEntity.getData()));
            articleEntity.setStatus(7);
            return articleEntity;
        }
        return articleEntity;

    }

    @GetMapping(value = "/getSubscribeInfo/{articleId}")
    @ResponseBody
    public Map<String,String> getSubscribeInfo(@PathVariable("articleId") String articleId) {

        ArticleEntity articleEntity = articleService.findBaseInfoById(articleId);
        if (articleEntity == null){
            throw new BaseException(500,"该文章不存在");
        }
        Map<String,String> rstMap = new HashMap<>();

        if (getUId().equals(Long.parseLong(articleEntity.getAuthorId()))){
            rstMap.put("subscribeInfo","isAuthor");
            return rstMap;
        }

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserSubscriptionEntity::getSubscriberId,getUId())
                .eq(UserSubscriptionEntity::getCreatorId,Long.parseLong(articleEntity.getAuthorId()))
                .le(UserSubscriptionEntity::getExpireDate,new Date());
        UserSubscriptionEntity userSubscriptionEntity = userSubscriptionService.getOne(queryWrapper);
        if (userSubscriptionEntity!=null){
            if (Long.valueOf(0l).equals(userSubscriptionEntity.getOrderId()) && Integer.valueOf(0).equals(userSubscriptionEntity.getOrderPrice())){
                if (Integer.valueOf(0).equals(articleEntity.getScope())){
                    rstMap.put("subscribeInfo","isFreeSubscription");
                }else {
                    rstMap.put("subscribeInfo","isNoPaidSubscription");
                }
            }else {
                rstMap.put("subscribeInfo","isPaidSubscription");
            }
        }else {
            rstMap.put("subscribeInfo","isNoSubscription");
        }
       return rstMap;
    }


    //======================= 文章互动 =============================
    // 点赞
    @PostMapping(value = "/star/{id}")
    @ResponseBody
    public Long star(@PathVariable("id") String id){
        Long starCount = articleService.handleStarCountByArticleId(id,getUId());
        return starCount;
    }



    @RequestMapping(value = "/rss")
    @ResponseBody
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
    @ResponseBody
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
    public Object sensitive( @RequestBody Map<String,String> param) {

        SensitiveFilter filter = SensitiveFilter.DEFAULT;
        Iterator<String> ite = param.values().iterator();
        if (ite.hasNext()){
            Object rst = filter.filter(ite.next(), '*');

            return rst;
        }
        return  null;

    }


    private JSONArray subList(JSONArray jsonArray){
        if (jsonArray == null || jsonArray.size() ==0){
            return jsonArray;
        }
        int capacity =  (int) (jsonArray.size() * 0.3);
        if (capacity == 0){
            return jsonArray;
        }else {
            List dataList = jsonArray.subList(0,capacity);
            return new JSONArray(dataList);
        }
    }


}
