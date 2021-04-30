package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.BenefitDao;
import com.qingboat.as.dao.MemberTierBenefitDao;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.entity.MemberTierBenefitEntity;
import com.qingboat.as.service.BenefitService;
import com.qingboat.as.service.MemberTierBenefitService;
import org.springframework.stereotype.Service;

@Service
public class MemberTierBenefitServiceImpl extends ServiceImpl<MemberTierBenefitDao, MemberTierBenefitEntity>  implements MemberTierBenefitService {

}
