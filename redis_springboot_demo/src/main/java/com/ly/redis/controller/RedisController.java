package com.ly.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * FileName:RedisController.class
 * Author:ly
 * Date:2022/10/26 0026
 * Description:
 */
@RestController
public class RedisController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/redis")
    public String testRedis(){
        redisTemplate.opsForValue().set("name","lucy");
        //只能取出用此自定义redis模板存的数据，否则会报无法识别类型
        return (String)redisTemplate.opsForValue().get("name");
    }

    /**
     * 测试基于redis的分步式锁的简单实现
     *  对num键操作，设置锁为键lock
     */
    @GetMapping("/testLock")
    public void testLock() {
        String uuid = UUID.randomUUID().toString();

        //尝试获取锁 (设置锁超时时间)
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,2L, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            //num锁获取成功
            Object num = redisTemplate.opsForValue().get("num");
            if (StringUtils.isEmpty(num)) {
                return;
            }
            int parseInt = Integer.parseInt(num + "");
            redisTemplate.opsForValue().set("num",++parseInt);
            //逻辑操作完毕，释放锁（校验uuid防止误删除）
            if (uuid.equals((String) redisTemplate.opsForValue().get("lock"))) {
                redisTemplate.delete("lock");
            }
        } else {
            try {
                //没有获取到锁，说明别人正在用 等待一段时间
                Thread.sleep(100);
                //System.out.println("再次尝试获取 " + new Date());
                //再次尝试获取锁
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @GetMapping("testLockLua")
    public void testLockLua() {
        //1 声明一个uuid ,将做为一个value 放入我们的key所对应的值中
        String uuid = UUID.randomUUID().toString();
        //2 定义一个锁：lua 脚本可以使用同一把锁，来实现删除！
        String skuId = "25"; // 访问skuId 为25号的商品 100008348542
        String locKey = "lock:" + skuId; // 锁住的是每个商品的数据

        // 3 获取锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(locKey, uuid, 3, TimeUnit.SECONDS);

        // 第一种： lock 与过期时间中间不写任何的代码。
        // redisTemplate.expire("lock",10, TimeUnit.SECONDS);//设置过期时间
        // 如果true
        if (lock) {
            // 执行的业务逻辑开始
            // 获取缓存中的num 数据
            Object value = redisTemplate.opsForValue().get("num");
            // 如果是空直接返回
            if (StringUtils.isEmpty(value)) {
                return;
            }
            // 不是空 如果说在这出现了异常！ 那么delete 就删除失败！ 也就是说锁永远存在！
            int num = Integer.parseInt(value + "");
            // 使num 每次+1 放入缓存
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
            /*使用lua脚本来锁*/
            // 定义lua 脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 使用redis执行lua执行
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            // 设置一下返回值类型 为Long
            // 因为删除判断的时候，返回的0,给其封装为数据类型。如果不封装那么默认返回String 类型，
            // 那么返回字符串与0 会有发生错误。
            redisScript.setResultType(Long.class);
            // 第一个要是script 脚本 ，第二个需要判断的key，第三个就是key所对应的值。
            redisTemplate.execute(redisScript, Arrays.asList(locKey), uuid);
        } else {
            // 其他线程等待
            try {
                // 睡眠
                Thread.sleep(1000);
                // 睡醒了之后，调用方法。
                testLockLua();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
