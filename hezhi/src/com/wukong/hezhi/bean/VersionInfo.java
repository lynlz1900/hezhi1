package com.wukong.hezhi.bean;

public class VersionInfo {
	private int versionsCode;// 版本号
	private String forceUpdateType;// 是否强制升级，是,TRUE，否,FALSE

	public void setVersionsCode(int versionsCode) {
		this.versionsCode = versionsCode;
	}

	public int getVersionsCode() {
		return this.versionsCode;
	}

	public String getForceUpdateType() {
		return forceUpdateType;
	}

	public void setForceUpdateType(String forceUpdateType) {
		this.forceUpdateType = forceUpdateType;
	}

}