package com.qingboat.base.api;

import lombok.Getter;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@FeignClient(name = "FeishuService",url = "https://open.feishu.cn/open-apis/bot/v2/hook")
public interface FeishuService {


    @RequestMapping(value = "/{hookId}", method = RequestMethod.POST, headers = {"Content-Type=application/json"})
    Map<String,Object> sendTextMsg(@RequestParam("hookId") String hookId, @RequestBody TextBody textBody );


    class TextBody{
        @Getter
        private  String msg_type ="text";

        @Getter
        private Map<String,String> content = new HashMap<>();

        public TextBody(String text){
            this.content.put("text",text);
        }

    }

}
