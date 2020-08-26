# spring-boot-ratelimiter
分布式锁、请求限流工具

目前支持计数法与令牌桶限流

###快速开始

项目里配置好 RedisTemplate

直接引入依赖，方法上使用 @RateLimiter 注解即可。
    
    <!-- https://mvnrepository.com/artifact/com.github.fangyanpg/spring-boot-ratelimiter -->
    <dependency>
        <groupId>com.github.fangyanpg</groupId>
        <artifactId>spring-boot-ratelimiter</artifactId>
        <version>0.0.2-RELEASE</version>
    </dependency>

**注意**：超过限流配置值会抛出 RateLimiterException 异常，请自行处理。


ps：若想拓展自定义限流，请继承 AbstractLimitMode 抽象类，并实现其部分方法。
