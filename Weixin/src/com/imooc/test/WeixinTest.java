package com.imooc.test;

import net.sf.json.JSONObject;

import com.imooc.menu.Menu;
import com.imooc.po.AccessToken;
import com.imooc.util.WeixinUtil;

public class WeixinTest {
	public static void main(String[] args) {
		try {
			AccessToken token = WeixinUtil.getAccessToken();
			System.out.println("票据"+token.getToken());
			System.out.println("有效时间"+token.getExpiresIn());
			
			//String path = "D:/imooc.jpg";
			//String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
			//System.out.println(mediaId);
			
			Menu menu = WeixinUtil.initMenu();
			String menuStr = JSONObject.fromObject(menu).toString();
			int result = WeixinUtil.createMenu(token.getToken(), menuStr);
			System.out.println(result);
			
//			String result = WeixinUtil.translate("my name is laobi");
			//String result = WeixinUtil.translateFull("");
//			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
