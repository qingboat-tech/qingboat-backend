package com.qingboat.as.filter;

import com.qingboat.as.entity.AuthTokenEntity;
import org.springframework.util.DigestUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
                AuthTokenEntity authTokenEntity = new AuthTokenEntity();
                authTokenEntity.setUserId(Long.parseLong(userId));
                request.setAttribute("USER_AUTHONTOKEN" , authTokenEntity);
                request.setAttribute("UID" ,Long.parseLong(userId));

            }
        }
        if (token !=null && token.indexOf(" ")>0 ){
            AuthTokenEntity authTokenEntity = new AuthTokenEntity();
            token = token.substring(token.lastIndexOf(" ")+1);
            authTokenEntity = authTokenEntity.selectById(token);

            if (authTokenEntity !=null && authTokenEntity.getUserId()!=null){
                request.setAttribute("USER_AUTHONTOKEN" , authTokenEntity);
                request.setAttribute("UID" , authTokenEntity.getUserId());

            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
