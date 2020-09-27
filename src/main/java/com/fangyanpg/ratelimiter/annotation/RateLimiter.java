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

    int limit() default 1;

    int expire() default 1;

    LimitType type() default LimitType.IP;

    String mode() default LimitMode.COUNT;

    String prefix() default "rateLimiter:";

    int[] key() default {};

    int timeout() default 30;

    Class<? extends AbstractFallbackHandler> fallback() default ThrowableFallbackHandler.class;

}
