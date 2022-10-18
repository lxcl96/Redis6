package com.ly.jedis.utils;

/**
 * FileName:RegexUtil.class
 * Author:ly
 * Date:2022/10/18 0018
 * Description:
 */
public class RegexUtil {
    private static final String phoneRegx = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    public static Boolean isPhoneNumber(String str){
        return str.matches(phoneRegx);
    }
}
