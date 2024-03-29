package com.noneykd.weixin.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.menu.Menu;
import com.noneykd.weixin.persist.redis.service.WexinRedisService;
import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.po.ApiTicket;
import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.util.MessageUtil;
import com.noneykd.weixin.util.WeixinUtil;

@Service
public class WeixinService {

	private static final Logger logger = LoggerFactory.getLogger(WeixinService.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	/**
	 * @see com.noneykd.weixin.persist.redis.service.WexinRedisService.isInDomain(String)
	 * @param domain
	 * @return
	 */
	public boolean isInDomain(String domain){
		return wexinRedisService.isInDomain(domain);
	}
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
	public UserInfo getUserInfo(String openid, String domain) throws IllegalArgumentException,
			ParseException, IOException, IllegalAccessError {
		if (StringUtils.isBlank(openid)) {
			throw new IllegalArgumentException("openid不能为空");
		}
		UserInfo user = wexinRedisService.getUserInfo(domain, openid);
		if (user != null) {
			return user;
		} else {
			JSONObject jsonObject = WeixinUtil.getUserInfo(wexinRedisService.getToken(domain),
					openid);
			if (jsonObject != null) {
				user = (UserInfo) JSONObject.toBean(jsonObject, UserInfo.class);
			}
			if (user != null) {
				logger.debug("记录该用户信息：{}", openid);
				wexinRedisService.setUserInfo(domain, openid, user);
				return user;
			} else {
				throw new IllegalAccessError("没有该openid：" + openid + "信息");
			}
		}
	}

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public String getToken(String domain) {
		String token = wexinRedisService.getToken(domain);
		if (StringUtils.isBlank(token)) {
			AccessToken accessToken;
			try {
				logger.debug("开始获取微信票据。");
				accessToken = WeixinUtil.getAccessToken(wexinRedisService.getAppid(domain),
						wexinRedisService.getSecret(domain));
				if (accessToken != null) {
					token = accessToken.getToken();
					wexinRedisService.setToken(token, domain);
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

	/**
	 * 获取jsapi_ticket
	 * 
	 * @param type
	 * @return
	 */
	public String getJsApiTicket(String type, String domain) {
		String ticket = wexinRedisService.getJsApiTicket(domain, type);
		if (StringUtils.isBlank(ticket)) {
			ApiTicket apiTicket = null;
			try {
				logger.debug("开始获取ticket。");
				String token = getToken(domain);
				apiTicket = WeixinUtil.getJsapiTicket(token, type);
				if (apiTicket != null) {
					ticket = apiTicket.getTicket();
					wexinRedisService.setJsApiTicket(domain, ticket, type);
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

	/**
	 * 签名
	 * 
	 * @param noncestr
	 * @param jsapi_ticket
	 * @param timestamp
	 * @param url
	 * @return
	 */
	public String signature(String noncestr, String jsapi_ticket, String timestamp, String url) {
		String[] arr = new String[] { "noncestr=" + noncestr, "jsapi_ticket=" + jsapi_ticket,
				"timestamp=" + timestamp, "url=" + url };
		String signature = WeixinUtil.signature("&", arr);
		logger.info("待签名字符串：{},签名：{}",arr, signature);
		return signature;
	}

	public int setMenu(String domain) throws ParseException, IOException {
		Menu menu = WeixinUtil.initMenu();
		String menuStr = JSONObject.fromObject(menu).toString();
		return WeixinUtil.createMenu(wexinRedisService.getToken(domain), menuStr);
	}

	public String upload(String imgurl, String domain) throws KeyManagementException,
			NoSuchAlgorithmException, NoSuchProviderException, IOException {
		// String path = "D:/imooc.jpg";
		String mediaId = WeixinUtil.upload(imgurl, wexinRedisService.getToken(domain), "thumb");
		return mediaId;
	}

	/**
	 * 自动回复
	 * 
	 * @param map
	 * @return
	 */
	public String autoresponce(Map<String, String> map) {
		String fromUserName = map.get("FromUserName");
		String toUserName = map.get("ToUserName");
		String msgType = map.get("MsgType");
		String content = map.get("Content");
		String message = null;
		if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {
			if ("1".equals(content)) {
				message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstMenu());
			} else if ("2".equals(content)) {
				message = MessageUtil.initNewsMessage(toUserName, fromUserName);
			} else if ("3".equals(content)) {
				message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.threeMenu());
			} else if ("?".equals(content) || "？".equals(content)) {
				message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
			} else if (content.startsWith("翻译")) {
				String word = content.replaceAll("^翻译", "").trim();
				if ("".equals(word)) {
					message = MessageUtil.initText(toUserName, fromUserName,
							MessageUtil.threeMenu());
				} else {
					try {
						message = MessageUtil.initText(toUserName, fromUserName,
								MessageUtil.translate(word));
					} catch (ParseException e) {
						logger.error(e.getMessage());
					} catch (IOException e) {
						logger.error(e.getMessage());
					} finally {
						message = StringUtils.isBlank(message) ? "System error." : message;
					}
				}
			}
		} else if (MessageUtil.MESSAGE_EVNET.equals(msgType)) {
			String eventType = map.get("Event");
			if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {
				message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
			} else if (MessageUtil.MESSAGE_CLICK.equals(eventType)) {
				message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
			} else if (MessageUtil.MESSAGE_VIEW.equals(eventType)) {
				String url = map.get("EventKey");
				message = MessageUtil.initText(toUserName, fromUserName, url);
			} else if (MessageUtil.MESSAGE_SCANCODE.equals(eventType)) {
				String key = map.get("EventKey");
				message = MessageUtil.initText(toUserName, fromUserName, key);
			}
		} else if (MessageUtil.MESSAGE_LOCATION.equals(msgType)) {
			String label = map.get("Label");
			message = MessageUtil.initText(toUserName, fromUserName, label);
		}
		logger.info(message);
		return message;
	}

}
