package com.wukong.hezhi.bean;

import java.io.Serializable;

public class JgExtraInfo implements Serializable {
	private String icon;
	private String corpName;
	private String userId;
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCorpName() {
		return corpName;
	}
	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}	
