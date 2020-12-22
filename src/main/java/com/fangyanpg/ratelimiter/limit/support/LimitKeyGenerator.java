package com.fangyanpg.ratelimiter.limit.support;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;
import com.fangyanpg.ratelimiter.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author fangyanpeng
 * @since 2020/9/27
 */
@Slf4j
public class LimitKeyGenerator {

    //private final HashSet<String> limitKeySet = new HashSet<>();

    public String generate(MethodInvocation invocation, RateLimiter rateLimiter){

        String methodName = invocation.getMethod().getName();
        return generate(invocation, methodName, rateLimiter);

        /*Class<?> aClass = invocation.getThis().getClass();
        String uniqueKey = aClass.toString() + methodName + rateLimiter.prefix();
        if(limitKeySet.add(uniqueKey)){
            return generate(invocation, methodName, rateLimiter);
        }
        log.warn("limitKey重复: uniqueKey:{}, 请尝试更改RateLimiter中prefix的值", uniqueKey);
        throw new DuplicateKeyException("limitKey重复");*/
    }

    public String generate(MethodInvocation invocation, String methodName, RateLimiter rateLimiter){
        StringBuilder rateKey = new StringBuilder(rateLimiter.prefix());
        if(LimitType.IP.equals(rateLimiter.type()) && !LimitMode.LOCK.equals(rateLimiter.mode())){
            String ip = WebUtils.getIP();
            rateKey.append(ip).append(":");
        }
        rateKey.append(methodName);
        if(rateLimiter.key().length > 0){
            Object[] arguments = invocation.getArguments();
            for (int i : rateLimiter.key()) {
                rateKey.append("&").append(arguments[i]);
            }
        }
        return rateKey.toString();
    }
}
