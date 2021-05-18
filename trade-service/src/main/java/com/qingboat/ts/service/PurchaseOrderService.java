package com.qingboat.ts.service;

import com.qingboat.ts.entity.PurchaseOrderEntity;

public interface PurchaseOrderService {

    /**
     *  创建订单
     * @param tierId  创作者套餐Id
     * @param periodKey 套餐类型 month、year
     * @param uid  订阅者UserId
     * @return
     */
    PurchaseOrderEntity createPurchaseOrder(Long tierId, String periodKey, Long uid);
}
