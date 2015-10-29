package com.noneykd.weixin.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.sf.json.JSONObject;

import org.apache.http.ParseException;


import com.noneykd.weixin.menu.Button;
import com.noneykd.weixin.menu.ClickButton;
import com.noneykd.weixin.menu.Menu;
import com.noneykd.weixin.menu.ViewButton;
import com.noneykd.weixin.po.AccessToken;
import com.noneykd.weixin.po.ApiTicket;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * 微信工具类
 * 
 * @author Stephen
 *
 */
public class WeixinUtil {

	// /**
	// * get请求
	// *
	// * @param url
	// * @return
	// * @throws ParseException
	// * @throws IOException
	// */
	// public static JSONObject doGetStr(String url) throws ParseException,
	// IOException {
	// DefaultHttpClient client = new DefaultHttpClient();
	// HttpGet httpGet = new HttpGet(url);
	// JSONObject jsonObject = null;
	// HttpResponse httpResponse = client.execute(httpGet);
	// HttpEntity entity = httpResponse.getEntity();
	// if (entity != null) {
	// String result = EntityUtils.toString(entity, "UTF-8");
	// jsonObject = JSONObject.fromObject(result);
	// }
	// return jsonObject;
	// }

	/**
	 * get请求
	 * 
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject doGetStr(String url) throws ParseException,
			IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			String result = response.body().string();
			return JSONObject.fromObject(result);
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	// /**
	// * POST请求
	// *
	// * @param url
	// * @param outStr
	// * @return
	// * @throws ParseException
	// * @throws IOException
	// */
	// public static JSONObject doPostStr(String url, String outStr)
	// throws ParseException, IOException {
	// DefaultHttpClient client = new DefaultHttpClient();
	// HttpPost httpost = new HttpPost(url);
	// JSONObject jsonObject = null;
	// httpost.setEntity(new StringEntity(outStr, "UTF-8"));
	// HttpResponse response = client.execute(httpost);
	// String result = EntityUtils.toString(response.getEntity(), "UTF-8");
	// jsonObject = JSONObject.fromObject(result);
	// return jsonObject;
	// }
	/**
	 * POST请求
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static JSONObject doPostStr(String url, String json)
			throws ParseException, IOException {
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(Constants.JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			String result = response.body().string();
			return JSONObject.fromObject(result);
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	/**
	 * 文件上传
	 * 
	 * @param filePath
	 * @param accessToken
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws KeyManagementException
	 */
	public static String upload(String filePath, String accessToken, String type)
			throws IOException, NoSuchAlgorithmException,
			NoSuchProviderException, KeyManagementException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("文件不存在");
		}

		String url = Constants.UPLOAD_URL.replace("ACCESS_TOKEN", accessToken)
				.replace("TYPE", type);

		URL urlObj = new URL(url);
		// 连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);

		// 设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		// 设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\""
				+ file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		// 获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		// 输出表头
		out.write(head);

		// 文件正文部分
		// 把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		// 结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		System.out.println(jsonObj);
		String typeName = "media_id";
		if (!"image".equals(type)) {
			typeName = type + "_media_id";
		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}

	/**
	 * 获取accessToken
	 * 
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static AccessToken getAccessToken() throws ParseException,
			IOException {
		AccessToken token = new AccessToken();
		String url = Constants.ACCESS_TOKEN_URL.replace("APPID",
				Constants.APPID).replace("APPSECRET", Constants.APPSECRET);
		JSONObject jsonObject = doGetStr(url);
		if (jsonObject != null) {
			token.setToken(jsonObject.getString("access_token"));
			token.setExpiresIn(jsonObject.getInt("expires_in"));
		}
		return token;
	}

	/**
	 * 通过code换取网页授权access_token
	 * 
	 * @param code
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject getUserAccessToken(String code)
			throws ParseException, IOException {
		String url = Constants.AUTHORIZE_TOKEN_URL
				.replace("APPID", Constants.APPID)
				.replace("SECRET", Constants.APPSECRET).replace("CODE", code);
		return doGetStr(url);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @param openid
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject getUserInfo(String token, String openid)
			throws ParseException, IOException {
		String url = Constants.USER_INFO_URL.replace("ACCESS_TOKEN", token)
				.replace("OPENID", openid).replace("zh_CN", Constants.LANG);
		return doGetStr(url);
	}

	/**
	 * 获取微信jsapi_ticket
	 * 
	 * @param token
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static ApiTicket getJsapiTicket(String token,String type) throws ParseException,
			IOException {
		ApiTicket apiTicket = new ApiTicket();
		String url = Constants.JSAPI_TICKET_URL.replace("ACCESS_TOKEN", token)
				.replace("TYPE", type);
		JSONObject jsonObject = doGetStr(url);
		doGetStr(url);
		if (!jsonObject.isEmpty() && jsonObject.getInt("errcode") == 0) {
			apiTicket = (ApiTicket) JSONObject.toBean(jsonObject,
					ApiTicket.class);
		}
		return apiTicket;
	}

	/**
	 * 组装菜单
	 * 
	 * @return
	 */
	public static Menu initMenu() {
		Menu menu = new Menu();
		ClickButton button11 = new ClickButton();
		button11.setName("主菜单");
		button11.setType("click");
		button11.setKey("11");

		ViewButton button21 = new ViewButton();
		button21.setName("百度一下");
		button21.setType("view");
		button21.setUrl("http://www.baidu.com");

		ClickButton button31 = new ClickButton();
		button31.setName("扫码事件");
		button31.setType("scancode_push");
		button31.setKey("31");

		ClickButton button32 = new ClickButton();
		button32.setName("地理位置");
		button32.setType("location_select");
		button32.setKey("32");

		Button button = new Button();
		button.setName("菜单");
		button.setSub_button(new Button[] { button31, button32 });

		menu.setButton(new Button[] { button11, button21, button });
		return menu;
	}

	public static int createMenu(String token, String menu)
			throws ParseException, IOException {
		int result = 0;
		String url = Constants.CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url, menu);
		if (jsonObject != null) {
			result = jsonObject.getInt("errcode");
		}
		return result;
	}

	public static JSONObject queryMenu(String token) throws ParseException,
			IOException {
		String url = Constants.QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
	}

	public static int deleteMenu(String token) throws ParseException,
			IOException {
		String url = Constants.DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		int result = 0;
		if (jsonObject != null) {
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
}
