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
import java.io.PrintWriter;
import java.io.StringWriter;

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
        FeishuService.PostBody postBody = create(e);
        postBody.setTitle("系统异常");
        feishuService.sendTextMsg(globExceptionHookKey,postBody);
    }


    @Async
    public void alarmBizException(BaseException e){
        FeishuService.PostBody postBody = create(e);
        postBody.setTitle("业务异常");
        feishuService.sendTextMsg(bizExceptionHookKey,postBody);
    }


    private FeishuService.PostBody create(Exception e){
        FeishuService.PostBody postBody = new FeishuService.PostBody();
        postBody.setTitle("异常标题");
        FeishuService.PostTextTag textTag = new FeishuService.PostTextTag();
        textTag.setText("用户Id:");
        FeishuService.PostLinkTag linkTag = new FeishuService.PostLinkTag();
        linkTag.setText(getUId()+"");
        linkTag.setHref("https://hypper.cn");

        FeishuService.PostTextTag textTag1 = new FeishuService.PostTextTag();
        textTag1.setText("请求路径:" +getRequestURI());

        FeishuService.PostTextTag contentTag = new FeishuService.PostTextTag();
        contentTag.setText(getTrace(e));

        postBody.addContent(textTag,linkTag);
        postBody.addContent(textTag1);
        postBody.addContent(contentTag);

        return postBody;
    }

    private String getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }


    protected HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            return  ((ServletRequestAttributes)requestAttributes).getRequest();
        }
        return null;
    }

    protected String getRequestURI(){
        HttpServletRequest request =getRequest();
        if(request !=null){
            return request.getRequestURI();
        }
        return null;
    }

    protected   Object getAttribute(String s) {
        HttpServletRequest request =getRequest();
        if(request !=null){
            return request.getAttribute(s);
        }
        return null;
    }

    protected Long getUId(){
        Long uid = (Long) getAttribute("UID");
        return uid;
    }


}
