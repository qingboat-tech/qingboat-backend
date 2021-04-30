package com.qingboat.as.controller;

import com.qingboat.as.entity.*;
import com.qingboat.as.service.ArticleCommentService;
import com.qingboat.as.service.ArticleService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.utils.AliyunOssUtil;
import com.qingboat.as.utils.RssUtil;
import com.qingboat.as.utils.sensi.SensitiveFilter;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/subscription")
@Slf4j
public class SubscriptionController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;


    //=======================针对 creator 接口=============================

    /**
     * 获取Creator的订阅者列表接口，包括当前订阅人数和昨日新增两个query parameter结果集
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public Page<UserSubscriptionEntity> findSubscriptionList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return null;
    }


    /**
     * 获取当前订阅人数数据
     */
    @GetMapping(value = "/currentCount")
    @ResponseBody
    public Integer getCurrentSubscriptionCount(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return 0;
    }

    /**
     * 获取当前昨日新增人数数据
     */
    @GetMapping(value = "/lastCount")
    @ResponseBody
    public Integer getLastSubscriptionCount(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return 0;
    }


    /**
     * 获取creater的权益列表
     */
    @GetMapping(value = "/getBenefits")
    @ResponseBody
    public List<BenefitEntity> getBenefits() {
        String uid = getUId();
        return null;
    }

    /**
     * 创建会员等级的接口
     */
    @PostMapping(value = "/tiers/add")
    @ResponseBody
    public MemberTierEntity addMemberTierEntityList() {
        String uid = getUId();
        return null;
    }

    /**
     * 创建会员和权益的组合关系表
     */
    @PostMapping(value = "/memberTiers/add")
    @ResponseBody
    public List<MemberTierBenefitEntity> addMemberTierBenefitEntityList() {
        String uid = getUId();
        return null;
    }



    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return null;
    }

    /**
     * 读者获取订阅的creator用户列表
     */
    @GetMapping(value = "/creators")
    @ResponseBody
    public List<UserSubscriptionEntity> getCreatorEntityList(@RequestParam("pageIndex") int pageIndex) {
        String uid = getUId();
        return null;
    }

    /**
     * 获取该阅读者
     */
//    @GetMapping(value = "/tiers")
//    @ResponseBody
//    public List<MemberTierEntity> getMemberTierEntityList(@RequestParam("pageIndex") int pageIndex) {
//        String uid = getUId();
//        return null;
//    }


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
