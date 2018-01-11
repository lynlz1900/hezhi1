package com.wukong.hezhi.bean;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;


/*
 * 收货地址信息
 * 
 * @author HuangFeiFei
 */
public class AddressReceiptInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 详细地址
	 */
	@SerializedName("address")
	private String detailAddr;
	
	/**
	 * 地区
	 */
	@SerializedName("district")
	private String district;
	
	/**
	 * 收货人名字
	 */
	@SerializedName("receiver")
	private String receiver;
	
	/**
	 * 地址id
	 */
	@SerializedName("id")
	private int addId;
	
	/**
	 * 收货人手机号
	 */
	@SerializedName("phone")
	private String receiverPhone;
	
	/**
	 * 用户ID
	 */
	@SerializedName("userId")
	private int userId;
	
	/**
	 * 是否设为默认地址，0代表否，1代表是
	 */
	@SerializedName("isDefault")
	private int isDefault;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public String getDetailAddr() {
		return detailAddr;
	}

	public void setDetailAddr(String detailAddr) {
		this.detailAddr = detailAddr;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public int getAddId() {
		return addId;
	}

	public void setAddId(int addId) {
		this.addId = addId;
	}

	public String getReceiverPhonePhone() {
		return receiverPhone;
	}

	public void setReceiverPhonePhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
}
