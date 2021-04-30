package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingboat.as.entity.*;

import com.qingboat.as.service.MemberTierBenefitService;
import com.qingboat.as.service.MemberTierService;
import com.qingboat.as.service.UserService;
import com.qingboat.as.service.UserSubscriptionService;

import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/readersubscription")
@Slf4j
public class ReaderSubscriptionController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private MemberTierService memberTierService;

    @Autowired
    private MemberTierBenefitService memberTierBenefitService;


    //=======================针对 reader 接口=============================
    /**
     * 获取某creator的全部会员等级的列表接口
     */
    @GetMapping(value = "/tiers")
    @ResponseBody
    public List<MemberTierEntity> getMemberTierEntityList(@Valid @RequestParam("creatorId") String creatorId) {
        MemberTierEntity memberTierEntity = new MemberTierEntity();
        memberTierEntity.setCreatorId(Long.parseLong(creatorId));
        QueryWrapper<MemberTierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(memberTierEntity);

        List<MemberTierEntity> list = memberTierService.list(queryWrapper);
        if (list!=null && !list.isEmpty()){
            for (MemberTierEntity entity :list ) {
                QueryWrapper<MemberTierBenefitEntity> wrapper = new QueryWrapper<>();
                wrapper.eq("member_tier_id",entity.getId());
                entity.setMemberTierBenefitEntityList(memberTierBenefitService.list(wrapper));
            }
        }
        return list;
    }

    /**
     * 读者获取订阅的creator用户列表
     */
    @GetMapping(value = "/creators")
    @ResponseBody
    public IPage<UserSubscriptionEntity> getCreatorEntityList(@RequestParam("pageIndex") int pageIndex) {
        UserSubscriptionEntity entity = new UserSubscriptionEntity();
        entity.setSubscriberId(getUId());

        QueryWrapper<UserSubscriptionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        IPage<UserSubscriptionEntity> page = new Page<>(pageIndex, 10);

        return userSubscriptionService.page(page,queryWrapper);
    }



}
