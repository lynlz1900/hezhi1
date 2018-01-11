package com.wukong.hezhi.bean;

import com.google.gson.annotations.SerializedName;
import com.wukong.hezhi.constants.Constant;

import java.io.Serializable;

/*
 * 订单信息
 * 
 * @author HuangFeiFei
 * 
 */
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int id;   //订单ID

    @SerializedName("customId")
    private String customId;// 定制id,放在此处,提供后台查询图片

    @SerializedName("productId")
    private int productId;    //商品id

    @SerializedName("productName")
    private String productName;    //商品名称

    @SerializedName("companyRealName")
    private String companyRealName;//商户名称

    @SerializedName("companyPhone")
    private String companyPhone;//商户电话

    @SerializedName("price")
    private double price;            //单价

    @SerializedName("totalPrice")
    private String priceTotal;            //总价

    @SerializedName("number")
    private int number;        //购买数量

    @SerializedName("state")
    private String state;//订单状态

    @SerializedName("status")
    private int status;        //订单状态

    @SerializedName("productPictureUrl")
    private String productPictureUrl;//产品图片

    @SerializedName("receiver")
    private String receiverName;    //买家姓名

    @SerializedName("phone")
    private String receiverPhone;    //买家手机号

    @SerializedName("district")
    private String receiverAddr;    //买家地址所在区域

    @SerializedName("address")
    private String receiverAddrDetail;    //买家地址详细地址

    @SerializedName("orderNo")
    private String orderNumber;    //订单编号

    @SerializedName("orderType")
    private int orderType;    //订单类型：0，直接购买；1定制购买

    @SerializedName("createDateLong")
    private long timePut;    //下单时间

    @SerializedName("remark")
    private String remark; //备注

    @SerializedName("expressName")
    private String logisticsCompany; //物流公司

    @SerializedName("express")
    private String logisticsCompanyNumber; //物流公司编号

    @SerializedName("expressNo")
    private String logisticsNumber;    //物流编号

    @SerializedName("pullDateLong")
    private long timeSend;    //发货时间

    @SerializedName("getDateLong")
    private long timeReceive;//收货时间

    @SerializedName("redPack")
    private String redbag;  //红包

    @SerializedName("redPackStatus")
    private int redPackStatus;//是否有红包：1  有

    private String htmlFiveUrl;//视频定制预览H5 URL

    private int commentType;//是否有评价 0:未评价
    
    public String getLogisticsCompanyNumber() {
        return logisticsCompanyNumber;
    }

    public void setLogisticsCompanyNumber(String logisticsCompanyNumber) {
        this.logisticsCompanyNumber = logisticsCompanyNumber;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getReceiverAddrDetail() {
        return receiverAddrDetail;
    }

    public void setReceiverAddrDetail(String receiverAddrDetail) {
        this.receiverAddrDetail = receiverAddrDetail;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStatus() {

        if (state == null) {
            status = -1;
        } else if (state.equals("PAYING"))
            status = Constant.OrderType.OBLIGATIONS.ordinal();
        else if (state.equals("DELIVERING"))
            status = Constant.OrderType.UNDELIVERED.ordinal();
        else if (state.equals("RECEIVING"))
            status = Constant.OrderType.UNRECEIVED.ordinal();
        else if (state.equals("END"))
            status = Constant.OrderType.RECEIVED.ordinal();
        else if (state.equals("CANCELED"))
            status = Constant.OrderType.CANCELED.ordinal();
        else
            status = -1;

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public long getTimePut() {
        return timePut;
    }

    public void setTimePut(long timePut) {
        this.timePut = timePut;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }

    public long getTimeReceive() {
        return timeReceive;
    }

    public void setTimeReceive(long timeReceive) {
        this.timeReceive = timeReceive;
    }

    public String getRedbag() {
        return redbag;
    }

    public void setRedbag(String redbag) {
        this.redbag = redbag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProductPictureUrl() {
        return productPictureUrl;
    }

    public void setProductPictureUrl(String productPictureUrl) {
        this.productPictureUrl = productPictureUrl;
    }

    public int getRedPackStatus() {
        return redPackStatus;
    }

    public void setRedPackStatus(int redPackStatus) {
        this.redPackStatus = redPackStatus;
    }

    public String getCompanyRealName() {
        return companyRealName;
    }

    public void setCompanyRealName(String companyRealName) {
        this.companyRealName = companyRealName;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getHtmlFiveUrl() {
        return htmlFiveUrl;
    }

    public void setHtmlFiveUrl(String htmlFiveUrl) {
        this.htmlFiveUrl = htmlFiveUrl;
    }

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}
}
