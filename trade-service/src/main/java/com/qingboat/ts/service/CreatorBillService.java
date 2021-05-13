package com.qingboat.ts.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;

import java.util.Map;

public interface CreatorBillService {

    /**
     * 查询创作者收益
     * @param creatorId
     * @return
     */
     Long currentMonthIncome(Long creatorId);

    /**
     * 查询创作者账单信息
     * @param creaatorId
     * @return
     */
    IPage<CreatorBillEntity> getCreatorBillList(Long creaatorId);
}

