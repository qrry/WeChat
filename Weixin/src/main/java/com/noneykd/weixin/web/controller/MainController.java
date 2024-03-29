package com.noneykd.weixin.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.noneykd.weixin.service.WeixinService;
import com.noneykd.weixin.util.MessageUtil;
import com.noneykd.weixin.util.WeixinUtil;

@Controller
@RequestMapping("/wx")
public class MainController {

	//@Value支持"${app.token}"这种方式读取配置，没搞明白，暂时用这种方式读取
	@Value("#{app.token}")
	private String token;

	// 日志调试
	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	private WeixinService weixinService;

	/**
	 * 接入验证
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.debug("开始接入验证,signature:{},timestamp:{},nonce:{},echostr:{}.", signature, timestamp,
				nonce, echostr);
		PrintWriter out = resp.getWriter();
		if (checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
	}

	/**
	 * 消息的接收与响应
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			Map<String, String> map = MessageUtil.xmlToMap(req);
			String message = weixinService.autoresponce(map);
			out.print(message);
		} catch (DocumentException e) {
			logger.error(e.getMessage());
		} finally {
			out.close();
		}
	}

	/**
	 * 验证签名
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	private boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		
		String temp = WeixinUtil.signature(null, arr);

		return temp.equals(signature);
	}
}
