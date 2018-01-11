package com.wukong.hezhi.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * 
 * @ClassName: PermissionUtil
 * @Description: TODO(权限工具类)
 * @author Huzhiyin
 * @date 2017年10月31日 上午10:45:20
 *
 */
public class PermissionUtil {
	public static Context mContext = ContextUtil.getContext();

	/**
	 * 是否有相机权限
	 */
	public static boolean cameraPermission() {
		boolean isCanUse = true;
		Camera mCamera = null;
		try {
			mCamera = Camera.open();
			Camera.Parameters mParameters = mCamera.getParameters(); // 针对魅族手机
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			isCanUse = false;
		}

		if (mCamera != null) {
			try {
				mCamera.release();
			} catch (Exception e) {
				e.printStackTrace();
				return isCanUse;
			}
		}
		return isCanUse;
	}

	/**
	 * 是否有录音权限
	 */
	public static boolean audioPermission() {
		// 音频获取源
		int audioSource = MediaRecorder.AudioSource.MIC;
		// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
		int sampleRateInHz = 44100;
		// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
		int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
		// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		// 缓冲区字节大小
		int bufferSizeInBytes = 0;

		bufferSizeInBytes = 0;
		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat,
				bufferSizeInBytes);
		// 开始录制音频
		try {
			// 防止某些手机崩溃，例如联想
			audioRecord.startRecording();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		/**
		 * 根据开始录音判断是否有录音权限
		 */
		if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
			return false;
		}
		audioRecord.stop();
		audioRecord.release();
		audioRecord = null;

		return true;
	}

//	/** 存储权限 */
//	public void storagePermission() {
//		final int REQUEST_EXTERNAL_STORAGE = 1;
//		String[] PERMISSIONS_STORAGE = { Manifest.permission.READ_EXTERNAL_STORAGE,
//				Manifest.permission.WRITE_EXTERNAL_STORAGE };
//		int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//		if (permission != PackageManager.PERMISSION_GRANTED) {
//			// We don't have permission so prompt the user
//			ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//		}
//	}

}
