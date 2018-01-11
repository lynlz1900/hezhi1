package com.wukong.hezhi.manager;

import com.wukong.hezhi.ui.activity.PhotoActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * 
 * @ClassName: PhotoManager
 * @Description: TODO(选择照片管理类)
 * @author Huzhiyin
 * @date 2017年9月11日 上午10:24:17
 *
 */
public class PhotoManager {

	private Activity mActivity;

	private Uri mPicFilePath;// 图片的路劲

	private int mFromWay;// 选择图片的方式，相册或相机

	public static final int FROM_ALBUM = 0;// 打开相册

	public static final int FROM_CAMERA = 1;// 打开相机

	public static final int FROM_VIDEO = 2;// 打开相册里的视频

	private boolean isTailor = true;// 是否需要裁剪

	private PhotoManager() {
	}

	private static class Holder {
		private static final PhotoManager SINGLETON = new PhotoManager();
	}

	public static PhotoManager getInstance() {
		return Holder.SINGLETON;
	}

	public void choiceVideo(Activity activity, OnSelectedVideoListener listener) {
		mActivity = activity;
		mFromWay = PhotoManager.FROM_VIDEO;
		videoListener = listener;
		selectVideo(mFromWay);
	}

	public void choicePhoto(Activity activity, int way, Uri picFilePath, OnSelectedPhotoListener listener) {
		mActivity = activity;
		mFromWay = way;
		mPicFilePath = picFilePath;
		mListener = listener;
		switch (way) {
		case PhotoManager.FROM_ALBUM:
			selectPhoto(PhotoManager.FROM_ALBUM);// 从相册打卡
			break;
		case PhotoManager.FROM_CAMERA:
			selectPhoto(PhotoManager.FROM_CAMERA);// 打开相机
			break;
		}
	}

	private int height;
	private int with;
	private int weightRatio;
	private int heightRatio;

	/** 设置图片的宽高 */
	public void setWandH(int w, int h) {
		with = w;
		height = h;
	}

	/** 设置图片的宽高比 */
	public void setWHRatio(int w, int h) {
		weightRatio = w;
		heightRatio = h;
	}

	public void setTailor(boolean isTailor) {
		this.isTailor = isTailor;
	}

	/** 选择视频 */
	private void selectVideo(int type) {
		Intent intent = new Intent(mActivity, PhotoActivity.class);
		intent.putExtra("Type", type);
		mActivity.startActivity(intent);
	}

	/** 选择照片，Album，从相册选择，Camera，拍照 */
	private void selectPhoto(int type) {
		Intent intent = new Intent(mActivity, PhotoActivity.class);
		intent.putExtra("Type", type);
		intent.putExtra("Uri", mPicFilePath);
		intent.putExtra("isTailor", isTailor);
		if (height != 0) {
			intent.putExtra("height", height);
		}
		if (with != 0) {
			intent.putExtra("with", with);
		}
		if (heightRatio != 0) {
			intent.putExtra("heightRatio", heightRatio);
		}
		if (weightRatio != 0) {
			intent.putExtra("weightRatio", weightRatio);
		}
		height = 0;
		with = 0;
		weightRatio = 0;
		heightRatio = 0;
		isTailor = true;
		mActivity.startActivity(intent);
	}

	/**
	 * 得到已经选择好的图片，这个方法必须在onActivityResult中进行回调
	 */
	public void getSelectedPhoto(Bitmap photo,Uri uri) {
		mListener.onSelectedPhoto(mFromWay, uri, photo);
	}

	public void getSelectedVideo(Uri uri) {
		videoListener.onSelectedVideo(uri);
	}

	private OnSelectedPhotoListener mListener;
	private OnSelectedVideoListener videoListener;

	public interface OnSelectedPhotoListener {

		public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo);

	}

	public interface OnSelectedVideoListener {

		public void onSelectedVideo(Uri uri);

	}

}
