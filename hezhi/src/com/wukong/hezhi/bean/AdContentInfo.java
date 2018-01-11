package com.wukong.hezhi.bean;

import java.io.Serializable;

public class AdContentInfo implements Serializable {
	private String type;// 可选 PICTURE VIDEO 文件类型
	private String httpUrl;// 文件url
	private String jumpUrl;// 跳转url
	private String text;// 显示文字

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public String getJumpUrl() {
		return jumpUrl;
	}

	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
