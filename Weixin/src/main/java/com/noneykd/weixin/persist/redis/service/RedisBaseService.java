package com.noneykd.weixin.persist.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.persist.redis.base.ShardedJedisSentinelPool;

import redis.clients.jedis.ShardedJedis;

@Component
public class RedisBaseService {

	@Autowired
	protected ShardedJedisSentinelPool sentinelPool;

	protected int MINUTES = 60;
	protected int HOURS = 60 * 60;
	protected int ONEDAY = 60 * 60 * 24;

	/**
	 * 使用完后释放jedis连接
	 * 
	 * @param jedis
	 */
	protected void colseJedis(ShardedJedis jedis) {

		if (jedis != null) {
			jedis.close();
		}
	}

	public void setSentinelPool(ShardedJedisSentinelPool sentinelPool) {
		this.sentinelPool = sentinelPool;
	}

	/**
	 * set string method
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void set(String key, String value, int expire) throws Exception {
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			jedis.set(key, value);
			jedis.expire(key, expire);
		} catch (Exception e) {
			throw new Exception("set key:" + key + ", value:" + value
					+ "error.");
		} finally {
			colseJedis(jedis);
		}
	}

	/**
	 * get string method
	 * @return
	 * @throws Exception
	 */
	public String get(String key) throws Exception {
		ShardedJedis jedis = null;
		String result = null;
		try {
			jedis = sentinelPool.getResource();
			result = jedis.get(key);
		} catch (Exception e) {
			throw new Exception("get key:" + key + "error.");
		} finally {
			colseJedis(jedis);
		}
		return result;
	}

}
