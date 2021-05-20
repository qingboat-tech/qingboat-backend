package com.qingboat.as.api;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service",url = "${user-service.host}", configuration = {FeignDecoder.class})
public interface UserProfileService {

    @RequestMapping(value = "/getUserProfile/" , method = RequestMethod.GET)
    UserProfileServiceResponse getUserProfile(@RequestParam(value = "userId",required = true) Long userId);

}
