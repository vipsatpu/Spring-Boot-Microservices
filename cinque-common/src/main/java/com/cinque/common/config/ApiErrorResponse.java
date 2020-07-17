package com.cinque.common.config;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class ApiErrorResponse extends ApiResponse{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(required = true, notes = "Status")
	private Integer status;
	@ApiModelProperty(required = true, notes = "Error code")
	private Integer errorCode;
	@ApiModelProperty(required = true, notes = "Brief summary of error")
	private String message;
	@ApiModelProperty(required = false, notes = "Detailed Message of error")
	private String detailedMessage;
	@ApiModelProperty(required = false, notes = "More information about error")
	private String moreInfo;
	
	public ApiErrorResponse() {

	}
	
	public ApiErrorResponse(Integer status, Integer errorCode, String message, String detailedMessage,
			String moreInfo) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
		this.detailedMessage = detailedMessage;
		this.moreInfo = moreInfo;
	}

	public ApiErrorResponse(Integer errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetailedMessage() {
		return detailedMessage;
	}

	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}	
}
