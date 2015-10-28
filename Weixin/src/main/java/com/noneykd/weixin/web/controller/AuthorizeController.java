package com.noneykd.weixin.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noneykd.weixin.po.UserAccessToken;
import com.noneykd.weixin.util.Constants;
import com.noneykd.weixin.util.WeixinUtil;

@Controller
@RequestMapping("/oauth")
public class AuthorizeController {
	
	private static final String redirectUrl = "http://noneykd.6655.la/Weixin/wx/oauth/redirect".intern();
	private static String returnTo;
	private static final Logger logger = LoggerFactory
			.getLogger(AuthorizeController.class);

	@RequestMapping("redirect")
	public String redirect(HttpServletRequest req, HttpServletResponse resp,String code,String state,Model model) {
		UserAccessToken user = null;
		if(StringUtils.isNotBlank(code)){
			try {
				JSONObject json = WeixinUtil.getUserAccessToken(code);
				logger.info(json.toString());
				if(json!=null){
					if(json.get("errcode")==null){
						user = (UserAccessToken) JSONObject.toBean(json,UserAccessToken.class);
					}
				}
			} catch (ParseException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			if(user!=null){
				model.addAttribute("openid", user.getOpenid());
			}else{
				model.addAttribute("openid", "null");
			}
		}
//		return "redirect:http://noneykd.6655.la/Weixin/wx/oauth/hello";
		return "redirect:"+returnTo;
	}
	
	@RequestMapping("hello")
	public String hello(HttpServletRequest req, HttpServletResponse resp,String openid, Model model) {

		model.addAttribute("openid", openid);

		return "hello";
	}
	
	@RequestMapping("")
	public String oauth(HttpServletRequest req, HttpServletResponse resp, String return_to) {
		String rurl = null;
		returnTo = return_to;
		try {
			rurl = URLEncoder.encode(redirectUrl,CharEncoding.UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		String url = Constants.AUTHORIZE_URL.replace("APPID", Constants.APPID)
				.replace("REDIRECT_URI", rurl).replace("SCOPE", Constants.BASE_SCOPE)
				.replace("STATE", Constants.STATE);
		logger.info(url);
		return "redirect:"+url;
	}
}
