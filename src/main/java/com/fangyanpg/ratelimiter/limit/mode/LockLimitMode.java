package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 * @since 2020/9/2
 */
public class LockLimitMode extends AbstractLimitMode{

    @Override
    public String scriptPath() {
        return "script/Count.lua";
    }

    @Override
    public String limitMode() {
        return LimitMode.LOCK;
    }


    @Override
    public String execute(RedisTemplate<String, Object> redisTemplate, String key, RateLimiter rateLimiter) {
        String locked;
        long waitTime = 100L;
        long timeout = rateLimiter.timeout() << 10;
        long begin = System.currentTimeMillis();
        for(;;){

            locked = redisTemplate.execute(script,
                    redisTemplate.getStringSerializer(),
                    redisTemplate.getStringSerializer(),
                    Collections.singletonList(key), String.valueOf(1), String.valueOf(rateLimiter.timeout()));

            if (locked.equals("true") || System.currentTimeMillis() - begin >= timeout){
                return locked;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}
