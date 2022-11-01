import com.ly.JedisClusterPoolUtil;
import com.ly.JedisPoolUtil;
import com.ly.JedisSentinelPoolUtil;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * FileName:TestSentinelPool.class
 * Author:ly
 * Date:2022/10/31 0031
 * Description:
 */
public class TestSentinelPool {

    @Test
    public void test() {
        JedisSentinelPool poolInstance = JedisSentinelPoolUtil.getSentinelPoolInstance();
        System.out.println(poolInstance);
        //必须要把哨兵模式里的ip修改为服务的真实ip，如果时127.0.0.1那么jedis连接时可能会是本机的而不是服务机的
        Jedis jedis = poolInstance.getResource();
        String info = jedis.info("replication");
        System.out.println(info);

        System.out.println("hello:" + jedis.get("hello"));
        jedis.close();
    }

    @Test
    public void testJedisCluster() throws IOException {
        JedisCluster cluster = JedisClusterPoolUtil.getJedisCluster();
        Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
        clusterNodes.forEach((node,jedisPool) -> System.out.println(node + "=" + jedisPool));
        System.out.println("k1: " + cluster.get("k1"));
        System.out.println("k2: " + cluster.get("k2"));
        System.out.println("person1_name: " + cluster.mget("name{person1}"));
        System.out.println("person1: " + cluster.mget("{person1}")); //取不到值，必须是上面的方式 键{组}
        cluster.close();
    }

}
