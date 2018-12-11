package com.dili.ss.boot;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class BaseInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //避免swagger配置不对，报错,handler对象可能是org.springframework.web.servlet.handler.AbstractHandlerMapping$PreFlightHandler
        if(!(handler instanceof HandlerMethod)) return true;
        // 提交表单token 校验
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(method == null) {
            System.out.println("非法请求");
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if(!(handler instanceof HandlerMethod)) return;
        // 刷新token
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(method == null) {
            System.out.println("非法请求");
        }else{
            System.out.println("正常请求");
        }
    }
}
