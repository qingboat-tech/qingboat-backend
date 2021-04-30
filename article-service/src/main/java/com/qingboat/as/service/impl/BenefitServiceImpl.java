package com.qingboat.as.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.as.dao.BenefitDao;
import com.qingboat.as.entity.BenefitEntity;
import com.qingboat.as.service.BenefitService;
import org.springframework.stereotype.Service;

@Service
public class BenefitServiceImpl extends ServiceImpl<BenefitDao,BenefitEntity>  implements BenefitService {

}
