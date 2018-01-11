package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.BusinessAdapter;
import com.wukong.hezhi.bean.BusinessInfo;
import com.wukong.hezhi.bean.BusinessInfos;
import com.wukong.hezhi.bean.GuanZhuInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreScrollView;
import com.wukong.hezhi.ui.view.LoadMoreScrollView.OnZdyScrollViewListener;
import com.wukong.hezhi.ui.view.NoScrollGridview;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.StringUtil;

/***
 * 
 * @ClassName: BusinessAcitivty
 * @Description: TODO(商户)
 * @author HuZhiyin
 * @date 2017-6-15 上午8:05:27
 * 
 */
public class BusinessAcitivty extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.business_name);
	public String pageCode ="business_name";
	
	@ViewInject(R.id.scrlview)
	private LoadMoreScrollView scrlview;

	@ViewInject(R.id.card_iv)
	private ImageView card_iv;

	@ViewInject(R.id.seller_header_iv1)
	private ImageView seller_header_iv1;

	@ViewInject(R.id.seller_name)
	private TextView seller_name;

	@ViewInject(R.id.summary_tv)
	private TextView summary_tv;

	@ViewInject(R.id.sum_tv)
	private TextView sum_tv;

	@ViewInject(R.id.funs_tv)
	private TextView funs_tv;

	@ViewInject(R.id.guanzhu_cb)
	private CheckBox guanzhu_cb;

	@ViewInject(R.id.load_gdv)
	private NoScrollGridview load_gdv;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	// @ViewInject(R.id.empty)
	private ViewStub empty;
	private BusinessAdapter adapter;
	private int businessId;// 商户id
	private List<BusinessInfo> businessInfos = new ArrayList<BusinessInfo>();// 商品列表
	private BusinessInfos mInfo = new BusinessInfos();

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_business_name;
	}

	public void refreshListView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				// mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initview();
		setListener();
		scrlview.smoothScrollTo(0, 0);
		getBusinessData();
	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getGuanZhuData(CHECK_GUANZHU);
	}

	private void initview() {
		headView.setTitleStr(ContextUtil.getString(R.string.business_name));
		businessId = getIntent().getIntExtra(Constant.Extra.BUSINESS_ID, 0);
		refreshListView();
		adapter = new BusinessAdapter(businessInfos, mActivity);
		load_gdv.setAdapter(adapter);
		load_gdv.setEmptyView(empty);
	}

	boolean isClick = false;

	public void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
				LogUtil.d("OnRefreshListener" + page);

			}
		});

		scrlview.setOnZdyScrollViewListener(new OnZdyScrollViewListener() {

			@Override
			public void ZdyScrollViewListener() {
				// TODO Auto-generated method stub
				page++;
				LogUtil.d("setOnLoadListener" + page);
				if (page <= countPage) {// 如果加载的页数小于或等于总页数，则加载
					loadData();
				} else {
					scrlview.loadingComponent();
				}
			}
		});

		// load_gdv.setLoadListener(new ILoadListener() {
		// @Override
		// public void onLoad() {
		// // TODO Auto-generated method stub
		// page++;
		// LogUtil.d("setOnLoadListener" + page);
		// if (page <= countPage) {// 如果加载的页数小于或等于总页数，则加载
		// loadData();
		// }
		// }
		// });

		guanzhu_cb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (UserInfoManager.getInstance().isLogin()) {// 如果登录
					isClick = true;
				} else {
					JumpActivityManager.getInstance().toActivity(
							BusinessAcitivty.this, LoginActivity.class);
					isClick = false;
				}

			}
		});
//		guanzhu_cb.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//
//				LogUtil.i("ss");
//				if (UserInfoManager.getInstance().isLogin()) {// 如果登录
//					isClick = true;
//				} else {
//					JumpActivityManager.getInstance().toActivity(
//							BusinessAcitivty.this, LoginActivity.class);
//				}
//
//				return false;
//			}
//		});
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

	private int page = 0;// 当前页
	private int countPage;// 总页数

	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1 + HttpURL.BUSINESS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("page", page);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				scrlview.loadingComponent();
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {

				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				scrlview.loadingComponent();
				// TODO Auto-generated method stub
				ResponseJsonInfo<BusinessInfos> info = JsonUtil.fromJson(
						arg0.result, BusinessInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					countPage = info.getBody().getCountPage();// 获取总页数
					if(page == 1){
						businessInfos.clear();// 清空数据
					}
					businessInfos.addAll(info.getBody().getDataList());
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	/** 获取商户信息 */
	private void getBusinessData() {
		String url = HttpURL.URL1 + HttpURL.BUSINESS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("businessId", businessId);
		map.put("page", page);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<BusinessInfos> info = JsonUtil.fromJson(
						arg0.result, BusinessInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					mInfo = info.getBody();
					refresView();
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
		map.put("businessId", businessId);
		map.put("op", type);// 1 关注 2 取消关注 3 检查有没有关注过该商户
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
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


	/** 刷新商户信息 */
	private void refresView() {
		headView.setTitleStr(mInfo.getBusinessName());
		GlideImgUitl.glideLoader(mActivity, mInfo.getBusinessCover(), card_iv,
				2);// 封面
		GlideImgUitl.glideLoader(mActivity, mInfo.getBusinessLogo(),
				seller_header_iv1, 0);// 商户头像
		seller_name.setText(mInfo.getBusinessName());// 商户名称
		summary_tv.setText(mInfo.getIntroduction());// 商户简介
		sum_tv.setText(mInfo.getRecord() + "作品");
	}

	/** 刷新关注信息 */
	private void refreshView2() {
		String num = StringUtil.change2W(guanZhuInfo.getTotal());
		funs_tv.setText(num + "人关注");
		if (guanZhuInfo.isAttention()) {
			guanzhu_cb.setChecked(true);
			guanzhu_cb.setText("已关注");
		} else {
			guanzhu_cb.setChecked(false);
			guanzhu_cb.setText("+关注");
		}
	}
}
