package com.ly;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;

/**
 * FileName:jedisSentinelPoolUtil.class
 * Author:ly
 * Date:2022/10/31 0031
 * Description: 哨兵模式下获取jedis连接，实现自动切换
 */
public class JedisSentinelPoolUtil {
    private static JedisSentinelPool jedisSentinelPool;
    // 启动哨兵时 必须设置观测的主服务名字 即mymaster
    private static final String masterName = "mymaster";
    //如果服务设置了密码，则必须填写
    private static final String passwd = "1024";

    //通过哨兵模式，获取jedis连接
    public static JedisSentinelPool getSentinelPoolInstance(){
        if (jedisSentinelPool != null) {
            return jedisSentinelPool;
        }

        synchronized(JedisSentinelPoolUtil.class) {
            if (null == jedisSentinelPool) {
                //设置jedis连接池信息
                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                jedisPoolConfig.setMaxTotal(10);//最大连接数
                jedisPoolConfig.setMaxIdle(5);//最大空闲连接数
                jedisPoolConfig.setMinIdle(5);//最小空闲连接数
                jedisPoolConfig.setBlockWhenExhausted(true);//连接耗尽时是否等待空闲连接
                jedisPoolConfig.setMaxWaitMillis(6000);//最大等待时间6秒
                jedisPoolConfig.setTestOnBorrow(true);//使用连接时测试一下

                //设置哨兵信息(因为哨兵有很多个，所以用集合)
                HashSet<String> sentinelSet = new HashSet<>();
                sentinelSet.add("192.168.77.3:26379");//哨兵服务对于的端口默认就是26379

                //新建jedisSentinelPool
                jedisSentinelPool = new JedisSentinelPool(masterName, sentinelSet, jedisPoolConfig, 3000, passwd);
            }
        }

        return jedisSentinelPool;
    }




}
