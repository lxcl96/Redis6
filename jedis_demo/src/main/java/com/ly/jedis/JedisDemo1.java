package com.ly.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * FileName:JedisDemo1.class
 * Author:ly
 * Date:2022/10/14 0014
 * Description:
 */
public class JedisDemo1 {
    private static final Logger log = LoggerFactory.getLogger(JedisDemo1.class);
    private static final String host = "192.168.77.3";
    private static final int port = 6379;
    private static final int timeout = 2000;
    private static final String passwd = "1024";

    public static void main(String[] args) {

        //jedis连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //创建Jedis连接池
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, passwd);
        //根据连接池获取jedis连接
        Jedis jedis = jedisPool.getResource();
        log.info(poolConfig.toString());
        log.info(jedisPool.toString());
        log.info(jedis.toString());
        if (jedis == null) {
            log.error("jedis连接失败");
        }
        System.out.println(jedis.ping());
        System.out.println("hello:" + jedis.get("hello"));
        //连接有数量，必须关闭
        jedis.close();

    }
}
