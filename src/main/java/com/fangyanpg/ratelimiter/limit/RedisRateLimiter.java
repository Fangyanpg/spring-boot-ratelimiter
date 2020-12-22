package com.fangyanpg.ratelimiter.limit;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
public class RedisRateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    private final LimitModeExecutor limitModeExecutor;

    public RedisRateLimiter(RedisTemplate<String, Object> redisTemplate, LimitModeExecutor limitModeExecutor) {
        this.redisTemplate = redisTemplate;
        this.limitModeExecutor = limitModeExecutor;
    }

    public boolean acquire(String key, RateLimiter rateLimiter){
        return Boolean.parseBoolean(limitModeExecutor.execute(redisTemplate, key, rateLimiter));
    }

    public void release(String key, RateLimiter rateLimiter){
        if(rateLimiter.mode().equals(LimitMode.LOCK)){
            redisTemplate.delete(key);
            limitModeExecutor.wake(rateLimiter);
        }
    }

}
