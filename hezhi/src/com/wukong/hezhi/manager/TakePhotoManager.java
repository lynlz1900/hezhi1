package com.wukong.hezhi.manager;

import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.activity.VideoRecordActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.net.Uri;

/**
 * 
 * @ClassName: TakePhotoManager
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Huzhiyin
 * @date 2017年11月2日 上午8:52:01
 *
 */
public class TakePhotoManager {

	private Activity activity;
	public static final int PHOTO = 0;// 拍照

	public static final int VIDEO = 1;// 录视频

	private TakePhotoManager() {
	}

	private static class Holder {
		private static final TakePhotoManager SINGLETON = new TakePhotoManager();
	}

	public static TakePhotoManager getInstance() {
		return Holder.SINGLETON;
	}

	public void takePhoto(Activity activity, OnSelectedListener onSelectedListener) {
		this.activity = activity;
		this.onSelectedListener = onSelectedListener;
		if (!PermissionUtil.cameraPermission()) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
			return;
		}
		JumpActivityManager.getInstance().toActivity(activity, VideoRecordActivity.class);
	}

	public void setSelected(int photoWay, String path) {
		onSelectedListener.onSelectedVideo(photoWay, path);
	}

	private OnSelectedListener onSelectedListener;

	public interface OnSelectedListener {
		public void onSelectedVideo(int photoWay, String path);
	}

}
