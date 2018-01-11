package com.wukong.hezhi.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private long id;//userId
	private String showImageURL;// 用户头像链接
	private Bitmap headImage;// 用户头像
	private String phone;// 手机号
	private String nickName;// 昵称
	private String sex;// 性别
	private String address;// 地址
	private String wxOpenId;// 微信openid
	private String wbOpenId;// 微博openid
	private String qqOpenId;// qq openid
	private boolean isActive;// 是否登录
	private int onlineType;// 1，正常在线，2，登录过期，3被挤下线

	public int getOnlineType() {
		return onlineType;
	}

	public void setOnlineType(int onlineType) {
		this.onlineType = onlineType;
	}

	public String getWxOpenId() {
		return wxOpenId;
	}

	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
	}

	public String getWbOpenId() {
		return wbOpenId;
	}

	public void setWbOpenId(String wbOpenId) {
		this.wbOpenId = wbOpenId;
	}

	public String getQqOpenId() {
		return qqOpenId;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Bitmap getHeadImage() {
		return headImage;
	}

	public void setHeadImage(Bitmap headImage) {
		this.headImage = headImage;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getShowImageURL() {
		return showImageURL;
	}

	public void setShowImageURL(String showImageURL) {
		this.showImageURL = showImageURL;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}
