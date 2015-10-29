package com.noneykd.weixin.rest.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.rest.resource.response.ErrorResponse;
import com.noneykd.weixin.service.WeixinService;
import com.noneykd.weixin.util.Constants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Component
@Path("/wx")
@Api(value = "/wx", description = "微信接口")
public class MainResource {

	/**
	 * 日志调试.
	 */
	private static Logger logger = LoggerFactory.getLogger(MainResource.class);

	@Autowired
	private WeixinService weixinService;

	@GET
	@Path("/token")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "获取票据", notes = "获取微信票据接口", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response token(@Context HttpServletRequest req) {
		try {
			String token = weixinService.getToken();
			JSONObject json = new JSONObject();
			json.put("token", token);
			return Response.status(Response.Status.OK).entity(json)
					.type(MediaType.APPLICATION_JSON_TYPE).build();

		} catch (Exception e) {
			logger.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}

	@GET
	@Path("/user")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "获取user信息", notes = "获取微信用户信息接口", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "参数错误", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response user(@QueryParam("openid") String openid,
			@Context HttpServletRequest req) {
		if (StringUtils.isBlank(openid)) {
			throw new WebApplicationException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("提交的openid为空",
							"openid id is null", Response.Status.BAD_REQUEST
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}
		try {
			UserInfo user = weixinService.getUserInfo(openid);
			return Response.status(Response.Status.OK).entity(user)
					.type(MediaType.APPLICATION_JSON_TYPE).build();

		} catch (Exception e) {
			logger.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}

	@GET
	@Path("/ticket")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "获取jsapi_ticket", notes = "获取jsapi_ticket接口,type可选{jsapi,wx_card}默认获取jsapi_ticket", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response ticket(@QueryParam("type") String type,
			@Context HttpServletRequest req) {
		String TYPE = type;
		if (StringUtils.isBlank(TYPE)) {
			TYPE = Constants.JS_TYPE;
		}
		try {
			String ticket = weixinService.getJsApiTicket(TYPE);
			JSONObject json = new JSONObject();
			json.put("ticket", ticket);
			return Response.status(Response.Status.OK).entity(json)
					.type(MediaType.APPLICATION_JSON_TYPE).build();

		} catch (Exception e) {
			logger.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}

	@GET
	@Path("/signature")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "签名", notes = "签名返回signature", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "参数错误", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response signature(@QueryParam("noncestr") String noncestr,
			@QueryParam("jsapi_ticket") String jsapi_ticket,
			@QueryParam("timestamp") String timestamp,
			@QueryParam("url") String url,
			@QueryParam("(可选)callback") String callback,
			@Context HttpServletRequest req) {
		if (StringUtils.isBlank(noncestr)) {
			throw new WebApplicationException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("提交的noncestr为空",
							"noncestr id is null", Response.Status.BAD_REQUEST
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}
		if (StringUtils.isBlank(jsapi_ticket)) {
			throw new WebApplicationException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("提交的jsapi_ticket为空",
							"jsapi_ticket id is null",
							Response.Status.BAD_REQUEST.getStatusCode(),
							StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}
		if (StringUtils.isBlank(timestamp)) {
			throw new WebApplicationException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("提交的timestamp为空",
							"timestamp id is null", Response.Status.BAD_REQUEST
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}
		if (StringUtils.isBlank(url)) {
			throw new WebApplicationException(Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("提交的url为空", "url id is null",
							Response.Status.BAD_REQUEST.getStatusCode(),
							StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}
		String _callback = callback;
		if (StringUtils.isBlank(_callback)) {
			_callback = "callback";
		}
		try {
			String signature = weixinService.signature(noncestr, jsapi_ticket,
					timestamp, url);
			JSONObject json = new JSONObject();
			json.put("signature", signature);
			json.put("noncestr", noncestr);
			json.put("jsapi_ticket", jsapi_ticket);
			json.put("timestamp", timestamp);
			json.put("url", url);
			return Response.status(Response.Status.OK)
					.entity(_callback + "(" + json + ")")
					.type(MediaType.APPLICATION_JSON_TYPE).build();

		} catch (Exception e) {
			logger.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}

}