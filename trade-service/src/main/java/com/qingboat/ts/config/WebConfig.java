package com.qingboat.ts.config;

import com.qingboat.ts.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new AuthFilter());
        //过滤所有路径
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("authFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
