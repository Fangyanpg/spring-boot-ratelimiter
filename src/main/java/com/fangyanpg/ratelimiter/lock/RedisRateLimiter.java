package com.fangyanpg.ratelimiter.lock;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
public class RedisRateLimiter {

    private static Map<LimitMode, DefaultRedisScript<String>> scriptMap;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean acquire(String key, RateLimiter rateLimiter){
        RedisScript<String> script = scriptMap.get(rateLimiter.mode());
        return Boolean.parseBoolean(redisTemplate.execute(script,
                redisTemplate.getStringSerializer(),
                redisTemplate.getStringSerializer(),
                Collections.singletonList(key), String.valueOf(rateLimiter.limit()), String.valueOf(rateLimiter.expire())));
    }

    @PostConstruct
    public void scriptInit(){
        scriptMap = new HashMap<>();
        DefaultRedisScript<String> count = new DefaultRedisScript<>();
        count.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/Count.lua")));
        scriptMap.put(LimitMode.COUNT, count);
        DefaultRedisScript<String> bucket = new DefaultRedisScript<>();
        bucket.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/TokenBucket.lua")));
        scriptMap.put(LimitMode.TOKEN_BUCKET, bucket);
    }

    class CountLimitMode{

    }

}
