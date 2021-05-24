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
        FeishuService.TextBody textBody = new FeishuService.TextBody("[系统异常]\n 用户ID：" +getUId()+"\n"+ e.toString());
        feishuService.sendTextMsg(globExceptionHookKey,textBody);
    }


    @Async
    public void alarmBizException(BaseException e){
        FeishuService.TextBody textBody = new FeishuService.TextBody("[业务异常]\n 用户ID：" +getUId()+"\n"+ e.toString());
        feishuService.sendTextMsg(bizExceptionHookKey,textBody);
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
