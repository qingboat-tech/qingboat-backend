package com.qingboat.api;

import com.qingboat.api.vo.TierVo;
import com.qingboat.api.vo.UserSubscriptionVo;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "article-service", configuration = {FeignDecoder.class})
public interface UserSubscriptionService {

    @RequestMapping(value = "/as/readersubscription/subscribe" , method = RequestMethod.POST)
    UserSubscriptionVo userSubscription(@RequestBody UserSubscriptionVo userSubscriptionEntity,
                            @RequestHeader("INNER-SEC") String innerSec,
                            @RequestHeader("UID") Long subscriberId);

}