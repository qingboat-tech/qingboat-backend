package com.qingboat.as.api;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "token-service",url = "${python.host}",configuration = {FeignDecoder.class})
public interface WxTokenService {

    @RequestMapping(value = "/api/global_access_token/" , method = RequestMethod.GET)
    String getWxUserToken(@RequestHeader("INNER-SEC") String innerSec, @RequestHeader("userId") String userId);

}
