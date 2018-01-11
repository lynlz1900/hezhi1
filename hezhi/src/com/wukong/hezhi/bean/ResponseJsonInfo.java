package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: ResponseJsonInfo
 * @Description: TODO(后台返回通用格式)
 * @author HuZhiyin
 * @date 2016-12-30 下午4:20:53
 * 
 * @param <Body>
 */
public class ResponseJsonInfo<Body> implements Serializable {
	private String httpCode;
	private Body body;
	private String promptMessage;

	public String getPromptMessage() {
		return promptMessage;
	}

	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

}
