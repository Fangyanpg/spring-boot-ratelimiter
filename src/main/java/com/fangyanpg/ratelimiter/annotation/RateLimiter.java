package com.fangyanpg.ratelimiter.annotation;

import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;
import com.fangyanpg.ratelimiter.limit.fallback.AbstractFallbackHandler;
import com.fangyanpg.ratelimiter.limit.fallback.ThrowableFallbackHandler;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    // 限流量
    int limit() default 1;

    // 过期时间
    int expire() default 1;

    // 限流类型 全局|IP
    LimitType type() default LimitType.IP;

    // 限流算法
    String mode() default LimitMode.COUNT;

    // key前缀
    String prefix() default "rateLimiter:";

    // 拼接方法参数坐标 （粗粒度）
    int[] key() default {};

    // 请求资源超时时间
    int timeout() default 30;

    // 自定义降级策略
    Class<? extends AbstractFallbackHandler> fallback() default ThrowableFallbackHandler.class;

}
