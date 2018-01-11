package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.segment.ProductCommentFragment;
import com.wukong.hezhi.ui.segment.ProductDetailFragment;
import com.wukong.hezhi.ui.segment.ProductEditCommentFragment;
import com.wukong.hezhi.ui.segment.ProductVideoFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: ProductMainActivity
 * @Description: TODO(商品页面)
 * @author HuZhiyin
 * @date 2017-7-28 上午9:47:00
 *
 */
public class ProductMainActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.shangpin_info);
	public String pageCode = "shangpin_info";
	private VuforiaInfo vuforiaInfo;
	public static ProductVideoFragment productVideoFragment;
	public static ProductDetailFragment productDetailFragment;
	public static ProductCommentFragment productCommentFragment;
	public static ProductEditCommentFragment productEditCommentFragment;
	public static String jsonStr;

	@ViewInject(R.id.header)
	private RelativeLayout header;

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;

	@ViewInject(R.id.video_fr)
	private FrameLayout video_fr;

	@ViewInject(R.id.detail_fr)
	private FrameLayout detail_fr;

	@ViewInject(R.id.comment_fr)
	private FrameLayout comment_fr;

	@ViewInject(R.id.edit_comment_fr)
	private FrameLayout edit_comment_fr;

	@ViewInject(R.id.header_title)
	private TextView header_title;

	@ViewInject(R.id.imageview_cust)
	private ImageView imageview_cust;

	@ViewInject(R.id.imageview_buyto)
	private ImageView imageview_buyto;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_product_main;
	}

	@Override
	protected void init() {
		setSwipeBackEnable(false);// 禁止侧滑消除
		vuforiaInfo = (VuforiaInfo) getIntent().getSerializableExtra(Constant.Extra.VUFORIA);
		header_title.setText(vuforiaInfo.getTitle());
		empty_ll.setVisibility(View.GONE);
		setImageView();
		getBusinessData();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		setFullScreen(newConfig);
		super.onConfigurationChanged(newConfig);

	}

	private UnityInfo unityInfo;

	/** 获取商户信息 */
	private void getBusinessData() {
		showProgressDialog();
		String URL = HttpURL.URL1 + HttpURL.GANZ_RESOURCE2;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("targetId", vuforiaInfo.getTargetId());
		map.put("version", HezhiConfig.SERVER_VERSION);
		map.put("ppid", vuforiaInfo.getPpid());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				String json = arg0.result;
				jsonStr = json;
				ResponseJsonInfo info = JsonUtil.fromJson(json, UnityInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					unityInfo = (UnityInfo) info.getBody();
					//
					// ObserveManager.getInstance().notifyState(
					// LiveFragment.class,ProductMainActivity.class,
					// unityInfo.getPpId());//
					// 通知品牌直播浏览次数变化了.getInstance().notifyState(LiveFragment.class,getClass(),currentFragmentState);//通知品牌直播浏览次数变化了
					if (unityInfo != null) {
						addFragment(unityInfo);
						setImageView();
					}
				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					empty_ll.setVisibility(View.VISIBLE);
				}
				dismissProgressDialog();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 如果是横屏，则切换成竖屏
				productVideoFragment.change();
			} else {
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void setFullScreen(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
			detail_fr.setVisibility(View.GONE);
			comment_fr.setVisibility(View.GONE);
			edit_comment_fr.setVisibility(View.GONE);
			header.setVisibility(View.GONE);

			imageview_cust.setVisibility(View.GONE);
			imageview_buyto.setVisibility(View.GONE);// 横屏隐藏定制，直购按钮
		} else {// 竖屏
			detail_fr.setVisibility(View.VISIBLE);
			comment_fr.setVisibility(View.VISIBLE);
			edit_comment_fr.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去除全屏

			setImageView();// 竖屏判断是否显示定制，直购按钮
		}
	}

	@OnClick(value = { R.id.header_left, R.id.imageview_buyto, R.id.imageview_cust })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			finish();
			break;
		case R.id.imageview_buyto:
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(Constant.Extra.WEB_URL, unityInfo.getShopURL());
			startActivity(intent);
			break;
		case R.id.imageview_cust:
			gotoCust();
			break;
		}
	}


	private void addFragment(UnityInfo unityInfo) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("unityInfo", unityInfo);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		productVideoFragment = new ProductVideoFragment();
		productVideoFragment.setArguments(bundle);
		transaction.replace(R.id.video_fr, productVideoFragment);
		productDetailFragment = new ProductDetailFragment();
		productDetailFragment.setArguments(bundle);
		transaction.replace(R.id.detail_fr, productDetailFragment);
		productCommentFragment = new ProductCommentFragment();
		productCommentFragment.setArguments(bundle);
		transaction.replace(R.id.comment_fr, productCommentFragment);
		productEditCommentFragment = new ProductEditCommentFragment();
		productEditCommentFragment.setArguments(bundle);
		transaction.replace(R.id.edit_comment_fr, productEditCommentFragment);
		transaction.commitAllowingStateLoss();
	}

	/** 视频是否被移除 */
	private boolean isRemove = false;
	/** 移除视频，防止从此页面进入商品页面，编辑定制页面导致闪退的bug。 */
	private void removeVideoFragment() {
		if (!isRemove) {
			this.getSupportFragmentManager().beginTransaction().remove(productVideoFragment).commitAllowingStateLoss();
			isRemove = true;
		}
	}

	/** 回显视频 ，返回此页面，将移除的视频，添加进来 */
	private void addVideoFragment() {
		if (isRemove) {
			this.getSupportFragmentManager().beginTransaction().replace(R.id.video_fr, productVideoFragment)
					.commitAllowingStateLoss();
			isRemove = false;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		addVideoFragment();
		super.onResume();
	}

	/** 设置直购按钮 定制按钮 **/
	private void setImageView() {
		if (unityInfo == null) {
			imageview_buyto.setVisibility(View.GONE);
			imageview_cust.setVisibility(View.GONE);
		} else {
			if (TextUtils.isEmpty(unityInfo.getShopURL())) {
				imageview_buyto.setVisibility(View.GONE);
			} else {
				imageview_buyto.setVisibility(View.VISIBLE);
			}

			if (TextUtils.isEmpty(unityInfo.getHasBookProduct())) {
				imageview_cust.setVisibility(View.GONE);
			} else {
				if (unityInfo.getHasBookProduct().equals("YES")) {
					imageview_cust.setVisibility(View.VISIBLE);
				} else {
					imageview_cust.setVisibility(View.GONE);
				}
			}
		}
	}

	/** 跳转到定制详情界面 **/
	private void gotoCust() {
		toActivity(CommodityInfoActivity.class, Constant.Extra.PRODUCTID, unityInfo.getProductId());
		ThreadUtil.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				removeVideoFragment();
			}
		}, 500);// 0.5秒后销毁
	}


}
