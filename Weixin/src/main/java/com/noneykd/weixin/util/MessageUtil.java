package com.noneykd.weixin.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.http.ParseException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.noneykd.weixin.po.Image;
import com.noneykd.weixin.po.ImageMessage;
import com.noneykd.weixin.po.Music;
import com.noneykd.weixin.po.MusicMessage;
import com.noneykd.weixin.po.News;
import com.noneykd.weixin.po.NewsMessage;
import com.noneykd.weixin.po.TextMessage;
import com.noneykd.weixin.trans.Data;
import com.noneykd.weixin.trans.Parts;
import com.noneykd.weixin.trans.Symbols;
import com.noneykd.weixin.trans.TransResult;
import com.thoughtworks.xstream.XStream;

/**
 * 消息封装类
 * 
 * @author Stephen
 *
 */
public class MessageUtil {

	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_NEWS = "news";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_MUSIC = "music";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_EVNET = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	public static final String MESSAGE_SCANCODE = "scancode_push";

	public static String url = "http://localhost:8080/";
	static {
		
	}
	/**
	 * xml转为map集合
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> xmlToMap(HttpServletRequest request)
			throws IOException, DocumentException {
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();

		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);

		Element root = doc.getRootElement();

		List<Element> list = root.elements();

		for (Element e : list) {
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}

	/**
	 * 将文本消息对象转为xml
	 * 
	 * @param textMessage
	 * @return
	 */
	public static String textMessageToXml(TextMessage textMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", textMessage.getClass());
		return xstream.toXML(textMessage);
	}

