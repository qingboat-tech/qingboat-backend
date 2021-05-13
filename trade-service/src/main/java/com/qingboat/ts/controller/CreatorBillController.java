package com.qingboat.ts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/bill")
public class CreatorBillController extends BaseController{

    @Autowired
    private CreatorBillService creatorBillService;

    @GetMapping("/getCurrentMonthIncome")
    @ResponseBody
    public Map<Object, Long> getCurrentMonthIncome(){
        // 本月收入
        Long currentMonthIncome = creatorBillService.getCurrentMonthIncome(getUId());
        Map<Object, Long> data = new HashMap<>();
        data.put("currentMonthIncome",currentMonthIncome);

        return data;
    }

    @GetMapping("/getCreatorBillList")
    @ResponseBody
    public IPage<CreatorWalletEntity> getCreatorBillList(){




        return null;
    }
}
