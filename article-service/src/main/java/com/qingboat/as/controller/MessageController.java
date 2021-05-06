package com.qingboat.as.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qingboat.as.entity.ArticleCommentEntity;
import com.qingboat.as.entity.MessageEntity;
import com.qingboat.as.service.MessageService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/msg")
@Slf4j
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;
    // reply列表
    @GetMapping(value = "/list")
    @ResponseBody
    public IPage<MessageEntity> list(@RequestParam("pageIndex") int pageIndex, @RequestParam(value = "msgType",required = false) Integer msgType) {
        return messageService.list(getUId(),msgType,pageIndex);
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

}
