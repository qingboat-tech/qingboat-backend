package com.qingboat.us.service;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "provider-service" ,configuration = {FeignDecoder.class})
public interface ProviderService {

    @RequestMapping(value = "/ps/hi" , method = RequestMethod.GET)
    public Map hi();

    @RequestMapping(value = "/ps/hello" , method = RequestMethod.GET)
    public String hello();

}
