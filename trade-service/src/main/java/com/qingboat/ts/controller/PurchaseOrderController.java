package com.qingboat.ts.controller;

import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.api.TierService;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import com.qingboat.ts.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public PurchaseOrderEntity createPurchaseOrder(@Valid @RequestBody OrderVo orderVo,
                                                   @RequestAttribute(name = "UID") Long uid
                                                   ){
        Long tierId = orderVo.getTierId();
        String periodKey = orderVo.getPeriodKey();





        log.info(" RequestParam: tierId=" +tierId);
        return purchaseOrderService.createPurchaseOrder(tierId, periodKey, uid);

    }
}
