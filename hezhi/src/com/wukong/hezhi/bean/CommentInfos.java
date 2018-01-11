package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class CommentInfos implements Serializable {
	private int page;// 当前页
	private int record;// 一页的条数
	private int countPage;// 总页数
	private int countRecord;// 总条数
	private List<CommentInfo> dataList;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRecord() {
		return record;
	}

	public void setRecord(int record) {
		this.record = record;
	}

	public int getCountPage() {
		return countPage;
	}

	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}

	public int getCountRecord() {
		return countRecord;
	}

	public void setCountRecord(int countRecord) {
		this.countRecord = countRecord;
	}

	public List<CommentInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CommentInfo> dataList) {
		this.dataList = dataList;
	}

}
