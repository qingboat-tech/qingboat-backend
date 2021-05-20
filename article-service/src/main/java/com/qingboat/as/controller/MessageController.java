package com.qingboat.as.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.api.WxMessageService;
import com.qingboat.api.WxTokenService;
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

    @Autowired
    WxTokenService wxTokenService;

    @Autowired
    WxMessageService wxMessageService;


    @PostMapping(value = "/sendMessage")
    @ResponseBody
    public void sendMessage(@RequestBody MessageEntity msg){
        messageService.asyncSendMessage(msg);
    }

    /**
     * 根据消息类型，分页返回消息列表
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<MessageEntity> list(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                     @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                     @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.list(getUId(),msgType,pageIndex,pageSize);
    }

    /**
     * 根据消息类型，返回1条最新未读消息
     */
    @GetMapping(value = "/getLastUnReadMessage")
    @ResponseBody
    public MessageEntity getLastUnReadMessage( @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.getLastUnReadMessage(getUId(),msgType);
    }

    /**
     * 根据消息类型，返回未读消息数
     */
    @GetMapping(value = "/unReadCount")
    @ResponseBody
    public Integer unReadCount( @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.getUnreadMessageCount(getUId(),msgType);
    }



    @GetMapping(value = "/getToken")
    @ResponseBody
    public Object getToken( ) {
        String sec = AuthFilter.getSecret(getUIdStr());
        return wxTokenService.getWxUserToken(sec,getUIdStr());
    }

    @GetMapping(value = "/sendWxMsg")
    @ResponseBody
    public Object sendWxMsg( ) {
        String sec = AuthFilter.getSecret(getUIdStr());
        String token =  wxTokenService.getWxUserToken(sec,getUIdStr());
        JSONObject body = new JSONObject();
        JSONObject data = new JSONObject();
        body.put("touser","oLESe1ahBn6O2OvE97PKrSX7K1kA");
        body.put("template_id","Yb6vx1hu0iXh7KcvUueOco4d2l4jOey5rnb6LoIIFQo");
        body.put("url","https://m.qingboat.com");
        body.put("data",data);

        data.put("first", JSON.parse("{'value': '有用户订阅成功'}"));
        data.put("keyword1", JSON.parse("{'value': '有用户订阅成功'}"));
        data.put("keyword2", JSON.parse("{'value': '2021-10-10'}"));
        data.put("remark", JSON.parse("{'value': '你的知识合集已被人成功订阅，立即查看'}"));

        log.info(" TOKEN: " +token);
        log.info( " request: " +body);

        Object obj = wxMessageService.sendMessage(token,body);

        log.info( " response: " +obj);

        return null;

    }


}
