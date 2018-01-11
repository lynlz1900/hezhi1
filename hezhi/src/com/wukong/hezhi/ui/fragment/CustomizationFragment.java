package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wukong.hezhi.adapter.CommodityActivityAdapter;
import com.wukong.hezhi.adapter.CommoditySceneAdapter;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.bean.AdContentInfos;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CommodityInfos;
import com.wukong.hezhi.bean.CommoditySceneInfo;
import com.wukong.hezhi.bean.CommoditySceneInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.receiver.JgPushReceiver;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.ui.activity.MsgCenterActivity;
import com.wukong.hezhi.ui.activity.SearchCommodityActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.view.BannerView;
import com.wukong.hezhi.ui.view.BannerView.ImageCycleViewListener;
import com.wukong.hezhi.ui.view.GuideView;
import com.wukong.hezhi.ui.view.LoadMoreGridView;
import com.wukong.hezhi.ui.view.GuideView.OnClickCallback;
import com.wukong.hezhi.ui.view.LoadMoreScrollView;
import com.wukong.hezhi.ui.view.LoadMoreScrollView.OnScrollToBottomListener;
import com.wukong.hezhi.ui.view.SwipeRefreshLayoutVertical;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: CustomizationFragment
 * @Description: TODO(定制)
 * @author HuangFeiFei
 * @date 2016-10-23 9:15
 * 
 */
public class CustomizationFragment extends BaseFragment{
	public String pageName = ContextUtil.getString(R.string.customiation);
	public String pageCode = "customiation";

	@ViewInject(R.id.header_left)
	private RelativeLayout header_left;

	@ViewInject(R.id.header_right)
	private RelativeLayout header_right;

	@ViewInject(R.id.red_point)
	private ImageView red_point;

	@ViewInject(R.id.lv_scroll)
	private LoadMoreGridView lv_scroll;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayoutVertical mSwipeLayout;

