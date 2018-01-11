package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class CashInfos implements Serializable {
	private List<CashInfo> dataList;
	private int page;// 当前页
	private int countPage;// 总的页数

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

	public List<CashInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CashInfo> dataList) {
		this.dataList = dataList;
	}

}
