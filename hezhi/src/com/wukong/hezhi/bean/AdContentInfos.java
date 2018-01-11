package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class AdContentInfos implements Serializable {
	private List<AdContentInfo> data;

	public List<AdContentInfo> getData() {
		return data;
	}

	public void setData(List<AdContentInfo> data) {
		this.data = data;
	}

}
