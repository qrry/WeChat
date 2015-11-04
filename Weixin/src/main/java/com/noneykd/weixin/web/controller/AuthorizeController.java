package com.noneykd.weixin.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noneykd.weixin.persist.redis.service.WexinRedisService;
import com.noneykd.weixin.po.UserAccessToken;
import com.noneykd.weixin.util.Constants;
import com.noneykd.weixin.util.WeixinUtil;

@Controller
@RequestMapping("/oauth")
public class AuthorizeController {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

	@Autowired
	private WexinRedisService wexinRedisService;

	@RequestMapping("redirect")
	public String redirect(HttpServletRequest req, HttpServletResponse resp, String code,
			String state, Model model) {
		if (StringUtils.isBlank(code)) {
			// code目前没有使用
		}
		if (StringUtils.isBlank(state)) {

		}
		String returnTo = wexinRedisService.getReturnUrl(state);
		UserAccessToken user = null;
		try {
			URL return_to_url = null;
			try {
				return_to_url = new URL(returnTo);
			} catch (MalformedURLException e1) {
				logger.error(e1.getMessage());
			}
			String domain = null;
			if (return_to_url != null) {
				domain = return_to_url.getAuthority();
			}
			if (StringUtils.isBlank(domain)) {
				domain = Constants.DEFAULT;
			}
			JSONObject json = WeixinUtil.getUserAccessToken(wexinRedisService.getAppid(domain),
					wexinRedisService.getSecret(domain), code);
			logger.info(json.toString());
			if (json != null) {
				if (json.get("errcode") == null) {
					user = (UserAccessToken) JSONObject.toBean(json, UserAccessToken.class);
				}
			}
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		if (user != null) {
			model.addAttribute("openid", user.getOpenid());
			Cookie cookie = new Cookie("openid", user.getOpenid());
			resp.addCookie(cookie);
		} else {
			model.addAttribute("openid", "null");
		}
		return "redirect:" + returnTo;
	}

	@RequestMapping("hello")
	public String hello(HttpServletRequest req, HttpServletResponse resp, String openid, Model model) {

		model.addAttribute("openid", openid);

		return "hello";
	}

	@RequestMapping
	public String oauth(HttpServletRequest req, HttpServletResponse resp, String return_to) {
		// 设置到微信认证后跳转路径
		StringBuffer redirectUrl = req.getRequestURL();
		redirectUrl.append("/redirect");
		// 保存用户返回的url到redis,方便后面获取
		String state = UUID.randomUUID().toString().replace("-", "");
		wexinRedisService.setReturnUrl(state, return_to);
		String domain = null;
		URL return_to_url = null;
		try {
			return_to_url = new URL(return_to);
		} catch (MalformedURLException e1) {
			logger.error(e1.getMessage());
		}
		// 如果用户传来的return_to中能够获取到域名，使用传来的url的域名下的微信服务
		if (return_to_url != null) {
			domain = return_to_url.getAuthority();
		}
		// 默认使用服务器所在域名
		if (StringUtils.isBlank(domain)) {
			domain = Constants.DEFAULT;
		}
		String rurl = null;// 为返回的url编码
		try {
			rurl = URLEncoder.encode(redirectUrl.toString(), CharEncoding.UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		String appid = wexinRedisService.getAppid(domain);
		String url = null;
		try {
			url = Constants.AUTHORIZE_URL.replace("APPID", appid).replace("REDIRECT_URI", rurl)
					.replace("SCOPE", Constants.BASE_SCOPE).replace("STATE", state);
			logger.info(url);
		} catch (NullPointerException e) {
			logger.error("参数错误：APPID:{},REDIRECT_URI:{},SCOPE:{},STATE:{}", appid, rurl,
					Constants.BASE_SCOPE, state);
			return "bizerror";
		}
		return "redirect:" + url;
	}

}
