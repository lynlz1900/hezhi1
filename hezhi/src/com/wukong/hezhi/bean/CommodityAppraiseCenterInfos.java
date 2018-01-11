package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class CommodityAppraiseCenterInfos implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<CommodityAppraiseCenterInfo> dataList;
	/**
	 *总页数
	 * **/
	private int countPage;
	
	public List<CommodityAppraiseCenterInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CommodityAppraiseCenterInfo> dataList) {
		this.dataList = dataList;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
}
