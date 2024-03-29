package com.qingboat.ts.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;

import java.util.Date;
import java.util.Map;

public interface CreatorBillService {

    /**
     * 创建账单和更新钱包余额
     * @param creatorBillEntity
     * @return
     */
    CreatorBillEntity createBillAndUpdateWallet(CreatorBillEntity creatorBillEntity);

    /**
     * 查询创作者收益
     * @param creatorId
     * @return
     */
     Long getCurrentMonthIncome(Long creatorId);

    /**
     * 查询创作者账单信息
     * @param creatorId
     * @return
     */
    IPage<CreatorBillEntity> getCreatorBillList(Long creatorId , Integer pageIndex, Integer pageSize, Date startTime, Date endTime);
}

