package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

/*
 * 收货地址信息集合
 * 
 * @author HuangFeiFei
 */
public class AddressReceiptInfos implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 *收货地址信息集合
	 * **/
	private List<AddressReceiptInfo> dataList;

	public List<AddressReceiptInfo> getDatalist() {
		return dataList;
	}

	public void setDatalist(List<AddressReceiptInfo> datalist) {
		this.dataList = datalist;
	}
}
