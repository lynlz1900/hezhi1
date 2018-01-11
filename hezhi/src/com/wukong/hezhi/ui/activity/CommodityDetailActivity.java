package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.segment.CommDityBannerFragment;
import com.wukong.hezhi.ui.segment.CommdityBottomFragment;
import com.wukong.hezhi.ui.segment.CommdityDetailFragment;
import com.wukong.hezhi.ui.segment.CommdityWebFragment;
import com.wukong.hezhi.ui.view.ObservableScrollView;
import com.wukong.hezhi.ui.view.ObservableScrollView.OnScollChangedListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName: CommodityDetail
 * @Description: TODO(商品详情)
 * @author HuZhiyin
 * @date 2017-8-10 上午8:38:43
 * 
 */
public class CommodityDetailActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.commodity_detail);
	public String pageCode = "commodity_detail";

	@ViewInject(R.id.sclv)
	private ObservableScrollView sclv;

	@ViewInject(R.id.content_rl)
	private RelativeLayout content_rl;

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;

	@ViewInject(R.id.web_base_rl)
	private LinearLayout web_base_rl;
	
	@ViewInject(R.id.top_iv)
	private ImageView top_iv;

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 产品id */
	private int productId;


	@Override
	protected boolean isNotAddTitle() {
		return false;
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_commodity_detail;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initView();
		initData();
	}

	private void initView() {
		headView.setTitleStr(ContextUtil.getString(R.string.commodity_detail));
		sclv.setOnScollChangedListener(new MyScrollListener());
	}

	/** 滑动的监听 */
	private class MyScrollListener implements OnScollChangedListener {

		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
			// TODO Auto-generated method stub
			LogUtil.d("x=" + x + "y=" + y + "oldx" + oldx + "oldy" + oldy);
			if (y >= ScreenUtil.getScreenHeight()) {// 当移动的距离大于屏幕的高度
				top_iv.setVisibility(View.VISIBLE);
			} else {
				top_iv.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initData() {
		productId = (int) getIntent().getSerializableExtra(Constant.Extra.PRODUCTID);
		getData();
	}

	private void getData() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", productId);// 产品id
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_DETAIL;
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
						commodityInfo = info.getBody();
					} catch (Exception e) {
					}

					if (commodityInfo != null) {
						if (commodityInfo.getProductIsDelete() == 1) {// 商品下架
							empty_ll.setVisibility(View.VISIBLE);
							content_rl.setVisibility(View.INVISIBLE);
						} else if (commodityInfo.getProductIsDelete() == 0) {
							empty_ll.setVisibility(View.INVISIBLE);
							content_rl.setVisibility(View.VISIBLE);
							addFragment();
						}
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
			}
		});
	}

	private void addFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("commodityInfo", commodityInfo);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		CommDityBannerFragment commDityBannerFragment = new CommDityBannerFragment();// banner
		commDityBannerFragment.setArguments(bundle);
		transaction.replace(R.id.banner_rl, commDityBannerFragment);

		CommdityDetailFragment commdityDetailFragment = new CommdityDetailFragment();// 产品详细信息
		commdityDetailFragment.setArguments(bundle);
		transaction.replace(R.id.detail_rl, commdityDetailFragment);

		if(!TextUtils.isEmpty(commodityInfo.getUrl())){
			CommdityWebFragment commdityWebFragment = new CommdityWebFragment();// 产品详细信息网页展示
			commdityWebFragment.setArguments(bundle);
			transaction.replace(R.id.web_rl, commdityWebFragment);
		}else{
			web_base_rl.setVisibility(View.GONE);
		}

		if(commodityInfo.getFaceCustom() == 1 || commodityInfo.getNfcCustom() == 1
				|| commodityInfo.getNowBuy() == 1){
			findViewById(R.id.bottom_rl).setVisibility(View.VISIBLE);
			CommdityBottomFragment commdityBottomFragment = new CommdityBottomFragment();// 底部
			commdityBottomFragment.setArguments(bundle);
			transaction.replace(R.id.bottom_rl, commdityBottomFragment);
		}else{
			findViewById(R.id.bottom_rl).setVisibility(View.GONE);
		}

		transaction.commitAllowingStateLoss();
	}

	@OnClick(value = { R.id.top_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_iv:// 回到最顶端
			sclv.smoothScrollTo(0, 0);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.isFromPage.isOrderDetailToCommDetail = false;
		// if (!isCommodityActivity) {
		// ActivitiesManager.getInstance().finishActivity(CommodityActivity.class);
		// }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MyCustomizationTobuyActivity.class)) {
			}
		}
	}

}
