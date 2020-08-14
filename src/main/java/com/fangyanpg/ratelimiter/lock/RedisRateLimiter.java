package com.fangyanpg.ratelimiter.lock;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
public class RedisRateLimiter {

    private static Map<LimitMode, RedisScript<String>> scriptMap;
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
        scriptMap.put(LimitMode.COUNT, new DefaultRedisScript<>("local count = tonumber(redis.call('incr', KEYS[1])) local ttl = redis.call('ttl', KEYS[1]) if(count)==1 then redis.call('expire', KEYS[1], tonumber(ARGV[2])) elseif ttl==-1 then redis.call('expire', KEYS[1], tonumber(ARGV[2])) end if(count>tonumber(ARGV[1])) then return 'false' end return 'true'", String.class));
        scriptMap.put(LimitMode.TOKEN_BUCKET, new DefaultRedisScript<>("22", String.class));
    }

}
