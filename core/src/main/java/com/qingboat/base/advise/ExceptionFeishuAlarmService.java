package com.qingboat.base.advise;

import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class ExceptionFeishuAlarmService {

    @Value("${glob-exception.hook-key}")
    private String globExceptionHookKey;

    @Value("${biz-exception.hook-key}")
    private String bizExceptionHookKey;

    @Autowired
    private FeishuService feishuService;

    @Async
    public void alarmSystemException(Exception e){
        FeishuService.PostBody postBody = new FeishuService.PostBody();
        postBody.setTitle("系统异常");
        FeishuService.PostTextTag textTag = new FeishuService.PostTextTag();
        textTag.setText("用户Id:");
        FeishuService.PostLinkTag linkTag = new FeishuService.PostLinkTag();
        linkTag.setText(getUId()+"");
        linkTag.setHref("https://creator.qingboat.com");

        FeishuService.PostTextTag contentTag = new FeishuService.PostTextTag();
        contentTag.setText(e.toString());

        postBody.addContent(textTag,linkTag);
        postBody.addContent(contentTag);

        feishuService.sendTextMsg(globExceptionHookKey,postBody);
    }


    @Async
    public void alarmBizException(BaseException e){
        FeishuService.PostBody postBody = new FeishuService.PostBody();
        postBody.setTitle("业务异常");
        FeishuService.PostTextTag textTag = new FeishuService.PostTextTag();
        textTag.setText("用户Id:");
        FeishuService.PostLinkTag linkTag = new FeishuService.PostLinkTag();
        linkTag.setText(getUId()+"");
        linkTag.setHref("https://creator.qingboat.com");

        FeishuService.PostTextTag contentTag = new FeishuService.PostTextTag();
        contentTag.setText(e.toString());

        postBody.addContent(textTag,linkTag);
        postBody.addContent(contentTag);

        feishuService.sendTextMsg(bizExceptionHookKey,postBody);
    }

    protected   Object getAttribute(String s) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request.getAttribute(s);
        }
        return null;
    }

    protected Long getUId(){
        Long uid = (Long) getAttribute("UID");
        return uid;
    }


}
