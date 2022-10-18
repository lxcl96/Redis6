package com.ly.jedis.servlet;

import com.ly.jedis.utils.JedisUtil;
import com.ly.jedis.utils.JsonUtil;
import com.ly.jedis.utils.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
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
 * Description:
 */
@WebServlet(urlPatterns = {"/login"})
public class UserLoginServlet extends HttpServlet {
    private final Logger log = LoggerFactory.getLogger(UserLoginServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf8");
        HashMap<String, String> map = new HashMap<>();
        String code = req.getParameter("code");
        String mobile = req.getParameter("mobile");
        String t = req.getParameter("t");
        log.info("mobile=" + mobile + "，code=" + code + "，t=" + t);

        if (!RegexUtil.isPhoneNumber(mobile)) {
            map.put("msg","手机号码格式不正确，请检查！");
            map.put("status","error");
            resp.getWriter().write(JsonUtil.mapToJson(map));
            return;
        }

        Jedis jedis = JedisUtil.getJedis();
        assert jedis != null : "jedis连接失败";
        try {
            String verify = jedis.get(mobile + ":verify");
            //验证码过期
            if (verify == null) {
                map.put("msg","验证码已过期，请重新发送！");
                map.put("status","error");
                resp.getWriter().write(JsonUtil.mapToJson(map));
                return;
            }
            //验证码不对
            if (!verify.equals(code)){
                map.put("msg","验证码不正确！");
                map.put("status","error");
                resp.getWriter().write(JsonUtil.mapToJson(map));
                return;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            jedis.close();
        }
        map.put("msg","登陆成功!");
        map.put("status","success");
        resp.getWriter().write(JsonUtil.mapToJson(map));
    }
}
