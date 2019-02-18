package com.dili.ss.servlet.aop;

import com.dili.ss.service.IdempotentTokenService;
import com.dili.ss.servlet.annotation.Idempotent;
import com.dili.ss.servlet.annotation.Token;
import com.dili.ss.util.RedisDistributedLock;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 幂等切面
 */
@Component
@Aspect
public class IdempotentAspect {
    @Autowired
    IdempotentTokenService idempotentTokenService;

    @Autowired
    RedisDistributedLock redisDistributedLock;

    public static final String TOKEN_VALUE = "token_value";

    /**
     * 设置token
     * @param point
     * @return
     * @throws Throwable
     */
    @Around( "@annotation(com.dili.ss.servlet.annotation.Token)")
    public Object token(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        Token token = currentMethod.getAnnotation(Token.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(StringUtils.isNotBlank(token.value())){
            request.setAttribute(TOKEN_VALUE, idempotentTokenService.getToken(token.value()).getValue());
        }else if(StringUtils.isNotBlank(token.url())){
            request.setAttribute(TOKEN_VALUE, idempotentTokenService.getToken(token.url()).getValue());
        }else{
            //value和url都为空，不进入页面
            return false;
        }
        return point.proceed();
    }

    /**
     * 幂等验证
     * @param point
     * @return
     * @throws Throwable
     */
    @Around( "@annotation(com.dili.ss.servlet.annotation.Idempotent)")
    public Object idempotent(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        Idempotent idempotent = currentMethod.getAnnotation(Idempotent.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String type = StringUtils.isBlank(idempotent.value()) ? idempotent.type() : idempotent.value();
        String tokenValue = type.equals(Idempotent.HEADER) ? request.getHeader(TOKEN_VALUE) : request.getParameter(TOKEN_VALUE);
        //当大量高并发下所有带token参数的请求进来时，进行分布式锁定,允许某一台服务器的一个线程进入，锁定时间3分钟
        if (redisDistributedLock.tryGetLock(tokenValue,tokenValue,180L)) {
            if (redisDistributedLock.exists(request.getRequestURI() + tokenValue)) {
                //当请求的url与token与redis中的存储相同时
                if (redisDistributedLock.get(request.getRequestURI() + tokenValue).equals(tokenValue)) {
                    //放行的该线程删除redis中存储的token
                    redisDistributedLock.remove(request.getRequestURI() + tokenValue);
                    //放行
                    try {
                        return point.proceed();
                    }finally {
                        //完成后释放锁
                        if (redisDistributedLock.exists(tokenValue)) {
                            redisDistributedLock.releaseLock(tokenValue, tokenValue);
                        }
                    }
                }
            }
            //当请求的url与token与redis中的存储不相同时，解除锁定
            redisDistributedLock.releaseLock(tokenValue, tokenValue);
        }
        //进行拦截
        return false;
    }

}
