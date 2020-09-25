package com.fangyanpg.ratelimiter.aop;

import cn.hutool.json.JSONUtil;
import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;
import com.fangyanpg.ratelimiter.limit.FallbackHandler;
import com.fangyanpg.ratelimiter.limit.RedisRateLimiter;
import com.fangyanpg.ratelimiter.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
@Slf4j
public class RateLimitInterceptor implements MethodInterceptor {

    private final RedisRateLimiter redisRateLimiter;
    private final FallbackHandler fallbackHandler;

    public RateLimitInterceptor(RedisRateLimiter redisRateLimiter, FallbackHandler fallbackHandler){
        this.redisRateLimiter = redisRateLimiter;
        this.fallbackHandler = fallbackHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RateLimiter rateLimiter = invocation.getMethod().getAnnotation(RateLimiter.class);
        Method method = invocation.getMethod();
        Parameter[] parameters = method.getParameters();
        StringBuilder rateKey = new StringBuilder(rateLimiter.prefix());
        if(LimitType.IP.equals(rateLimiter.type()) && !LimitMode.LOCK.equals(rateLimiter.mode())){
            String ip = WebUtils.getIP();
            rateKey.append(ip).append(":");
        }
        rateKey.append(method.getName());
        String key = rateKey.toString();
        Object proceed;
        if(redisRateLimiter.acquire(key, rateLimiter)){
            // 放行
            proceed = invocation.proceed();
            // 释放锁
            redisRateLimiter.release(key, rateLimiter);
        }else{
            // 降级处理
            proceed = fallbackHandler.fallback(JSONUtil.toJsonStr(parameters), rateLimiter);
        }

        return proceed;
    }
}
