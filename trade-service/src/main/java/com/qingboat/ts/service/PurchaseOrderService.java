package com.qingboat.ts.service;

import com.qingboat.ts.entity.PurchaseOrderEntity;

public interface PurchaseOrderService {

    PurchaseOrderEntity createPurchaseOrder(Long tierId, Long periodKey, Long uid);
}
