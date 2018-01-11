package com.wukong.hezhi.manager;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.connect.share.QQShare;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.ui.activity.BaseApplication;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;

/**
 * @author HuZhiyin
 * @ClassName: QQShareManager
 * @Description: TODO(QQ分享管理类)
 * @date 2017-12-20 上午11:38:23
 */
public class QQShareManager {
    private int QQ_ZONE = 3;
    private int QQ_FRIDEND = 2;

    private QQShareManager() {
    }

    private static class Holder {
        private static final QQShareManager SINGLETON = new QQShareManager();
    }

    /**
     * 单一实例
     */
    public static QQShareManager getInstance() {
        return Holder.SINGLETON;
    }

    /**
     * 分享到qq
     */
    public void shareToQQ(ShareInfo shareInfo) {
        if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
            ScreenUtil.showToast("QQ未安装");
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getDescription());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImagUrl());
        if (shareInfo.getType() == QQ_ZONE) {// 分享给空间
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        BaseApplication.mTencent.shareToQQ(ActivitiesManager.getInstance().currentActivity(), params, null);
    }

    /***
     * 分享图片到qq,系统分享的方法
     */
    public void sharPic(String picPath) {
        if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
            ScreenUtil.showToast("QQ未安装");
            return;
        }
        Uri imageUri = Uri.fromFile(new File(picPath));
        Intent shareIntent = new Intent();
        // 发送图片到qq
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        shareIntent.setComponent(comp);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }

    /***
     * 分享视频到qq,系统分享的方法
     */
    public void sharVideo(String videoPath) {
        if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
            ScreenUtil.showToast("QQ未安装");
            return;
        }
//		String path = videoPath.substring(7);
        Uri imageUri = Uri.fromFile(new File(videoPath));
        Intent shareIntent = new Intent();
        // 发送图片到qq
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        shareIntent.setComponent(comp);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("video/*");
        ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享视频"));
    }

    /**
     * 分享本地照片到QQ空间
     */
    public void shareToQQZone(String picPath) {
        if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
            ScreenUtil.showToast("QQ未安装");
            return;
        }
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, picPath);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        BaseApplication.mTencent.shareToQQ(ActivitiesManager.getInstance().currentActivity(), params, null);
    }
}
