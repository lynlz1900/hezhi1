package com.wukong.hezhi.bean;

import java.io.Serializable;

public class CommentInfo implements Serializable {
	private String createDate;
	private String content;
	private String nickName;
	private String ppId;
	private String showImageURL;

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPpId() {
		return ppId;
	}

	public void setPpId(String ppId) {
		this.ppId = ppId;
	}

	public String getShowImageURL() {
		return showImageURL;
	}

	public void setShowImageURL(String showImageURL) {
		this.showImageURL = showImageURL;
	}

}
