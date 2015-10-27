package com.noneykd.weixin.service;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.redis.service.WexinRedisService;
import com.noneykd.weixin.util.WeixinUtil;

@Service
public class WeixinService {

	private static final Logger logger = LoggerFactory
			.getLogger(WeixinService.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	public UserInfo getUserInfo(String openid) throws IllegalArgumentException,
			ParseException, IOException, IllegalAccessError {
		if (StringUtils.isBlank(openid)) {
			throw new IllegalArgumentException("openid不能为空");
		}
		UserInfo user = wexinRedisService.getUserInfo(openid);
		if (user != null) {
			return user;
		} else {
			user = WeixinUtil.getUserInfo(wexinRedisService.getToken(), openid);
			if (user != null) {
				logger.debug("记录该用户信息：{}",openid);
				wexinRedisService.setUserInfo(openid, user);
				return user;
			} else {
				throw new IllegalAccessError("没有该openid：" + openid + "信息");
			}
		}
	}

}
