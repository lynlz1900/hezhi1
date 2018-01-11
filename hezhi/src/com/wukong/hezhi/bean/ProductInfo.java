package com.wukong.hezhi.bean;

import java.io.Serializable;

public class ProductInfo implements Serializable {

	/* 产品大类相关 */

	private String materialTypeName;// 产品大类

	private String materialTypeSpecification;// 产品规格（箱、盒、袋）

	private Integer materialTypePackCount;// 包装数量

	/* 产品明细相关 */

	private String materialName;// 产品名称

	private String materialSpecification;// 产品规格

	private String materialExtInfo;// 扩展信息(可包括个性化的信息拼装起来)

	private String picturePath;// 图片路径

	private String createDate;// 生产日期

	private String materialFlowNo;//标签对应的epc号
	private String sale;// 销售状态

	private String bathNo;// 销售状态
	/** 承诺pv */
	private int pvPermitedScore;
	/** 基准pv */
	private int pvStandardScore;
	/** 当前pv */
	private int pvAssessedScore;
	/**商城链接*/
	private String guideUrl;
	private String tagStateType;
	private String companyName;
	private String productId;//产品id
	private String authId;//授权码
	private String productTypeCode;//产品类型编码
	private String productCode;//产品编码
	private String produceDate;//产品生产时间

	
	
	public String getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(String produceDate) {
		this.produceDate = produceDate;
	}

	public String getProductTypeCode() {
		return productTypeCode;
	}

	public void setProductTypeCode(String productTypeCode) {
		this.productTypeCode = productTypeCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTagStateType() {
		return tagStateType;
	}

	public void setTagStateType(String tagStateType) {
		this.tagStateType = tagStateType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getPvPermitedScore() {
		return pvPermitedScore;
	}

	public void setPvPermitedScore(int pvPermitedScore) {
		this.pvPermitedScore = pvPermitedScore;
	}

	public int getPvStandardScore() {
		return pvStandardScore;
	}

	public void setPvStandardScore(int pvStandardScore) {
		this.pvStandardScore = pvStandardScore;
	}

	public int getPvAssessedScore() {
		return pvAssessedScore;
	}

	public void setPvAssessedScore(int pvAssessedScore) {
		this.pvAssessedScore = pvAssessedScore;
	}

	public String getGuideUrl() {
		return guideUrl;
	}

	public void setGuideUrl(String guideUrl) {
		this.guideUrl = guideUrl;
	}

	public String getMaterialFlowNo() {
		return materialFlowNo;
	}

	public void setMaterialFlowNo(String materialFlowNo) {
		this.materialFlowNo = materialFlowNo;
	}

	public String getMaterialTypeName() {
		return materialTypeName;
	}

	public void setMaterialTypeName(String materialTypeName) {
		this.materialTypeName = materialTypeName;
	}

	public String getMaterialTypeSpecification() {
		return materialTypeSpecification;
	}

	public void setMaterialTypeSpecification(String materialTypeSpecification) {
		this.materialTypeSpecification = materialTypeSpecification;
	}

	public Integer getMaterialTypePackCount() {
		return materialTypePackCount;
	}

	public void setMaterialTypePackCount(Integer materialTypePackCount) {
		this.materialTypePackCount = materialTypePackCount;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialSpecification() {
		return materialSpecification;
	}

	public void setMaterialSpecification(String materialSpecification) {
		this.materialSpecification = materialSpecification;
	}

	public String getMaterialExtInfo() {
		return materialExtInfo;
	}

	public void setMaterialExtInfo(String materialExtInfo) {
		this.materialExtInfo = materialExtInfo;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}


	public String getBathNo() {
		return bathNo;
	}

	public void setBathNo(String bathNo) {
		this.bathNo = bathNo;
	}




}
