package com.wukong.hezhi.bean;

import java.io.Serializable;

public class ArticleTabInfo implements Serializable {
	private String id;// 菜单ID
	private String name;// 菜单名称
	private String icon;// 菜单图标URL

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
