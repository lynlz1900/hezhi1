package com.wukong.hezhi.bean;

import java.io.Serializable;

public class GuanZhuInfo implements Serializable {
	private boolean attention;// 是否关注
	private int total;// 关注总数

	public boolean isAttention() {
		return attention;
	}

	public void setAttention(boolean attention) {
		this.attention = attention;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
