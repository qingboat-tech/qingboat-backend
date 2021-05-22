package com.qingboat.us.controller;

import com.qingboat.base.exception.BaseException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected Long getUId(){
        Long uid = (Long) getAttribute("UID");
        if (uid == null){
            throw new BaseException(401,"AUTH_ERROR");
        }
        return uid;
    }
    protected String getUIdStr(){
        return String.valueOf(getUId());
    }


    protected   Object getAttribute(String s) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request.getAttribute(s);
        }
        return null;
    }

}
