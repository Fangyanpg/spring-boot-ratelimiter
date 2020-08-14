package com.fangyanpg.ratelimiter.annotation;

import com.fangyanpg.ratelimiter.constants.LimitMode;
import com.fangyanpg.ratelimiter.constants.LimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    int limit() default 1;
    int expire() default 1;

    LimitType type() default LimitType.IP;
    LimitMode mode() default LimitMode.COUNT;

    String prefix() default "rateLimiter:";

}
