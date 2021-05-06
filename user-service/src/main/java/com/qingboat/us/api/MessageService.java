package com.qingboat.us.api;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "message-service",url = "${messageService.url}",configuration = {FeignDecoder.class})
public interface MessageService {

    @RequestMapping(value = "/ms/sendMessage" , method = RequestMethod.POST)
    public Map hi();
}
