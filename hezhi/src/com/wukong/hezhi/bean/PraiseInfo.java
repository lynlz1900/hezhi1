package com.wukong.hezhi.bean;

import java.io.Serializable;

public class PraiseInfo implements Serializable {
	private boolean thumbsUp;// 是否点赞
	private int total;// 点赞总数

	public boolean isThumbsUp() {
		return thumbsUp;
	}

	public void setThumbsUp(boolean thumbsUp) {
		this.thumbsUp = thumbsUp;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
