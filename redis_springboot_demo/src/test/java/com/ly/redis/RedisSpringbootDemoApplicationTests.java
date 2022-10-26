package com.ly.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisSpringbootDemoApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
//        String s = stringRedisTemplate.opsForValue().get("15195464589:count");
//        System.out.println(s);
//        stringRedisTemplate.opsForValue().set("hello","ly");
    }

}
