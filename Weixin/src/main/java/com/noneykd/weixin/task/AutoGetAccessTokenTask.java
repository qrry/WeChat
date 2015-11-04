package com.noneykd.weixin.task;

import java.io.IOException;
import java.util.Set;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.persist.redis.service.WexinRedisService;
import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.po.ApiTicket;
import com.noneykd.weixin.util.Constants;
import com.noneykd.weixin.util.WeixinUtil;

/**
 * 定时获取微信AccessToken\jsapi_ticket\卡券api_ticket
 * 
 * @author zoukai
 *
 */
@Service
public class AutoGetAccessTokenTask {

	private static int count = 0;

	private static final Logger logger = LoggerFactory.getLogger(AutoGetAccessTokenTask.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	@Scheduled(cron = "0 */30 * * * ?")
	public void getAccessToken() {
		if (count < 3) {
			count++;
		} else {
			count = 0;
			AccessToken token;
			try {
				Set<String> domains = wexinRedisService.getDomains();
				logger.debug("开始获取微信票据、卡券api_ticket、jsapi_ticket。");
				for (String domain : domains) {
					token = WeixinUtil.getAccessToken(wexinRedisService.getAppid(domain),
							wexinRedisService.getSecret(domain));
					if (token != null) {
						wexinRedisService.setToken(token.getToken(), domain);
						logger.info("获取到的微信票据:{}", token.getToken());
						ApiTicket kqticket = WeixinUtil.getJsapiTicket(token.getToken(),
								Constants.KQ_TYPE);
						if (kqticket != null) {
							wexinRedisService.setJsApiTicket(domain, kqticket.getTicket(),
									Constants.KQ_TYPE);
							logger.info("获取到的卡券jsapi_ticket:{}", token.getToken());
						}
						ApiTicket jsticket = WeixinUtil.getJsapiTicket(token.getToken(),
								Constants.JS_TYPE);
						if (jsticket != null) {
							wexinRedisService.setJsApiTicket(domain, jsticket.getTicket(),
									Constants.JS_TYPE);
							logger.info("获取到的jsapi_ticket:{}", token.getToken());
						}
					}
				}
				logger.debug("获取微信票据、卡券api_ticket、jsapi_ticket结束。");
			} catch (ParseException e) {
				logger.error("getAccessToken出错：{}", e);
			} catch (IOException e) {
				logger.error("getAccessToken出错：{}", e);
			}
		}
	}
}
