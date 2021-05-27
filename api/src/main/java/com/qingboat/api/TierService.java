package com.qingboat.api;

import com.qingboat.api.vo.TierVo;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "article-service", configuration = {FeignDecoder.class})
public interface TierService {

    @RequestMapping(value = "/as/readersubscription/tier" , method = RequestMethod.GET)
    TierVo getTierById(@RequestParam(value = "tierId",required = true) Long tierId);


    @RequestMapping(value="/as/creatorsubscription/getTierSubscritionCount", method = RequestMethod.GET)
    Long getTierSubscritionCount(@RequestParam("tierId") Long tierId);

}
