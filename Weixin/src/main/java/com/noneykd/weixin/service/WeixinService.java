package com.noneykd.weixin.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.menu.Menu;
import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.po.ApiTicket;
import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.redis.service.WexinRedisService;
import com.noneykd.weixin.util.WeixinUtil;

@Service
public class WeixinService {

	private static final Logger logger = LoggerFactory
			.getLogger(WeixinService.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	/**
	 * 获取用户信息
	 * 
	 * @param openid
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ParseException
	 * @throws IOException
	 * @throws IllegalAccessError
	 */
	public UserInfo getUserInfo(String openid) throws IllegalArgumentException,
			ParseException, IOException, IllegalAccessError {
		if (StringUtils.isBlank(openid)) {
			throw new IllegalArgumentException("openid不能为空");
		}
		UserInfo user = wexinRedisService.getUserInfo(openid);
		if (user != null) {
			return user;
		} else {
			JSONObject jsonObject = WeixinUtil.getUserInfo(
					wexinRedisService.getToken(), openid);
			if (jsonObject != null) {
				user = (UserInfo) JSONObject.toBean(jsonObject, UserInfo.class);
			}
			if (user != null) {
				logger.debug("记录该用户信息：{}", openid);
				wexinRedisService.setUserInfo(openid, user);
				return user;
			} else {
				throw new IllegalAccessError("没有该openid：" + openid + "信息");
			}
		}
	}

	public String getToken() {
		String token = wexinRedisService.getToken();
		if (StringUtils.isBlank(token)) {
			AccessToken accessToken;
			try {
				logger.debug("开始获取微信票据。");
				accessToken = WeixinUtil.getAccessToken();
				if (accessToken != null) {
					token = accessToken.getToken();
					wexinRedisService.setToken(token);
					logger.info("获取到的票据:{}", accessToken.getToken());
				}
				logger.debug("获取微信票据结束。");
			} catch (ParseException e) {
				logger.error("微信票出错：{}", e);
			} catch (IOException e) {
				logger.error("微信票出错：{}", e);
			}
		}
		return token;
	}

	public String getJsApiTicket(String type) {
		String ticket = wexinRedisService.getJsApiTicket(type);
		if (StringUtils.isBlank(ticket)) {
			ApiTicket apiTicket = null;
			try {
				logger.debug("开始获取ticket。");
				String token = getToken();
				apiTicket = WeixinUtil.getJsapiTicket(token, type);
				if (apiTicket != null) {
					ticket = apiTicket.getTicket();
					wexinRedisService.setJsApiTicket(ticket, type);
					logger.info("获取到的ticket:{}", apiTicket.getTicket());
				}
				logger.debug("获取ticket结束。");
			} catch (ParseException e) {
				logger.error("获取ticket出错：{}", e);
			} catch (IOException e) {
				logger.error("获取ticket出错：{}", e);
			}
		}
		return ticket;
	}

	public int setMenu() throws ParseException, IOException {
		Menu menu = WeixinUtil.initMenu();
		String menuStr = JSONObject.fromObject(menu).toString();
		return WeixinUtil.createMenu(wexinRedisService.getToken(), menuStr);
	}

	public String upload(String imgurl) throws KeyManagementException,
			NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// String path = "D:/imooc.jpg";
		String mediaId = WeixinUtil.upload(imgurl,
				wexinRedisService.getToken(), "thumb");
		return mediaId;
	}

}
