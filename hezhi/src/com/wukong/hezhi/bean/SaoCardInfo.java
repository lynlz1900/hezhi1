package com.wukong.hezhi.bean;

import android.graphics.drawable.Drawable;

public class SaoCardInfo {
	private String title;
	private Drawable drawable;
	public SaoCardInfo(String title, Drawable drawable) {
		super();
		this.title = title;
		this.drawable = drawable;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	

}
