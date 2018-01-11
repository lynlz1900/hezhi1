package com.wukong.hezhi.wxapi;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.ui.activity.BaseApplication;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.PhotoUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

/***
 * 
 * @ClassName: WXShareManager
 * @Description: TODO(微信分享)
 * @author HuZhiyin
 * @date 2017-8-9 上午10:26:11
 * 
 */

public class WXShareManager {
	private WXShareManager() {
	}

	private static class Holder {
		private static final WXShareManager SINGLETON = new WXShareManager();
	}

	public static WXShareManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 分享微信 ,不带带回调接口 */
	public void share(ShareInfo shareInfo) {
		share(shareInfo, null);
	}

	/** 分享微信 ,带回调接口 */
	@SuppressWarnings("unchecked")
	public void share(final ShareInfo shareInfo, IShareCallBack wxShareCallBack) {

		if (!PackageUtil.isWeixinAvilible(ContextUtil.getContext())) {
			ScreenUtil.showToast("微信未安装");
			return;
		}
		if (shareInfo == null) {
			ScreenUtil.showToast("微信分享参数为空");
			return;
		}
		if (wxShareCallBack != null) {
			shareCallBack = wxShareCallBack;
		}
		String thumUrl = HezhiConfig.HEZHI_LOGO_URL;

		if (!TextUtils.isEmpty(shareInfo.getImagUrl())) {
			thumUrl = shareInfo.getImagUrl();
		}

		Glide.with(ContextUtil.getContext()).load(thumUrl).asBitmap().fitCenter().into(new SimpleTarget(250, 250) {
			@Override
			public void onResourceReady(Object arg0, GlideAnimation arg1) {
				// TODO Auto-generated method stub
				Bitmap bitmap = null;
				try {
					bitmap = PhotoUtil.changeColor((Bitmap) arg0); //防止图片背景变黑
				} catch (Exception e) {
					bitmap = (Bitmap) arg0;
				}
				// 初始化一个WXWebpageObject对象，填写url
				WXWebpageObject webpage = new WXWebpageObject();
				webpage.webpageUrl = shareInfo.getUrl();
				// 用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题，描述
				final WXMediaMessage msg = new WXMediaMessage(webpage);
				msg.title = shareInfo.getTitle();
				msg.description = shareInfo.getDescription();
				msg.thumbData = bmpToByteArray(bitmap, getImageType(shareInfo));
				// 构造一个Req
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("webpage");// transaction字段用于唯一标识一个请求
				req.message = msg;
				// 发送到聊天界面——WXSceneSession，发送到朋友圈——WXSceneTimeline，添加到微信收藏——WXSceneFavorite
				req.scene = shareInfo.getType();
				BaseApplication.wxApi.sendReq(req);
			}
		});
	}

	/** 判断图片的格式 */
	private String getImageType(final ShareInfo shareInfo) {
		String type = "";
		String imagUrl = "";
		if (shareInfo != null) {
			imagUrl = shareInfo.getImagUrl();
			try {
				type = imagUrl.substring(imagUrl.length() - 3, imagUrl.length());
			} catch (Exception e) {
				return "error";
			}
		}
		return type;
	}

	/** 分享微信图片 ，系统分享的方法 */
	public void sharePic(String picPath, String tpye) {
		if (!PackageUtil.isWeixinAvilible(ContextUtil.getContext())) {
			ScreenUtil.showToast("微信未安装");
			return;
		}
		Uri imageUri = Uri.fromFile(new File(picPath));
		Intent shareIntent = new Intent();
		// 发送图片给好友。
		ComponentName comp = new ComponentName("com.tencent.mm", tpye);
		shareIntent.setComponent(comp);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		shareIntent.setType("image/*");
		ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享图片"));
	}

	/** 分享微信视频 ，系统分享的方法 */
	public void shareVideo(String videoPath, String tpye) {
		if (!PackageUtil.isWeixinAvilible(ContextUtil.getContext())) {
			ScreenUtil.showToast("微信未安装");
			return;
		}
//		String path = videoPath.substring(7);
		Uri imageUri = Uri.fromFile(new File(videoPath));
		Intent shareIntent = new Intent();
		// 发送图片给好友。
		ComponentName comp = new ComponentName("com.tencent.mm", tpye);
		shareIntent.setComponent(comp);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		shareIntent.setType("video/*");
		ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享视频"));
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 压缩图片
	 */
	private byte[] bmpToByteArray(final Bitmap bmp, String type) {
		
		Bitmap mBp = Bitmap.createScaledBitmap(bmp, 120, 120, true);//缩小图片，图片过大无法分享
		 if (bmp != null && !bmp.isRecycled()) 
			 bmp.recycle();
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		if (type != null && type.equals("jpg")) {
			mBp.compress(CompressFormat.JPEG, 100, output);
		} else {
			mBp.compress(CompressFormat.PNG, 100, output);
		}

		 if (mBp != null && !mBp.isRecycled()) {
			 mBp.recycle();
		 }
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String params = "";

	private void addParams(String key, String value) {
		if (TextUtils.isEmpty(params)) {
			params = key + "=" + value;
		}
		params = params + "&" + key + "=" + value;
	}

	private IShareCallBack shareCallBack = new IShareCallBack() {

		@Override
		public void sucess() {
			// TODO Auto-generated method stub

		}

		@Override
		public void fail() {
			// TODO Auto-generated method stub

		}
	};

	/** 微信分享回调接口 */
	public interface IShareCallBack {
		void sucess();

		void fail();
	}

}
