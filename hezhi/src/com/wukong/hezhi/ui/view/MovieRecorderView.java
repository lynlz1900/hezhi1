package com.wukong.hezhi.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.scan.lib.camera.PictureCallback;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.utils.BitmapCompressUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ThreadUtil;
import com.wukong.hezhi.utils.ViewShowAniUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class MovieRecorderView extends LinearLayout implements OnErrorListener, OnClickListener {
	private static final String LOG_TAG = "MovieRecorderView";

	private Context context;

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private ImageView change_camera_iv;
	private ProgressBar progressBar;

	private MediaRecorder mediaRecorder;
	private Camera camera;
	private Timer timer;// 计时器

	private int mWidth;// 视频录制分辨率宽度
	private int mHeight;// 视频录制分辨率高度
	private boolean isOpenCamera;// 是否一开始就打开摄像头
	private int recordMaxTime;// 最长拍摄时间
	private int timeCount;// 时间计数
	private File recordFile = null;// 视频文件
	private long sizePicture = 0;
	/** 手机拍摄时旋转的角度（屏幕朝脸，顺时针旋转） */
	private int angle = 0;

	public MovieRecorderView(Context context) {
		this(context, null);
	}

	public MovieRecorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);
		mWidth = a.getInteger(R.styleable.MovieRecorderView_record_width, 640);// 默认640
		mHeight = a.getInteger(R.styleable.MovieRecorderView_record_height, 360);// 默认360

		isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开摄像头
		recordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 11);// 默认最大拍摄时间为10s

		LayoutInflater.from(context).inflate(R.layout.layout_video, this);
		surfaceView = (SurfaceView) findViewById(R.id.surface_view);
		change_camera_iv = (ImageView) findViewById(R.id.change_camera_iv);
		change_camera_iv.setOnClickListener(this);
		// TODO 需要用到进度条，打开此处，也可以自己定义自己需要的进度条，提供了拍摄进度的接口
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar.setMax(recordMaxTime);// 设置进度条最大量
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new CustomCallBack());
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		a.recycle();
	}

	/**
	 * SurfaceHolder回调
	 */
	private class CustomCallBack implements Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (!isOpenCamera)
				return;
			try {
				initCamera();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (!isOpenCamera)
				return;
			freeCameraResource();
		}
	}

	/**
	 * 初始化摄像头
	 */
	public void initCamera() throws IOException {
		setCameraVisible(true);// 重置后摄像头可以翻转
		if (camera != null) {
			freeCameraResource();
		}
		try {
			if (!checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);// TODO
																			// 默认打开前置摄像头
			} else if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)) {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			freeCameraResource();
			((Activity) context).finish();
		}
		if (camera == null)
			return;

		setCameraParams();
		camera.setDisplayOrientation(90);
		camera.setPreviewDisplay(surfaceHolder);
		camera.startPreview();
	}

	boolean cameraBack = true;

	public void changeCamera() {
		setCameraVisible(true);// 重置后摄像头可以翻转
		if (camera != null) {
			freeCameraResource();
		}
		try {
			if (cameraBack) {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);// 默认打开前置摄像头
				cameraBack = false;
			} else {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
				cameraBack = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			freeCameraResource();
			((Activity) context).finish();
		}
		if (camera == null)
			return;
		setCameraParams();
		try {
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 设置摄像头按钮的显示状态 */
	private void setCameraVisible(boolean isChangable) {
		if (change_camera_iv != null) {
			if (isChangable) {
				ViewShowAniUtil.playAni(change_camera_iv, true);
			} else {
				ViewShowAniUtil.playAni(change_camera_iv, false);
			}
		}
	}

	/**
	 * 检查是否有摄像头
	 *
	 * @param facing
	 *            前置还是后置
	 * @return
	 */
	private boolean checkCameraFacing(int facing) {
		int cameraCount = Camera.getNumberOfCameras();
		Camera.CameraInfo info = new Camera.CameraInfo();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, info);
			if (facing == info.facing) {
				return true;
			}
		}
		return false;
	}

	Parameters params;

	/**
	 * 设置摄像头为竖屏
	 */
	private void setCameraParams() {
		if (camera != null) {
			params = camera.getParameters();
			params.set("orientation", "portrait");
			params.setZoom(1);
			setPreviewSize();
			// 手机支持的最大像素
			List<Camera.Size> supportedPictureSizes = params.getSupportedPictureSizes();
			for (Camera.Size size : supportedPictureSizes) {
				sizePicture = (size.height * size.width) > sizePicture ? size.height * size.width : sizePicture;
			}

			// 实现Camera自动对焦
			List<String> focusModes = params.getSupportedFocusModes();
			if (focusModes != null) {
				for (String mode : focusModes) {
					mode.contains("continuous-video");
					params.setFocusMode("continuous-video");
				}
			}
			try {
				camera.setParameters(params);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	/**
	 * 根据手机支持的视频分辨率，设置预览尺寸
	 */
	private void setPreviewSize() {
		if (camera == null) {
			return;
		}
		// 获取手机支持的分辨率集合，并以宽度为基准降序排序
		List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
		Collections.sort(previewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				if (lhs.width > rhs.width) {
					return -1;
				} else if (lhs.width == rhs.width) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		float tmp = 0f;
		float minDiff = 100f;
		float ratio = ScreenUtil.screenRatioW2H(); // 高宽比率最接近屏幕宽度的分辨率
		Camera.Size best = null;
		for (Camera.Size s : previewSizes) {
			tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
			Log.e(LOG_TAG, "setPreviewSize: width:" + s.width + "...height:" + s.height);
			// LogUtil.e(LOG_TAG,"tmp:" + tmp);
			if (tmp < minDiff) {
				minDiff = tmp;
				best = s;
			}
		}
		params.setPreviewSize(best.width, best.height);// 预览比率
		Log.e(LOG_TAG, "setPreviewSize BestSize: width:" + best.width + "...height:" + best.height);
	}

	/**
	 * 根据手机支持的视频分辨率，设置录制尺寸
	 */
	private void setVideoSize() {
		if (camera == null) {
			return;
		}
		// 获取手机支持的分辨率集合，并以宽度为基准降序排序
		List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
		Collections.sort(previewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				if (lhs.width > rhs.width) {
					return -1;
				} else if (lhs.width == rhs.width) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		float tmp = 0f;
		float minDiff = 100f;
		float ratio = ScreenUtil.screenRatioW2H();// 高宽比率最接近屏幕宽度的分辨率
		Camera.Size best = null;
		for (Camera.Size s : previewSizes) {
			tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
			Log.e(LOG_TAG, "setVideoSize: width:" + s.width + "...height:" + s.height);
			if (tmp < minDiff) {
				minDiff = tmp;
				best = s;
			}
		}
		Log.e(LOG_TAG, "setVideoSize BestSize: width:" + best.width + "...height:" + best.height);
		// 设置录制尺寸
		mWidth = best.width;
		mHeight = best.height;
	}

	/**
	 * 根据手机支持的照片分辨率，设置图片尺寸
	 */
	private void setPictureSize() {
		if (camera == null) {
			return;
		}
		// 获取手机支持的分辨率集合，并以宽度为基准降序排序
		List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
		Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				if (lhs.width > rhs.width) {
					return -1;
				} else if (lhs.width == rhs.width) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		Camera.Size best = null;
		boolean start = true;
		for (Camera.Size s : pictureSizes) {
			Log.e(LOG_TAG, "setPictureSize: width:" + s.width + "...height:" + s.height);
			if (start && s.height < 1000) {// 选择最适合的分辨率
				start = false;
				best = s;
			}
		}
		Log.e(LOG_TAG, "setPictureSize BestSize: width:" + best.width + "...height:" + best.height);
		params.setPictureSize(best.width, best.height);// 拍照保存比
	}

	/**
	 * 释放摄像头资源
	 */
	private void freeCameraResource() {
		try {
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.lock();
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			camera = null;
		}
	}

	private String videoPath;

	/**
	 * 创建视频文件
	 */
	private void createRecordDir() {
		File sampleDir = new File(HezhiConfig.VIDEO_PATH);
		if (!sampleDir.exists()) {
			sampleDir.mkdirs();
		}
		try {
			// TODO 文件名用的时间戳，可根据需要自己设置，格式也可以选择3gp，在初始化设置里也需要修改
			videoPath = HezhiConfig.VIDEO_PATH + System.currentTimeMillis() + ".mp4";
			recordFile = new File(videoPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return videoPath;
	}

	/**
	 * 录制视频初始化
	 */
	private void initRecord() throws Exception {
		mediaRecorder = new MediaRecorder();
		mediaRecorder.reset();
		if (camera != null) {
			camera.unlock();
			mediaRecorder.setCamera(camera);
		}

		mediaRecorder.setOnErrorListener(this);
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		mediaRecorder.setVideoSource(VideoSource.CAMERA);// 视频源
		if (PermissionUtil.audioPermission()) {
			mediaRecorder.setAudioSource(AudioSource.MIC);// 音频源
		}
		mediaRecorder.setOutputFormat(OutputFormat.MPEG_4);// 视频输出格式
		if (PermissionUtil.audioPermission()) {
			mediaRecorder.setAudioEncoder(AudioEncoder.AAC);// 音频格式
		}
		mediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率

		if (sizePicture < 5000000) {// 这里设置可以调整清晰度
			mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 512);
		} else if (sizePicture < 10000000) {
			mediaRecorder.setVideoEncodingBitRate(15 * 1024 * 512);
		} else {
			mediaRecorder.setVideoEncodingBitRate(25 * 1024 * 512);
		}

		if (cameraBack) {
			mediaRecorder.setOrientationHint(getBackCameraAngle());
		} else {
			mediaRecorder.setOrientationHint(getFrontCameraAngle());
		}

		mediaRecorder.setVideoEncoder(VideoEncoder.H264);// 视频录制格式
		// mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
		mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
		mediaRecorder.prepare();
		mediaRecorder.start();
	}

	public void record(int angle) {
		this.angle = angle;
		record(null);
	}

	/** 后置摄像头，输出录像/照片旋转的角度 */
	private int getBackCameraAngle() {
		return 90 + angle == 360 ? 0 : 90 + angle;
	}

	/** 前置摄像头，输出录像/照片旋转的角度 */
	private int getFrontCameraAngle() {
		return 270 - angle;
	}

	/**
	 * 开始录制视频
	 *
	 * @param onRecordFinishListener
	 *            达到指定时间之后回调接口
	 */
	public void record(final OnRecordFinishListener onRecordFinishListener) {
		if (onRecordFinishListener == null) {
			this.onRecordFinishListener = new OnRecordFinishListener() {

				@Override
				public void onRecordFinish() {
					// TODO Auto-generated method stub

				}
			};
		}
		this.onRecordFinishListener = onRecordFinishListener;
		setVideoSize();
		setCameraVisible(false);// 录制中禁止翻转摄像头
		createRecordDir();
		try {
			if (!isOpenCamera) {// 如果未打开摄像头，则打开
				initCamera();
			}
			initRecord();
			timeCount = 0;// 时间计数器重新赋值
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					timeCount++;
					progressBar.setProgress(timeCount);// 设置进度条
					if (onRecordProgressListener != null) {
						onRecordProgressListener.onProgressChanged(recordMaxTime, timeCount);
					}

					// 达到指定时间，停止拍摄
					if (timeCount == recordMaxTime) {
						stop();
						if (MovieRecorderView.this.onRecordFinishListener != null)
							MovieRecorderView.this.onRecordFinishListener.onRecordFinish();
					}
				}
			}, 0, 1000);
		} catch (Exception e) {
			e.printStackTrace();
			if (mediaRecorder != null) {
				mediaRecorder.release();
			}
			freeCameraResource();
		}
	}

	/** 拍照 */
	public void photo(int angle, PhotoCallback photoCallback) {
		this.angle = angle;
		if (photoCallback == null) {
			this.photoCallback = new PhotoCallback() {

				@Override
				public void onSucess(String path) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub

				}
			};
		}

		this.photoCallback = photoCallback;
		setPictureSize();
		setCameraVisible(false);// 录制中禁止翻转摄像头
		if (camera != null) {
			try {
				camera.setParameters(params);
				camera.takePicture(shutterCallback, null, pictureCallback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ShutterCallback shutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() { // 不做任何处理，会发出系统快门声音

		}
	};

	public interface PhotoCallback {
		void onSucess(String path);

		void onFail(String msg);
	}

	private PhotoCallback photoCallback;
	private Bitmap bitmapBottomCompress = null;// 压缩后的图片
	// 创建一个PictureCallback对象，并实现其中的onPictureTaken方法
	private PictureCallback pictureCallback = new PictureCallback() {
		// 该方法用于处理拍摄后的照片数据
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 停止照片拍摄
			try {
				camera.stopPreview();
			} catch (Exception e) {
				photoCallback.onFail("拍照失败");
			}
			if (data == null) {
				photoCallback.onFail("拍照失败");
				return;
			}
			final byte[] dt = data;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap photo = BitmapFactory.decodeByteArray(dt, 0, dt.length);
					Bitmap bitmapBottomCompress = BitmapCompressUtil.compress(photo);// 压缩后的图片后再旋转，否则容易内存溢出
					Matrix m = new Matrix();
					if (cameraBack) {// 如果是后置摄像头，将图片顺时针旋转90度
						m.setRotate(getBackCameraAngle(), (float) bitmapBottomCompress.getWidth() / 2,
								(float) bitmapBottomCompress.getHeight() / 2);
					} else {// 如果是后置摄像头，将图片顺时针旋转270度
						m.setRotate(getFrontCameraAngle(), (float) bitmapBottomCompress.getWidth() / 2,
								(float) bitmapBottomCompress.getHeight() / 2);
					}
					Bitmap bmRotate = Bitmap.createBitmap(bitmapBottomCompress, 0, 0, bitmapBottomCompress.getWidth(),
							bitmapBottomCompress.getHeight(), m, true);// 旋转后的图片
					final String picPath = FileUtil.saveBitmap2File(bmRotate, HezhiConfig.PIC_PATH,
							System.currentTimeMillis() + ".png");// 保存图片
					ThreadUtil.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							photoCallback.onSucess(picPath);
						}
					});

				}
			}).start();
		}
	};

	/**
	 * 停止拍摄
	 */
	public void stop() {
		stopRecord();
		releaseRecord();
		freeCameraResource();
	}

	/**
	 * 停止录制
	 */
	public void stopRecord() {
		progressBar.setProgress(0);
		if (timer != null)
			timer.cancel();
		if (mediaRecorder != null) {
			mediaRecorder.setOnErrorListener(null);// 设置后防止崩溃
			mediaRecorder.setPreviewDisplay(null);
			try {
				mediaRecorder.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 释放资源
	 */
	private void releaseRecord() {
		if (mediaRecorder != null) {
			mediaRecorder.setOnErrorListener(null);
			try {
				mediaRecorder.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mediaRecorder = null;
	}

	/**
	 * 获取当前录像时间
	 *
	 * @return timeCount
	 */
	public int getTimeCount() {
		return timeCount;
	}

	/**
	 * 设置最大录像时间
	 *
	 * @param recordMaxTime
	 */
	public void setRecordMaxTime(int recordMaxTime) {
		this.recordMaxTime = recordMaxTime;
	}

	/**
	 * 返回录像文件
	 *
	 * @return recordFile
	 */
	public File getRecordFile() {
		return recordFile;
	}

	/**
	 * 录制完成监听
	 */
	private OnRecordFinishListener onRecordFinishListener;

	/**
	 * 录制完成接口
	 */
	public interface OnRecordFinishListener {
		void onRecordFinish();
	}

	/**
	 * 录制进度监听
	 */
	private OnRecordProgressListener onRecordProgressListener;

	/**
	 * 设置录制进度监听
	 *
	 * @param onRecordProgressListener
	 */
	public void setOnRecordProgressListener(OnRecordProgressListener onRecordProgressListener) {
		this.onRecordProgressListener = onRecordProgressListener;
	}

	/**
	 * 录制进度接口
	 */
	public interface OnRecordProgressListener {
		/**
		 * 进度变化
		 *
		 * @param maxTime
		 *            最大时间，单位秒
		 * @param currentTime
		 *            当前进度
		 */
		void onProgressChanged(int maxTime, int currentTime);
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		try {
			if (mr != null)
				mr.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.change_camera_iv:
			changeCamera();
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getPointerCount() == 1) {
			handleFocusMetering(event, camera);
		}
		return super.onTouchEvent(event);
	}

	private String TAG = "MovieRecorderView";
	private void handleFocusMetering(MotionEvent event, Camera camera) {
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, viewWidth, viewHeight);
		Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f, viewWidth, viewHeight);

		camera.cancelAutoFocus();
		Camera.Parameters params = camera.getParameters();
		if (params.getMaxNumFocusAreas() > 0) {
			List<Camera.Area> focusAreas = new ArrayList<>();
			focusAreas.add(new Camera.Area(focusRect, 800));
			params.setFocusAreas(focusAreas);
		} else {
			Log.i(TAG, "focus areas not supported");
		}
		if (params.getMaxNumMeteringAreas() > 0) {
			List<Camera.Area> meteringAreas = new ArrayList<>();
			meteringAreas.add(new Camera.Area(meteringRect, 800));
			params.setMeteringAreas(meteringAreas);
		} else {
			Log.i(TAG, "metering areas not supported");
		}
		final String currentFocusMode = params.getFocusMode();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		camera.setParameters(params);

		camera.autoFocus(new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				Camera.Parameters params = camera.getParameters();
				params.setFocusMode(currentFocusMode);
				camera.setParameters(params);
			}
		});
	}
	private static Rect calculateTapArea(float x, float y, float coefficient, int width, int height) {
		float focusAreaSize = 300;
		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
		int centerX = (int) (x / width * 2000 - 1000);
		int centerY = (int) (y / height * 2000 - 1000);

		int halfAreaSize = areaSize / 2;
		RectF rectF = new RectF(clamp(centerX - halfAreaSize, -1000, 1000)
				, clamp(centerY - halfAreaSize, -1000, 1000)
				, clamp(centerX + halfAreaSize, -1000, 1000)
				, clamp(centerY + halfAreaSize, -1000, 1000));
		return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
	}

	private static int clamp(int x, int min, int max) {
		if (x > max) {
			return max;
		}
		if (x < min) {
			return min;
		}
		return x;
	}
}
