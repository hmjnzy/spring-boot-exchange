package com.exchange.web.crawlers.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableCaching
public class RedissonConfig {

    @Value("${spring.redis.host}")
    String REDIS_HOST;

    @Value("${spring.redis.port}")
    String REDIS_PORT;

    @Value("${spring.redis.password}")
    String REDIS_PASSWORD;

    @Value("${spring.redis.database}")
    int REDIS_DATABASE;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
            config.useSingleServer()
                .setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT)
                //.setPassword(REDIS_PASSWORD)
                .setDatabase(REDIS_DATABASE)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10);
        return Redisson.create(config);
    }

}
