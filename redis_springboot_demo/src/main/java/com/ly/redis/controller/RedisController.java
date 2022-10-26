package com.ly.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
