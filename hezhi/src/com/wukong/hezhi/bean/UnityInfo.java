package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class UnityInfo implements Serializable {
	private String ppId;// "感知应用ID",
	private String name;// "name": "感知图名称",
	private String picName;// "picName": "图片名称",
	private String picId;// "感知图片ID",
	private String picUrl;// "picUrl":"图片URL",
	private String posterUrl;// 封面图URL
	private String shopURL;
	private int businessId;// 商户ID
	private String businessLogo;// 商户头像URL
	private String businessCover;// 商户封面URL
	private String businessName;// 商户名称
	private String introduction;// 商户简介
	private int attentionNum;// 关注数
	private int commentNum;// 评论数
	private String targetId;// 高通产生的targetId
	private List<UnityResourceInfo> resourceList;// 对应的资源
	private String modelName; // 模板名称
	private int modelId; // 模板id
	private String hasBookProduct;//是否有定制：YES有 ，NO无     
	private int productId;//定制商品ID
	private String resourceType; //资源类型  MP4 UNITY3D URL 等等

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public int getAttentionNum() {
		return attentionNum;
	}

	public void setAttentionNum(int attentionNum) {
		this.attentionNum = attentionNum;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public String getShopURL() {
		return shopURL;
	}

	public void setShopURL(String shopURL) {
		this.shopURL = shopURL;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public String getBusinessLogo() {
		return businessLogo;
	}

	public void setBusinessLogo(String businessLogo) {
		this.businessLogo = businessLogo;
	}

	public String getBusinessCover() {
		return businessCover;
	}

	public void setBusinessCover(String businessCover) {
		this.businessCover = businessCover;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPpId() {
		return ppId;
	}

	public void setPpId(String ppId) {
		this.ppId = ppId;
	}

	public List<UnityResourceInfo> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<UnityResourceInfo> resourceList) {
		this.resourceList = resourceList;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getHasBookProduct() {
		return hasBookProduct;
	}

	public void setHasBookProduct(String hasBookProduct) {
		this.hasBookProduct = hasBookProduct;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

}
