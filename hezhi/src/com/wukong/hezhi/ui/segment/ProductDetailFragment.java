package com.wukong.hezhi.ui.segment;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.GuanZhuInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.BusinessAcitivty;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.NetWorkUitl;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.StringUtil;

/**
 * 
 * @ClassName: ProductDetailFragment
 * @Description: TODO(品牌直播显示商家信息部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ProductDetailFragment extends BaseFragment {
	@ViewInject(R.id.seller_header_iv)
	private ImageView seller_header_iv;

	@ViewInject(R.id.seller_name)
	private TextView seller_name;

	@ViewInject(R.id.summary_tv)
	private TextView summary_tv;

	@ViewInject(R.id.funs_tv)
	private TextView funs_tv;

	@ViewInject(R.id.guanzhu_cb)
	private CheckBox guanzhu_cb;

	@ViewInject(R.id.buy_bt)
	private Button buy_bt;

	@OnClick(value = { R.id.detail_rl, R.id.buy_bt })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.detail_rl:// 查看全部
			toActivity(BusinessAcitivty.class, Constant.Extra.BUSINESS_ID,
					unityInfo.getBusinessId());
			break;
		case R.id.buy_bt:// 直购
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra(Constant.Extra.WEB_URL, unityInfo.getShopURL());
			getActivity().startActivity(intent);
			break;
		}
	}


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_product_detail,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		unityInfo = (UnityInfo) getArguments().getSerializable("unityInfo");
		init();
		return rootView;
	}

	private void init() {
		setListener();
		refreshView();
		postData();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getGuanZhuData(CHECK_GUANZHU);
	}

	boolean isClick = false;

	public void setListener() {
//		guanzhu_cb.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//
//				if (UserInfoManager.getInstance().isLogin()) {// 如果登录
//					isClick = true;
//				} else {
//					JumpActivityManager.getInstance().toActivity(getActivity(),
//							LoginActivity.class);
//				}
//
//				return false;
//			}
//		});
		guanzhu_cb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (UserInfoManager.getInstance().isLogin()) {// 如果登录
					isClick = true;
				} else {
					JumpActivityManager.getInstance().toActivity(getActivity(),
							LoginActivity.class);
				}				
			}
		});
		guanzhu_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(UserInfoManager.getInstance().isLogin()){
					if (isChecked) {// 点赞
						getGuanZhuData(GUANZHU);
					} else {// 取消点赞
						getGuanZhuData(CANCEL_GUANZHU);
					}
				}
			}
		});
	}

	private static int GUANZHU = 1;// 关注
	private static int CANCEL_GUANZHU = 2;// 取消关注
	private static int CHECK_GUANZHU = 3;// 检查有没有关注过该商户
	private GuanZhuInfo guanZhuInfo;

	private void getGuanZhuData(int type) {
		String URL = HttpURL.URL1 + HttpURL.GUANZHU;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserIdOrDeviceId());
		map.put("businessId", unityInfo.getBusinessId());
		map.put("op", type);// 1 关注 2 取消关注 3 检查有没有关注过该商户
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String json = arg0.result;
				ResponseJsonInfo info = JsonUtil.fromJson(json,
						GuanZhuInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					guanZhuInfo = (GuanZhuInfo) info.getBody();
					if (guanZhuInfo != null) {
						refreshView2();
					}
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private UnityInfo unityInfo = new UnityInfo();

	/** 上传大数据信息 */
	private void postData() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("memberId", UserInfoManager
				.getInstance().getUserId() + "");
		params.addQueryStringParameter("address", UnityManger.getInstance().getDeatailAddr());
		params.addQueryStringParameter("GPS", UnityManger.getInstance()
				.getGPS());
		params.addQueryStringParameter("hardwareAddress",
				PackageUtil.getPhoneId());
		params.addQueryStringParameter("IP",
				NetWorkUitl.getIPAddress(getActivity()));
		params.addQueryStringParameter("perceptionPictureName",
				unityInfo.getName());
		params.addQueryStringParameter("perceptionPictureId",
				unityInfo.getPpId());
		String URL = HttpURL.URL1 + HttpURL.RECORDAPPLOG;
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});

	}

	public void refreshView() {
		GlideImgUitl.glideLoader(getActivity(), unityInfo.getBusinessLogo(),
				seller_header_iv, 0);

		seller_name.setText(unityInfo.getBusinessName());// 商户名称
		summary_tv.setText(unityInfo.getIntroduction());// 商户简介
		String shopUrl = unityInfo.getShopURL();
		if (TextUtils.isEmpty(shopUrl)) {
			buy_bt.setVisibility(View.GONE);
		} else {
			buy_bt.setVisibility(View.GONE);
		}

	}

	public void refreshView2() {
		funs_tv.setText(StringUtil.change2W(guanZhuInfo.getTotal()) + "人关注");
		if (guanZhuInfo.isAttention()) {
			guanzhu_cb.setChecked(true);
			guanzhu_cb.setText("已关注");
		} else {
			guanzhu_cb.setChecked(false);
			guanzhu_cb.setText("+关注");
		}
	}

}