	/**
	 * 组装文本消息
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @return
	 */
	public static String initText(String toUserName, String fromUserName,
			String content) {
		TextMessage text = new TextMessage();
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MessageUtil.MESSAGE_TEXT);
		text.setCreateTime(new Date().getTime());
		text.setContent(content);
		return textMessageToXml(text);
	}

	/**
	 * 主菜单
	 * 
	 * @return
	 */
	public static String menuText() {
		StringBuffer sb = new StringBuffer();
		sb.append("欢迎您的关注，请按照菜单提示进行操作：\n\n");
		sb.append("1、简介\n");
		sb.append("2、待定\n");
		sb.append("3、词组翻译\n\n");
		sb.append("回复？调出此菜单。");
		return sb.toString();
	}

	public static String firstMenu() {
		StringBuffer sb = new StringBuffer();
		sb.append("该公众号暂时开发中，没有什么新东西");
		return sb.toString();
	}

	public static String secondMenu() {
		StringBuffer sb = new StringBuffer();
		sb.append("部分代码来自慕课网，有兴趣的同学可以去看看。");
		return sb.toString();
	}

	public static String threeMenu() {
		StringBuffer sb = new StringBuffer();
		sb.append("词组翻译使用指南\n\n");
		sb.append("使用示例：\n");
		sb.append("翻译足球\n");
		sb.append("翻译中国足球\n");
		sb.append("翻译football\n\n");
		sb.append("回复？显示主菜单。");
		return sb.toString();
	}

	/**
	 * 图文消息转为xml
	 * 
	 * @param newsMessage
	 * @return
	 */
	public static String newsMessageToXml(NewsMessage newsMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new News().getClass());
		return xstream.toXML(newsMessage);
	}

	/**
	 * 图片消息转为xml
	 * 
	 * @param imageMessage
	 * @return
	 */
	public static String imageMessageToXml(ImageMessage imageMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", imageMessage.getClass());
		return xstream.toXML(imageMessage);
	}

	/**
	 * 音乐消息转为xml
	 * 
	 * @param musicMessage
	 * @return
	 */
	public static String musicMessageToXml(MusicMessage musicMessage) {
		XStream xstream = new XStream();
		xstream.alias("xml", musicMessage.getClass());
		return xstream.toXML(musicMessage);
	}

	/**
	 * 图文消息的组装
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String initNewsMessage(String toUserName, String fromUserName) {
		String message = null;
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();

		News news = new News();
		news.setTitle("公众号介绍");
		news.setDescription("慕课网是垂直的互联网IT技能免费学习网站。以独家视频教程、在线编程工具、学习计划、问答社区为核心特色。在这里，你可以找到最好的互联网技术牛人，也可以通过免费的在线公开视频课程学习国内领先的互联网IT技术。慕课网课程涵盖前端开发、PHP、Html5、Android、iOS、Swift等IT前沿技术语言，包括基础课程、实用案例、高级分享三大类型，适合不同阶段的学习人群。");
		news.setPicUrl("http://noneykd.6655.la/Weixin/images/imooc.jpg");
		news.setUrl("www.imooc.com");

		newsList.add(news);

		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType(MESSAGE_NEWS);
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());

		message = newsMessageToXml(newsMessage);
		return message;
	}

	/**
	 * 组装图片消息
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String initImageMessage(String toUserName, String fromUserName) {
		String message = null;
		Image image = new Image();
		image.setMediaId("JTH8vBl0zDRlrrn2bBnMleySuHjVbMhyAo0U2x7kQyd1ciydhhsVPONbnRrKGp8m");
		ImageMessage imageMessage = new ImageMessage();
		imageMessage.setFromUserName(toUserName);
		imageMessage.setToUserName(fromUserName);
		imageMessage.setMsgType(MESSAGE_IMAGE);
		imageMessage.setCreateTime(new Date().getTime());
		imageMessage.setImage(image);
		message = imageMessageToXml(imageMessage);
		return message;
	}

	/**
	 * 组装音乐消息
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @return
	 */
	public static String initMusicMessage(String toUserName, String fromUserName) {
		String message = null;
		Music music = new Music();
		music.setThumbMediaId("WsHCQr1ftJQwmGUGhCP8gZ13a77XVg5Ah_uHPHVEAQuRE5FEjn-DsZJzFZqZFeFk");
		music.setTitle("see you again");
		music.setDescription("速7片尾曲");
		music.setMusicUrl("http://zapper.tunnel.mobi/Weixin/resource/See You Again.mp3");
		music.setHQMusicUrl("http://zapper.tunnel.mobi/Weixin/resource/See You Again.mp3");

		MusicMessage musicMessage = new MusicMessage();
		musicMessage.setFromUserName(toUserName);
		musicMessage.setToUserName(fromUserName);
		musicMessage.setMsgType(MESSAGE_MUSIC);
		musicMessage.setCreateTime(new Date().getTime());
		musicMessage.setMusic(music);
		message = musicMessageToXml(musicMessage);
		return message;
	}

	public static String translate(String source) throws ParseException,
			IOException {
		String url = "http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
		url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
		JSONObject jsonObject = WeixinUtil.doGetStr(url);
		String errno = jsonObject.getString("errno");
		Object obj = jsonObject.get("data");
		StringBuffer dst = new StringBuffer();
		if ("0".equals(errno) && !"[]".equals(obj.toString())) {
			TransResult transResult = (TransResult) JSONObject.toBean(
					jsonObject, TransResult.class);
			Data data = transResult.getData();
			Symbols symbols = data.getSymbols()[0];
			String phzh = symbols.getPh_zh() == null ? "" : "中文拼音："
					+ symbols.getPh_zh() + "\n";
			String phen = symbols.getPh_en() == null ? "" : "英式英标："
					+ symbols.getPh_en() + "\n";
			String pham = symbols.getPh_am() == null ? "" : "美式英标："
					+ symbols.getPh_am() + "\n";
			dst.append(phzh + phen + pham);

			Parts[] parts = symbols.getParts();
			String pat = null;
			for (Parts part : parts) {
				pat = (part.getPart() != null && !"".equals(part.getPart())) ? "["
						+ part.getPart() + "]"
						: "";
				String[] means = part.getMeans();
				dst.append(pat);
				for (String mean : means) {
					dst.append(mean + ";");
				}
			}
		} else {
			dst.append(translateFull(source));
		}
		return dst.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String translateFull(String source) throws ParseException,
			IOException {
		String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
		url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
		JSONObject jsonObject = WeixinUtil.doGetStr(url);
		StringBuffer dst = new StringBuffer();
		List<Map> list = (List<Map>) jsonObject.get("trans_result");
		for (Map map : list) {
			dst.append(map.get("dst"));
		}
		return dst.toString();
	}
}
