package com.fangyanpg.ratelimiter.limit.fallback;

import com.fangyanpg.ratelimiter.exception.RateLimiterException;

/**
 * @author fangyanpeng
 * @since 2020/9/25
 */
public class ThrowableFallbackHandler extends AbstractFallbackHandler {
    @Override
    public Object fallback(String param) {
        throw new RateLimiterException(param);
    }
}
