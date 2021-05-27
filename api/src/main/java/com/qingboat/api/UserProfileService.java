package com.qingboat.api;

import com.qingboat.api.vo.UserProfileVo;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = {FeignDecoder.class})
public interface UserProfileService {

    @RequestMapping(value = "/us/getUserProfile/" , method = RequestMethod.GET)
    UserProfileVo getUserProfile(@RequestParam(value = "userId",required = true) Long userId);

}
