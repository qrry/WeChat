package com.noneykd.weixin.persist.redis.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.ShardedJedis;

import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.util.Constants;

@Component
public class WexinRedisService extends RedisBaseService {

	private static final Logger logger = LoggerFactory.getLogger(WexinRedisService.class);

	private final static String APPID = "DOMAIN.appid".intern();
	private final static String SECRET = "DOMAIN.appsecret".intern();
	private final static String DOMAIN = "DOMAIN".intern();
	private final static String WEIXIN_TOKEN = "DOMAIN.token".intern();
	private final static String JSAPI_TICKET = "DOMAIN.jsapi_ticket".intern();// jsapi
	private final static String KQAPI_TICKET = "DOMAIN.kqapi_ticket".intern();// 卡券api
	private final static String USER_PREDIX = "DOMAIN.user.OPENID".intern();
	private final static String OPENID = "OPENID".intern();

	@PostConstruct
	public void init() {
		Properties prop = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config/weixin.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			logger.error("读取配置文件出错!");
		}
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			String domain;
			String value;
			for (Object s : prop.keySet()) {
				domain = (String) s;
				value = prop.getProperty(domain);
				jedis.set(domain, value);
				logger.info("set:{key:{},value:{}}", domain, value);
				jedis.sadd(DOMAIN, domain.substring(0, domain.lastIndexOf('.')));
			}
		} catch (Exception e) {
			logger.error("init error " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}

	}

	/**
	 * 获取到所有域名，用于定时调用
	 * 
	 * @return
	 */
	public Set<String> getDomains() {
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			return jedis.smembers(DOMAIN);
		} catch (Exception e) {
			logger.error("getDomains error " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
		return null;
	}
	
	/**
	 * 判断域名是否包含在domain中
	 * @param domain
	 * @return
	 */
	public boolean isInDomain(String domain) {
		ShardedJedis jedis = null;
		try {
			jedis = sentinelPool.getResource();
			return jedis.sismember(DOMAIN, domain);
		} catch (Exception e) {
			logger.error("getDomains error " + e.getMessage());
		} finally {
			colseJedis(jedis);
		}
		return false;
	}

	/**
	 * 获取appid
	 * 
	 * @param domain
	 * @return
	 */
	public String getAppid(String domain) {
		String result = null;
		try {
			result = get(APPID.replace(DOMAIN, domain));
		} catch (Exception e) {
			logger.error("getAppid " + e.getMessage());
		}
		return result;
	}

	/**
	 * 获取appsecret
	 * 
	 * @param domain
	 * @return
	 */
	public String getSecret(String domain) {
		String result = null;
		try {
			result = get(SECRET.replace(DOMAIN, domain));
		} catch (Exception e) {
			logger.error("getSecret " + e.getMessage());
		}
		return result;
	}

	/**
	 * 保存token
	 * 
	 * @param token
	 */
	public void setToken(String token, String domain) {
		try {
			set(WEIXIN_TOKEN.replace(DOMAIN, domain), token, HOURS * 2);
		} catch (Exception e) {
			logger.error("setToken " + e.getMessage());
		}
	}

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public String getToken(String domain) {
		String result = null;
		try {
			result = get(WEIXIN_TOKEN.replace(DOMAIN, domain));
		} catch (Exception e) {
			logger.error("getToken " + e.getMessage());
		}
		return result;
	}

	/**
	 * 保存jsapi_ticket
	 * 
	 * @param token
	 */
	public void setJsApiTicket(String domain, String ticket, String type) {
		String key = JSAPI_TICKET;
		if (StringUtils.isNotBlank(type) && type.equals(Constants.KQ_TYPE)) {
			key = KQAPI_TICKET;
		}
		try {
			set(key, ticket, HOURS * 2);
		} catch (Exception e) {
			logger.error("setJsApiTicket " + e.getMessage());
		}
	}

	/**
	 * 获取jsapi_ticket
	 * 
	 * @return
	 */
	public String getJsApiTicket(String domain, String type) {
		String key = JSAPI_TICKET;
		if (StringUtils.isNotBlank(type) && type.equals(Constants.KQ_TYPE)) {
			key = KQAPI_TICKET;
		}
		String result = null;
		try {
			result = get(key);
		} catch (Exception e) {
			logger.error("getJsApiTicket " + e.getMessage());
		}
		return result;
	}

	/**
	 * 以openid为key来保存用户信息
	 * 
	 * @param openid
	 * @param user
	 */
	public void setUserInfo(String domain, String openid, UserInfo user) {
		String key = USER_PREDIX.replace(DOMAIN, domain).replace(OPENID, openid);
		try {
			JSONObject json = JSONObject.fromObject(user);
			set(key, json.toString(), ONEDAY);
		} catch (Exception e) {
			logger.error("setUserInfo " + e.getMessage());
		}
	}

	/**
	 * 根据openid查询用户信息
	 * 
	 * @param openid
	 * @return
	 */
	public UserInfo getUserInfo(String domain, String openid) {
		String key = USER_PREDIX.replace(DOMAIN, domain).replace(OPENID, openid);
		UserInfo user = null;
		try {
			String result = get(key);
			JSONObject json = JSONObject.fromObject(result);
			user = (UserInfo) JSONObject.toBean(json, UserInfo.class);
		} catch (Exception e) {
			logger.error("getUserInfo " + e.getMessage());
		}
		return user;
	}

	/**
	 * 保存一分钟微信授权后需要跳转的url
	 * 
	 * @param key
	 * @param url
	 */
	public void setReturnUrl(String key, String url) {
		try {
			set(key, url, MINUTES);
		} catch (Exception e) {
			logger.error("setReturnUrl " + e.getMessage());
		}
	}

	/**
	 * 获取微信授权后需要跳转的url
	 * 
	 * @param key
	 * @return
	 */
	public String getReturnUrl(String key) {
		String result = null;
		try {
			result = get(key);
		} catch (Exception e) {
			logger.error("getReturnUrl " + e.getMessage());
		}
		return result;
	}

}
