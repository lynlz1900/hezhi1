package com.wukong.hezhi.bean;

import java.io.Serializable;

public class ArticleAppendInfo implements Serializable {
	private String id;// 文章ID
	private String thumbupCount;// 点赞人数
	private String thumbupStatus;// YES-已点赞 NO-未点赞
	private String collectionCount;// 收藏人数
	private String collectionStatus;// YES-已收藏 NO-未收藏
	private String commentCount;// 评论数量

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getThumbupCount() {
		return thumbupCount;
	}

	public void setThumbupCount(String thumbupCount) {
		this.thumbupCount = thumbupCount;
	}

	public String getThumbupStatus() {
		return thumbupStatus;
	}

	public void setThumbupStatus(String thumbupStatus) {
		this.thumbupStatus = thumbupStatus;
	}

	public String getCollectionCount() {
		return collectionCount;
	}

	public void setCollectionCount(String collectionCount) {
		this.collectionCount = collectionCount;
	}

	public String getCollectionStatus() {
		return collectionStatus;
	}

	public void setCollectionStatus(String collectionStatus) {
		this.collectionStatus = collectionStatus;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

}
