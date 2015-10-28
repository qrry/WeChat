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
			JSONObject jsonObject = WeixinUtil.getUserInfo(wexinRedisService.getToken(), openid);
			if (jsonObject != null) {
				user = (UserInfo) JSONObject.toBean(jsonObject, UserInfo.class);
			}
			if (user != null) {
				logger.debug("记录该用户信息：{}",openid);
				wexinRedisService.setUserInfo(openid, user);
				return user;
			} else {
				throw new IllegalAccessError("没有该openid：" + openid + "信息");
			}
		}
	}
	
	public int setMenu() throws ParseException, IOException {
		Menu menu = WeixinUtil.initMenu();
		String menuStr = JSONObject.fromObject(menu).toString();
		return WeixinUtil.createMenu(wexinRedisService.getToken(), menuStr);
	}
	
	public String upload(String imgurl) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
//		String path = "D:/imooc.jpg";
		String mediaId = WeixinUtil.upload(imgurl, wexinRedisService.getToken(), "thumb");
		return mediaId;
	}
	

}
