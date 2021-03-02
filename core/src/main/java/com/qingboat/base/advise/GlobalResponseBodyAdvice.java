package com.qingboat.base.advise;

import com.alibaba.fastjson.JSON;
import com.qingboat.base.api.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice(annotations = RestController.class)
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        ServletRequestAttributes sr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sr.getRequest();
        //这里是往request获取一个参数，这个参数标识是否需要统一返回格式，　　
        //设置参数的过程就是写了一个拦截器，判断有无@ResponseResult注解，如果有存入这个标识参数

        Boolean wrapper = (Boolean) request.getAttribute("RESPONSE_RESULT_WRAPPER");
        request.removeAttribute("RESPONSE_RESULT_WRAPPER");
        return wrapper == null ? true:wrapper;

    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 防止二次封装

        if (body instanceof ApiResponse) {
            return body;
        }
        if (body instanceof String){
            return body;
        }
        if (aClass == Jaxb2RootElementHttpMessageConverter.class) {
            return body;
        }

        if (aClass == StringHttpMessageConverter.class) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setData(body);
            return JSON.toJSON(apiResponse);
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(body);
        return apiResponse;


    }
}
