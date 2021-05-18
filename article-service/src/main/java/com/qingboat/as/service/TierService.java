package com.qingboat.as.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingboat.as.entity.TierEntity;

import java.util.List;

public interface TierService extends IService<TierEntity>  {


    List<TierEntity> getTierListByBenefitKey(Long creatorId,String benefitKey);


}
