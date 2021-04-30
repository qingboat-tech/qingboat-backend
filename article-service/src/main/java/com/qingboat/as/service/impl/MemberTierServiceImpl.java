package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.as.dao.BenefitDao;
import com.qingboat.as.dao.MemberTierDao;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.MemberTierEntity;
import com.qingboat.as.service.MemberTierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MemberTierServiceImpl implements MemberTierService {

    @Autowired
    private BenefitDao benefitDao;

    @Autowired
    private MemberTierDao memberTierDao;


    @Override
    public List<BenefitEntity> getBenefitList() {
        QueryWrapper<BenefitEntity> queryWrapper = new QueryWrapper<>();
        return benefitDao.selectList(queryWrapper);
    }

    @Override
    public List<MemberTierEntity> getMemberTierList(Long creatorId) {

        QueryWrapper<MemberTierEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator_id",creatorId);
        List<MemberTierEntity> list = memberTierDao.selectList(queryWrapper);
        if (list ==null || list.isEmpty()){
            //添加默认会员计划
        }else {
            for ( MemberTierEntity memberTierEntity : list ) {

            }
        }
        return null;
    }

    @Override
    public List<MemberTierEntity> saveMemberTierList(MemberTierEntity... memberTierEntities) {
        return null;
    }
}
