package com.wukong.hezhi.bean;

import java.io.Serializable;

public class UnityResourceInfo implements Serializable {
	private String fileName;
	private String resourceType;//资源类型(MP4,UNITY3D,PICTURE，URL，PDF)
	private String ossUrl;
	private String AndroidUrl;
	private String IOSUrl;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getOssUrl() {
		return ossUrl;
	}

	public void setOssUrl(String ossUrl) {
		this.ossUrl = ossUrl;
	}

	public String getAndroidUrl() {
		return AndroidUrl;
	}

	public void setAndroidUrl(String androidUrl) {
		AndroidUrl = androidUrl;
	}

	public String getIOSUrl() {
		return IOSUrl;
	}

	public void setIOSUrl(String iOSUrl) {
		IOSUrl = iOSUrl;
	}

}
