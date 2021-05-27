package com.qingboat.api;

import com.qingboat.api.vo.CreatorBillVo;
import com.qingboat.base.config.FeignDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "trade-service",configuration = {FeignDecoder.class})
public interface TradeService {

    @RequestMapping(value = "/ts/bill/createBillAndUpdateWallet/" , method = RequestMethod.POST)
    Object createBillAndUpdateWallet(@RequestBody CreatorBillVo creatorBillVo,
                                     @RequestHeader("INNER-SEC") String innerSec,
                                     @RequestHeader("UID") String creatorId);

}
