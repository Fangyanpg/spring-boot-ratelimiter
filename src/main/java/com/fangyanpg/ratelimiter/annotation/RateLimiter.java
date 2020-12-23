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

    // 限流时间间隔
    int expire() default 1;

    // 限流类型 全局|IP
    LimitType type() default LimitType.IP;

    // 限流算法
    String mode() default LimitMode.COUNT;

    // key前缀
    String prefix() default "rl:";

    // 拼接方法参数索引坐标
    // 例： key = [0, 1]
    // 如果是对象请重写toString()
    int[] key() default {};

    // 独占资源的超时时间
    // 若请求锁资源时间超出约 timeout*2 会进入降级
    int timeout() default 3;

    // 自定义降级策略
    Class<? extends AbstractFallbackHandler> fallback() default ThrowableFallbackHandler.class;

}
