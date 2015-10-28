package com.noneykd.weixin.util;

import com.squareup.okhttp.MediaType;

public class Constants {

	//微信appid
	public static final String APPID = "wx722baeed0b329f2a".intern();
	//微信appsecret
	public static final String APPSECRET = "689b84fe6646abd48cd952b10a62f044".intern();
	//语言设置
	public static final String LANG = "zh_CN".intern();
	//简单授权方式
	public static final String BASE_SCOPE = "snsapi_base".intern();
	//需要用户点击的授权
	public static final String USERINFO_SCOPE = "snsapi_userinfo".intern();
	//授权state
	public static final String STATE = "STATE".intern();
	//post请求传输格式
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	//获取access_token
	public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//授权
	public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	//通过授权的code拿用户access_token
	public static final String AUTHORIZE_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	//获取用户信息
	public static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	//上传
	public static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	//创建菜单，及修改
	public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	//查询菜单
	public static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	//删除菜单
	public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
}
