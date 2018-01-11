package com.wukong.hezhi.ui.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UploadWebInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.manager.PhotoManager.OnSelectedPhotoListener;
import com.wukong.hezhi.manager.PhotoManager.OnSelectedVideoListener;
import com.wukong.hezhi.manager.TakePhotoManager;
import com.wukong.hezhi.manager.TakePhotoManager.OnSelectedListener;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnClickListener;
import com.wukong.hezhi.utils.BitmapCompressUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.MediaUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;
import com.wukong.hezhi.utils.UriUtil;

import java.io.File;

/**
 * 
 * @ClassName: UploadActivity
 * @Description: TODO(上传信息)
 * @author Huzhiyin
 * @date 2017年10月16日 上午11:06:13
 *
 */
public class UploadActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.upload);
	public String pageCode = "upload";

	@ViewInject(R.id.input_et)
	private EditText input_et;

	@ViewInject(R.id.word_count_tv)
	private TextView word_count_tv;

	@ViewInject(R.id.add_iv)
	private ImageView add_iv;

	@ViewInject(R.id.play_iv)
	private ImageView play_iv;

	@ViewInject(R.id.pic_iv)
	private ImageView pic_iv;

	@ViewInject(R.id.pic_bg_rl)
	private RelativeLayout pic_bg_rl;

	@ViewInject(R.id.loading_pb)
	private ProgressBar loading_pb;

	@ViewInject(R.id.submit_bt)
	private Button submit_bt;

	@ViewInject(R.id.upload_pb)
	private ProgressBar upload_pb;

	@ViewInject(R.id.preview_tv)
	private TextView preview_tv;

	private boolean isInputWord = false;// 是否输入了文字
	private String videoPath;// 视频路径
	private String picPath;// 视频封面图路径
	private int mediaType;// 类型：0，照片，1,视频
	private final int PHOTO = 0;// 照片
	private final int VIDEO = 1;// 视频
	private String url;// 生成预览页面的Url
	private CommodityInfo commodityInfo;// 商品信息

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_upload;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		headView.setTitleStr(ContextUtil.getString(R.string.upload));
		input_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(s)) {
					isInputWord = false;
					word_count_tv.setText("");
				} else {
					isInputWord = true;
					word_count_tv.setText(s.length() + "/30");
				}
				showSubmit();
			}
		});
	}

	/** 控制预览按钮的可点击状态 */
	private void showSubmit() {
		if (isInputWord && pic_bg_rl.getVisibility() == View.VISIBLE) {// 当输入了文字并且显示了图片，才可点击
			submit_bt.setEnabled(true);
		} else {
			submit_bt.setEnabled(false);
		}
	}

	/** 上传数据 */
	private void postData() {
		String content = input_et.getText().toString().trim();
		if (StringUtil.isContainsEmoji(content)) {// 判断是否含有表情
			ScreenUtil.showToast(ContextUtil.getString(R.string.no_emoji));
			return;
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("text", content);
		if (mediaType == PHOTO && !TextUtils.isEmpty(picPath)) {// 上传图片
			params.addBodyParameter("imgs", new File(picPath));
		} else if (mediaType == VIDEO && !TextUtils.isEmpty(videoPath)) {// 上传视频
			params.addBodyParameter("video", new File(videoPath), "video/*");
		}

		setProgressBarVisible(true);
		new HttpUtils().send(HttpRequest.HttpMethod.POST, HttpURL.URL1 + HttpURL.UPLOAD, params,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						// TODO Auto-generated method stub
						super.onLoading(total, current, isUploading);
						LogUtil.d("total:" + total + "current:" + current);
						upload_pb.setMax((int) total);
						upload_pb.setProgress((int) current);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						setProgressBarVisible(false);
						ResponseJsonInfo<UploadWebInfo> info = JsonUtil.fromJson(responseInfo.result,
								UploadWebInfo.class);
						if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
							url = info.getBody().getPrivewUrl();
							commodityInfo.setPreviewUrl(url);// 生成的url
							commodityInfo.setCustomizationType(2);// 视频定制
							toActivity(WebVideoPreviewActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 预览
						} else {
							ScreenUtil.showToast(ContextUtil.getString(R.string.post_fail));
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						setProgressBarVisible(false);
						ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
					}
				});

	}

	private void setProgressBarVisible(boolean visible) {
		if (visible) {
			showProgressDialog();
			upload_pb.setVisibility(View.VISIBLE);
			preview_tv.setVisibility(View.VISIBLE);
			submit_bt.setVisibility(View.INVISIBLE);
		} else {
			dismissProgressDialog();
			upload_pb.setVisibility(View.INVISIBLE);
			preview_tv.setVisibility(View.INVISIBLE);
			submit_bt.setVisibility(View.VISIBLE);
		}
	}

	@OnClick(value = { R.id.submit_bt, R.id.add_iv, R.id.pic_iv, R.id.play_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.add_iv:// 弹框选择
			showPhotoDialog();
			break;
		case R.id.play_iv:// 预览视频
			toActivity(VideoPreviewActivity.class, Constant.Extra.VIDEO_PATH, videoPath);
			break;
		case R.id.pic_iv:// 预览照片
			toActivity(PicPreviewActivity.class, Constant.Extra.PIC_PATH_LIST, picPath);
			break;
		case R.id.submit_bt:// 上传
			postData();
			break;
		}
	}

	/**
	 * 选择照片对话框
	 */
	private void showPhotoDialog() {
		String[] items = { ContextUtil.getString(R.string.photo_video), ContextUtil.getString(R.string.select_album),
				ContextUtil.getString(R.string.select_video) };
		new CustomAlterDialog.Builder(mActivity).setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:// 拍照/拍摄
					takePhoto();
					break;
				case 1:// 选择本地照片
					selectPic();
					break;
				case 2:// 选择本地视频
					selectVideo();
					break;
				}

			}
		}).build().show();
	}

	/** 拍摄/拍照 */
	private void takePhoto() {
		TakePhotoManager.getInstance().takePhoto(mActivity, onSelectedListener);
	}

	/** 拍摄/拍着回调监听 */
	private OnSelectedListener onSelectedListener = new OnSelectedListener() {

		@Override
		public void onSelectedVideo(int photoWay, String path) {
			// TODO Auto-generated method stub
			switch (photoWay) {
			case TakePhotoManager.PHOTO:
				setTakePhoto(path);
				break;
			case TakePhotoManager.VIDEO:
				setTakeVideo(path);
				break;
			}
		}
	};

	private void setTakePhoto(String path) {
		mediaType = PHOTO;
		picPath = path;
		refreshPhoto();
	}

	private void setTakeVideo(final String path) {
		loading_pb.setVisibility(View.VISIBLE);// 压缩图片，显示等待提示。
		new Thread(new Runnable() {
			@Override
			public void run() {
				String fileName = System.currentTimeMillis() + ".png";
				Bitmap bitmap = MediaUtil.getLocalVideoThumbnail(path);// 获取第一帧图片
				picPath = FileUtil.saveBitmap2File(bitmap, HezhiConfig.PIC_PATH, fileName);// 第一帧图片的路径
				runOnUiThread(new Runnable() {
					public void run() {
						loading_pb.setVisibility(View.GONE);// 对图片处理完后，隐藏等待提示。
						mediaType = VIDEO;
						videoPath = path;
						refreshPhoto();
					}
				});
			}
		}).start();
	}

	/** 从相册选择照片 */
	private void selectPic() {
		PhotoManager manager = PhotoManager.getInstance();
		manager.setTailor(false);// 不需要裁剪
		manager.choicePhoto(mActivity, PhotoManager.FROM_ALBUM, null, new PhotoListener());
	}

	/** 相册选择照片的监听 */
	private class PhotoListener implements OnSelectedPhotoListener {
		@Override
		public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo) {
			// TODO Auto-generated method stub
			setPic(mPicFilePath, photo);
		}
	}

	/** 设置显示的图片 */
	private void setPic(final Uri mPicFilePath, final Bitmap photo) {
		loading_pb.setVisibility(View.VISIBLE);// 压缩图片，显示等待提示。
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmapBottomCompress = null;
				File f = new File(UriUtil.getImageAbsolutePath(mPicFilePath));
				if (f != null && f.length() > HezhiConfig.ZERO_POINT_FIVE_M) {// 如果文件大于0.5M.则压缩
					bitmapBottomCompress = BitmapCompressUtil.compress(photo);// 压缩后图片后再旋转，否认容易内存溢出
				} else {
					bitmapBottomCompress = photo;
				}
				Bitmap bitRotate = BitmapCompressUtil.rotateBitmap(bitmapBottomCompress,
						UriUtil.getImageAbsolutePath(mPicFilePath));// 小米手机上，uri转path会出现异常，此处先trycatch掉，后续修改
				String fileName = System.currentTimeMillis() + ".png";
				picPath = FileUtil.saveBitmap2File(bitRotate, HezhiConfig.PIC_PATH, fileName);// 将压缩的图片保存到文件
				runOnUiThread(new Runnable() {
					public void run() {
						loading_pb.setVisibility(View.GONE);// 对图片处理完后，隐藏等待提示。
						mediaType = PHOTO;
						refreshPhoto();
					}
				});

			}
		}).start();
	}

	/** 从相册选择视频 */
	private void selectVideo() {
		PhotoManager manager = PhotoManager.getInstance();
		manager.choiceVideo(mActivity, new VideoListener());
	}

	/** 相册选择视频的监听 */
	private class VideoListener implements OnSelectedVideoListener {
		@Override
		public void onSelectedVideo(Uri uri) {
			// TODO Auto-generated method stub
			setVideo(uri);
		}
	}

	/** 设置显示的视频 */
	private void setVideo(final Uri uri) {
		loading_pb.setVisibility(View.VISIBLE);// 压缩图片，显示等待提示。
		new Thread(new Runnable() {
			@Override
			public void run() {
				String oldPath = UriUtil.getImageAbsolutePath(uri);
				String format = ".mp4";
				try {
					format = "";
					String[] arr = oldPath.split("\\.");
					format = "." + arr[1];
				} catch (Exception e) {
					e.printStackTrace();
				}
				String newPath = HezhiConfig.VIDEO_PATH + System.currentTimeMillis() + format;
				videoPath = FileUtil.copyFile(oldPath, newPath);// 相册里的视频复制一份，并保存至videoPath路径下
				Bitmap bitmap = MediaUtil.getLocalVideoThumbnail(videoPath);// 获取第一帧图片
				String fileName = System.currentTimeMillis() + ".png";
				picPath = FileUtil.saveBitmap2File(bitmap, HezhiConfig.PIC_PATH, fileName);// 将压缩的图片保存到文件
				runOnUiThread(new Runnable() {
					public void run() {
						loading_pb.setVisibility(View.GONE);// 对图片处理完后，隐藏等待提示。
						mediaType = VIDEO;
						refreshPhoto();
					}
				});

			}
		}).start();
	}

	private void refreshPhoto() {
		pic_bg_rl.setVisibility(View.VISIBLE);
//		ViewShowAniUtil.playAni(pic_bg_rl, true);
		switch (mediaType) {
		case PHOTO:// 如果是照片
			play_iv.setVisibility(View.GONE);
			pic_iv.setImageBitmap(BitmapFactory.decodeFile(picPath));// 设置视频封面图
			break;
		case VIDEO:// 如果是视频
			play_iv.setVisibility(View.VISIBLE);
			pic_iv.setImageBitmap(BitmapFactory.decodeFile(picPath));// 设置视频封面图
			break;
		}
		LogUtil.d("picPath:" + picPath);
		LogUtil.d("videoPath:" + videoPath);
		showSubmit();
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass()) && notifyFrom.equals(VideoPreviewActivity.class)) {// 视频预览界面发来的通知
			picPath = null;
			videoPath = null;
			pic_bg_rl.setVisibility(View.INVISIBLE);
//			ViewShowAniUtil.playAni(pic_bg_rl, false);
		}
		if (notifyTo.equals(getClass()) && notifyFrom.equals(PicPreviewActivity.class)) {// 图片预览界面发来的通知
			picPath = null;
			videoPath = null;
			pic_bg_rl.setVisibility(View.INVISIBLE);
//			ViewShowAniUtil.playAni(pic_bg_rl, false);
		}
		LogUtil.d("picPath:" + picPath);
		LogUtil.d("videoPath:" + videoPath);
		showSubmit();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		delete();// 删除文件
	}

	/** 删除图片和视频 */
	private void delete() {
		FileUtil.DeleteFolder(picPath);
		FileUtil.DeleteFolder(videoPath);
	}
}
