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

    /**
     * 当前节点是否占有锁
     */
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
        Node node = new Node(Thread.currentThread(), rateLimiter.timeout());

        for(;;){
            locked = redisTemplate.execute(script,
                    redisTemplate.getStringSerializer(),
                    redisTemplate.getStringSerializer(),
                    Collections.singletonList(key), String.valueOf(1), String.valueOf(rateLimiter.timeout()));

            if ("true".equals(locked)){
                node.acquired();
                return locked;
            }

            node.wait2Lock();

            if(node.waitingTimeout()){
                this.wake();
                log.warn("lock limit waiting timeout key:{}", key);
                return locked;
            }
        }
    }

    @Override
    public void wake() {
        owner = Boolean.FALSE;
        Optional.ofNullable(queue.poll()).ifPresent(Node::wake);
    }

    private class Node{
        volatile boolean interruptible = Boolean.TRUE;
        Thread thread;
        long waitMaxTime;
        long waitTime;
        long begin;

        public Node(Thread thread, long timeout){
            this.begin = System.currentTimeMillis();
            this.thread = thread;
            this.waitMaxTime = timeout << 11;
            this.waitTime = this.waitMaxTime >> 2;
        }

        public void wait2Lock(){
            if(!queue.contains(this)){
                queue.add(this);
            }
            if(!owner){
                // 占有者不是当前节点 则缩短轮询时间
                waitTime = 50;
            }
            try {
                interruptible = Boolean.TRUE;
                TimeUnit.MILLISECONDS.sleep(waitTime);
                interruptible = Boolean.FALSE;
            } catch (InterruptedException ignored) {

            }
            waitTime = waitMaxTime - currentWaitTime();
        }

        public void wake(){
            if(this.interruptible){
                this.thread.interrupt();
            }
        }

        public long currentWaitTime(){
            return System.currentTimeMillis() - this.begin;
        }

        public boolean waitingTimeout(){
            return currentWaitTime() >= this.waitMaxTime;
        }

        public void acquired(){
            owner = Boolean.TRUE;
            queue.remove(this);
        }
    }

}
