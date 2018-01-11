package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: ShareInfo
 * @Description: TODO(分享实体类)
 * @author HuZhiyin
 * @date 2017-7-18 下午4:59:33
 * 
 */
public class ShareInfo implements Serializable {
	private String url;// 分享的连接
	private String imagUrl;// 图片的连接
	private String title;// 标题
	private String description;// 描述
	private int imageResource;// 图片的地址
	private int type;// 0,微信好友，1，朋友圈，2，qq好友，3,qq空间

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImagUrl() {
		return imagUrl;
	}

	public void setImagUrl(String imagUrl) {
		this.imagUrl = imagUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

}
