package com.noneykd.weixin.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.ShardedJedis;

@Component
public class WexinRedisService extends RedisBaseService {

	private static final Logger logger = LoggerFactory
			.getLogger(WexinRedisService.class);

	private final static String WEIXIN_TOKEN = "WX_TOKEN".intern();
	private final static String USER_PREDIX = "WX_USER_USERNAME".intern();

	public void setToken(String token) {
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			jedis.set(WEIXIN_TOKEN, token);
			jedis.expire(WEIXIN_TOKEN, HOURS * 2);
		} catch (Exception e) {
			logger.error("setToken error : " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
	}

	public String getToken() {
		ShardedJedis jedis = null;
		String result = null;
		try {
			jedis = sentinelPool.getResource();
			result = jedis.get(WEIXIN_TOKEN);
		} catch (Exception e) {
			logger.error("getToken error : " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
		return result;
	}
	
	
}
