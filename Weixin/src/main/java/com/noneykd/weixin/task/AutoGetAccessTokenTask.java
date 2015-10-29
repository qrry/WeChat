package com.noneykd.weixin.task;

import java.io.IOException;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.po.ApiTicket;
import com.noneykd.weixin.redis.service.WexinRedisService;
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

	private static final Logger logger = LoggerFactory
			.getLogger(AutoGetAccessTokenTask.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	@Scheduled(cron = "0 0 */2 * * ?")
	public void getAccessToken() {
		AccessToken token;
		try {
			logger.debug("开始获取微信票据、卡券api_ticket、jsapi_ticket。");
			token = WeixinUtil.getAccessToken();
			if (token != null) {
				wexinRedisService.setToken(token.getToken());
				logger.info("获取到的微信票据:{}", token.getToken());
				ApiTicket kqticket = WeixinUtil.getJsapiTicket(
						token.getToken(), Constants.KQ_TYPE);
				if (kqticket != null) {
					wexinRedisService.setJsApiTicket(kqticket.getTicket(),
							Constants.KQ_TYPE);
					logger.info("获取到的卡券jsapi_ticket:{}", token.getToken());
				}
				ApiTicket jsticket = WeixinUtil.getJsapiTicket(
						token.getToken(), Constants.JS_TYPE);
				if (jsticket != null) {
					wexinRedisService.setJsApiTicket(jsticket.getTicket(),
							Constants.JS_TYPE);
					logger.info("获取到的jsapi_ticket:{}", token.getToken());
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
