# spring-boot-ratelimiter
分布式锁、请求限流

目前仅支持计数法限流

项目里配置好RedisTemplate
直接引入依赖，方法上使用 @RateLimiter 注解即可。
    
    <!-- https://mvnrepository.com/artifact/com.github.fangyanpg/spring-boot-ratelimiter -->
    <dependency>
        <groupId>com.github.fangyanpg</groupId>
        <artifactId>spring-boot-ratelimiter</artifactId>
        <version>0.0.1-RELEASE</version>
    </dependency>


