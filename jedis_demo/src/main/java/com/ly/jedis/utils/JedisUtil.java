package com.ly.jedis.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * FileName:JedisUtil.class
 * Author:ly
 * Date:2022/10/18 0018
 * Description:
 */
public class JedisUtil {
    private static final String host = "192.168.77.3";
    private static final int port = 6379;
    private static final int timeout = 2000;
    private static final String passwd = "1024";
    private static Jedis jedis;


    public static Jedis getJedis() {
        //jedis连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //创建Jedis连接池
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, passwd);
        //根据连接池获取jedis连接
        jedis = jedisPool.getResource();
        if (!"PONG".equals(jedis.ping())) {
            jedis = null;
        }

        return jedis;
    }
}
