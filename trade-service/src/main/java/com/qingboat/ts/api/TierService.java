package com.qingboat.ts.api;

import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "tier-service",url = "${article-service.url}", configuration = {FeignDecoder.class})
public interface TierService {

    @RequestMapping(value = "/readersubscription/tier" , method = RequestMethod.GET)
    TierServiceResponse getTierById(@RequestParam(value = "tierId",required = true) Long tierId);

}
