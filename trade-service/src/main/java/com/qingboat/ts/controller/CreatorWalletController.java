package com.qingboat.ts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.entity.OrderEntity;
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

    @GetMapping("/currentMonthIncome")
    @ResponseBody
    public Map<Object, Long> currentMonthIncome(){
        // 本月收入
        Map<Object, Long> data = new HashMap<>();
        data.put("currentMonthIncome",12000l);

        return data;
    }

    @GetMapping("/creatorBalance")
    @ResponseBody
    public CreatorWalletEntity creatorBalance(){
        // 返回余额
        CreatorWalletEntity entity = new CreatorWalletEntity();
        entity.setBalance(10000l);
        entity.setIncome(1000l);
        return entity;
    }

    @GetMapping("/creatorBillList")
    @ResponseBody
    public IPage<CreatorWalletEntity> creatorBillList(){
        return null;
    }


}
