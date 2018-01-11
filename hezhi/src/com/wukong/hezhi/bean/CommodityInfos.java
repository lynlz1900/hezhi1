package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class CommodityInfos implements Serializable{
	
	private List<CommodityInfo> dataList;
	/**
	 *总页数
	 * **/
	private int countPage;
	
	public List<CommodityInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CommodityInfo> dataList) {
		this.dataList = dataList;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
}
