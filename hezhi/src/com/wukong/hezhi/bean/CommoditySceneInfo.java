package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * 定制场景信息
 * @author HuangFeiFei
 *
 */
public class CommoditySceneInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;//场景id 
	private String caseName;// 场景标题
	private String description;//场景描述
	private String imgUrl;// 场景图片
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
