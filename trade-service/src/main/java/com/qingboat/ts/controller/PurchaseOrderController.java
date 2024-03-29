package com.qingboat.ts.controller;

import com.qingboat.api.TierService;
import com.qingboat.api.vo.TierVo;
import com.qingboat.base.exception.BaseException;
import com.qingboat.ts.entity.PurchaseOrderEntity;
import com.qingboat.ts.service.PurchaseOrderService;
import com.qingboat.ts.utils.RedisUtil;
import com.qingboat.ts.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(value = "/purchaseOrder")
public class PurchaseOrderController extends BaseController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private TierService tierService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/create")
    @ResponseBody
    public PurchaseOrderEntity createPurchaseOrder(@Valid @RequestBody OrderVo orderVo,
                                                   @RequestAttribute(name = "UID") Long uid
                                                   ){
        Long tierId = orderVo.getTierId();
        String periodKey = orderVo.getPeriodKey();

        TierVo tierVo = tierService.getTierById(tierId);
        if ( tierVo.getSubscribeLimit()>0) {
            try {
                boolean lockFlag = redisUtil.synLock("tier_"+tierVo.getId());
                if (lockFlag){
                    if (tierService.getTierSubscritionCount(tierId) >= tierVo.getSubscribeLimit()){
                        throw new BaseException(500,"SUBSCRIBE LIMIT ERROR");
                    }else {
                        return purchaseOrderService.createPurchaseOrder(tierId, periodKey, uid);
                    }
                }
            }finally {
                redisUtil.unLock("tier_"+tierVo.getId());
            }
        }
        return purchaseOrderService.createPurchaseOrder(tierId, periodKey, uid);
    }
}
