# spring-boot-ratelimiter
基于redis的分布式锁、请求限流实现

基于redis的布隆过滤器实现


### 快速开始

首先项目里需要配置好 RedisTemplate

直接引入依赖，方法上使用 @RateLimiter 注解即可。
    
    <!-- https://mvnrepository.com/artifact/com.github.fangyanpg/spring-boot-ratelimiter -->
    <dependency>
        <groupId>com.github.fangyanpg</groupId>
        <artifactId>spring-boot-ratelimiter</artifactId>
        <version>0.3.0-RELEASE</version>
    </dependency>
    
### 请求限流用例（默认一秒一次）：

    @RateLimiter(mode = LimitMode.TOKEN_BUCKET)
    public void submit(int key){
        // 业务逻辑 start
        int count = selectCount();
        count = 1;
        setCount(count);
        // 业务逻辑 end
    }
其中 mode用来指定哪种限流模式，默认为LimitMode.COUNT，若想拓展自定义限流，请继承 AbstractLimitMode 抽象类，并实现其部分方法。
    
### 分布式锁用例：
    // 若想使用分布式锁则必须为该方法指定 mode = LimitMode.LOCK
    @RateLimiter(mode = LimitMode.LOCK, fallback = MyFallbackHandler.class, key = {0})
    public void tryLock(int key){
        // 业务逻辑 start
        int count = selectCount();
        count = 1;
        setCount(count);
        // 业务逻辑 end
    }

**注意**：超过限流配置值会抛出 RateLimiterException 异常，请自行捕获。也可继承 AbstractFallbackHandler 实现自定义降级逻辑。

### 布隆过滤器用例：
    @Autowired
    private RedisBloomFilter redisBloomFilter;
    
    public void filterTest(){
        String key = "k1";
        
        // 1、向布隆过滤器新增
        redisBloomFilter.addByBloomFilter(key, String.valueOf(1));
        
        // 2、判断是否存在
        for (int i = 0; i < 100; i++) {
            if (redisBloomFilter.includeByBloomFilter(key, String.valueOf(i))){
                // 可能存在
                // 业务..
            } else {
                // 一定不存在
                // 业务..
            }
        }
    }
    
**注意**：过滤器的预计容量为50000，误判率0.00943。
