package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: QQLoginInfo
 * @Description: TODO(qq登录返回信息)
 * @author HuZhiyin
 * @date 2017-1-16 下午5:23:58
 * 
 */
public class QQLoginInfo implements Serializable{
	private int ret;

	private String openid;

	private String access_token;

	private String pay_token;

	private String expires_in;

	private String pf;

	private String pfkey;

	private String msg;

	private int login_cost;

	private int query_authority_cost;

	private int authority_cost;

	public void setRet(int ret) {
		this.ret = ret;
	}

	public int getRet() {
		return this.ret;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getOpenid() {
		return this.openid;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getAccess_token() {
		return this.access_token;
	}

	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}

	public String getPay_token() {
		return this.pay_token;
	}


	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public void setPf(String pf) {
		this.pf = pf;
	}

	public String getPf() {
		return this.pf;
	}

	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}

	public String getPfkey() {
		return this.pfkey;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setLogin_cost(int login_cost) {
		this.login_cost = login_cost;
	}

	public int getLogin_cost() {
		return this.login_cost;
	}

	public void setQuery_authority_cost(int query_authority_cost) {
		this.query_authority_cost = query_authority_cost;
	}

	public int getQuery_authority_cost() {
		return this.query_authority_cost;
	}

	public void setAuthority_cost(int authority_cost) {
		this.authority_cost = authority_cost;
	}

	public int getAuthority_cost() {
		return this.authority_cost;
	}
}