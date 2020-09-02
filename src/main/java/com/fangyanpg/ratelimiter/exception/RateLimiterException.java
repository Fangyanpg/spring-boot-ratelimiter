package com.fangyanpg.ratelimiter.exception;

import lombok.extern.slf4j.Slf4j;

import java.security.AccessControlException;
import java.security.Permission;

/**
 * @author fangyanpeng
 * @since 2020/8/14
 */
@Slf4j
public class RateLimiterException extends AccessControlException {

    public RateLimiterException(String s) {
        super(s);
        log.warn("限流异常: {}", s);
    }

    public RateLimiterException(String s, Permission p) {
        super(s, p);
    }


}
