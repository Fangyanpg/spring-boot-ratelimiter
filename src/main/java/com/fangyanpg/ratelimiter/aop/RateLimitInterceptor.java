package com.fangyanpg.ratelimiter.aop;

import cn.hutool.json.JSONUtil;
import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;
import com.fangyanpg.ratelimiter.limit.FallbackHandler;
import com.fangyanpg.ratelimiter.limit.RedisRateLimiter;
import com.fangyanpg.ratelimiter.limit.support.LimitKeyGenerator;
import com.fangyanpg.ratelimiter.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
@Slf4j
public class RateLimitInterceptor implements MethodInterceptor {

    private final RedisRateLimiter redisRateLimiter;
    private final FallbackHandler fallbackHandler;
    private final LimitKeyGenerator limitKeyGenerator;

    public RateLimitInterceptor(RedisRateLimiter redisRateLimiter, FallbackHandler fallbackHandler,
                                LimitKeyGenerator limitKeyGenerator){
        this.redisRateLimiter = redisRateLimiter;
        this.fallbackHandler = fallbackHandler;
        this.limitKeyGenerator = limitKeyGenerator;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RateLimiter rateLimiter = invocation.getMethod().getAnnotation(RateLimiter.class);
        Method method = invocation.getMethod();

        String key = limitKeyGenerator.generate(invocation, rateLimiter);

        Object proceed;

        if(redisRateLimiter.acquire(key, rateLimiter)){
            // 放行
            proceed = invocation.proceed();
            // 释放锁
            redisRateLimiter.release(key, rateLimiter);
        }else{
            // 降级处理
            proceed = fallbackHandler.fallback(method.getName(),
                    JSONUtil.toJsonStr(getParam(method.getParameters(), invocation.getArguments())),
                    rateLimiter);
        }

        return proceed;
    }

    private Map<String, Object> getParam(Parameter[] parameters, Object[] arguments){
        HashMap<String, Object> param = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            param.put(parameters[i].getName(), arguments[i]);
        }
        return param;
    }
}
