package com.wukong.hezhi.bean;

import java.io.Serializable;

import android.text.TextUtils;

public class AddressInfo implements Serializable {
	/**
	 * 详细地址
	 */
	private String detailAddr;
	/**
	 * 大楼的名字
	 */
	private String building;
	/**
	 * 省
	 */
	private String province;
	/**
	 * 市
	 */
	private String city;
	/**
	 * 区
	 */
	private String area;
	/**
	 * 经度
	 */
	private double longitude;

	/**
	 * 纬度
	 */
	private double latitude;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDetailAddr() {
		if(TextUtils.isEmpty(detailAddr)){
			detailAddr="";
		}
		return detailAddr;
	}

	public void setDetailAddr(String detailAddr) {
		this.detailAddr = detailAddr;
	}

	public String getBuilding() {
		if(TextUtils.isEmpty(building)){
			building="";
		}
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

}
