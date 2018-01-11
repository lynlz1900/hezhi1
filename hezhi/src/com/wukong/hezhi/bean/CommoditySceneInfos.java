package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 定制场景信息集合
 * @author HuangFeiFei
 *
 */
public class CommoditySceneInfos implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<CommoditySceneInfo> dataList;

	public List<CommoditySceneInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CommoditySceneInfo> dataList) {
		this.dataList = dataList;
	}
}
