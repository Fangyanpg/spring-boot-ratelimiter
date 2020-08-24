package com.fangyanpg.ratelimiter.config;

import com.fangyanpg.ratelimiter.aop.RateLimitAnnotationAdvisor;
import com.fangyanpg.ratelimiter.aop.RateLimitInterceptor;
import com.fangyanpg.ratelimiter.limit.RedisRateLimiter;
import com.fangyanpg.ratelimiter.limit.mode.CountLimitMode;
import com.fangyanpg.ratelimiter.limit.mode.TokenBucketLimitMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
@Configuration
public class RateLimitConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedisTemplate.class)
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter();
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimitInterceptor rateLimitInterceptor(RedisRateLimiter redisRateLimiter){
        return new RateLimitInterceptor(redisRateLimiter);
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimitAnnotationAdvisor rateLimitAnnotationAdvisor(RateLimitInterceptor rateLimitInterceptor){
        return new RateLimitAnnotationAdvisor(rateLimitInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public CountLimitMode countLimitMode(){
        return new CountLimitMode();
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenBucketLimitMode tokenBucketLimitMode(){
        return new TokenBucketLimitMode();
    }



}
