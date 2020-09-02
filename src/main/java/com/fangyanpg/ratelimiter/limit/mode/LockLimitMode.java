package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 * @since 2020/9/2
 */
public class LockLimitMode extends CountLimitMode{

    @Override
    public String limitMode() {
        return LimitMode.LOCK;
    }

    @Override
    public String execute(RedisTemplate<String, String> redisTemplate, String key, RateLimiter rateLimiter) {
        String locked;
        for(;;){
            locked = super.execute(redisTemplate, key, rateLimiter);
            if (locked.equals("true")){

                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        return locked;
    }
}
