package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: TailorPicInfo
 * @Description: TODO(鉴真结果结果判断是否定制)
 * @author HuZhiyin
 * @date 2017-8-15 下午4:07:43
 * 
 */
public class CustomFlagInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String customizeFlag;// 是否定制：YES是
	private int id;
	
	public String getCustomizeFlag() {
		return customizeFlag;
	}

	public void setCustomizeFlag(String customizeFlag) {
		this.customizeFlag = customizeFlag;
	}

	public int getProductId() {
		return id;
	}

	public void setProductId(int productId) {
		this.id = productId;
	}

	
}
