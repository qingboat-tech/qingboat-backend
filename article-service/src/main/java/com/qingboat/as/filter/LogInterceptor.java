package com.qingboat.as.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {


    private static final ThreadLocal<Long> START_TTIME_THREAD_LOCAL = new NamedThreadLocal<Long>("ThreadLocal StartTime");

    /**
     * 拦截的方法执行之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //1、开始时间
        long beginTime = System.currentTimeMillis();
        //线程绑定变量（该数据只有当前请求的线程可见）
        START_TTIME_THREAD_LOCAL.set(beginTime);
        return true;
    }

    /**
     * 拦截的方法执行过程中，返回model and view之前调用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            log.info("ViewName: " + modelAndView.getViewName());
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        // 打印JVM信息。
        //1. 得到线程绑定的局部变量（开始时间）
        long beginTime = START_TTIME_THREAD_LOCAL.get();
        START_TTIME_THREAD_LOCAL.remove();
        //2、结束时间
        long endTime = System.currentTimeMillis();
        Map<String, String[]> pm = request.getParameterMap();
        log.info("耗时:{}ms\tURI:{}\t请求参数:{}\t最大内存:{}m\t已分配内存:{}m\t已分配内存中的剩余空间:{}m\t最大可用内存:{}m",
                (endTime - beginTime), request.getRequestURI(), JSON.toJSON(pm),
                Runtime.getRuntime().maxMemory() / 1024 / 1024,
                Runtime.getRuntime().totalMemory() / 1024 / 1024,
                Runtime.getRuntime().freeMemory() / 1024 / 1024,
                (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024);

    }
}
