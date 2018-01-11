package com.wukong.hezhi.bean;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * 
 * @ClassName: LogisticsInfo
 * @Description: TODO(物流信息)
 * @author HuZhiyin
 * @date 2016-10-3 下午7:55:40
 * 
 */

public class LogisticsInfo implements Serializable {
	/** uid */
	private String materialFlowNo;
	/** 地址 */
	private String address;
	/** 0入库1出库2盘点3装车4卸货5批发 */
	private int inOutType;
	/** 生产日期 */
	private String businessDate;
	/** 上传用户id */
	private String uploadUserId;
	/** id 数据表的外键 */
	private int id;
	/** 备注 */
	private String memo;
	/** 上传用户名称 */
	private String realName;

	public String getRealName() {
		if (TextUtils.isEmpty(realName)) {
			realName = "";
		}
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMaterialFlowNo() {
		return materialFlowNo;
	}

	public void setMaterialFlowNo(String materialFlowNo) {
		this.materialFlowNo = materialFlowNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getInOutType() {
		return inOutType;
	}

	public void setInOutType(int inOutType) {
		this.inOutType = inOutType;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}

	public String getUploadUserId() {
		return uploadUserId;
	}

	public void setUploadUserId(String uploadUserId) {
		this.uploadUserId = uploadUserId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMemo() {
		if (TextUtils.isEmpty(memo)) {
			memo = "";
		}
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
