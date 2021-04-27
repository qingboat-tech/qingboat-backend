package com.qingboat.as.controller;

import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.ArticleEntity;
import com.qingboat.as.entity.UserEntity;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.UserService;
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
import sun.plugin.util.UserProfile;

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

    @Autowired
    private ArticleCommentService articleCommentService;

    @Autowired
    private FeishuService feishuService;


    //=======================针对 creator 接口=============================

    /**
     * 根据作者分页查询草稿的文章列表
     */
    @GetMapping(value = "/findDraftArticleList")
    @ResponseBody
    public Page<ArticleEntity> findDraftArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return articleService.findDraftListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询正在审核的文章列表
     */
    @GetMapping(value = "/findReviewArticleList")
    @ResponseBody
    public Page<ArticleEntity> findReviewingArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return articleService.findReviewListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询审核未通过的文章列表
     */
    @GetMapping(value = "/findRefuseArticleList")
    @ResponseBody
    public Page<ArticleEntity> findRefuseArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return articleService.findRefuseListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者分页查询已发布文章列表
     */
    @GetMapping(value = "/findPublishArticleList")
    @ResponseBody
    public Page<ArticleEntity> findPublishArticleList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return articleService.findPublishListByAuthorId(uid,pageIndex);
    }

    /**
     * 根据作者查询最新发布的前10篇文章
     */
    @GetMapping(value = "/findByAuthorIdByUpdateTimeDesc")
    @ResponseBody
    public List<ArticleEntity> findByAuthorIdByUpdateTimeDesc() {
        String uid = getUId();
        return articleService.findByAuthorIdByUpdateTimeDesc(uid);
    }

    /**
     * 根据作者查询最热发布的前10篇文章
     */
    @GetMapping(value = "/findByAuthorIdByReadCountDesc")
    @ResponseBody
    public List<ArticleEntity> findByAuthorIdByReadCountDesc() {
        String uid = getUId();
        return articleService.findByAuthorIdByReadCountDesc(uid);
    }

    /**
     * 根据文章Id返回文章内容
     */
    @GetMapping(value = "/preview/{id}")
    @ResponseBody
    public ArticleEntity findByArticleId(@PathVariable("id") String id)  {
        ArticleEntity articleEntity = articleService.findArticleById(id);
        String userId = getUId();
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
        String uid = getUId();
        return articleService.saveArticle(article,uid);
    }

    /**
     * 根据文章Id删除文章，状态为4的（已发布）不能删除
     */
    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public Boolean delArticle(@PathVariable("id") String id) {
        String uid = getUId();
        return articleService.removeArticleById(id,uid);
    }

    /**
     * 提交文章审核
     */
    @PostMapping(value = "/submitReview")
    @ResponseBody
    public Boolean submitReview(@Valid @RequestBody ArticlePublishVo articlePublishVo) {
        String uid = getUId();
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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<ArticleEntity> list() {
        //TODO 很复杂
        return null;
    }

    @GetMapping(value = "/{id}")
    public ArticleEntity viewArticleByArticleId(@PathVariable("id") String id) {
        ArticleEntity articleEntity = articleService.findArticleById(id);
        if (articleEntity!=null){

            if (articleEntity.getScope() ==0){
                articleService.increaseReadCountByArticleId(id);//增加该文章阅读数
                return articleEntity;
            }

            String readerId = getUId();
            String authorId = articleEntity.getAuthorId();
            if (readerId.equals(authorId)){
                return articleEntity;
            }

            //TODO  检查该readerId 是否订阅了authorId
            if(true){
                articleService.increaseReadCountByArticleId(id);//增加该文章阅读数
                return articleEntity;
            }else {
                articleEntity.setData(null);
                return articleEntity;
            }
        }
        return articleEntity;

    }

    //======================= 文章互动 =============================
    // 点赞
    @PostMapping(value = "/star/{id}")
    @ResponseBody
    public Long star(@PathVariable("id") String id){
        return articleService.handleStarCountByArticleId(id,Long.parseLong(getUId()));
    }

    // 评论
    @PostMapping(value = "/comment")
    @ResponseBody
    public ArticleCommentEntity comment(@Valid @RequestBody ArticleCommentVo articleCommentVo){
        String uidString = getUId();
        Long uid = Long.parseLong(uidString);

        ArticleCommentEntity articleCommentEntity = new ArticleCommentEntity();
        articleCommentEntity.setArticleId(articleCommentVo.getArticleId());
        articleCommentEntity.setContent(articleCommentVo.getContent());
        articleCommentEntity.setUserId(uid);

        UserEntity userOperate = userService.findByUserId(uid);
        articleCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
        articleCommentEntity.setNickName(userOperate.getNickname());

        return articleCommentService.addArticleComment(articleCommentEntity);
    }

    // 删除评论
    @DeleteMapping(value = "/{articleId}/comment/{id}")
    @ResponseBody
    public Boolean delComment(@PathVariable("id") String id, @PathVariable("articleId") String articleId) {

        // TODO: service层验证这条评论是否可以删除，是不是自己的
        return articleCommentService.removeArticleComment(articleId,Long.parseLong(id));
    }

    // 回复评论
    @PostMapping(value = "/comment/replay")
    @ResponseBody
    public ArticleCommentEntity replyComment(@Valid @RequestBody ArticleCommentVo articleCommentVo) {
        String uidString = getUId();
        Long uid = Long.parseLong(uidString);

        ArticleCommentEntity articleCommentEntity = new ArticleCommentEntity();
        articleCommentEntity.setArticleId(articleCommentVo.getArticleId());
        articleCommentEntity.setContent(articleCommentVo.getContent());
        articleCommentEntity.setParentId(articleCommentVo.getParentId());

        // 这是在用户信息
        articleCommentEntity.setUserId(uid);

        UserEntity userOperate = userService.findByUserId(uid);
        articleCommentEntity.setHeadImgUrl(userOperate.getHeadimgUrl());
        articleCommentEntity.setNickName(userOperate.getNickname());

        return articleCommentService.replyComment(articleCommentEntity);
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
        String StrUid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof  ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            Long uid = (Long) request.getAttribute("UID");
            if (uid == null){
                throw new BaseException(401,"AUTH_ERROR");
            }
            StrUid = String.valueOf(uid);
        }
        return StrUid;
    }



}
