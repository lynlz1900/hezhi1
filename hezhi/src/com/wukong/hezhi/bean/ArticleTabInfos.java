package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticleTabInfos implements Serializable{
	private ArrayList<ArticleTabInfo> dataList;

	public ArrayList<ArticleTabInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<ArticleTabInfo> dataList) {
		this.dataList = dataList;
	}

	
}
