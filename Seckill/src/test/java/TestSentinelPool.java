import com.ly.JedisPoolUtil;
import com.ly.JedisSentinelPoolUtil;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

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

}
