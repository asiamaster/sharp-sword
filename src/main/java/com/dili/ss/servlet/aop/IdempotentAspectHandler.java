package com.dili.ss.servlet.aop;

import com.dili.ss.service.IdempotentTokenService;
import com.dili.ss.util.RedisDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author asiam
 */
public interface IdempotentAspectHandler {

    Object aroundIdempotent(ProceedingJoinPoint point, RedisDistributedLock redisDistributedLock) throws Throwable;

    Object aroundToken(ProceedingJoinPoint point, IdempotentTokenService idempotentTokenService) throws Throwable;
}
