package com.wukong.hezhi.ui.view;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.ui.activity.QQActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXEntryActivity;
import com.wukong.hezhi.wxapi.WXShareManager;

public class ShareWindows extends PopupWindow implements OnClickListener {
	private TextView share_wechat_tv, share_wechatcircle_tv, share_qq_tv,
			share_qzone_tv, cancel_tv;
	private RelativeLayout ll_popup;

	private ShareWindows() {
		View view = View.inflate(ContextUtil.getContext(),
				R.layout.layout_popupwindow_share, null);
		this.setAnimationStyle(R.style.popwindow_anim_buttomfromparent_style);
		ll_popup = (RelativeLayout) view.findViewById(R.id.ll_popup);
		share_wechat_tv = (TextView) view.findViewById(R.id.share_wechat_tv);
		share_wechatcircle_tv = (TextView) view
				.findViewById(R.id.share_wechatcircle_tv);
		share_qq_tv = (TextView) view.findViewById(R.id.share_qq_tv);
		share_qzone_tv = (TextView) view.findViewById(R.id.share_qzone_tv);
		cancel_tv = (TextView) view.findViewById(R.id.cancel_tv);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		// setBackgroundDrawable(null);// 不响应返回键
		// setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		// setOutsideTouchable(false);
		setContentView(view);
	}

	private static class Holder {
		private static final ShareWindows SINGLETON = new ShareWindows();
	}

	/**
	 * 单一实例
	 * */
	public static ShareWindows getInstance() {
		return Holder.SINGLETON;
	}

	public void show(View parent, ShareInfo shareInfo) {
		this.shareInfo = shareInfo;
		ll_popup.setOnClickListener(this);
		share_wechat_tv.setOnClickListener(this);
		share_wechatcircle_tv.setOnClickListener(this);
		share_qq_tv.setOnClickListener(this);
		share_qzone_tv.setOnClickListener(this);
		cancel_tv.setOnClickListener(this);
		int heght = 0;
		if (Build.VERSION.SDK_INT >= 24) {// 如果是7.0以上的系统，需要获取状态栏的高度。
			heght = ScreenUtil.getStatusBarHeight();
		}
		showAtLocation(parent, Gravity.BOTTOM, 0, heght);
		update();
	}

	private ShareInfo shareInfo;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.share_wechat_tv:// 分享到微信好友
			shareInfo.setType(SendMessageToWX.Req.WXSceneSession);
			WXShareManager.getInstance().share(shareInfo);
			dismiss();
			break;
		case R.id.share_wechatcircle_tv:// 分享到朋友圈
			shareInfo.setType(SendMessageToWX.Req.WXSceneTimeline);
			WXShareManager.getInstance().share(shareInfo);
			dismiss();
			break;
		case R.id.share_qq_tv:// 分享qq好友
			shareInfo.setType(QQActivity.QQ_FRIDEND);
			QQActivity.shareToQQ(shareInfo);
			dismiss();
			break;
		case R.id.share_qzone_tv:// 分享qzone
			shareInfo.setType(QQActivity.QQ_ZONE);
			QQActivity.shareToQQ(shareInfo);
			dismiss();
			break;
		case R.id.cancel_tv://取消
			dismiss();
			break;
		case R.id.ll_popup:// 分享微博
			dismiss();
			break;
		}
	}

}