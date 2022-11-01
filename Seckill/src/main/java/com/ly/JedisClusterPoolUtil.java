package com.ly;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * FileName:JedisClusterUtil.class
 * Author:ly
 * Date:2022/11/1 0001
 * Description: 集群的连接（无无密码）
 */
public class JedisClusterPoolUtil {
    private static volatile JedisCluster jedisCluster = null;
    private static final String host = "192.168.77.3";
    private static final int port = 6379;

    public static JedisCluster getJedisCluster() {
        if (jedisCluster == null) {
            synchronized(JedisPoolUtil.class) {
                if (null == jedisCluster) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(50);
                    jedisPoolConfig.setMaxIdle(10);
                    jedisPoolConfig.setMinIdle(10);
                    jedisPoolConfig.setMaxWaitMillis(15000L);
                    jedisPoolConfig.setTestOnBorrow(true);

                    //等同于 redis-cli -c -p 6379
                    //可以将HostAndPort 设置为set类型的HostAndPort，保证连接可用性
                    jedisCluster = new JedisCluster(new HostAndPort(host,port),15000,jedisPoolConfig);
                }
            }
        }
        return jedisCluster;
    }


}
