package com.wukong.hezhi.bean;

import java.io.Serializable;
//"title": "劲嘉",
//"ossUrl": "https://hezhicloud.oss-cn-shenzhen.aliyuncs.com/perceptionLogo/2017-05-26-62d8d77dbba046538f93c9b0bca6bc74.jpg",
//"ar": false,
//"resourceType": "MP4",
//"maxNum": 15411,
//"ppid": 66,
//"targetId": "5bae9b8ab7594848977b8c0561c3422c",
//"hasRedPack": true,
//"businessId": 1,
//"receiveMark": "2"

public class VuforiaInfo implements Serializable {
	private String title;// 应用名称+品牌
	private String targetId;// 高通产生的targetId
	private int maxNum;// 观看次数
	private boolean ar;// 是否是ar应用
	private String ossUrl;// 图片链接
	private boolean hasRedPack;// 是否显示红包
	private String ppid;// 感知应用ID
	private int businessId;// 商户id
	private int receiveMark;// 1表示已领取 2 未领取 或者 没有红包
	private String resourceType;
	private String urlHttp;
	
	
	public String getUrlHttp() {
		return urlHttp;
	}

	public void setUrlHttp(String urlHttp) {
		this.urlHttp = urlHttp;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public int getReceiveMark() {
		return receiveMark;
	}

	public void setReceiveMark(int receiveMark) {
		this.receiveMark = receiveMark;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public boolean isAr() {
		return ar;
	}

	public void setAr(boolean ar) {
		this.ar = ar;
	}

	public boolean isHasRedPack() {
		return hasRedPack;
	}

	public void setHasRedPack(boolean hasRedPack) {
		this.hasRedPack = hasRedPack;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetId() {
		return this.targetId;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public int getMaxNum() {
		return this.maxNum;
	}

	public void setOssUrl(String ossUrl) {
		this.ossUrl = ossUrl;
	}

	public String getOssUrl() {
		return this.ossUrl;
	}
}
