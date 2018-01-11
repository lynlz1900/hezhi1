package com.wukong.hezhi.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.bean.AdContentInfos;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: EmptyRedBagDialog
 * @Description: TODO(空红包)
 * @author HuZhiyin
 * @date 2017-5-15 下午3:32:47
 * 
 */
public class EmptyRedBagDialog extends Dialog {
	private Context context;
	private View layout;
	private ImageView close_iv;// 关闭
	private ImageView company_logo_iv;// 公司logo
	private TextView company_tv;// 公司名称
	private TextView redbug_tips_tv;// 祝福语
	private FrameLayout ad_fr;// 广告位
	private RedBagInfo redBagInfo;

	public EmptyRedBagDialog(Context context) {
		super(context, R.style.AdDialogStyle);
		this.context = context;
		this.setCanceledOnTouchOutside(false);// 点击空白不消失
		initView();
	}

	private void initView() {
		layout = View.inflate(context, R.layout.layout_emptyredbug, null);
		close_iv = (ImageView) layout.findViewById(R.id.close_iv);
		company_logo_iv = (ImageView) layout.findViewById(R.id.company_logo_iv);
		company_tv = (TextView) layout.findViewById(R.id.company_tv);
		redbug_tips_tv = (TextView) layout.findViewById(R.id.redbug_tips_tv);
		ad_fr = (FrameLayout) layout.findViewById(R.id.ad_fr);
	}

	/**
	 * 
	 * @Title: show
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param url 公司Logo链接
	 * @param @param comanpanyName 公司名称
	 * @param @param blessing 祝福语言
	 * @param @param openListener 打开红包监听
	 * @return void 返回类型
	 * @throws
	 */
	public void show(RedBagInfo redBagInfo,
			android.view.View.OnClickListener openListener) {
		close_iv.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		this.redBagInfo = redBagInfo;
		GlideImgUitl.glideLoader(context, redBagInfo.getLogoUrl(),
				company_logo_iv, 0);

		company_tv.setText(redBagInfo.getSendName());
		if (redBagInfo.isOver()) {// 红包结束
			redbug_tips_tv.setText("手慢了，红包已领完");
		} else {// 空红包
			redbug_tips_tv.setText(redBagInfo.getTips());
		}

		disPlayAD();
		this.setContentView(layout);
		this.show();
	}

	/** 红包广告 */
	private void disPlayAD() {
		RelativeLayout layout = (RelativeLayout) ScreenUtil
				.inflate(R.layout.layout_ad_redbag);
		final ImageView ad_iv = (ImageView) layout.findViewById(R.id.ad_iv);
		ImageView close_iv = (ImageView) layout.findViewById(R.id.close_iv);
		final TextView tip_tv = (TextView) layout.findViewById(R.id.tip_tv);
		ad_fr.addView(layout);
		close_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ad_fr.setVisibility(View.GONE);
			}
		});
		Map<String, Object> map = new HashMap<String, Object>();

		if (!TextUtils.isEmpty(redBagInfo.getPpid())) {// 如果是感知红包
			map.put("ppid", redBagInfo.getPpid());
			map.put("placement",
					Constant.AdLocation.REDPACK_PERCEPTION.toString());
		}
		if (!TextUtils.isEmpty(redBagInfo.getAuthId())
				&& !TextUtils.isEmpty(redBagInfo.getProductId())) {// 如果是nfc红包
			map.put("placement", Constant.AdLocation.REDPACK_NFC.toString());
			map.put("productId", redBagInfo.getProductId());
			map.put("authId", redBagInfo.getAuthId());
		}
		if (!TextUtils.isEmpty(redBagInfo.getCustomizeProductId())) {// 如果是定制红包
			map.put("placement", Constant.AdLocation.REDPACK_CUSTOMIZE.toString());
			map.put("customizeProductId", redBagInfo.getCustomizeProductId());
		}
		String URL = HttpURL.URL1 + HttpURL.AD;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				LogUtil.i(responseInfo.result);
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						AdContentInfos.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						AdContentInfos adContentInfos = (AdContentInfos) info
								.getBody();
						List<AdContentInfo> list = adContentInfos.getData();
						if (list == null) {
							return;
						}
						for (AdContentInfo adContentInfo : list) {
							if (adContentInfo.getType().equals(
									Constant.AdContentType.PICTURE.toString())) {
								String url = adContentInfo.getHttpUrl();
								final String jumurl = adContentInfo
										.getJumpUrl();
								BitmapUtils bitmapUtils = new BitmapUtils(
										ContextUtil.getContext());
								tip_tv.setText(adContentInfo.getText());
								bitmapUtils.display(ad_iv, url);
								ad_fr.setVisibility(View.VISIBLE);
								ad_iv.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										// JumpActivityManager
										// .getInstance()
										// .toActivity(
										// (Activity) context,
										// WebViewActivity.class,
										// Constant.Extra.WEB_URL,
										// jumurl);
										if (!TextUtils.isEmpty(jumurl)) {// 如果链接地址不为空，则跳转
											// boolean isFromUnity =
											// !TextUtils.isEmpty(redBagInfo.getPpid());//如果ppid不为空，则是来自Unity
											boolean isFromUnity = redBagInfo
													.isAr();// 是否来自Unity
											Intent intent = new Intent(context,
													WebViewActivity.class);
											intent.putExtra(
													Constant.Extra.WEB_URL,
													jumurl);
											context.startActivity(intent);
										}
									}
								});
							}
						}
					}
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}

}
