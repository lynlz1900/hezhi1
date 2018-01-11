package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
* @ClassName: ResponseJsonArrInfo 
* @Description: TODO() 
* @author HuZhiyin 
* @date 2016-10-5 上午8:57:17 
* 
* @param <T>
 */
public class ResponseJsonArrInfo<T> implements Serializable {
	private int httpCode;
	private ArrayList<T> body;
	private String promptMessage;
	
	
	
	public String getPromptMessage() {
		return promptMessage;
	}
	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}
	public int getHttpCode() {
		return httpCode;
	}
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
	public ArrayList<T> getBody() {
		return body;
	}
	public void setBody(ArrayList<T> body) {
		this.body = body;
	}
	
}
