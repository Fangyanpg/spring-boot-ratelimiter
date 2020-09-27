package com.fangyanpg.ratelimiter.exception;

import java.security.AccessControlException;
import java.security.Permission;

/**
 * @author fangyanpeng
 * @since 2020/8/14
 */
public class RateLimiterException extends AccessControlException {

    public RateLimiterException(String s) {
        super(s);
    }

    public RateLimiterException(String s, Permission p) {
        super(s, p);
    }


}
