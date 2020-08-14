package com.fangyanpg.ratelimiter.config;

import com.fangyanpg.ratelimiter.aop.RateLimitAnnotationAdvisor;
import com.fangyanpg.ratelimiter.aop.RateLimitInterceptor;
import com.fangyanpg.ratelimiter.lock.RedisRateLimiter;
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


    public static void main(String[] args) throws InterruptedException {
        int limit = 100;
        int expire = 10;
        int rate = limit/expire;
        int curr = 1;

        int permits = 1;
        long last_mill = 0;
        int sum = 0;
        for (int i = 0; i<400; i++){
            if(i>398){
                TimeUnit.SECONDS.sleep(7);
            }
            int curr_permits_local = curr;
            long curr_mill = System.currentTimeMillis();
            if(last_mill != 0l){
                if(curr_mill - last_mill < 0){
                    System.out.println("请求失效");
                    break;
                }
                double reverse = Math.floor((curr_mill-last_mill) / 1000 * rate);
                if(reverse>0){
                    double expect = curr_permits_local + reverse;
                    curr_permits_local = (int)Math.min(expect, limit);
                    last_mill = curr_mill;
                    System.out.println("生成令牌 "+reverse+" 个 现有令牌数 "+curr_permits_local);
                }
            }else{
                last_mill = curr_mill;
            }
            if(curr_permits_local - permits >= 0){
                curr = curr_permits_local - permits;
                sum++;
                System.out.println("==== 获取令牌成功 当前curr:"+curr);
            }else{
                curr = curr_permits_local;
                System.out.println("**** 获取令牌失败 当前curr:"+curr);
            }
        }
        System.out.println(sum);
    }
}
