# spring-boot-ratelimiter
分布式锁、请求限流工具

目前支持计数法与令牌桶限流

### 快速开始

项目里配置好 RedisTemplate

直接引入依赖，方法上使用 @RateLimiter 注解即可。
    
    <!-- https://mvnrepository.com/artifact/com.github.fangyanpg/spring-boot-ratelimiter -->
    <dependency>
        <groupId>com.github.fangyanpg</groupId>
        <artifactId>spring-boot-ratelimiter</artifactId>
        <version>0.2.0-RELEASE</version>
    </dependency>
    
### 请求限流用例（默认一秒一次）：

    @RateLimiter
    public void submit(int key){
        // 业务逻辑 start
        int count = selectCount();
        count = 1;
        setCount(count);
        // 业务逻辑 end
    }
LimitMode用来指定哪种限流模式，默认为LimitMode.COUNT，若想拓展自定义限流，请继承 AbstractLimitMode 抽象类，并实现其部分方法。
    
### 分布式锁用例：

    @RateLimiter(mode = LimitMode.LOCK, fallback = MyFallbackHandler.class, key = {0})
    public void tryLock(int key){
        // 业务逻辑 start
        int count = selectCount();
        count = 1;
        setCount(count);
        // 业务逻辑 end
    }
**注意**：超过限流配置值会抛出 RateLimiterException 异常，请自行捕获。也可继承 AbstractFallbackHandler 实现自定义降级逻辑。


