package com.qingboat.ts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingboat.ts.dao.PurchaseOrderDao;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderDao, PurchaseOrderEntity> implements PurchaseOrderService {


}
