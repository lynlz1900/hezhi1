package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MyGuanZhuInfos implements Serializable {

	private int page;// 当前页
	private int countPage;// 总的页数
	private ArrayList<MyGuanZhuInfo> dataList;
	private String dataType;//attention | recommend表示是推荐的还是关注的数据.
	
	
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

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

	public ArrayList<MyGuanZhuInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<MyGuanZhuInfo> dataList) {
		this.dataList = dataList;
	}

}
