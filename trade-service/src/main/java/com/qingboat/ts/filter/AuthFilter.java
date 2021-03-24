package com.qingboat.ts.filter;

import com.alibaba.fastjson.JSON;
import com.qingboat.base.api.ApiResponse;
import com.qingboat.ts.entity.AuthToken;
import org.springframework.util.DigestUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthFilter implements Filter {

    private final String SEC_KEY = "UJU2@#9kDJIVVWJ";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token  = request.getHeader("Authorization");
        String innerSec = request.getHeader("INNER_SEC");
        String userId = request.getHeader("UID");

        if (userId !=null && innerSec != null){
            String md5 = DigestUtils.md5DigestAsHex((SEC_KEY + userId).getBytes());
            if (md5.equals(innerSec)){
                AuthToken authToken = new AuthToken();
                authToken.setUserId(Long.parseLong(userId));
                request.setAttribute("USER_AUTHONTOKEN" ,authToken);
                request.setAttribute("UID" ,Long.parseLong(userId));

            }
        }
        if (token !=null && token.indexOf(" ")>0 ){
            AuthToken authToken = new AuthToken();
            token = token.substring(token.lastIndexOf(" ")+1);
            authToken = authToken.selectById(token);

            if (authToken !=null && authToken.getUserId()!=null){
                request.setAttribute("USER_AUTHONTOKEN" ,authToken);
                request.setAttribute("UID" ,authToken.getUserId());

            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
