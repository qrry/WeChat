package com.noneykd.weixin.rest.resource.response;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "接口统一错误状态返回信息", description = "当接口出现错误时，统一返回此格式")
public class ErrorResponse {
	@ApiModelProperty(value = "错误代码", notes = "HTTP错误代码")
	private Integer code;

	@ApiModelProperty(value = "内部错误信息", notes = "用于客户端开发人员阅读")
	private String innerMessage;

	@ApiModelProperty(value = "错误提示信息", notes = "用于展示给用户阅读的错误提示信息")
	private String customMessage;

	@ApiModelProperty(value = "更多信息", notes = "例如此错误参见...")
	private String moreInfo;

	public ErrorResponse(String customMessage, String innerMessage,
			Integer code, String moreInfo) {
		super();
		this.code = code;
		this.innerMessage = innerMessage;
		this.customMessage = customMessage;
		this.moreInfo = moreInfo;
	}

	public ErrorResponse(String customMessage, String innerMessage, Integer code) {
		super();
		this.code = code;
		this.innerMessage = innerMessage;
		this.customMessage = customMessage;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getInnerMessage() {
		return innerMessage;
	}

	public void setInnerMessage(String innerMessage) {
		this.innerMessage = innerMessage;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

}
