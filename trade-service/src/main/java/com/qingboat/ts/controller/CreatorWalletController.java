package com.qingboat.ts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.CreatorBillService;
import com.qingboat.ts.service.CreatorWalletService;
import com.qingboat.ts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping(value = "/wallet")
public class CreatorWalletController extends BaseController {


    @Autowired
    private CreatorWalletService creatorWalletService;


    @GetMapping("/getCreatorBalance")
    @ResponseBody
    public CreatorWalletEntity getCreatorBalance(){
        // 返回余额
        CreatorWalletEntity entity = creatorWalletService.getCreatorWalletByCreatorId(getUId());
        CreatorWalletEntity resultEntity = new CreatorWalletEntity();
        if (entity==null){
            resultEntity.setBalance(0l);
            resultEntity.setIncome(0l);
            resultEntity.setCreatorId(getUId());
        }else {
            resultEntity.setBalance(entity.getBalance());
            resultEntity.setIncome(entity.getIncome());
            resultEntity.setCreatorId(entity.getCreatorId());
        }

        return resultEntity;
    }


}
