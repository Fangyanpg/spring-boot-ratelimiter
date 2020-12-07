package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.limit.mode.AbstractLimitMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

/**
 * @author fangyanpeng
 * @since 2020/8/24
 */
public class TokenBucketLimitMode extends AbstractLimitMode {

    int permits = 1;

    @Override
    public String limitMode() {
        return LimitMode.TOKEN_BUCKET;
    }

    @Override
    public String scriptPath() {
        return "script/TokenBucket.lua";
    }

    @Override
    public String execute(RedisTemplate<String, Object> redisTemplate, String key, RateLimiter rateLimiter) {
        int rate = rateLimiter.limit() / rateLimiter.expire();
        rate = rate > 0 ? rate : 100;
        return redisTemplate.execute(script,
                redisTemplate.getStringSerializer(),
                redisTemplate.getStringSerializer(),
                Collections.singletonList(key), String.valueOf(permits), String.valueOf(System.currentTimeMillis()),
                String.valueOf(rateLimiter.limit()), String.valueOf(rate));
    }

}
