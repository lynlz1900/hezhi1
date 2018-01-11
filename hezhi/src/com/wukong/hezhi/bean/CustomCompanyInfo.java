package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: TailorPicInfo
 * @Description: TODO(定制图片公司名实体类)
 * @author HuangFeiFei
 * @date 2017-9-14 10:00
 * 
 */
public class CustomCompanyInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String companyName;// 公司名称
	private String name;// 姓名
	private int onLineType;//登录状态  1 正常；2登录过期；3 被挤下线
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOnLineType() {
		return onLineType;
	}

	public void setOnLineType(int onLineType) {
		this.onLineType = onLineType;
	}

}
