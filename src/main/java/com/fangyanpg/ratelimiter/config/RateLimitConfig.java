package com.fangyanpg.ratelimiter.config;

import com.fangyanpg.ratelimiter.aop.RateLimitAnnotationAdvisor;
import com.fangyanpg.ratelimiter.aop.RateLimitInterceptor;
import com.fangyanpg.ratelimiter.bloom.BloomFilterHelper;
import com.fangyanpg.ratelimiter.bloom.RedisBloomFilter;
import com.fangyanpg.ratelimiter.limit.FallbackHandler;
import com.fangyanpg.ratelimiter.limit.LimitModeExecutor;
import com.fangyanpg.ratelimiter.limit.RedisRateLimiter;
import com.fangyanpg.ratelimiter.limit.fallback.ThrowableFallbackHandler;
import com.fangyanpg.ratelimiter.limit.mode.CountLimitMode;
import com.fangyanpg.ratelimiter.limit.mode.LockLimitMode;
import com.fangyanpg.ratelimiter.limit.mode.TokenBucketLimitMode;
import com.fangyanpg.ratelimiter.limit.support.LimitKeyGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author fangyanpeng
 * @since 2020/7/22
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration
public class RateLimitConfig {

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiter redisRateLimiter(RedisTemplate<String, Object> redisTemplate, LimitModeExecutor limitModeExecutor){
        return new RedisRateLimiter(redisTemplate, limitModeExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisBloomFilter redisBloomFilter(RedisTemplate<String, Object> redisTemplate, BloomFilterHelper bloomFilterHelper){
        return new RedisBloomFilter(redisTemplate, bloomFilterHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public BloomFilterHelper bloomFilterHelper(){
        return new BloomFilterHelper(100000, 50000, 5);
    }
    @Bean
    @ConditionalOnMissingBean
    public RateLimitInterceptor rateLimitInterceptor(RedisRateLimiter redisRateLimiter, FallbackHandler fallbackHandler,
                                                     LimitKeyGenerator limitKeyGenerator){
        return new RateLimitInterceptor(redisRateLimiter, fallbackHandler, limitKeyGenerator);
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

    @Bean
    @ConditionalOnMissingBean
    public LockLimitMode lockLimitMode(){
        return new LockLimitMode();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThrowableFallbackHandler throwableFallbackHandler(){
        return new ThrowableFallbackHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public LimitKeyGenerator limitKeyGenerator(){
        return new LimitKeyGenerator();
    }
}
