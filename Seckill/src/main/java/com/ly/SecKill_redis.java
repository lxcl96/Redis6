package com.ly;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * 没有考虑多线程，高并发的情况秒杀
 */
public class SecKill_redis {
	private final static Logger log = LoggerFactory.getLogger(SecKill_redis.class);
	private static final Object obj = new Object();

	public static void main(String[] args) {
		Jedis jedis =new Jedis("192.168.77.3",6379);
		System.out.println(jedis.ping());
		jedis.close();
	}

	//秒杀过程
	public static boolean doSecKill(String uid,String prodid) {
		JedisPool jedisPool = null;
		Jedis jedis = null;
		try {
			//1.uid和proid非空判断
			if ("".equals(uid)||null==uid||"".equals(prodid)||null==prodid) {
				log.warn("uid=" + uid + ", prodid=" + prodid);
				return false;
			}

			//2.使用jedis连接redis
			jedisPool = JedisPoolUtil.getJedisPoolInstance();
			jedis = jedisPool.getResource();
			//3.拼接key放在redis中
			// 3.1 库存key
			// 3.2 秒杀成功用户的key (用set存储)
			String sk_prodId_qt = "sk:" + prodid + ":qt";//库存key
			String sk_prodId_usr = "sk:" + prodid + ":usr";//秒杀成功key

			//watch + multi 是乐观锁 （必须最好放在redis操作之前就观察，提高准确率，如果放在multi前还是会超卖）
			jedis.watch(sk_prodId_qt);

			//4.获取库存数量，如果为空，则秒杀还没开始
			if (!jedis.exists(sk_prodId_qt)) {
				log.info("秒杀尚未开始！");
				return false;
			}
			//5.判断用户是否重复秒杀
			if (jedis.sismember(sk_prodId_usr,uid)) {
				log.info("该用户已经参与秒杀，且秒杀成功了！");
				return false;
			}
			//6.判断库存数量，如果小于1，则秒杀结束（使用java多线程解决超卖问题）
			/*
			synchronized (obj) {
				if (Integer.parseInt(jedis.get(sk_prodId_qt)) < 1) {
					log.info("商品已经秒杀结束！");
					return false;
				} else {
					//如果不是Java加锁了，redis光有事务，没有乐观锁也不行
					Transaction multi = jedis.multi();
					multi.decr(sk_prodId_qt);
					multi.sadd(sk_prodId_usr, uid);
					multi.exec();
					log.info("用户 uid=" + uid + "，秒杀成功！");
				}
			}
			 */

			//使用redis乐观锁，解决超卖问题
			if (Integer.parseInt(jedis.get(sk_prodId_qt)) < 1) {
				log.info("商品已经秒杀结束！");
				return false;
			} else {

				//使用乐观锁，其实就是 watch + multi
				Transaction multi = jedis.multi();
				multi.decr(sk_prodId_qt);
				multi.sadd(sk_prodId_usr, uid);
				List<Object> exec = multi.exec();
				//判断redis事务是否提交
				if (exec == null||exec.size()==0) {
					log.info("商品秒杀失败！");
					return false;
				}
				log.info("用户 uid=" + uid + "，秒杀成功！");
			}

			//7.秒杀过程
			//7.1 秒杀到了库存-1
			//7.2秒杀成功了，成功秒杀人员+1


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != jedis|| null != jedisPool)
				JedisPoolUtil.release(jedisPool,jedis);
		}

		return true;
	}


}
















