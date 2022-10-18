package com.ly.jedis.utils;

/**
 * FileName:MessageAPIUtil.class
 * Author:ly
 * Date:2022/10/18 0018
 * Description: 模拟短信api调用，随机生成6位数字
 */
public class MessageAPIUtil {

    public static String getVerify() {

        double random = Math.random() * 1000000;
        return String.valueOf(Math.round(random));
    }
}
