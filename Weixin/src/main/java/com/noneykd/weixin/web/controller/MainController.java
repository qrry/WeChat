package com.noneykd.weixin.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.noneykd.weixin.service.WeixinService;
import com.noneykd.weixin.util.MessageUtil;

@Controller
@RequestMapping("/wx")
public class MainController {

	private static final String token = "noneykd".intern();

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
		logger.debug("开始接入验证,signature:{},timestamp:{},nonce:{},echostr:{}.", signature, timestamp, nonce, echostr);
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

	private boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// 排序
		Arrays.sort(arr);

		// 生成字符串
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}

		// sha1加密
		String temp = DigestUtils.sha1Hex(content.toString());

		return temp.equals(signature);
	}
}
