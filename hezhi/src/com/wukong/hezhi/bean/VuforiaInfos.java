package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class VuforiaInfos implements Serializable {
	private List<VuforiaInfo> dataList;
	private int page;//当前页
	private int countPage;//总的页数
	
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

	public List<VuforiaInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<VuforiaInfo> dataList) {
		this.dataList = dataList;
	}

}
