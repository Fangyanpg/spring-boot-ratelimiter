package com.fangyanpg.ratelimiter.exception;

import java.security.AccessControlException;

/**
 * @author fangyanpeng
 * @since 2020/9/27
 */
public class DuplicateKeyException extends AccessControlException {

    public DuplicateKeyException(String s) {
        super(s);
    }
}
