package com.ly.jedis.servlet;

import com.ly.jedis.utils.JedisUtil;
import com.ly.jedis.utils.JsonUtil;
import com.ly.jedis.utils.MessageAPIUtil;
import com.ly.jedis.utils.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * FileName:UserLoginServlet.class
 * Author:ly
 * Date:2022/10/18 0018
 * Description: 用户根据手机验证码实现登陆
 */
@WebServlet(urlPatterns = {"/sendVerify"})
public class VerifyServlet extends HttpServlet {
    private final Logger log = LoggerFactory.getLogger(VerifyServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("utf8");

        HashMap<String, String> map = new HashMap<>();
        String mobile = req.getParameter("mobile");
        String t = req.getParameter("t");
        log.info("mobile=" + mobile + "，t=" + t);
        if (!RegexUtil.isPhoneNumber(mobile)) {
            map.put("msg","手机号码格式不正确，请检查！");
            map.put("status","error");
            resp.getWriter().write(JsonUtil.mapToJson(map));
            return;
        }

        Jedis jedis = JedisUtil.getJedis();
        assert jedis != null : "jedis连接失败";

        //调用短信api发送验证码，并保存到redis中 (放在这会发生超过三次依旧调用)
//        String verify = MessageAPIUtil.getVerify();
        log.info("**** 用户" + mobile + "当前验证码：");
        //保存到jedis中
        try {
            //先判断是否手机号是否存在或是否已过24h
            if (jedis.exists(mobile)) {
                //获取count，判断次数
                if (Integer.parseInt(jedis.get(mobile +  ":count")) >= 3) {
                    map.put("msg","每天最多能发送，三次验证码！");
                    map.put("status","error");
                    resp.getWriter().write(JsonUtil.mapToJson(map));
                    return;
                }
                jedis.set(mobile + ":verify", MessageAPIUtil.getVerify());
                jedis.expire(mobile + ":verify",120);//2m过期
                jedis.incr(mobile + ":count");


            } else {
                //第一次发送
                jedis.set(mobile,"exists");//用来标记该手机号似否存在
                jedis.set(mobile + ":verify", MessageAPIUtil.getVerify());
                jedis.set(mobile + ":count","1");
                jedis.expire(mobile,24*60*60);//24h过期
                jedis.expire(mobile + ":verify",120);//2m过期
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            jedis.close();
        }

        map.put("msg","验证码发送成功！");
        map.put("status","success");
        resp.getWriter().write(JsonUtil.mapToJson(map));

    }
}
