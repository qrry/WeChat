package com.noneykd.weixin.rest.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noneykd.weixin.po.UserInfo;
import com.noneykd.weixin.rest.resource.response.ErrorResponse;
import com.noneykd.weixin.service.WeixinService;
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
	private static Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

	@Autowired
	private WeixinService weixinService;

	@GET
	@Path("/token")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "获取票据", notes = "获取微信票据接口", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "参数错误", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response token(@Context HttpServletRequest req) {
		try {
			String token = weixinService.getToken();
			Map<String, String> map = new HashMap<String, String>();
			map.put("token", token);
			return Response.status(Response.Status.OK).entity(map)
					.type(MediaType.APPLICATION_JSON_TYPE).build();

		} catch (Exception e) {
			LOGGER.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}

	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "获取user信息", notes = "获取微信用户信息接口", response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "参数错误", response = ErrorResponse.class),
			@ApiResponse(code = 500, message = "服务器内部错误", response = ErrorResponse.class),
			@ApiResponse(code = 200, message = "接口调用成功", response = ErrorResponse.class) })
	public Response user(@FormParam("openid") String openid,
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
			LOGGER.error("服务器内部错误", e);
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("服务器内部错误", e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), StringUtils.EMPTY))
					.type(MediaType.APPLICATION_JSON_TYPE).build());
		}

	}
}