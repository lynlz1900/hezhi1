package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

/*
 * 订单信息集合
 * 
 * @author HuangFeiFei
 */
public class OrderInfos implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 *订单信息集合
	 * **/
	private List<OrderInfo> dataList;
	/**
	 *总页数
	 * **/
	private int countPage;
	
	public List<OrderInfo> getDatalist() {
		return dataList;
	}

	public void setDatalist(List<OrderInfo> datalist) {
		this.dataList = datalist;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
}
