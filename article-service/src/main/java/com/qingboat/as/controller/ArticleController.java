package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.as.entity.*;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.as.utils.RssUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
import com.qingboat.as.vo.ArticleCommentVo;
import com.qingboat.as.vo.ArticlePublishVo;
import com.qingboat.base.api.FeishuService;
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
import java.time.LocalDate;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
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

    //=======================针对 creator 接口=============================

    /**
     * 根据作者分页查询草稿的文章列表
     */
    @GetMapping(value = "/findDraftArticleList")
    @ResponseBody
    public Page<ArticleEntity> findDraftArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUIdStr();
        return articleService.findDraftListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询正在审核的文章列表
     */
    @GetMapping(value = "/findReviewArticleList")
    @ResponseBody
    public Page<ArticleEntity> findReviewingArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUIdStr();
        return articleService.findReviewListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询审核未通过的文章列表
     */
    @GetMapping(value = "/findRefuseArticleList")
    @ResponseBody
    public Page<ArticleEntity> findRefuseArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUIdStr();
        return articleService.findRefuseListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询已发布文章列表
     */
    @GetMapping(value = "/findPublishArticleList")
    @ResponseBody
    public Page<ArticleEntity> findPublishArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUIdStr();
        return articleService.findPublishListByAuthorId(uid,pageIndex);
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
    public Boolean submitReview(@Valid @RequestBody ArticlePublishVo articlePublishVo) {
        String uid = getUIdStr();
        Boolean rst =articleService.submitReviewByArticleId(articlePublishVo.getArticleId(),uid,articlePublishVo.getScope());

        //TODO 发送消息给氢舟客服，通知其审核。
        FeishuService.TextBody textBody = new FeishuService.TextBody(
                new StringBuilder().append("创作这提交文章审核").append("\n")
                .append("创作者Id：").append(uid).append("\n")
                .append("文章Id：").append(articlePublishVo.getArticleId()).append("\n").toString());
        feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);
        return rst;
    }


    //=======================针对 reader 接口=============================

    @RequestMapping(value = "/subscriptionArticleList", method = RequestMethod.GET)
    public Page<ArticleEntity> subscriptionArticleList(@RequestParam("pageIndex") int pageIndex, @RequestParam("scope") int scope) {
        Long subscriberId = getUId();

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        LocalDate today = LocalDate.now();
        queryWrapper.lambda()
                .eq(UserSubscriptionEntity::getSubscriberId,subscriberId)
                .lt(UserSubscriptionEntity::getExpireDate,today);

        List<UserSubscriptionEntity> subscriptionEntityList = userSubscriptionService.list(queryWrapper);
        if (subscriptionEntityList == null || subscriptionEntityList.isEmpty()){
            return null;
        }

        List<String> creatorIds = new ArrayList<>();
        if (scope  ==0){
            for (UserSubscriptionEntity s: subscriptionEntityList) {
                creatorIds.add(String.valueOf(s.getCreatorId()));
            }
        }else if (scope == 1){
            for (UserSubscriptionEntity s: subscriptionEntityList) {
                for (BenefitEntity benefitEntity : s.getBenefitList()) {
                    if ("READ".equals(benefitEntity.getKey())) {
                        creatorIds.add(String.valueOf(s.getCreatorId()));
                        break;
                    }
                }
            }
        }
        return  articleService.findByAuthorIdsAndScope(creatorIds,scope,pageIndex);
    }



    @GetMapping(value = "/{id}")
    public ArticleEntity viewArticleByArticleId(@PathVariable("id") String id) {
        ArticleEntity articleEntity = articleService.findArticleById(id);
        if (articleEntity!=null){
            String readerId = getUIdStr();
            String authorId = articleEntity.getAuthorId();
            if (readerId.equals(authorId)){
                return articleEntity;
            }
            // 获取订阅信息
            QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserSubscriptionEntity::getSubscriberId,getUId())
                    .eq(UserSubscriptionEntity::getCreatorId,Long.parseLong(articleEntity.getAuthorId()))
                    .le(UserSubscriptionEntity::getExpireDate,new Date());
            UserSubscriptionEntity userSubscriptionEntity = userSubscriptionService.getOne(queryWrapper);
            if (userSubscriptionEntity !=null ){
                if (Integer.valueOf(0).equals(articleEntity.getScope()) ){ //免费文章
                    articleService.increaseReadCountByArticleId(id);//增加该文章阅读数
                    return articleEntity;
                }else{//付费文章
                    for (BenefitEntity benefitEntity :  userSubscriptionEntity.getBenefitList()) {
                        if ("READ".equals(benefitEntity.getKey())){
                            articleService.increaseReadCountByArticleId(id);//增加该文章阅读数
                            return articleEntity;
                        }
                    }
                    //TODO 免费订阅后，查看付费文章（处理试读）
                    return articleEntity;
                }
            }
            //TODO 没有订阅，查看付费文章（处理试读）
            return articleEntity;
        }
        return articleEntity;

    }

    //======================= 文章互动 =============================
    // 点赞
    @PostMapping(value = "/star/{id}")
    @ResponseBody
    public Long star(@PathVariable("id") String id){
        return articleService.handleStarCountByArticleId(id,Long.parseLong(getUIdStr()));
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


}
