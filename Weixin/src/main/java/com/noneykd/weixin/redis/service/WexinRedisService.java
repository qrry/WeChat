package com.noneykd.weixin.redis.service;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.po.UserInfo;

import redis.clients.jedis.ShardedJedis;

@Component
public class WexinRedisService extends RedisBaseService {

	private static final Logger logger = LoggerFactory
			.getLogger(WexinRedisService.class);

	private final static String WEIXIN_TOKEN = "WX_TOKEN".intern();
	private final static String USER_PREDIX = "WX_USER_OPENID".intern();

	/**
	 * 保存token
	 * @param token
	 */
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

	/**
	 * 获取token
	 * @return
	 */
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
	
	/**
	 * 以openid为key来保存用户信息
	 * @param openid
	 * @param user
	 */
	public void setUserInfo(String openid, UserInfo user) {
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			String key = USER_PREDIX.replace("USERNAME", openid);
			JSONObject json = JSONObject.fromObject(user);
			jedis.set(key, json.toString());
			jedis.expire(key, ONEDAY);
		} catch (Exception e) {
			logger.error("setUserInfo error : " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
	}

	/**
	 * 根据openid查询用户信息
	 * @param openid
	 * @return
	 */
	public UserInfo getUserInfo(String openid) {
		ShardedJedis jedis = null;
		UserInfo user = null;
		try {
			jedis = sentinelPool.getResource();
			String key = USER_PREDIX.replace("OPENID", openid);
			String result = jedis.get(key);
			JSONObject json = JSONObject.fromObject(result);
			user = (UserInfo) JSONObject.toBean(json, UserInfo.class);
		} catch (Exception e) {
			logger.error("getUserInfo error : " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
		return user;
	}
	
}
