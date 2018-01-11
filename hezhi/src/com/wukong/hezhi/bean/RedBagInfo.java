package com.wukong.hezhi.bean;

import java.io.Serializable;

public class RedBagInfo implements Serializable {
	private String name;

	private String logoUrl;

	private String wishing;

	private String sendName;

	private String adTitle;

	private float money;

	private int id;

	private boolean result;// 是否领取

	private int resultCode;// resultCode：1 没有绑定微信 resultCode：2 没有关注盒知公众号

	private boolean isOver;// 是否结束

	private int regbagType;// 1，NFC红包，2，感知红包
	private String ppid;
	private String productId;
	private String customizeProductId;
	private String authId;
	private String tips;
	private boolean ar;// 是否是ar应用

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public boolean isAr() {
		return ar;
	}

	public void setAr(boolean ar) {
		this.ar = ar;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public int getRegbagType() {
		return regbagType;
	}

	public void setRegbagType(int regbagType) {
		this.regbagType = regbagType;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogoUrl() {
		return this.logoUrl;
	}

	public void setWishing(String wishing) {
		this.wishing = wishing;
	}

	public String getWishing() {
		return this.wishing;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getSendName() {
		return this.sendName;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public String getAdTitle() {
		return this.adTitle;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getCustomizeProductId() {
		return customizeProductId;
	}

	public void setCustomizeProductId(String customizeProductId) {
		this.customizeProductId = customizeProductId;
	}
}
