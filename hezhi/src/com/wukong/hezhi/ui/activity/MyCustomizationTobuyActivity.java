package com.wukong.hezhi.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.OrderWindows;
import com.wukong.hezhi.ui.view.OrderWindows.IOrderCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXShareManager;

import java.util.HashMap;
import java.util.Map;

/***
 * 
 * @ClassName: MyCustomizationTobuyActivity
 * @Description: TODO(我的定制商品购买)
 * @author HuangFeiFei
 * @date 2017-8-9 下午2：26
 * 
 */
public class MyCustomizationTobuyActivity extends BaseActivity {

	public String pageName = ContextUtil.getString(R.string.buy_now);
	public String pageCode = "buy_now";

	@ViewInject(R.id.image_type)
	private ImageView image_type;

	@ViewInject(R.id.image_nfc_url)
	private ImageView image_nfc_url;

	@ViewInject(R.id.customization_name_long)
	private TextView customization_name_long;

	@ViewInject(R.id.customization_price)
	private TextView customization_price;

	private CommodityInfo commodityInfo;// 商品信息

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_customization_tobuy;
	}

	@Override
	protected void init() {
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.CUSTOMIZATION_INFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		setSwipeBackEnable(false);
		headView.setLeftTitleText("", R.drawable.icon_close1);
		headView.setLeftLis(this);
		headView.setTitleStr(ContextUtil.getString(R.string.commodity));
		initView();
	}

	/** 初始化控件 **/
	private void initView() {

		if (commodityInfo.getName() == null || commodityInfo.getName().length() <= 0)
			customization_name_long.setText("");
		else
			customization_name_long.setText(commodityInfo.getName());
		customization_price.setText(ContextUtil.getString(R.string.money_type)
				+ (commodityInfo.getPrice() <= 0 ? 0.00 : DataUtil.double2pointString(commodityInfo.getPrice())));
		GlideImgUitl.glideLoaderNoAnimation(mActivity, commodityInfo.getImageUrl(), image_type, 2);
		if (commodityInfo.getCustomizationType() == 2 && !TextUtils.isEmpty(commodityInfo.getPreviewUrl())) {
			image_nfc_url.setVisibility(View.VISIBLE);
		}

	}

	@OnClick(value = { R.id.header_left, R.id.text_buy_now, R.id.image_share_wechat, R.id.image_share_wechatcircle,
			R.id.image_share_qq, R.id.image_share_qzone, R.id.image_nfc_url })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:// 立即购买
			finish();
			break;
		case R.id.text_buy_now:// 立即购买
			buyNow();
			break;
		case R.id.image_nfc_url:// 预览链接（此链接只对视频定制有效果）
			gotoMedioUrlPre();
			break;
		case R.id.image_share_wechat:// 微信分享好友
			shared(Constant.SharedType.SHARE_WECHAT.ordinal());
			break;
		case R.id.image_share_wechatcircle:// 微信分享朋友
			shared(Constant.SharedType.SHARE_WECHATCIRCLE.ordinal());
			break;
		case R.id.image_share_qq:// qq分享
			shared(Constant.SharedType.SHARE_QQ.ordinal());
			break;
		case R.id.image_share_qzone:// 空间分享
			shared(Constant.SharedType.SHARE_QZONE.ordinal());
			break;
		}
	}

	/** 跳转到视频定制预览界面 */
	private void gotoMedioUrlPre() {
		toActivity(WebCustomVideoPreviewActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);
	}

	/** 立即购买 **/
	private void buyNow() {
		if (commodityInfo == null) {// 定制信息不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.commodity_isnull));
			return;
		}
		
		getCommodityInfoDataHttp();
	}

	/**
	 * 跳转到确认订单界面
	 */
	private void gotoPayConfirm() {
		if (commodityInfoNew == null)
			return;

		if(commodityInfoNew.getInventory() <1){
			ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
			return;
		}
		
		commodityInfoNew.setPreviewUrl(commodityInfo.getPreviewUrl());
		commodityInfoNew.setImageUrl(commodityInfo.getImageUrl());
		commodityInfoNew.setBuyType(commodityInfo.getCustomizationType());
		commodityInfoNew.setCustomId(commodityInfo.getCustomId());
		commodityInfoNew.setOrderNum(1);

		if (commodityInfoNew.getIsAnniversary() == 1) {
			CheckIsBought();
		} else {
			OrderWindows.getInstance().show(commodityInfoNew, new IOrderCallBack() {
				@Override
				public void response(Object object) {
					ObserveManager.getInstance().notifyState(CommodityInfoActivity.class,
							MyCustomizationTobuyActivity.class, null);// 通知商品详情,处理弹出unity界面的bug
					commodityInfoNew = (CommodityInfo) object;
					JumpActivityManager.getInstance().toActivity(mActivity, OrderConfirmActivity.class,
							Constant.Extra.COMMDITYINFO, commodityInfoNew);
				}
			});// 弹出数量选择对话框
		}
	}

	CommodityInfo commodityInfoNew = null;

	/** 通过productId获取商品信息http接口 **/
	private void getCommodityInfoDataHttp() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", commodityInfo.getId());// 产品id
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_DETAIL;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<CommodityInfo> info = JsonUtil.fromJson(arg0.result, CommodityInfo.class);

				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						commodityInfoNew = info.getBody();
					} catch (Exception e) {
					}

					if (commodityInfoNew != null) {
						if (commodityInfoNew.getProductIsDelete() == 1)
							ScreenUtil.showToast(ContextUtil.getString(R.string.custom_delete));
						else if (commodityInfoNew.getProductIsDelete() == 0)
							gotoPayConfirm();
					}
				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.i(arg1);
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}

	/** 检测员工是否购买过20周年产品 */
	private void CheckIsBought() {
		String URL = HttpURL.URL1 + HttpURL.CHECKISBOUGHT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfoNew.getId());
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo<CommodityInfo> info = JsonUtil.fromJson(arg0.result, CommodityInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					JumpActivityManager.getInstance().toActivity(mActivity, OrderConfirmActivity.class,
							Constant.Extra.COMMDITYINFO, commodityInfoNew);
				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}

	/** 分享给微信，QQ，QQ空间，朋友圈 **/
	private void shared(int type) {

		if (commodityInfo == null) {// 定制信息不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.commodity_isnull));
			return;
		}

		ShareInfo shareInfo = new ShareInfo();
		String url = HttpURL.URL1 + HttpURL.SHARE_CUSTOM + "?userid=" + UserInfoManager.getInstance().getUserId()
				+ "&productid=" + commodityInfo.getCustomId();
		shareInfo.setUrl(url);
		if (UserInfoManager.getInstance().getUserInfo() == null) {
			shareInfo.setTitle(ContextUtil.getString(R.string.shared_customization_to));
			shareInfo.setDescription(ContextUtil.getString(R.string.shared_customization_info_to));
		} else {
			shareInfo.setTitle(UserInfoManager.getInstance().getUserInfo().getNickName()
					+ ContextUtil.getString(R.string.shared_customization_to));
			shareInfo.setDescription(UserInfoManager.getInstance().getUserInfo().getNickName()
					+ ContextUtil.getString(R.string.shared_customization_info_to));
		}
		shareInfo.setImagUrl(commodityInfo.getImageUrl());
		// shareInfo.setImagUrl(HezhiConfig.HEZHI_LOGO_URL);

		if (type == Constant.SharedType.SHARE_WECHAT.ordinal()) {// 微信
			shareInfo.setType(SendMessageToWX.Req.WXSceneSession);
			WXShareManager.getInstance().share(shareInfo);
		} else if (type == Constant.SharedType.SHARE_WECHATCIRCLE.ordinal()) {// 朋友圈
			shareInfo.setType(SendMessageToWX.Req.WXSceneTimeline);
			WXShareManager.getInstance().share(shareInfo);
		} else if (type == Constant.SharedType.SHARE_QQ.ordinal()) {// QQ
			shareInfo.setType(QQActivity.QQ_FRIDEND);
			QQActivity.shareToQQ(shareInfo);
		} else if (type == Constant.SharedType.SHARE_QZONE.ordinal()) {// QQ空间
			shareInfo.setType(QQActivity.QQ_ZONE);
			QQActivity.shareToQQ(shareInfo);
		}
	}
}
