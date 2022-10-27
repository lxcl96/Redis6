import com.ly.SecKill_redis;
import org.junit.Test;

/**
 * FileName:TestSecKill.class
 * Author:ly
 * Date:2022/10/27 0027
 * Description:
 */
public class TestSecKill {
    @Test
    public void test() {
        boolean ret = SecKill_redis.doSecKill("1", "1");
        System.out.println(ret);
    }
}