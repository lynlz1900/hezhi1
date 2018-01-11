package com.wukong.hezhi.ui.segment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CustomCompanyInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.CommodityCustomUploadActivity;
import com.wukong.hezhi.ui.activity.CustomTemplateActivity;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.OrderConfirmActivity;
import com.wukong.hezhi.ui.activity.UploadActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.OrderWindows;
import com.wukong.hezhi.ui.view.OrderWindows.IOrderCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.HashMap;
import java.util.Map;

public class CommdityBottomFragment extends BaseFragment {

	@ViewInject(R.id.buy_tv)
	private TextView buy_tv;

	@ViewInject(R.id.custom_tv)
	private TextView custom_tv;

	@ViewInject(R.id.infoupload_tv)
	private TextView infoupload_tv;

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_bottom, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = (CommodityInfo) getArguments().getSerializable("commodityInfo");
		init();
		return rootView;
	}

	private void init() {
		if (commodityInfo.getIsAnniversary() == 1) {
			infoupload_tv.setVisibility(View.VISIBLE);
			buy_tv.setVisibility(View.GONE);
			custom_tv.setVisibility(View.GONE);
		} else {
			infoupload_tv.setVisibility(View.GONE);
			
			if(commodityInfo.getNowBuy() == 1)
				buy_tv.setVisibility(View.VISIBLE);
			else
				buy_tv.setVisibility(View.GONE);
			
			if(commodityInfo.getFaceCustom() == 1){
				custom_tv.setVisibility(View.VISIBLE);
			}else if(commodityInfo.getNfcCustom() == 1){
				custom_tv.setVisibility(View.VISIBLE);
				custom_tv.setText(ContextUtil.getString(R.string.go_custom));
			}else{
				custom_tv.setVisibility(View.GONE);
			}
		}
	}

	@OnClick(value = { R.id.buy_tv, R.id.custom_tv, R.id.infoupload_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buy_tv:// 直接购买
			handleLeftButton();
			break;
		case R.id.custom_tv:
			handleRightButton();
			break;
		case R.id.infoupload_tv:
			handleInfoupload();
			break;
		}
	}

	private void handleInfoupload() {
		if (!UserInfoManager.getInstance().isLogin()) {
			toActivity(LoginActivity.class);
		} else {
			if(commodityInfo.getInventory() <1){
				ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
				return;
			}
			CheckIdentity();
		}
	}

	private void handleLeftButton() {
		if (!UserInfoManager.getInstance().isLogin()) {
			toActivity(LoginActivity.class);
		} else {
			if(commodityInfo.getInventory() <1){
				ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
				return;
			}
			buy();// 直接购买
		}
	}

	private void handleRightButton() {
		if (!UserInfoManager.getInstance().isLogin()) {
			toActivity(LoginActivity.class);
		} else {
			
			if(commodityInfo.getInventory() <1){
				ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
				return;
			}
			
			if(commodityInfo.getFaceCustom() == 1)
//				toActivity(CustomComposeActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 去定制
				toActivity(CustomTemplateActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 去定制
			else if(commodityInfo.getNfcCustom() == 1)
				toActivity(UploadActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 去定制
		}
	}

	private void buy() {
		OrderWindows.getInstance().show(commodityInfo, new IOrderCallBack() {
			@Override
			public void response(Object object) {
				// TODO Auto-generated method stub
				CommodityInfo commodityInfo = (CommodityInfo) object;
				commodityInfo.setCustomizationType(Constant.Commodity.BUY);// 标记为直接购买
				toActivity(OrderConfirmActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);
			}
		});
	}

	/** 检测会员身份 */
	private void CheckIdentity() {
		String URL = HttpURL.URL1 + HttpURL.CHECKIDENTITY;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo<CustomCompanyInfo> info = JsonUtil.fromJson(arg0.result, CustomCompanyInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					try {
						CustomCompanyInfo customCompanyInfo = info.getBody();
						if (UserInfoManager.getInstance().userLoginStatus(getActivity(),
								customCompanyInfo.getOnLineType())) {
							commodityInfo.setCompanyName(customCompanyInfo.getCompanyName());
							commodityInfo.setUsername(customCompanyInfo.getName());
							toActivity(CommodityCustomUploadActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 上传信息
						}
					} catch (Exception e) {
					}
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

	/** 检测员工是否购买过20周年产品 */
	@SuppressWarnings("unused")
	private void CheckIsBought() {
		String URL = HttpURL.URL1 + HttpURL.CHECKISBOUGHT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
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
					CheckIdentity();
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

}
