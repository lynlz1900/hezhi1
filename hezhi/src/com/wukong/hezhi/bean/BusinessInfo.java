package com.wukong.hezhi.bean;

import java.io.Serializable;

public class BusinessInfo implements Serializable {
	private String title;
	private String ossUrl;
	private boolean ar;
	private int maxNum;
	private int ppid;
	private String targetId;
	private boolean hasRedPack;
	private int businessId;
	private int receiveMark;// 1表示已领取 2 未领取 或者 没有红包
	private String resourceType;
	private String urlHttp;
	
	
	
	
	public int getReceiveMark() {
		return receiveMark;
	}

	public void setReceiveMark(int receiveMark) {
		this.receiveMark = receiveMark;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getUrlHttp() {
		return urlHttp;
	}

	public void setUrlHttp(String urlHttp) {
		this.urlHttp = urlHttp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOssUrl() {
		return ossUrl;
	}

	public void setOssUrl(String ossUrl) {
		this.ossUrl = ossUrl;
	}

	public boolean isAr() {
		return ar;
	}

	public void setAr(boolean ar) {
		this.ar = ar;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public int getPpid() {
		return ppid;
	}

	public void setPpid(int ppid) {
		this.ppid = ppid;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public boolean isHasRedPack() {
		return hasRedPack;
	}

	public void setHasRedPack(boolean hasRedPack) {
		this.hasRedPack = hasRedPack;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

}
