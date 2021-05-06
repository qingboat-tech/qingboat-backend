package com.qingboat.ts.controller;

import com.qingboat.ts.entity.OrderEntity;
import com.qingboat.ts.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@Slf4j
@RequestMapping(value = "/account")
public class CreatorAccountController extends BaseController {

    @GetMapping("/monthlyIncome")
    @ResponseBody
    public Long monthlyIncome(){

        return 12000l;
    }




}
