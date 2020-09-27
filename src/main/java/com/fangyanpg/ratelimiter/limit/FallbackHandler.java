package com.fangyanpg.ratelimiter.limit;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.limit.fallback.AbstractFallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyanpeng
 * @since 2020/9/25
 */
@Slf4j
public class FallbackHandler implements BeanPostProcessor {

    private final Map<String, AbstractFallbackHandler> fallbackHandlerMap;

    public FallbackHandler(){
        fallbackHandlerMap = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AbstractFallbackHandler){
            fallbackHandlerMap.put(bean.getClass().toString(), (AbstractFallbackHandler) bean);
        }
        return bean;
    }

    public Object fallback(String method, String param, RateLimiter rateLimiter){
        String className = rateLimiter.fallback().toString();
        AbstractFallbackHandler handler = fallbackHandlerMap.get(className);
        if(handler == null){
            log.warn("no fallback handler instance to execute for class: {}", className);
            throw new NullPointerException("no fallback handler instance");
        }
        return handler.fallback(method, param);
    }
}
