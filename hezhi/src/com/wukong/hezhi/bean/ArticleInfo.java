package com.wukong.hezhi.bean;

import java.io.Serializable;

public class ArticleInfo implements Serializable {
	private String id;// 文章id
	private String title;// 文章标题
	private String thumbnail;// 文章略缩图
	private String label;// 文章标签：分隔符是空格
	private String commentCount;// 文章评论数
	private String url;
	private boolean havaToSee;// 文章是否被查看

	
	
	public boolean isHavaToSee() {
		return havaToSee;
	}

	public void setHavaToSee(boolean havaToSee) {
		this.havaToSee = havaToSee;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

}
