package com.wukong.hezhi.bean;

import java.io.Serializable;

public class WalletInfo implements Serializable {
	public float availableAmount;

	public float getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(float availableAmount) {
		this.availableAmount = availableAmount;
	}

}
