package com.qingboat.api;

import com.qingboat.api.vo.MessageVo;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "article-service",configuration = {FeignDecoder.class})
public interface MessageService {

    @RequestMapping(value = "/msg/sendMessage" , method = RequestMethod.POST)
    void sendMessage(@RequestBody MessageVo messageVo);
}
