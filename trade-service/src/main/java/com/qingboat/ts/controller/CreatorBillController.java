package com.qingboat.ts.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.base.utils.DateUtil;
import com.qingboat.ts.entity.CreatorBillEntity;
import com.qingboat.ts.entity.CreatorWalletEntity;
import com.qingboat.ts.service.CreatorBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/bill")
public class CreatorBillController extends BaseController{

    @Autowired
    private CreatorBillService creatorBillService;

    /**
     * 内部调用
     * 添加bill记录,修改钱包金额
     */
    @PostMapping(value = "/createBillAndUpdateWallet")
    @ResponseBody
    public CreatorBillEntity userSubscription(@RequestBody CreatorBillEntity creatorBillEntity,
                                                   @RequestHeader("INNER-SEC") String innerSec,
                                                   @RequestHeader("UID") Long creatorId) {

        return null;

    }

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
    public IPage<CreatorBillEntity> getCreatorBillList(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                       @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                       @RequestParam(value = "startTime",required = false) String startTime,
                                                       @RequestParam(value = "endTime",required = false) String endTime ){

        return creatorBillService.getCreatorBillList(getUId(),pageIndex,pageSize,
                DateUtil.parseStrToDate(startTime,DateUtil.DATE_FORMAT_YYYY_MM_DD),
                DateUtil.parseStrToDate(endTime,DateUtil.DATE_FORMAT_YYYY_MM_DD));
    }
}
