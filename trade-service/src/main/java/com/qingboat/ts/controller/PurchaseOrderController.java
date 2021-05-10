package com.qingboat.ts.controller;

import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.api.TierService;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/purchaseOrder")
public class PurchaseOrderController extends BaseController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private TierService tierService;

    @PostMapping("/create")
    @ResponseBody
    public PurchaseOrderEntity createPurchaseOrder(@RequestBody Map<String,Long> param ,
                                                   @RequestAttribute(name = "UID") Long uid
                                                   ){
        Long tierId = param.get("tierId");
        Long periodKey = param.get("periodKey");

        log.info(" RequestParam: tierId=" +tierId);
        return purchaseOrderService.createPurchaseOrder(tierId, periodKey, uid);

    }
}
