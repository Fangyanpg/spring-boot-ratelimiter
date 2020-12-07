package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.annotation.PostConstruct;

/**
 * @author fangyanpeng
 * @since 2020/8/24
 */
public abstract class AbstractLimitMode {

    public String limitMode;

    protected DefaultRedisScript<String> script;

    @PostConstruct
    protected void init(){
        limitMode = limitMode();
        script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPath())));
        script.setResultType(String.class);
    }

    public abstract String limitMode();

    public abstract String scriptPath();

    public abstract String execute(RedisTemplate<String, Object> redisTemplate,
                            String key, RateLimiter rateLimiter);
}
