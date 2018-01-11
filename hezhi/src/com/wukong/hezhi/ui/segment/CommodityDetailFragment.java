package com.wukong.hezhi.ui.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.bean.CommodityAppraiseInfos;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.segment.CommDityBannerFragment;
import com.wukong.hezhi.ui.segment.CommdityDetailFragment;
import com.wukong.hezhi.ui.segment.CommdityWebFragment;
import com.wukong.hezhi.ui.view.ObservableScrollView;
import com.wukong.hezhi.ui.view.ObservableScrollView.OnScollChangedListener;
import com.wukong.hezhi.ui.view.RatingBar;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: CommodityDetailFragment
 * @Description: TODO(商品tab商品页)
 * @author HuangFeiFei
 * @date 2017-12-13
 * 
 */
public class CommodityDetailFragment extends BaseFragment {

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

	@ViewInject(R.id.appraise_rl)
	private LinearLayout appraise_rl;
	
	@ViewInject(R.id.appraise_rl_display)
	private LinearLayout appraise_rl_display;
	
	@ViewInject(R.id.appraise_rl_empty)
	private LinearLayout appraise_rl_empty;
	
	@ViewInject(R.id.btn_appraise_all)
	private TextView btn_appraise_all;
	
	@ViewInject(R.id.text_appraise_num)
	private TextView text_appraise_num;
	
	@ViewInject(R.id.text_appraise_favourable)
	private TextView text_appraise_favourable;
	
	@ViewInject(R.id.image_appraise_user)
	private ImageView image_appraise_user;
	
	@ViewInject(R.id.text_appraise_user)
	private TextView text_appraise_user;
	
	@ViewInject(R.id.text_appraise_info)
	private TextView text_appraise_info;
	
	@ViewInject(R.id.text_time)
	private TextView text_time;
	
	@ViewInject(R.id.ratingBar)
	private RatingBar ratingBar;
	
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 评价信息 */
	private CommodityAppraiseInfos infos;
	
	/** 评价列表信息 */
	private List<CommodityAppraiseInfo> commodityAppraiseInfos;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.activity_commodity_detail, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getAppraiseData();
	}
	
	private void initView() {
		commodityInfo = ((CommodityInfoFragment)getParentFragment()).getCommodityInfo();
		if(commodityInfo !=null){
			addFragment();
			appraise_rl.setVisibility(View.VISIBLE);
			appraise_rl_display.setVisibility(View.GONE);
			appraise_rl_empty.setVisibility(View.VISIBLE);
		}
		commodityAppraiseInfos = new ArrayList<>();
		sclv.setOnScollChangedListener(new MyScrollListener());
	}

	/** 设置评价信息 */
	private void setAppraiseData() {
		if(commodityAppraiseInfos != null && commodityAppraiseInfos.size() >0){
			appraise_rl_display.setVisibility(View.VISIBLE);
			appraise_rl_empty.setVisibility(View.GONE);
			
			CommodityAppraiseInfo appraiseInfo = commodityAppraiseInfos.get(0);
			
			GlideImgUitl.glideLoaderNoAnimation(getActivity(), appraiseInfo.getUserImage(), image_appraise_user, 0);
			text_appraise_num.setText(getString(R.string.appraise_num)+"("+infos.getCountRecord()+")");
			text_appraise_favourable.setText(getString(R.string.appraise_favourable) + Math.round(infos.getGoodCommentPercent())+"%");
			text_appraise_user.setText(appraiseInfo.getUserName());
			text_appraise_info.setText(appraiseInfo.getAppraiseMessage());
			text_time.setText(DateUtil.getYMD_(appraiseInfo.getTime()));
			ratingBar.setStarMark(Float.valueOf(appraiseInfo.getAppraiseStatus()+""));
		}
	}
	
	/** 获取评价信息 */
	private void getAppraiseData(){
		String URL = HttpURL.URL1 + HttpURL.APPRAISE_NEW_INFO;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo<CommodityAppraiseInfos> info = JsonUtil.fromJson(arg0.result, CommodityAppraiseInfos.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					try {
						infos = info.getBody();
						commodityAppraiseInfos = infos.getDataList();
						setAppraiseData();
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

	private void addFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("commodityInfo", commodityInfo);
		FragmentManager fm = getChildFragmentManager();
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

		transaction.commitAllowingStateLoss();
	}

	@OnClick(value = { R.id.top_iv ,R.id.btn_appraise_all})
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_iv:// 回到最顶端
			sclv.smoothScrollTo(0, 0);
			break;
		case R.id.btn_appraise_all:
			(((CommodityInfoFragment)getParentFragment()).getRadioGroup()).check(
					((CommodityInfoFragment)getParentFragment()).getViewId());
			break;
		}
	}
}
