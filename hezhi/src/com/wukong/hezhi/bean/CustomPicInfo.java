package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: TailorPicInfo
 * @Description: TODO(定制图片实体类)
 * @author HuZhiyin
 * @date 2017-8-15 下午4:07:43
 * 
 */
public class CustomPicInfo implements Serializable {
	private String pictureUrl;// 图片地址
	private String customId;//定制id
	private int onLineType;//登录状态  1 正常；2登录过期；3 被挤下线
	
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public int getOnLineType() {
		return onLineType;
	}

	public void setOnLineType(int onLineType) {
		this.onLineType = onLineType;
	}

}
