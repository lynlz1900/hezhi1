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
import com.wukong.hezhi.adapter.CommodityAppraiseAdapter;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.bean.CommodityAppraiseInfos;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.LoadMoreScrollView;
import com.wukong.hezhi.ui.view.SwipeRefreshLayoutVertical;
import com.wukong.hezhi.ui.view.LoadMoreScrollView.OnScollChangedListener;
import com.wukong.hezhi.ui.view.LoadMoreScrollView.OnScrollToBottomListener;
import com.wukong.hezhi.ui.view.MyFlowLayout;
import com.wukong.hezhi.ui.view.ListViewForScrollview;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName: CommodityDetailFragment
 * @Description: TODO(商品tab评价页)
 * @author HuangFeiFei
 * @date 2017-12-13
 * 
 */
public class CommodityAppraiseFragment extends BaseFragment {

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayoutVertical swprf;

	@ViewInject(R.id.scrlview)
	private LoadMoreScrollView scrlview;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.lv_scroll)
	private ListViewForScrollview lv_scroll;
	
	@ViewInject(R.id.text_appraise_favourable)
	private TextView text_appraise_favourable;
	
	@ViewInject(R.id.layout_flowlayout)
	private MyFlowLayout layout_flowlayout;
	
	@ViewInject(R.id.top_iv)
	private ImageView top_iv;
	
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	private int page = 0;// 当前页
	@SuppressWarnings("unused")
	private boolean isOver = false;// 是否加载介绍
	/*** 总页数 **/
	private int countPage = 1;
	
	private CommodityAppraiseInfos commodityAppraiseInfos = null;
	private CommodityAppraiseAdapter adapter;
	private List<CommodityAppraiseInfo> infos = new ArrayList<CommodityAppraiseInfo>();
	
	public static final String[] appraise_datas01 = new String[] { "全部", "好评", "中评 ", "差评"};
	public static final String[] appraise_datas02 = new String[] { "全部", "好评", "中评 ", "差评","有图评价"};
	public static final String[] appraise_datas03 = new String[] { "全部", "好评", "中评 ", "差评","追加评价"};
	public static final String[] appraise_datas04 = new String[] { "全部", "好评", "中评 ", "差评","有图评价","追加评价"};
	public String[] appraise_datas = appraise_datas01 ;
	private List<CheckBox> checkBoxs = new ArrayList<>(); 
	private LayoutInflater mInflater;
	private int appraiseType = 0;
	private String state = "ALL";
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commodity_appraise, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		setAdapter();
		setListener();
		setAppraiseData();
		getAppraiseListData();
		return rootView;
	}

	private void initView() {
		commodityAppraiseInfos = new CommodityAppraiseInfos();
		commodityInfo = ((CommodityInfoFragment)getParentFragment()).getCommodityInfo();
		if(commodityInfo == null){
			commodityInfo = new CommodityInfo();
		}
	}
	
	/** 设置评论列表适配器 **/
	private void setAdapter(){
		swprf.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		
		adapter = new CommodityAppraiseAdapter(infos, getActivity());
		lv_scroll.setAdapter(adapter);
//		lv_scroll.setEmptyView(empty);
	}
	
	/** 设置监听 */
	private void setListener() {

		swprf.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				getAppraiseNumberData();
				loadData();
			}
		});

		scrlview.setOnScrollToBottomLintener(new OnScrollToBottomListener() {
			@Override
			public void onScrollBottomListener() {
				page++;
				LogUtil.i("countPage："+countPage);
				if (page <= countPage) {// 如果加载结束
					loadData();
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
					scrlview.setComplete();
				}
			}
		});
		
		scrlview.setOnScollChangedListener(new OnScollChangedListener() {
			@Override
			public void onScrollChanged(LoadMoreScrollView scrollView, int x, int y, int oldx, int oldy) {
				if (y >= ScreenUtil.getScreenHeight()) {// 当移动的距离大于屏幕的高度
					top_iv.setVisibility(View.VISIBLE);
				} else {
					top_iv.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	@OnClick(value = { R.id.top_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_iv:// 回到最顶端
			scrlview.smoothScrollTo(0, 0);
			break;
		}
	}
	
	/** 获取评价列表 */
	private void getAppraiseListData(){
		swprf.post(new Runnable() {
			@Override
			public void run() {
				swprf.setRefreshing(true);
				page = 1;// 刷新将page置为1
				getAppraiseNumberData();
				loadData();
			}
		});// 进入页面，自动加载
	}
	
	/** 获取评价数量，好评度 */
	private void getAppraiseNumberData() {
		String URL = HttpURL.URL1 + HttpURL.APPRAISE_NUMBER;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				ResponseJsonInfo<CommodityAppraiseInfos> info = JsonUtil.fromJson(arg0.result, CommodityAppraiseInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						CommodityAppraiseInfos cInfos = info.getBody();
						commodityAppraiseInfos.setNumber(cInfos);
					} catch (Exception e) {
					}
					
					setPageCount();
					setAppraiseData();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
				}
			}
		});
	}
	
	/** 加载数据 */
	private void loadData() {
		if(appraiseType == 0)
			state = "ALL";
		else if(appraiseType == 1)
			state = "0";
		else if(appraiseType == 2)
			state = "1";
		else if(appraiseType == 3)
			state = "2";
		else if(appraiseType == 4 && appraise_datas == appraise_datas02)
			state = "3";
		else if(appraiseType == 4 && appraise_datas == appraise_datas04)
			state = "3";
		else if(appraiseType == 4 && appraise_datas == appraise_datas03)
			state = "4";
		else if(appraiseType == 5)
			state = "4";
		String URL = HttpURL.URL1 + HttpURL.APPRAISE_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
		map.put("state", state);
		map.put("page", page+"");
		LogUtil.i(URL);
		LogUtil.i(map.toString());
//		((BaseActivity)getActivity()).showProgressDialog();
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
//				((BaseActivity)getActivity()).dismissProgressDialog();
				swprf.setRefreshing(false);// 将下拉刷新隐藏
				scrlview.setComplete();// 上拉加载隐藏
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
//				((BaseActivity)getActivity()).dismissProgressDialog();
				swprf.setRefreshing(false);// 加载完成将下拉刷新隐藏
				scrlview.setComplete();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CommodityAppraiseInfos> info = JsonUtil.fromJson(arg0.result, CommodityAppraiseInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					ArrayList<CommodityAppraiseInfo> list = new ArrayList<>(); 
					try {
						CommodityAppraiseInfos cInfos = info.getBody();
						list = (ArrayList<CommodityAppraiseInfo>) cInfos.getDataList();
						commodityAppraiseInfos.setDataList(list);
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
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
				}
			}
		});
	}
	
	/** 设置评价信息 */
	private void setAppraiseData() {
		if(commodityAppraiseInfos != null){
			text_appraise_favourable.setText(Math.round(commodityAppraiseInfos.getGoodCommentPercent())+"%");
		
			if(commodityAppraiseInfos.getHasPictureComment() <1 && commodityAppraiseInfos.getAddToComment() <1)
				appraise_datas = appraise_datas01;
			else if(commodityAppraiseInfos.getHasPictureComment() >0 && commodityAppraiseInfos.getAddToComment() <1)
				appraise_datas = appraise_datas02;
			else if(commodityAppraiseInfos.getHasPictureComment() <1 && commodityAppraiseInfos.getAddToComment() >0)
				appraise_datas = appraise_datas03;
			else if(commodityAppraiseInfos.getHasPictureComment() >0 && commodityAppraiseInfos.getAddToComment() >0)
				appraise_datas = appraise_datas04;
			
			setFlowLayout();
		
		}else{
			commodityAppraiseInfos = new CommodityAppraiseInfos();
			text_appraise_favourable.setText(getString(R.string.appraise_favourable) + 100+"%");
			setFlowLayout();
		}
	}
	
	/**
     * 找到搜索评价标签的控件
     */
	private void setFlowLayout(){
		layout_flowlayout.removeAllViews();
		checkBoxs.clear();
		int position = 0;
		mInflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < appraise_datas.length; i++) {
            final CheckBox checkBox = (CheckBox) mInflater.inflate(
                    R.layout.view_lable_tv, layout_flowlayout, false);
            if(i == 0)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getCountRecord()));
            else  if(i == 1)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getGoodComment()));
            else  if(i == 2)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getMediumComment()));
            else  if(i == 3)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getBadComment()));
            else  if(i == 4 && appraise_datas == appraise_datas02)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getHasPictureComment()));
            else  if(i == 4 && appraise_datas == appraise_datas03)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getAddToComment()));
            else  if(i == 4 && appraise_datas == appraise_datas04)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getHasPictureComment()));
            else  if(i == 5 && appraise_datas == appraise_datas04)
            	checkBox.setText(appraise_datas[i] + " "+StringUtil.change2W(commodityAppraiseInfos.getAddToComment()));
            
            position = i;
            if(position == appraiseType)
            	checkBox.setChecked(true);
            else
            	checkBox.setChecked(false);
            checkBox.setTag(position);
            checkBoxs.add(checkBox);
            layout_flowlayout.addView(checkBox);//添加到父View
            
            checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(((CheckBox)v).isChecked()){
						try {
							setFlowLayoutTag(Integer.valueOf(String.valueOf(v.getTag())));
							appraiseType = Integer.valueOf(String.valueOf(v.getTag()));
						} catch (Exception e) {
						}
						
						page = 1;
						setPageCount();
						loadData();
					}else{
						((CheckBox)v).setChecked(true);
					}
				}
			});
        }
	}
	
	/** 点击设置评价标签 **/
	private void setFlowLayoutTag(int position){
		layout_flowlayout.removeAllViews();
		 for (int i = 0; i < checkBoxs.size(); i++) {
			 if(position == i)
				 checkBoxs.get(i).setChecked(true);
			 else
				 checkBoxs.get(i).setChecked(false);
			 
			 layout_flowlayout.addView(checkBoxs.get(i));//添加到父View
			 
		 }
	}
	
	/** 设置页数 **/
	private void setPageCount(){
		if(appraiseType == 0)
			countPage = commodityAppraiseInfos.getCountPage();
		else if(appraiseType == 1)
			countPage = commodityAppraiseInfos.getGoodCommentPage();
		else if(appraiseType == 2)
			countPage = commodityAppraiseInfos.getMediumCommentPage();
		else if(appraiseType == 3)
			countPage = commodityAppraiseInfos.getBadCommentPage();
		else if(appraiseType == 4 && appraise_datas == appraise_datas02)
			countPage = commodityAppraiseInfos.getHasPictureCommentPage();
		else if(appraiseType == 4 && appraise_datas == appraise_datas04)
			countPage = commodityAppraiseInfos.getHasPictureCommentPage();
		else if(appraiseType == 4 && appraise_datas == appraise_datas03)
			countPage = commodityAppraiseInfos.getAddToCommentPage();
		else if(appraiseType == 5)
			countPage = commodityAppraiseInfos.getAddToCommentPage();
	}
}
