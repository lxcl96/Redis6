package com.ly.jedis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * FileName:JsonUtil.class
 * Author:ly
 * Date:2022/10/18 0018
 * Description:
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
    }
    public static String mapToJson(Map<String,String > map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }

}
