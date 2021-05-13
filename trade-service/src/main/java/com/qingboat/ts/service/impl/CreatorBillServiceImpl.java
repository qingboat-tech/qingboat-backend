package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.CreatorBillDao;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class CreatorBillServiceImpl extends ServiceImpl <CreatorBillDao, CreatorBillEntity> implements CreatorBillService {

    @Override
    public Long currentMonthIncome(Long creatorId) {
        return null;
    }

    @Override
    public IPage<CreatorBillEntity> getCreatorBillList(Long creaatorId) {
        return null;
    }
}

