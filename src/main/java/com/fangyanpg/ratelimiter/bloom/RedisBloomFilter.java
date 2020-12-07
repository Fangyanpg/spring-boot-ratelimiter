package com.fangyanpg.ratelimiter.bloom;

import cn.hutool.core.lang.Assert;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author fangyanpeng
 * @since 2020/10/15
 */
public class RedisBloomFilter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final BloomFilterHelper bloomFilterHelper;

    public RedisBloomFilter(RedisTemplate<String, Object> redisTemplate, BloomFilterHelper bloomFilterHelper){
        this.redisTemplate = redisTemplate;
        this.bloomFilterHelper = bloomFilterHelper;
    }

    /**
     * 新增元素
     * @param key 键
     * @param value 需要计算的值
     */
    public void addByBloomFilter(String key, String value) {
        addByBloomFilter(bloomFilterHelper, key, value);
    }

    /**
     * 判断是否存在
     * @param key 键
     * @param value 需要计算的值
     * @return true：可能存在  false：一定不存在
     */
    public boolean includeByBloomFilter(String key, String value) {
        return includeByBloomFilter(bloomFilterHelper, key, value);
    }

    /**
     * 获取默认的误报概率
     * @return
     */
    public double getDefaultFalsePositiveProbability(){
        return bloomFilterHelper.getFalsePositiveProbability();
    }

    /**
     * 获取指定规则的误报概率
     * @param bloomFilterHelper
     * @return
     */
    public double getFalsePositiveProbability(BloomFilterHelper bloomFilterHelper){
        return bloomFilterHelper.getFalsePositiveProbability();
    }

    /**
     * 新增元素
     * @param bloomFilterHelper 指定的过滤规则
     * @param key 键
     * @param value 需要计算的值
     */
    public void addByBloomFilter(BloomFilterHelper bloomFilterHelper, String key, String value) {
        Assert.notNull(bloomFilterHelper, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            redisTemplate.opsForValue().setBit(key, i, true);
        }
    }
    /**
     * 判断是否存在
     * @param bloomFilterHelper 指定的过滤规则
     * @param key 键
     * @param value 需要计算的值
     * @return true：可能存在  false：一定不存在
     */
    public boolean includeByBloomFilter(BloomFilterHelper bloomFilterHelper, String key, String value) {
        Assert.notNull(bloomFilterHelper, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }
        return true;
    }
}
