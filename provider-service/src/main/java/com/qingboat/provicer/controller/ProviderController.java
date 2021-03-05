package com.qingboat.provicer.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProviderController {

    @GetMapping("/hello")
    public String hello(){
        return "hello provicer";
    }

    @GetMapping("/hi")
    public Map hi(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("name","david");
        map.put("age","40");
        return map;
    }
}
