package com.qingboat.as.api;

import com.alibaba.fastjson.JSONObject;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "wx-service",url = "${wx.host}")
public interface WxMessageService {

    @RequestMapping(value = "/cgi-bin/message/template/send" , method = RequestMethod.POST)
    Object sendMessage(@RequestParam("access_token") String accessToken, @RequestBody JSONObject body);


}
