package com.wukong.hezhi.wxapi;

import java.io.Serializable;

/**
 * 
* @ClassName: WXPayInfo 
* @Description: TODO(微信支付实体类) 
* @author HuZhiyin 
* @date 2017-8-9 下午3:54:51 
*
 */

public class WXPayInfo implements Serializable {
	private String appId;// 微信开放平台审核通过的应用APPID
	private String partnerId;// 微信支付分配的商户号
	private String prepayId;// 微信返回的支付交易会话ID
	private String packageValue;// 暂填写固定值Sign=WXPay
	private String nonceStr;// 随机字符串，不长于32位。推荐随机数生成算法
	private String timeStamp;// 时间戳，请见接口规则-参数规定
	private String sign;// 签名，详见签名生成算法
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
