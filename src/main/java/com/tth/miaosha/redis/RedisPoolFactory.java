package com.tth.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(poolConfig.getMinIdle());
        poolConfig.setMaxTotal(poolConfig.getMaxTotal());
        poolConfig.setMaxWaitMillis(poolConfig.getMaxWaitMillis() * 1000);
        JedisPool jp = new JedisPool(poolConfig,redisConfig.getHost(),
                redisConfig.getPort(),redisConfig.getTimeout() * 1000,
                redisConfig.getPassword(),0);
        return jp;
    }
}
