package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: CustomUploadPicInfo
 * @Description: TODO(定制上传图片实体类)
 * @author HuZhiyin
 * @date 2017-9-12 下午4:00
 * 
 */
public class CustomUploadPicInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String imageUrlOne; // 第一张图片地址
	private String imageUrlTwo; // 第二张图片地址
	private String imageUrlThree; // 第三张图片地址
	private String bitmapOneString;// 第一张图片Bitmap转String
	private String bitmapTwoString;// 第二张图片Bitmap转String
	private String bitmapThreeString;// 第三张图片Bitmap转String
	private String companyName;// 公司名称
	private String userName;// 员工姓名
	private int imagetype;//图片类型
	private String coverMark;// 封面标志（red  blue  yellow）
	
	public String getImageUrlOne() {
		return imageUrlOne;
	}
	public void setImageUrlOne(String imageUrlOne) {
		this.imageUrlOne = imageUrlOne;
	}
	public String getImageUrlTwo() {
		return imageUrlTwo;
	}
	public void setImageUrlTwo(String imageUrlTwo) {
		this.imageUrlTwo = imageUrlTwo;
	}
	public String getImageUrlThree() {
		return imageUrlThree;
	}
	public void setImageUrlThree(String imageUrlThree) {
		this.imageUrlThree = imageUrlThree;
	}
	public String getBitmapOneString() {
		return bitmapOneString;
	}
	public void setBitmapOneString(String bitmapOneString) {
		this.bitmapOneString = bitmapOneString;
	}
	public String getBitmapTwoString() {
		return bitmapTwoString;
	}
	public void setBitmapTwoString(String bitmapTwoString) {
		this.bitmapTwoString = bitmapTwoString;
	}
	public String getBitmapThreeString() {
		return bitmapThreeString;
	}
	public void setBitmapThreeString(String bitmapThreeString) {
		this.bitmapThreeString = bitmapThreeString;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getImagetype() {
		return imagetype;
	}
	public void setImagetype(int imagetype) {
		this.imagetype = imagetype;
	}
	public String getCoverMark() {
		return coverMark;
	}
	public void setCoverMark(String coverMark) {
		this.coverMark = coverMark;
	}
}
