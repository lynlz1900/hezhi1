package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import android.text.TextUtils;

/**
 * 
 * 商品评论信息
 * @author HuangFeiFei
 *
 */
public class CommodityAppraiseInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;//评论id 
	
	@SerializedName("createDateLong")
	private long time;// 时间
	
	@SerializedName("nickName")
	private String userName;//用户名称
	
	@SerializedName("showImageURL")
	private String userImage;// 用户头像
	
	private int appraiseStatus;// 评论星级
	
	@SerializedName("commentContent")
	private String appraiseMessage;// 评论信息
	
	@SerializedName("AddCommentContent")
	private String appraiseAddMessage;// 追加信息
	
	@SerializedName("AddCreateDateLong")
	private long appraiseAddTime;// 追加信息时间
	
	@SerializedName("repliedContent")
	private String appraiseResponseMessage;// 商家回复信息
	
	@SerializedName("AddRepliedContent")
	private String appraiseResponseAddMessage;// 商家追加回复信息
	
	@SerializedName("commentImg")
	private String appraiseImages;// 评论图片
	
	@SerializedName("AddCommentImg")
	private String appraiseImagesAdd;// 评论图片
	
	private List<String> appraiseListImages;// 评论图片
	private List<String> appraiseListImagesAdd;// 追加评论图片
	private int appraiseIsAdd;// 是否有追加信息 1有；0无
	private int appraiseIsResponse;// 是否有商家回复信息 1有；0无
	
	private String score;//评分（ONE_STAR：一星，TWO_STARS：二星，THREE_STARS：三星，FOUR_STARS：四星，FIVE_STARS：五星）
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
		if(this.score.equals("ONE_STAR")){
			appraiseStatus = 1;
		}else if(this.score.equals("TWO_STARS")){
			appraiseStatus = 2;
		}else if(this.score.equals("THREE_STARS")){
			appraiseStatus = 3;
		}else if(this.score.equals("FOUR_STARS")){
			appraiseStatus = 4;
		}else if(this.score.equals("FIVE_STARS")){
			appraiseStatus = 5;
		}else{
			appraiseStatus = 0;
		}
	}
	
	public static String getScoreString(int appraiseStatus){
		String scoreString = "";
		if(appraiseStatus == 1){
			scoreString = "ONE_STAR";
		}else if(appraiseStatus == 2){
			scoreString = "TWO_STARS";
		}else if(appraiseStatus == 3){
			scoreString = "THREE_STARS";
		}else if(appraiseStatus == 4){
			scoreString = "FOUR_STARS";
		}else if(appraiseStatus == 5){
			scoreString = "FIVE_STARS";
		}
		
		return scoreString;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	public int getAppraiseStatus() {
		if(this.score == null){
			appraiseStatus = 0;
		}else if(this.score.equals("ONE_STAR")){
			appraiseStatus = 1;
		}else if(this.score.equals("TWO_STARS")){
			appraiseStatus = 2;
		}else if(this.score.equals("THREE_STARS")){
			appraiseStatus = 3;
		}else if(this.score.equals("FOUR_STARS")){
			appraiseStatus = 4;
		}else if(this.score.equals("FIVE_STARS")){
			appraiseStatus = 5;
		}else{
			appraiseStatus = 0;
		}
		return appraiseStatus;
	}
	public void setAppraiseStatus(int appraiseStatus) {
		this.appraiseStatus = appraiseStatus;
	}
	public String getAppraiseMessage() {
		return appraiseMessage;
	}
	public void setAppraiseMessage(String appraiseMessage) {
		this.appraiseMessage = appraiseMessage;
	}
	public String getAppraiseAddMessage() {
		return appraiseAddMessage;
	}
	public void setAppraiseAddMessage(String appraiseAddMessage) {
		this.appraiseAddMessage = appraiseAddMessage;
	}
	public long getAppraiseAddTime() {
		return appraiseAddTime;
	}
	public void setAppraiseAddTime(long appraiseAddTime) {
		this.appraiseAddTime = appraiseAddTime;
	}
	public String getAppraiseResponseMessage() {
		return appraiseResponseMessage;
	}
	public void setAppraiseResponseMessage(String appraiseResponseMessage) {
		this.appraiseResponseMessage = appraiseResponseMessage;
	}
	public String getAppraiseImages() {
		return appraiseImages;
	}
	public void setAppraiseImages(String appraiseImages) {
		this.appraiseImages = appraiseImages;
		try {
			this.appraiseListImages = java.util.Arrays.asList(this.appraiseImages.split(","));
		} catch (Exception e) {
		}
	}
	public int getAppraiseIsAdd() {
		return appraiseIsAdd;
	}
	public void setAppraiseIsAdd(int appraiseIsAdd) {
		this.appraiseIsAdd = appraiseIsAdd;
	}
	public int getAppraiseIsResponse() {
		return appraiseIsResponse;
	}
	public void setAppraiseIsResponse(int appraiseIsResponse) {
		this.appraiseIsResponse = appraiseIsResponse;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getAppraiseListImages() {
		if(!TextUtils.isEmpty(appraiseImages)){
			try {
				appraiseListImages =  java.util.Arrays.asList(appraiseImages.split(","));
			} catch (Exception e) {
			}
		}
		return appraiseListImages;
	}
	public void setAppraiseListImages(List<String> appraiseListImages) {
		this.appraiseListImages = appraiseListImages;
	}
	
	public List<String> getAppraiseListImagesAdd() {
		if(!TextUtils.isEmpty(appraiseImagesAdd)){
			try {
				appraiseListImagesAdd =  java.util.Arrays.asList(appraiseImagesAdd.split(","));
			} catch (Exception e) {
			}
		}
		return appraiseListImagesAdd;
	}
	public void setAppraiseListImagesAdd(List<String> appraiseListImagesAdd) {
		this.appraiseListImagesAdd = appraiseListImagesAdd;
	}
	public String getAppraiseImagesAdd() {
		return appraiseImagesAdd;
	}
	public void setAppraiseImagesAdd(String appraiseImagesAdd) {
		this.appraiseImagesAdd = appraiseImagesAdd;
		try {
			this.appraiseListImagesAdd = java.util.Arrays.asList(this.appraiseImagesAdd.split(","));
		} catch (Exception e) {
		}
	}
	public String getAppraiseResponseAddMessage() {
		return appraiseResponseAddMessage;
	}
	public void setAppraiseResponseAddMessage(String appraiseResponseAddMessage) {
		this.appraiseResponseAddMessage = appraiseResponseAddMessage;
	}
}
