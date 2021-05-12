package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.api.WxService;
import com.qingboat.as.entity.MessageEntity;
import com.qingboat.as.filter.AuthFilter;
import com.qingboat.as.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/msg")
@Slf4j
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;


    @PostMapping(value = "/sendMessage")
    @ResponseBody
    public void sendMessage(@RequestBody MessageEntity msg){
        messageService.asyncSendMessage(msg);
    }

    // reply列表
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<MessageEntity> list(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                     @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                     @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.list(getUId(),msgType,pageIndex,pageSize);
    }

    // reply列表
    @GetMapping(value = "/getLastUnReadMessage")
    @ResponseBody
    public MessageEntity getLastUnReadMessage( @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.getLastUnReadMessage(getUId(),msgType);
    }

    // reply列表
    @GetMapping(value = "/unReadCount")
    @ResponseBody
    public Integer unReadCount( @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.getUnreadMessageCount(getUId(),msgType);
    }


    @Autowired
    WxService wxService;

    @GetMapping(value = "/getToken")
    @ResponseBody
    public Object getToken( ) {
        String userId = getUIdStr();
        String sec = AuthFilter.getSecret(userId);
        Object token =   wxService.getWxUserToken(sec,userId);
        System.err.println(" token :" +token);
        return token== null?null: token.toString();
    }

}
