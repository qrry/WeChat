package com.noneykd.weixin.task;

import java.io.IOException;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.redis.service.WexinRedisService;
import com.noneykd.weixin.util.WeixinUtil;

/**
 * 定时获取微信AccessToken
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
	public void getAccessToken(){
    	AccessToken token;
		try {
			logger.debug("开始获取微信票据。");
			token = WeixinUtil.getAccessToken();
			if(token!=null){
				wexinRedisService.setToken(token.getToken());
				logger.info("获取到的票据:{}",token.getToken());
			}
			logger.debug("获取微信票据结束。");
		} catch (ParseException e) {
			logger.error("微信票出错：{}",e);
		} catch (IOException e) {
			logger.error("微信票出错：{}",e);
		}
    }

}