	@ViewInject(R.id.scrlview)
	private LoadMoreScrollView scrlview;
	
	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.banner)
	private BannerView banner;
	
	@ViewInject(R.id.lv_scroll_scene)
	private LoadMoreGridView lv_scroll_scene;
	
	@ViewInject(R.id.layout_scene)
	private LinearLayout layout_scene;
	
	private List<AdContentInfo> adContentInfos = new ArrayList<AdContentInfo>();
	private List<CommoditySceneInfo> commoditySceneInfos = new ArrayList<CommoditySceneInfo>();
	private CommoditySceneAdapter commoditySceneAdapter;
	
	private CommodityActivityAdapter adapter;
	private List<CommodityInfo> infos = new ArrayList<CommodityInfo>();

	private int page = 0;// 当前页
	private boolean isOver = false;// 是否加载介绍
	private boolean isShowRedPoint = false;// 小红点是否显示

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_customization, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		setListener();
		getBanner();
		getScene();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setPoint();
		if(adContentInfos.size() >1)
			banner.startImageCycle();//开始轮播
	}

	@Override
	public void onPause() {
		super.onPause();
		banner.pushImageCycle();//暂停轮播
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		banner.pushImageCycle();//暂停轮播
	}
	
	private void initView() {
		((BaseActivity) getActivity()).showProgressDialog();
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		
		adapter = new CommodityActivityAdapter(infos, getActivity());
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);//定制商品列表
		commoditySceneAdapter = new CommoditySceneAdapter(commoditySceneInfos, (BaseActivity)getActivity());
		lv_scroll_scene.setAdapter(commoditySceneAdapter);//场景列表
	}

	/** 设置监听 */
	private void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
				getBanner();
				getScene();
			}
		});

		scrlview.setOnScrollToBottomLintener(new OnScrollToBottomListener() {
			@Override
			public void onScrollBottomListener() {
				page++;
				if (!isOver) {// 如果加载结束
					loadData();
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
					scrlview.setComplete();
				}
			}
		});
	}

	/** 加载数据 */
	private void loadData() {
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				((BaseActivity) getActivity()).dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				scrlview.setComplete();// 上拉加载隐藏
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				((BaseActivity) getActivity()).dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				scrlview.setComplete();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CommodityInfos> info = JsonUtil.fromJson(arg0.result, CommodityInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CommodityInfos cInfos = info.getBody();
					ArrayList<CommodityInfo> list = (ArrayList<CommodityInfo>) cInfos.getDataList();
					if (page == 1) {
						infos.clear();
					}
					if (list.size() == 0) {
						isOver = true;
					} else {
						isOver = false;
						infos.addAll(list);
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void setPoint() {
		isShowRedPoint = SPUtil.getShareBoolean(ContextUtil.getContext(), HezhiConfig.SP_COMMON_CONFIG,
				Constant.Sp.REP_POINT);
		if (isShowRedPoint) {
			red_point.setVisibility(View.VISIBLE);
		} else {
			red_point.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 *获取广告图 ***/
	private void getBanner(){
		String URL = HttpURL.URL1 + HttpURL.AD;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("placement",
				Constant.AdLocation.CUSTOMIZATION.toString());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.i(responseInfo.result);
				adContentInfos.clear();
				List<AdContentInfo> list = new ArrayList<AdContentInfo>();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						AdContentInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						AdContentInfos contentInfos = (AdContentInfos) info
								.getBody();
						list = contentInfos.getData();
					} catch (Exception e) {
					}
					
					if (list == null) {
						setBanner();
					}else{
						adContentInfos.addAll(list);
						setBanner();
					}
				}else{
					setBanner();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				setBanner();
			}
		});
	}
	
	/**
	 * 设置广告图 ***/
	private void setBanner(){
		banner.setImageResources(adContentInfos, new ImageCycleViewListener() {
			@Override
			public void onImageClick(AdContentInfo info, int postion, View imageView) {
				LogUtil.i(info.getHttpUrl());
				if(info != null && !TextUtils.isEmpty(info.getJumpUrl()))
					JumpActivityManager.getInstance().toActivity(getActivity(), WebViewActivity.class,
							Constant.Extra.WEB_URL,info.getJumpUrl());
			}
			
			@Override
			public void displayImage(String imageURL, ImageView imageView) {
				GlideImgUitl.glideLoaderNoAnimation(getActivity(), imageURL, imageView, 2);
			}
		});
	}
	
	/**
	 *获取场景信息***/
	private void getScene(){
		String URL = HttpURL.URL1 + HttpURL.CUSTOM_SCENE;
		Map<String, Object> map = new HashMap<String, Object>();
		LogUtil.i(URL);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.i(responseInfo.result);
				commoditySceneInfos.clear();
				List<CommoditySceneInfo> list = new ArrayList<CommoditySceneInfo>();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						CommoditySceneInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						CommoditySceneInfos commoditySceneInfos = (CommoditySceneInfos) info
								.getBody();
						list = commoditySceneInfos.getDataList();
					} catch (Exception e) {
					}
					
					if (list == null) {
						setScene();
					}else{
						commoditySceneInfos.addAll(list);
						setScene();
					}
				}else{
					setScene();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				setScene();
			}
		});
	}
	
	/** 设置场景列表 **/
	private void setScene(){
		if(commoditySceneInfos.size() >0){
			layout_scene.setVisibility(View.VISIBLE);
			commoditySceneAdapter.notifyDataSetChanged();
		}else{
			layout_scene.setVisibility(View.GONE);
		}
	}
	
	@OnClick(value = { R.id.search_ll, R.id.header_right})
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.header_right:
			SPUtil.savaToShared(ContextUtil.getContext(), HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.REP_POINT, false);// 当点击按钮时候，小红点消失
			toActivity(MsgCenterActivity.class);
			break;
		case R.id.search_ll:
			toActivity(SearchCommodityActivity.class);
			break;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MainActivity.class)) {
				// showGuideView();
			}
			if (notifyFrom.equals(JgPushReceiver.class)) {
				if ((int) msg == JgPushReceiver.MSG) {// 极光推送的消息
					setPoint();
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void showGuideView() {
		final GuideView.Builder builder = new GuideView.Builder(getActivity(), PackageUtil.getVersionName());
		LinearLayout layout_saoyisao = (LinearLayout) ScreenUtil.inflate(R.layout.layout_guide_saoyisao);
		LinearLayout layout_msg = (LinearLayout) ScreenUtil.inflate(R.layout.layout_guide_msg);
		builder.setTextSize(20).addHintView(header_left, layout_saoyisao, GuideView.Direction.RIGHT_BOTTOM,
				GuideView.MyShape.CIRCULAR, 0, 0, new OnClickCallback() {

					@Override
					public void onGuideViewClicked() {
						// TODO Auto-generated method stub
						builder.showNext();
					}
				}).addHintView(header_right, layout_msg, GuideView.Direction.LEFT_BOTTOM, GuideView.MyShape.CIRCULAR, 0,
						0, new OnClickCallback() {
							@Override
							public void onGuideViewClicked() {
								// TODO Auto-generated method stub
								builder.showNext();
							}
						});
		builder.show();
	}
}
