package com.fangyanpg.ratelimiter.limit.fallback;

/**
 * @author fangyanpeng
 * @since 2020/9/25
 */
public abstract class AbstractFallbackHandler {
    public abstract Object fallback(String str);
}
