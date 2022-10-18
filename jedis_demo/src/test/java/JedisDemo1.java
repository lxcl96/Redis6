import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    private static Jedis jedis;

    static {
        //jedis连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //创建Jedis连接池
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, passwd);
        //根据连接池获取jedis连接
        jedis = jedisPool.getResource();
//        log.info(poolConfig.toString());
//        log.info(jedisPool.toString());
//        log.info(jedis.toString());
        if (!"PONG".equals(jedis.ping())) {
            log.error("jedis连接失败");
        }
    }

    @After
    public void closeJedis() {
        //连接有数量，必须关闭
        System.out.println("关闭jedis连接");
        jedis.close();
    }

    @Test
    public void test_jedis_string(){
        String s = jedis.mset("k1", "v1", "k2", "v2"); //返回jedis的状态码 ok 正常
        List<String> mget = jedis.mget("k1", "k2");
        System.out.println(s);
        System.out.println(mget);
        System.out.println("hello:" + jedis.get("hello"));
    }

    @Test
    public void test_jedis_list(){
        //一般jedis的命令和原生的一样都有返回值(追加操作)
        jedis.lpush("key1", "lucy", "mary", "jack","张三");
        List<String> list = jedis.lrange("key1", 0, -1);
        System.out.println(list);
    }

    @Test
    public void test_jedis_set() {
        jedis.sadd("key_set", "c", "c++", "php");
        Set<String> key_set = jedis.smembers("key_set");
        System.out.println(key_set);
    }

    @Test
    public void test_jedis_hash() {
        HashMap<String,String> map = new HashMap<>();
        map.put("name","张三");
        map.put("age","18");
        jedis.hset("key_hash_1","type","hash");
        jedis.hset("person",map);
        Set<String> personKeys = jedis.hkeys("person");
        List<String> personVals = jedis.hvals("person");
        System.out.println(personKeys);
        System.out.println(personVals);

    }

    @Test
    public void test_jedis_zset(){
        HashMap<String, Double> map = new HashMap<>();
        map.put("重庆",3d);
        map.put("武汉",4d);
        jedis.zadd("city",0d,"上海");
        jedis.zadd("city",1d,"深圳");
        jedis.zadd("city",map);

        Set<Tuple> city = jedis.zrangeWithScores("city", 0, -1);//tuple双重数组
        System.out.println(city);
    }
    @Test
    public void test() throws JsonProcessingException {
//        HashMap<String, Double> map = new HashMap<>();
//        map.put("重庆",3d);
//        map.put("武汉",4d);
//        ObjectMapper mapper = new ObjectMapper();
//        String s = mapper.writeValueAsString(map);
//        System.out.println(s);

        double random = Math.random() * 1000000;
        System.out.println(String.valueOf(Math.round(random)));
    }
}
