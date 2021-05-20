package com.qingboat.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wx-service",url = "${wx.host}")
public interface WxMessageService {

    @RequestMapping(value = "/cgi-bin/message/template/send" , method = RequestMethod.POST)
    Object sendMessage(@RequestParam("access_token") String accessToken, @RequestBody JSONObject body);


}
