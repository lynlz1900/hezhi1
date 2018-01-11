package com.wukong.hezhi.bean;

/**
 * @author HuZhiyin
 * @ClassName: ${FILE_NAME}
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @date 2017/11/10
 */


public class ArShareInfo {
    private String coverUrl;//视频封面图片地址
    private String shareUrl;//视频地址

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
