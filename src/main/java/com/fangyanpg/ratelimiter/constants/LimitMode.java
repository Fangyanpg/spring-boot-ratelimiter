package com.fangyanpg.ratelimiter.constants;

/**
 * @author fangyanpeng
 * @since 2020/8/13
 */
public interface LimitMode {

    String COUNT = "COUNT";

    String TOKEN_BUCKET = "TOKEN_BUCKET";

    String LOCK = "LOCK";
}
