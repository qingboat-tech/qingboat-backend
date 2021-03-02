package com.qingboat.uc.controller;

import com.qingboat.base.api.ApiResponse;
import com.qingboat.uc.config.Configration;
import com.qingboat.uc.entity.UserEntity;
import com.qingboat.uc.service.ProviderService;
import com.qingboat.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Configration configration;

    @Autowired
    private ProviderService providerService;

    @GetMapping("/jerry")
    public Map jerry(){
        Map rst = providerService.hi();
        return rst;
    }

    @GetMapping("/hello")
    public String hello(){
        String rst = providerService.hello();
        return rst;
    }

    @GetMapping("/getUserById")
    @ResponseBody
    public UserEntity getUserById( @RequestParam(value = "userId") Long userId ){
        System.err.println( "==========  "+configration.getAppName() +"  " +configration.getPort());
        UserEntity user = userService.getUserById(userId);
        return user;
    }

}
