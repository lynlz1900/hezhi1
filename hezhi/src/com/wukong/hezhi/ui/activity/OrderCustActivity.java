package com.wukong.hezhi.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyCustomizationOrderAdapter;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.OrderInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.RedBagOrderManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 
 * @ClassName: OrderCustActivity
 * @Description: TODO(我的定制订单)
 * @author HuangFeiFei
 * @date 2017-8-4 下午16：12
 * 
 */
public class OrderCustActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.order_customization);
	public String pageCode ="order_customization";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private int page = 0;// 当前页
	@SuppressWarnings("unused")
	private boolean isOver = false;// 是否加载介绍
	/*** 总页数 **/
	private int countPage = 0;
	
	private MyCustomizationOrderAdapter myCustomizationOrderAdapter;
	private List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
	private List<OrderInfo> orderInfosSel = new ArrayList<OrderInfo>();
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_customization;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.order));
		headView.setRihgtTitleText("", R.drawable.icon_search);
		initView();
		setListener();
	}
	
	/**初始化控件**/
	private void initView() {
		orderInfosSel.clear();
		orderInfosSel.addAll(orderInfos);
		setAdapter();
		setSwipeLayout();
	}
	
	/**初始化adapter**/
	private void setAdapter(){
		myCustomizationOrderAdapter = new MyCustomizationOrderAdapter(orderInfosSel,mActivity);
		lv_scroll.setAdapter(myCustomizationOrderAdapter);
		lv_scroll.setEmptyView(empty);
	}
	
	/** 初始化加载**/
	public void setSwipeLayout() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		refresListView();
	}
	
	/** 设置监听 */
	private void setListener() {
		
		//下拉刷新
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				mSwipeLayout.setRefreshing(true);
				loadData();
			}
		});

		//上拉加载更多
		lv_scroll.setLoadListener(new ILoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				if (page <= countPage) {// 如果加载结束
					loadData();
				} else {
					lv_scroll.loadCompleted();
					ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
				}
			}
		});
	}
	
	@OnClick(value = { R.id.header_right })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			toActivity(SearchOrderCustActivity.class);
			break;
		}
	}
	
	/**加载首页数据**/
	private void refresListView() {
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				showProgressDialog();
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}
	
	/**接到订单确认通知更新UI**/
	private void  refresListViewByReceive(OrderInfo info){
		orderInfos.clear();
		orderInfos.addAll(orderInfosSel);//先更新orderInfos，否则会导致数据不是最新的
		for(int i=0;i<orderInfos.size();i++){
			if(orderInfos.get(i).getId() == info.getId()){
				Collections.replaceAll(orderInfos, orderInfos.get(i), info);
			}
		}
		
		orderInfosSel.clear();
		orderInfosSel.addAll(orderInfos);
		myCustomizationOrderAdapter.notifyDataSetChanged();
		
//		loadData();
	}
		
	/**接到订单取消通知更新UI**/
	private void  refresListViewByCancel(int id){
		orderInfos.clear();
		orderInfos.addAll(orderInfosSel);//先更新orderInfos，否则会导致数据不是最新的
		for(int i=0;i<orderInfos.size();i++){
			if(orderInfos.get(i).getId() == id){
				orderInfos.get(i).setState("CANCELED");
			}
		}
		
		orderInfosSel.clear();
		orderInfosSel.addAll(orderInfos);
		myCustomizationOrderAdapter.notifyDataSetChanged();
	}
	
	/**接到订单删除通知更新UI**/
	private void  refresListViewByDel(int id){
		orderInfos.clear();
		orderInfos.addAll(orderInfosSel);//先更新orderInfos，否则会导致数据不是最新的
		for(int i=0;i<orderInfos.size();i++){
			if(orderInfos.get(i).getId() == id){
				orderInfos.remove(orderInfos.get(i));
			}
		}
		
		orderInfosSel.clear();
		orderInfosSel.addAll(orderInfos);
		myCustomizationOrderAdapter.notifyDataSetChanged();
	}
	
	/**接到红包通知更新UI**/
	private void  refresListViewByRedbag(int id,String redbag){
		orderInfos.clear();
		orderInfos.addAll(orderInfosSel);//先更新orderInfos，否则会导致数据不是最新的
		for(int i=0;i<orderInfos.size();i++){
			if(orderInfos.get(i).getId() == id){
				orderInfos.get(i).setRedbag(redbag);
			}
		}
		
		orderInfosSel.clear();
		orderInfosSel.addAll(orderInfos);
		myCustomizationOrderAdapter.notifyDataSetChanged();
	}
	
	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1 + HttpURL.ORDER_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId",  UserInfoManager.getInstance().getUserId());
		map.put("page",  page);
		LogUtil.d(url);
		LogUtil.d(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
				dismissProgressDialog();
				LogUtil.d(arg1);
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings({"unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				orderInfos.clear();
				orderInfos.addAll(orderInfosSel);//先更新orderInfos，否则会导致数据不是最新的
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<OrderInfos> info = JsonUtil.fromJson(
						arg0.result, OrderInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					OrderInfos infos = new OrderInfos();
					try {
						infos = (OrderInfos)info.getBody();
						List<OrderInfo> list = infos.getDatalist();
						countPage = infos.getCountPage();
						if (page == 1) {
							orderInfos.clear();
						}
						if (list == null || list.size() == 0) {
							isOver = true;
						} else {
							isOver = false;
							orderInfos.addAll(list);
						}
						
						orderInfosSel.clear();
						orderInfosSel.addAll(orderInfos);
						myCustomizationOrderAdapter.notifyDataSetChanged();
					} catch (Exception e) {
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
				}
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		super.updateState(notifyTo, notifyFrom, msg);
		
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(OrderCustCancelActivity.class)) {  //订单取消通知
				
				try {
					int orderid = (int) msg;
					refresListViewByCancel(orderid);
				} catch (Exception e) {
				}

			}else if (notifyFrom.equals(OrderCustDetailActivity.class)
					|| notifyFrom.equals(MyCustomizationOrderAdapter.class)) {
				
				try {
					if(msg instanceof String){
						String typeStr = ((String)msg).split(",")[0];
						String IdStr = ((String)msg).split(",")[1];
						int orderid = Integer.valueOf(IdStr);
						
						if(typeStr.equals("isDelete"))  //订单删除通知
							refresListViewByDel(orderid);
					}else if(msg instanceof OrderInfo){//订单确认通知
						OrderInfo info = (OrderInfo)msg;
						refresListViewByReceive(info);
					}
					
				} catch (Exception e) {
				}
			}else if (notifyFrom.equals(RedBagOrderManager.class)) {  //红包通知
				
				try {
					String strMsg = (String)msg;
					String strId = strMsg.split(",")[0];
					String strRedbag = strMsg.split(",")[1];
					int id = Integer.valueOf(strId);
					refresListViewByRedbag(id, strRedbag);
				} catch (Exception e) {
				}

			}else if (notifyFrom.equals(OrderPayActivity.class)) {  //红包通知
				
				try {
					refresListView();
				} catch (Exception e) {
				}

			}
		}
	}
}
