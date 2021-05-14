package com.qingboat.us.api;

import com.qingboat.base.config.FeignDecoder;
import com.qingboat.us.api.vo.MessageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "message-service",url = "${article-service.host}",configuration = {FeignDecoder.class})
public interface MessageService {

    @RequestMapping(value = "/msg/sendMessage" , method = RequestMethod.POST)
    void sendMessage(@RequestBody MessageVo messageVo);
}
