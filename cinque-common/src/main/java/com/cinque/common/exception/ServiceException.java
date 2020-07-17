package com.cinque.common.exception;

public class ServiceException extends Exception{

	private static final long serialVersionUID = 11L;

	private Integer errorCode;
	private String message;
	private String description;
	private String moreInfo;

	public ServiceException(final int errorCode, final String message, final String description) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.description = description;
	}

	public ServiceException(final int errorCode, final String message, final String description, final String moreInfo) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.description = description;
		this.moreInfo = moreInfo;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}
}
