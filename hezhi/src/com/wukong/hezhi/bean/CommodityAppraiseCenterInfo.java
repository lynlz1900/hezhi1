package com.wukong.hezhi.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * 商品评价中心信息
 * @author HuangFeiFei
 *
 */
public class CommodityAppraiseCenterInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;//评论id 
	
	@SerializedName("productPictureUrl")
	private String ImageUrl;// 商品图片URL
	
	private String productName;//商品名称
	
	@SerializedName("commentType")
	private int appraiseStatus;// 评论状态 0：未评价  1：评价了一次 2：已评价
	
	@SerializedName("orderNo")
	private String orderNumber;    //订单编号

	@SerializedName("productId")
	private int productId;    //商品id
	 
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getAppraiseStatus() {
		return appraiseStatus;
	}
	public void setAppraiseStatus(int appraiseStatus) {
		this.appraiseStatus = appraiseStatus;
	}
}
