package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class CommodityAppraiseInfos implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<CommodityAppraiseInfo> dataList;
	/**
	 *总页数
	 * **/
	private int countPage;
	/***好评总页数*/
	private int goodCommentPage;
	/***中评总页数*/
	private int mediumCommentPage;
	/***差评总页数*/
	private int badCommentPage;
	/***有图评价总页数*/
	private int hasPictureCommentPage;
	/***追加评价总页数*/
	private int addToCommentPage;
	/**
	 *总行数
	 * **/
	private int countRecord;
	/***好评数*/
	private int goodComment;
	/***中评数*/
	private int mediumComment;
	/***差评数*/
	private int badComment;
	/***有图评数*/
	private int hasPictureComment;
	/***追加评数*/
	private int addToComment;
	/***好评度*/
	private double goodCommentPercent;
	
	public void setNumber(CommodityAppraiseInfos commodityAppraiseInfos){
		if(commodityAppraiseInfos != null){
			this.countPage = commodityAppraiseInfos.getCountPage();
			this.countRecord = commodityAppraiseInfos.getCountRecord();
			this.goodComment = commodityAppraiseInfos.getGoodComment();
			this.goodCommentPage = commodityAppraiseInfos.getGoodCommentPage();
			this.goodCommentPercent = commodityAppraiseInfos.getGoodCommentPercent();
			this.badComment = commodityAppraiseInfos.getBadComment();
			this.badCommentPage = commodityAppraiseInfos.getBadCommentPage();
			this.mediumComment = commodityAppraiseInfos.getMediumComment();
			this.mediumCommentPage = commodityAppraiseInfos.getMediumCommentPage();
			this.hasPictureComment = commodityAppraiseInfos.getHasPictureComment();
			this.hasPictureCommentPage = commodityAppraiseInfos.getHasPictureCommentPage();
			this.addToComment = commodityAppraiseInfos.getAddToComment();
			this.addToCommentPage = commodityAppraiseInfos.getAddToCommentPage();
		}
	}
	
	public int getGoodCommentPage() {
		return goodCommentPage;
	}

	public void setGoodCommentPage(int goodCommentPage) {
		this.goodCommentPage = goodCommentPage;
	}

	public int getMediumCommentPage() {
		return mediumCommentPage;
	}

	public void setMediumCommentPage(int mediumCommentPage) {
		this.mediumCommentPage = mediumCommentPage;
	}

	public int getBadCommentPage() {
		return badCommentPage;
	}

	public void setBadCommentPage(int badCommentPage) {
		this.badCommentPage = badCommentPage;
	}

	public int getHasPictureCommentPage() {
		return hasPictureCommentPage;
	}

	public void setHasPictureCommentPage(int hasPictureCommentPage) {
		this.hasPictureCommentPage = hasPictureCommentPage;
	}

	public int getAddToCommentPage() {
		return addToCommentPage;
	}

	public void setAddToCommentPage(int addToCommentPage) {
		this.addToCommentPage = addToCommentPage;
	}

	public int getGoodComment() {
		return goodComment;
	}

	public void setGoodComment(int goodComment) {
		this.goodComment = goodComment;
	}

	public int getMediumComment() {
		return mediumComment;
	}

	public void setMediumComment(int mediumComment) {
		this.mediumComment = mediumComment;
	}

	public int getBadComment() {
		return badComment;
	}

	public void setBadComment(int badComment) {
		this.badComment = badComment;
	}

	public int getHasPictureComment() {
		return hasPictureComment;
	}

	public void setHasPictureComment(int hasPictureComment) {
		this.hasPictureComment = hasPictureComment;
	}

	public int getAddToComment() {
		return addToComment;
	}

	public void setAddToComment(int addToComment) {
		this.addToComment = addToComment;
	}

	public double getGoodCommentPercent() {
		return goodCommentPercent;
	}

	public void setGoodCommentPercent(double goodCommentPercent) {
		this.goodCommentPercent = goodCommentPercent;
	}

	public List<CommodityAppraiseInfo> getDataList() {
		return dataList;
	}

	public void setDataList(List<CommodityAppraiseInfo> dataList) {
		this.dataList = dataList;
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
}
