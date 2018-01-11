package com.wukong.hezhi.bean;

import java.io.Serializable;
//"businessId": 26,
//"businessName": "admin",
//"attentionNum": 1,
//"page": 1,
//"record": 20,
//"countPage": 1,
import java.util.ArrayList;
//"businessId":"商户ＩＤ";
//"businessLogo":"logoURL";
//"businessCover":"封面URL";
//"businessName":"商户名称";
//"introduction":"商户简介";
//"attentionNum":"关注数";
//"page": 1,
//"record": 20,
//"countPage": 1,

public class BusinessInfos implements Serializable {
	private int businessId;// "商户ＩＤ";
	private String businessLogo;// "logoURL";
	private String businessName;// 商户名称;
	private String businessCover;// "封面URL";
	private String introduction;// "商户简介";
	private int attentionNum;
	private int page;
	private int record;
	private int countPage;
	private ArrayList<BusinessInfo> dataList;

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

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public ArrayList<BusinessInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<BusinessInfo> dataList) {
		this.dataList = dataList;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public int getAttentionNum() {
		return attentionNum;
	}

	public void setAttentionNum(int attentionNum) {
		this.attentionNum = attentionNum;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

}
