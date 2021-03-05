package com.qingboat.as.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello")
public class ArticleHelloController {

    @RequestMapping("/")
    public String hello(){
        return "hello world";
    }
}
