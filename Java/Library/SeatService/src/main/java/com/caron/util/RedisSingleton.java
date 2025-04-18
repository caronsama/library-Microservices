package com.caron.util;

import io.lettuce.core.RedisClient;

public class RedisSingleton {
    private static volatile RedisClient redisClient;

    private RedisSingleton() {}

    public static RedisClient getInstance() {
        if (redisClient == null) {
            synchronized (RedisSingleton.class) {
                if (redisClient == null) {
                    redisClient = RedisClient.create("redis://192.168.241.97:6379");
                }
            }
        }
        return redisClient;
    }
}
