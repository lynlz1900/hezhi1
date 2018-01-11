package com.wukong.hezhi.bean;

import java.util.List;

public class ResultInfo {
	private String place;// 产地(贵州)
	private String name;// 名称(茅台)
	private String date;// 生产日期(2014年5月1日)
	private String picture;// 产品图片（图片下载路径）
	private String specification;// 规格（50°）
	private String volume;// 容量(500ml)
	private String pack;// 包装规格（6瓶/箱）
	private String sale;//待销售
	private String bathNo;//批次号
	public String getSale() {
		return sale;
	}

	public void setSale(String sale) {
		this.sale = sale;
	}

	private String extInfo;// 扩展信息(可包括个性化的信息拼装起来)

	private List<Logistic> logistics; // 物流信息list

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}

	public List<Logistic> getLogistics() {
		return logistics;
	}

	public void setLogistics(List<Logistic> logistics) {
		this.logistics = logistics;
	}

	public String getBathNo()
	{
		return bathNo;
	}

	public void setBathNo(String bathNo)
	{
		this.bathNo = bathNo;
	}
}

