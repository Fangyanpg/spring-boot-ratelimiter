package com.fangyanpg.ratelimiter.limit.fallback;

import com.fangyanpg.ratelimiter.exception.RateLimiterException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fangyanpeng
 * @since 2020/9/25
 */
@Slf4j
public class ThrowableFallbackHandler extends AbstractFallbackHandler {
    @Override
    public Object fallback(String method, String param) {
        log.warn("限流异常: method:{} param:{}", method, param);
        throw new RateLimiterException(method);
    }
}
