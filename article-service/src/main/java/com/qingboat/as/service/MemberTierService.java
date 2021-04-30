package com.qingboat.as.service;

import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.MemberTierEntity;

import java.util.List;

public interface MemberTierService {
    /**
     * 查询氢舟平台权益列表
     */
    List<BenefitEntity> getBenefitList();

    /**
     * 查询创作者会员计划权益列表，没有则创建一个默认
     */
    List<MemberTierEntity> getMemberTierList(Long creatorId);

    /**
     * 保存创作者会员计划权益列表
     */
    List<MemberTierEntity> saveMemberTierList(MemberTierEntity...memberTierEntities);


}
