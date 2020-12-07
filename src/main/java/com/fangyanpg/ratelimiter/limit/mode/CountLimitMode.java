package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

/**
 * @author fangyanpeng
 * @since 2020/8/24
 */
public class CountLimitMode extends AbstractLimitMode{

    @Override
    public String limitMode() {
        return LimitMode.COUNT;
    }

    @Override
    public String scriptPath() {
        return "script/Count.lua";
    }

    @Override
    public String execute(RedisTemplate<String, Object> redisTemplate, String key, RateLimiter rateLimiter) {
        return redisTemplate.execute(script,
                redisTemplate.getStringSerializer(),
                redisTemplate.getStringSerializer(),
                Collections.singletonList(key), String.valueOf(rateLimiter.limit()), String.valueOf(rateLimiter.expire()));
    }
}
