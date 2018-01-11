package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
* @ClassName: QQUserInfo 
* @Description: TODO(QQ用户信息) 
* @author HuZhiyin 
* @date 2017-1-16 下午5:23:42 
*
 */
public class QQUserInfo implements Serializable{
	private int ret;

	private String msg;

	private int is_lost;

	private String nickname;

	private String gender;

	private String province;

	private String city;

	private String figureurl;

	private String figureurl_1;

	private String figureurl_2;

	private String figureurl_qq_1;

	private String figureurl_qq_2;

	private String is_yellow_vip;

	private String vip;

	private String yellow_vip_level;

	private String level;

	private String is_yellow_year_vip;
	
	private String imageBase64Str;
	
	private String openId;
	
	

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getImageBase64Str() {
		return imageBase64Str;
	}

	public void setImageBase64Str(String imageBase64Str) {
		this.imageBase64Str = imageBase64Str;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public int getRet() {
		return this.ret;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setIs_lost(int is_lost) {
		this.is_lost = is_lost;
	}

	public int getIs_lost() {
		return this.is_lost;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return this.gender;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvince() {
		return this.province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return this.city;
	}

	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}

	public String getFigureurl() {
		return this.figureurl;
	}

	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}

	public String getFigureurl_1() {
		return this.figureurl_1;
	}

	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}

	public String getFigureurl_2() {
		return this.figureurl_2;
	}

	public void setFigureurl_qq_1(String figureurl_qq_1) {
		this.figureurl_qq_1 = figureurl_qq_1;
	}

	public String getFigureurl_qq_1() {
		return this.figureurl_qq_1;
	}

	public void setFigureurl_qq_2(String figureurl_qq_2) {
		this.figureurl_qq_2 = figureurl_qq_2;
	}

	public String getFigureurl_qq_2() {
		return this.figureurl_qq_2;
	}

	public void setIs_yellow_vip(String is_yellow_vip) {
		this.is_yellow_vip = is_yellow_vip;
	}

	public String getIs_yellow_vip() {
		return this.is_yellow_vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getVip() {
		return this.vip;
	}

	public void setYellow_vip_level(String yellow_vip_level) {
		this.yellow_vip_level = yellow_vip_level;
	}

	public String getYellow_vip_level() {
		return this.yellow_vip_level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return this.level;
	}

	public void setIs_yellow_year_vip(String is_yellow_year_vip) {
		this.is_yellow_year_vip = is_yellow_year_vip;
	}

	public String getIs_yellow_year_vip() {
		return this.is_yellow_year_vip;
	}
}