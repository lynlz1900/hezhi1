package com.wukong.hezhi.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author HuZhiyin
 * @ClassName: TempletInfo
 * @Description: TODO(模板实体类)
 * @date 2017-8-16 上午10:09:25
 */

public class TempletInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int id;// 模板id
    @SerializedName("templetResultUrl")
    private String templetResultUrl;//模板效果图片
    @SerializedName("templetUrl")
    private String templetUrl;// 模板图片url
    @SerializedName("picTopLeftX")
    private int left_x;//左上角,左边距
    @SerializedName("picTopLeftY")
    private int left_y;//左上角，上边距
    @SerializedName("picLowerRightX")
    private int right_x;//右下角，左边距
    @SerializedName("picLowerRightY")
    private int ringht_y;//右下角，上边距
    @SerializedName("charStartPositX")
    private int wenzi_left_x;//文字，左上角,左边距
    @SerializedName("charStartPositY")
    private int wenzi_left_y;//文字，左上角，上边距
    @SerializedName("charColour")
    private String wenzi_color = "#FFFFFF";//文字颜色
    @SerializedName("charSize")
    private int wenzi_size;//文字大小
    @SerializedName("pictureShape")
    private int pictureShape = 1;//图片形状（1 方形 2 圆形）
    @SerializedName("uploadPicTagApp")
    private boolean isAddPic = true;//是否需要添加图片
    @SerializedName("editCharTagApp")
    private boolean isAppWenzi = true;//是否添加文字
    @SerializedName("charCount")
    private int charCount ;//文字的字数
    @SerializedName("charEndPositX")
    private int wenzi_right_x;//文字，右下角,左边距
    @SerializedName("charEndPositY")
    private int wenzi_right_y;//文字，右下角，上边距


    public int getWenzi_right_x() {
        return wenzi_right_x;
    }

    public void setWenzi_right_x(int wenzi_right_x) {
        this.wenzi_right_x = wenzi_right_x;
    }

    public int getWenzi_right_y() {
        return wenzi_right_y;
    }

    public void setWenzi_right_y(int wenzi_right_y) {
        this.wenzi_right_y = wenzi_right_y;
    }

    public int getCharCount() {
        return charCount;
    }

    public void setCharCount(int charCount) {
        this.charCount = charCount;
    }

    public String getTempletResultUrl() {
        return templetResultUrl;
    }

    public void setTempletResultUrl(String templetResultUrl) {
        this.templetResultUrl = templetResultUrl;
    }

    public boolean isAppWenzi() {
        return isAppWenzi;
    }

    public void setAppWenzi(boolean appWenzi) {
        isAppWenzi = appWenzi;
    }

    public boolean isAddPic() {
        return isAddPic;
    }

    public void setAddPic(boolean addPic) {
        isAddPic = addPic;
    }

    public int getPictureShape() {
        return pictureShape;
    }

    public void setPictureShape(int pictureShape) {
        this.pictureShape = pictureShape;
    }

    public String getWenzi_color() {
        return wenzi_color;
    }

    public void setWenzi_color(String wenzi_color) {
        this.wenzi_color = wenzi_color;
    }

    public int getWenzi_size() {
        return wenzi_size;
    }

    public void setWenzi_size(int wenzi_size) {
        this.wenzi_size = wenzi_size;
    }

    public int getWenzi_left_x() {
        return wenzi_left_x;
    }

    public void setWenzi_left_x(int wenzi_left_x) {
        this.wenzi_left_x = wenzi_left_x;
    }

    public int getWenzi_left_y() {
        return wenzi_left_y;
    }

    public void setWenzi_left_y(int wenzi_left_y) {
        this.wenzi_left_y = wenzi_left_y;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTempletUrl() {
        return templetUrl;
    }

    public void setTempletUrl(String templetUrl) {
        this.templetUrl = templetUrl;
    }

}
