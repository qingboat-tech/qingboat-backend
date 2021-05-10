package com.qingboat.ts.api;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-service",url = "${user-service.url}", configuration = {FeignDecoder.class})
public interface UserService {

    @RequestMapping(value = "/getUserProfile/" , method = RequestMethod.GET)
    UserServiceResponse getUserProfile(@RequestParam(value = "userId",required = true) Long userId);

}
