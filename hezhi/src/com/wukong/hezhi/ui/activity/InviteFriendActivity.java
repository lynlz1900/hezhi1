package com.wukong.hezhi.ui.activity;

import android.view.View;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.StatusBarUtil;
import com.wukong.hezhi.wxapi.WXShareManager;

/**
 * 
 * @ClassName: InviteFriendActivity
 * @Description: TODO(邀请)
 * @author HuZhiyin
 * @date 2017-2-28 上午9:45:22
 *
 */
public class InviteFriendActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.invite_friend);
	public String pageCode = "invite_friend";

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_invite_friend;
	}

	private ShareInfo shareInfo;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.base));// 设置状态栏颜色
		shareInfo = new ShareInfo();
		shareInfo.setUrl("http://download.pkgiot.com/hezhi");
		shareInfo.setDescription("想要什么？盒子知道");
		shareInfo.setTitle("邀请您加入盒知");
		shareInfo.setImagUrl(HezhiConfig.HEZHI_LOGO_URL);
	}

	@OnClick(value = { R.id.header_left, R.id.share_wechat_tv, R.id.share_wechatcircle_tv, R.id.share_qq_tv,
			R.id.share_qzone_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			finish();
			break;
		case R.id.share_wechat_tv:// 分享到微信好友
			shareInfo.setType(SendMessageToWX.Req.WXSceneSession);
			WXShareManager.getInstance().share(shareInfo);
			break;
		case R.id.share_wechatcircle_tv:// 分享到朋友圈
			shareInfo.setType(SendMessageToWX.Req.WXSceneTimeline);
			WXShareManager.getInstance().share(shareInfo);
			break;
		case R.id.share_qq_tv:// 分享qq好友
			shareInfo.setType(QQActivity.QQ_FRIDEND);
			QQActivity.shareToQQ(shareInfo);
			break;
		case R.id.share_qzone_tv:// 分享qzone
			shareInfo.setType(QQActivity.QQ_ZONE);
			QQActivity.shareToQQ(shareInfo);
			break;
		}
	}

}
