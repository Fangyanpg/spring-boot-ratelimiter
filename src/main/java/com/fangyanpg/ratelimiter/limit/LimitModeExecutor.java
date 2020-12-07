package com.fangyanpg.ratelimiter.limit;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.limit.mode.AbstractLimitMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyanpeng
 * @since 2020/8/24
 */
public class LimitModeExecutor implements BeanPostProcessor {

    private final Map<String, AbstractLimitMode> limitModeMap;

    public LimitModeExecutor(){
        limitModeMap = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AbstractLimitMode){
            AbstractLimitMode limitMode = (AbstractLimitMode) bean;
            limitModeMap.put(limitMode.limitMode, limitMode);
        }
        return bean;
    }

    public String execute(RedisTemplate<String, Object> redisTemplate, String key, RateLimiter rateLimiter) {

        return limitModeMap.get(rateLimiter.mode()).execute(redisTemplate, key, rateLimiter);
    }
}
