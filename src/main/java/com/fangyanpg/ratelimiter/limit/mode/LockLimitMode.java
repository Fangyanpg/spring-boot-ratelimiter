package com.fangyanpg.ratelimiter.limit.mode;

import com.fangyanpg.ratelimiter.annotation.RateLimiter;
import com.fangyanpg.ratelimiter.constants.LimitMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 * @since 2020/9/2
 */
@Slf4j
public class LockLimitMode extends AbstractLimitMode{

    private final ArrayBlockingQueue<Node> queue = new ArrayBlockingQueue<>(256);
    private volatile boolean owner = Boolean.FALSE;

    @Override
    public String scriptPath() {
        return "script/Count.lua";
    }

    @Override
    public String limitMode() {
        return LimitMode.LOCK;
    }

    @Override
    public String execute(RedisTemplate<String, Object> redisTemplate, String key, RateLimiter rateLimiter) {
        String locked;
        long waitMaxTime = rateLimiter.timeout() << 11;
        long waitTime = waitMaxTime >> 2;
        long begin = System.currentTimeMillis();
        Node current = new Node(Thread.currentThread());
        for(;;){
            locked = redisTemplate.execute(script,
                    redisTemplate.getStringSerializer(),
                    redisTemplate.getStringSerializer(),
                    Collections.singletonList(key), String.valueOf(1), String.valueOf(rateLimiter.timeout()));
            if ("true".equals(locked)){
                owner = Boolean.TRUE;
                queue.remove(current);
                return locked;
            }

            wait2Lock(current, waitTime);

            long currentWaitTime = System.currentTimeMillis() - begin;
            if(currentWaitTime >= waitMaxTime){
                log.warn("lock limit waiting timeout key:{}", key);
                return locked;
            }
            waitTime = waitMaxTime - currentWaitTime;

        }
    }

    private void wait2Lock(Node n, long waitTime){
        if(!queue.contains(n)){
            queue.add(n);
        }
        if(!owner){
            waitTime = 50;
        }
        try {
            n.interruptible = Boolean.TRUE;
            TimeUnit.MILLISECONDS.sleep(waitTime);
            n.interruptible = Boolean.FALSE;
        } catch (InterruptedException ignored) {

        }
    }

    @Override
    public void wake() {
        owner = Boolean.FALSE;
        Optional.ofNullable(queue.poll()).ifPresent(node -> {
            if(node.interruptible){
                node.thread.interrupt();
            }
        });
    }

    private static class Node{
        volatile boolean interruptible = Boolean.TRUE;
        Thread thread;

        public Node(Thread thread){
            this.thread = thread;
        }
    }

}
