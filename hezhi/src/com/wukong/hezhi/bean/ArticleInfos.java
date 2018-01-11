package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticleInfos implements Serializable {
	private ArrayList<ArticleInfo> dataList;

	public ArrayList<ArticleInfo> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<ArticleInfo> dataList) {
		this.dataList = dataList;
	}


}
