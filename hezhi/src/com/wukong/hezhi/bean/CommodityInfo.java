package com.wukong.hezhi.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommodityInfo implements Serializable {
	@SerializedName("id")
	private int id;// 商品ID

	@SerializedName("productId")
	private int productId;// 商品ID 用于我的定制
	
	@SerializedName("pictureUrl")
	private String imageUrl;// 图片的链接

	@SerializedName("productName")
	private String name;// 商品的名称

	@SerializedName("originalPrice")
	private double costPrice;// 原价

	@SerializedName("price")
	private double price;// 成交价

	@SerializedName("succCount")
	private int volume;// 成交量

	@SerializedName("extInfo")
	private String url;// 链接

	@SerializedName("detailUrl")
	private String bannerUrl;// banner链接

	@SerializedName("bottomUrl")
	private String bottomUrl;// 定制底图

	@SerializedName("customId")
	private String customId;// 定制id,放在此处,提供后台查询图片
	
	private int orderNum;// 选了多个个商品,默认是1
	private int buyType;// 0表示直接购买，1表示定制
	private int productIsDelete;//0.代表已删除或非定制,1.代表正常数据
	
	@SerializedName("anniversaryMark")
	private int isAnniversary; //是否为周年庆产品1：是周年庆产品 0：不是周年庆产品
	private String companyName;//公司名称
	private String username;// 姓名
	
	private int nfcCustom;//1:视频定制
	private int faceCustom;//1:外观定制
	private int nowBuy;//1:立即购买
	private int customizationType;//定制类型   1 外观定制 2 nfc定制
	private String previewUrl;//预览url
	private String nfcUrl;//nfc预览url
	
	private int inventory;//剩余库存
	
	private long createDateLong;//时间


	@SerializedName("leftPositionX")
	private int left_x=50;//左上角,左边距
	@SerializedName("leftPositionY")
	private int left_y=500;//左上角，上边距
	@SerializedName("rightPositionX")
	private int right_x=500;//右下角，左边距
	@SerializedName("rightPositionY")
	private int ringht_y=1500;//右下角，上边距


	public int getLeft_x() {
		return left_x;
	}

	public void setLeft_x(int left_x) {
		this.left_x = left_x;
	}

	public int getLeft_y() {
		return left_y;
	}

	public void setLeft_y(int left_y) {
		this.left_y = left_y;
	}

	public int getRight_x() {
		return right_x;
	}

	public void setRight_x(int right_x) {
		this.right_x = right_x;
	}

	public int getRinght_y() {
		return ringht_y;
	}

	public void setRinght_y(int ringht_y) {
		this.ringht_y = ringht_y;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getBottomUrl() {
		return bottomUrl;
	}

	public void setBottomUrl(String bottomUrl) {
		this.bottomUrl = bottomUrl;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuyType() {
		return buyType;
	}

	public void setBuyType(int buyType) {
		this.buyType = buyType;
	}

	public double getPrice() {
		return price;

	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getOrderNum() {
		if (orderNum == 0) {
			return 1;
		}
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	public int getProductIsDelete() {
		return productIsDelete;
	}

	public void setProductIsDelete(int productIsDelete) {
		this.productIsDelete = productIsDelete;
	}

	public int getIsAnniversary() {
		return isAnniversary;
	}

	public void setIsAnniversary(int isAnniversary) {
		this.isAnniversary = isAnniversary;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getNfcCustom() {
		return nfcCustom;
	}

	public void setNfcCustom(int nfcCustom) {
		this.nfcCustom = nfcCustom;
	}

	public int getFaceCustom() {
		return faceCustom;
	}

	public void setFaceCustom(int faceCustom) {
		this.faceCustom = faceCustom;
	}

	public int getNowBuy() {
		return nowBuy;
	}

	public void setNowBuy(int nowBuy) {
		this.nowBuy = nowBuy;
	}

	public int getCustomizationType() {
		return customizationType;
	}

	public void setCustomizationType(int customizationType) {
		this.customizationType = customizationType;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getNfcUrl() {
		return nfcUrl;
	}

	public void setNfcUrl(String nfcUrl) {
		this.nfcUrl = nfcUrl;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public long getCreateDateLong() {
		return createDateLong;
	}

	public void setCreateDateLong(long createDateLong) {
		this.createDateLong = createDateLong;
	}

}
