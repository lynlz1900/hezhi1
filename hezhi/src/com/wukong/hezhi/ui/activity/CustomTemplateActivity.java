package com.wukong.hezhi.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ViewStub;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CommodityTemplateAdapter;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.bean.TempletInfos;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.view.LoadMoreGridView;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;

/**
 * 
 * @ClassName: CustomTemplateActivity
 * @Description: TODO(定制模板界面)
 * @author HuangFeiFei
 * @date 2016-11-13 14:48
 * 
 */
public class CustomTemplateActivity extends BaseActivity{
	public String pageName = ContextUtil.getString(R.string.custom_template);
	public String pageCode = "custom_template";

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	@ViewInject(R.id.lv_scroll)
	private LoadMoreGridView lv_scroll;
	
	@ViewInject(R.id.empty)
	private ViewStub empty;

	private CommodityTemplateAdapter adapter;
	private List<TempletInfo> infos = new ArrayList<TempletInfo>();

	private int page = 0;// 当前页
	@SuppressWarnings("unused")
	private boolean isOver = false;// 是否加载介绍

	/** 商品信息 */
	private CommodityInfo commodityInfo;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_commodity_template;
	}

	@Override
	protected void init() {
		initView();
		setListener();
	}

	private void initView() {
		headView.setTitleStr(ContextUtil.getString(R.string.custom_template));
		
		try {
			commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		} catch (Exception e) {
		}
		if(commodityInfo == null)
			commodityInfo = new CommodityInfo();
		
		showProgressDialog();
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		
		adapter = new CommodityTemplateAdapter(infos, mActivity,commodityInfo);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
	}

	/** 设置监听 */
	private void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
			}
		});
	}

	/** 加载数据 */
	private void loadData() {
		String URL = HttpURL.URL1 + HttpURL.TEMPLETS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
		if(Constant.customScene.isFromCustomScene)
			map.put("customizationCaseId", Constant.customScene.customSceneId);
		else
			map.put("customizationCaseId", 0);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<TempletInfos> info = JsonUtil.fromJson(arg0.result, TempletInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					
					ArrayList<TempletInfo> list = new ArrayList<TempletInfo>();
					try {
						TempletInfos cInfos = info.getBody();
						list = (ArrayList<TempletInfo>) cInfos.getDataList();
					} catch (Exception e) {
					}
					
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
}
