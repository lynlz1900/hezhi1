package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.bean.AdContentInfos;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.RedBagGanzManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StatusBarUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: RedBagActivity
 * @Description: TODO(红包页面)
 * @author HuZhiyin
 * @date 2017-3-28 下午6:36:19
 * 
 */
public class RedBagActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.redbag);
	public String pageCode ="redbag";
	@ViewInject(R.id.header_left)
	private TextView header_left;

	@ViewInject(R.id.company_logo_iv)
	private ImageView company_logo_iv;// 公司logo

	@ViewInject(R.id.company_tv)
	private TextView company_tv;// 公司名称

	@ViewInject(R.id.redbug_tips_tv)
	private TextView redbug_tips_tv;// 祝福语

	@ViewInject(R.id.money_tv)
	private TextView money_tv;// 红包大小

	@ViewInject(R.id.store_tv)
	private TextView store_tv;// 已存入

	@ViewInject(R.id.ad_fr)
	private FrameLayout ad_fr;// 广告位置
	/** 红包 */
	private RedBagInfo redBagInfo = new RedBagInfo();

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_redbag;
	}

	@Override
	protected void init() {
		StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.bg_redbag));// 设置状态栏颜色
		redBagInfo = (RedBagInfo) getIntent().getSerializableExtra(
				Constant.Extra.REDBAG_INFO);
		if (redBagInfo != null) {
			GlideImgUitl.glideLoader(this, redBagInfo.getLogoUrl(),
				company_logo_iv, 0);
			company_tv.setText(redBagInfo.getSendName());
			redbug_tips_tv.setText(redBagInfo.getWishing());
			money_tv.setText(redBagInfo.getMoney() + "");
		}
		disPlayAD();
	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@OnClick(value = {  R.id.store_tv ,R.id.header_left})
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.store_tv:
			toActivity(MyWalletActivity.class);
			break;
		case R.id.header_left:
			finish();
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		boolean isFromUnity = (redBagInfo != null
				&& redBagInfo.getRegbagType() == RedBagGanzManager.GANZ_REDBAG);
		if (isFromUnity) {
			Intent intent = new Intent(this, UnityPlayerActivity.class);
			startActivity(intent);
		}
		super.onDestroy();
	}
	

	/** 红包广告 */
	private void disPlayAD() {
		RelativeLayout layout = (RelativeLayout) ScreenUtil
				.inflate(R.layout.layout_ad_redbag);
		final ImageView ad_iv = (ImageView) layout.findViewById(R.id.ad_iv);
		ImageView close_iv = (ImageView) layout.findViewById(R.id.close_iv);
		final TextView tip_tv = (TextView) layout.findViewById(R.id.tip_tv);
		ad_fr.addView(layout);
		close_iv.setOnClickListener(new OnClickListener() {
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
								ad_iv.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (!TextUtils.isEmpty(jumurl)) {// 如果链接地址不为空，则跳转
											toActivity(WebViewActivity.class,
													Constant.Extra.WEB_URL,
													jumurl);
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
