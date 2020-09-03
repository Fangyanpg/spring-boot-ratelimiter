package com.fangyanpg.ratelimiter.aop;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;
import com.fangyanpg.ratelimiter.exception.RateLimiterException;
import com.fangyanpg.ratelimiter.limit.RedisRateLimiter;
import com.fangyanpg.ratelimiter.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
@Slf4j
public class RateLimitInterceptor implements MethodInterceptor {

    private final RedisRateLimiter redisRateLimiter;

    public RateLimitInterceptor(RedisRateLimiter redisRateLimiter){
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RateLimiter rateLimiter = invocation.getMethod().getAnnotation(RateLimiter.class);
        Method method = invocation.getMethod();
        //Parameter[] parameters = method.getParameters();
        StringBuilder rateKey = new StringBuilder(rateLimiter.prefix());
        if(LimitType.IP.equals(rateLimiter.type()) && !LimitMode.LOCK.equals(rateLimiter.mode())){
            String ip = WebUtils.getIP();
            rateKey.append(ip).append(":");
        }
        rateKey.append(method.getName());
        String key = rateKey.toString();
        Object proceed;
        if(redisRateLimiter.acquire(key, rateLimiter)){
            proceed = invocation.proceed();
        }else{
            throw new RateLimiterException(key);
        }
        redisRateLimiter.release(key, rateLimiter);
        return proceed;
    }
}
