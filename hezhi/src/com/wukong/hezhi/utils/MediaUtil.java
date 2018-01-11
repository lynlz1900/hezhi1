package com.wukong.hezhi.utils;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public class MediaUtil {
	/** 获取流视频的第一帧图片 */
	public static Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	/**
	 * 获取本地视频的第一帧
	 */
	public static Bitmap getLocalVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		// MediaMetadataRetriever 是android中定义好的一个类，提供了统一
		// 的接口，用于从输入的媒体文件中取得帧和元数据；
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			// 根据文件路径获取缩略图
			retriever.setDataSource(filePath);
			// 获得第一帧图片
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			retriever.release();
		}
		return bitmap;
	}
}
