package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.AddressReceiptAdapter;
import com.wukong.hezhi.bean.AddressReceiptInfo;
import com.wukong.hezhi.bean.AddressReceiptInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

/***
 * 
 * @ClassName: AddressActivity
 * @Description: TODO(收货地址界面)
 * @author HuangFeiFei
 * @date 2017-8-9 下午17：15
 * 
 */
public class AddressActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.address);
	public String pageCode ="address_receipt";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	@ViewInject(R.id.layout_l)
	private View layout_l;
	
	private int page = 1;// 当前页
	private boolean isOver = false;// 是否加载介绍
	
	private AddressReceiptAdapter addressReceiptAdapter;
	private List<AddressReceiptInfo> addressReceiptInfos = new ArrayList<AddressReceiptInfo>();
	private List<AddressReceiptInfo> addressReceiptInfosSel = new ArrayList<AddressReceiptInfo>();
	
	/** 默认选中的地址 **/
	public static AddressReceiptInfo addressReceiptInfoDefault; 
	
	public static AddressActivity addressActivity = null;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_address;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.address));
		initView();
		setListener();
	}
	
	/**初始化控件**/
	private void initView() {
		addressActivity = this;
		
		addressReceiptInfoDefault = null;
		addressReceiptInfoDefault = (AddressReceiptInfo) getIntent().getSerializableExtra
				(Constant.Extra.MAILING_ADDRESS);
		
		addressReceiptInfosSel.clear();
		addressReceiptInfosSel.addAll(addressReceiptInfos);
		setAdapter();
		setSwipeLayout();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showProgressDialog();
		loadData();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		addressActivity = null;
	}
	
	/**初始化adapter**/
	private void setAdapter(){
		addressReceiptAdapter = new AddressReceiptAdapter
				(addressReceiptInfosSel,mActivity);
		lv_scroll.setAdapter(addressReceiptAdapter);
		lv_scroll.setEmptyView(empty);
	}
	
	/** 初始化加载**/
	public void setSwipeLayout() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
//		refresListView();
	}
	
	/** 设置监听 */
	private void setListener() {
		//下拉刷新
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
			}
		});

		//上拉加载更多
		lv_scroll.setLoadListener(new ILoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
//				page++;
				isOver = true;
				if (!isOver) {// 如果加载结束
					loadData();
				} else {
					lv_scroll.loadCompleted();
				}
			}
		});
	}
	
	@OnClick(value = { R.id.btn_address_add })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_address_add:
			toActivity(AddressAddActivity.class);
			break;
		}
	}
	
	/**加载首页数据**/
	@SuppressWarnings("unused")
	private void refresListView() {
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}
	
	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1 + HttpURL.ADDRESS_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId()); // 用户id
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
				dismissProgressDialog();
				LogUtil.i(arg1);
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings({"unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				LogUtil.i(arg0.result);
				ResponseJsonInfo<AddressReceiptInfos> info = JsonUtil.fromJson(
						arg0.result, AddressReceiptInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					AddressReceiptInfos infos = new AddressReceiptInfos();
					try {
						infos = (AddressReceiptInfos)info.getBody();
					} catch (Exception e) {
					}
					List<AddressReceiptInfo> receiptInfos = infos.getDatalist();
					if (page == 1) {
						addressReceiptInfos.clear();
					}
					if (receiptInfos == null || receiptInfos.size() == 0) {
						isOver = true;
					} else {
						isOver = false;
						addressReceiptInfos.addAll(receiptInfos);
					}
					
					addressReceiptInfosSel.clear();
					addressReceiptInfosSel.addAll(addressReceiptInfos);
					addressReceiptAdapter.notifyDataSetChanged();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
				}
			}
		});
	}
}
