package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.TierDao;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.TierEntity;
import com.qingboat.as.service.TierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class TierServiceImpl extends ServiceImpl<TierDao, TierEntity> implements TierService {


    @Override
    public List<TierEntity> getTierListByBenefitKey(Long creatorId,String benefitKey) {
        QueryWrapper<TierEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("creator_id",creatorId);
        wrapper.eq("`status`",1);
        List<TierEntity> list = list(wrapper);
        List<TierEntity> resultList = new ArrayList<>();

        if (list !=null){
            for (TierEntity tier:list){
                for (BenefitEntity benefitEntity:tier.getBenefitList()){
                    if (benefitKey.equals(benefitEntity.getKey())){
                        resultList.add(tier);
                        break;
                    }
                }
            }
        }
        return resultList;
    }
}
