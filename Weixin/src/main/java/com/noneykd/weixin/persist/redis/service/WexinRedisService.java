package com.noneykd.weixin.persist.redis.service;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.util.Constants;

@Component
public class WexinRedisService extends RedisBaseService {

	private static final Logger logger = LoggerFactory
			.getLogger(WexinRedisService.class);

	private final static String WEIXIN_TOKEN = "WX_TOKEN".intern();
	private final static String JSAPI_TICKET = "JSAPI_TICKET".intern();//jsapi
	private final static String KQAPI_TICKET = "KQAPI_TICKET".intern();//卡券api
	private final static String USER_PREDIX = "WX_USER_OPENID".intern();

	/**
	 * 保存token
	 * 
	 * @param token
	 */
	public void setToken(String token) {
		try {
			set(WEIXIN_TOKEN, token, HOURS * 2);
		} catch (Exception e) {
			logger.error("setToken " + e.getMessage());
		}
	}

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public String getToken() {
		String result = null;
		try {
			result = get(WEIXIN_TOKEN);
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
	public void setJsApiTicket(String ticket, String type) {
		String key = JSAPI_TICKET;
		if(StringUtils.isNotBlank(type)&&type.equals(Constants.KQ_TYPE)){
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
	public String getJsApiTicket(String type) {
		String key = JSAPI_TICKET;
		if(StringUtils.isNotBlank(type)&&type.equals(Constants.KQ_TYPE)){
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
	public void setUserInfo(String openid, UserInfo user) {
		String key = USER_PREDIX.replace("OPENID", openid);
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
	public UserInfo getUserInfo(String openid) {
		String key = USER_PREDIX.replace("OPENID", openid);
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
