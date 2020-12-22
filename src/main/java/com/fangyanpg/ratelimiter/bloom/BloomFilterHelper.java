package com.fangyanpg.ratelimiter.bloom;

import cn.hutool.bloomfilter.BitSetBloomFilter;

/**
 * @author fangyanpeng
 * @since 2020/10/15
 */
public class BloomFilterHelper extends BitSetBloomFilter {
    private final int bitSetSize;
    private final int hashFunctionNumber;
    /**
     * 构造一个布隆过滤器，过滤器的容量为c * n 个bit.
     *
     * @param c 当前过滤器预先开辟的最大包含记录,通常要比预计存入的记录多一倍.
     * @param n 当前过滤器预计所要包含的记录.
     * @param k 哈希函数的个数，等同每条记录要占用的bit数.
     */
    public BloomFilterHelper(int c, int n, int k) {
        super(c, n, k);
        this.bitSetSize = (int) Math.ceil(c * k);
        this.hashFunctionNumber = k;
    }

    public int[] murmurHashOffset(String key){
        int[] offset = new int[hashFunctionNumber];
        for(int i = 0; i < hashFunctionNumber; i++) {
            offset[i] = Math.abs(hash(key, i) % bitSetSize);
        }
        return offset;
    }

}
