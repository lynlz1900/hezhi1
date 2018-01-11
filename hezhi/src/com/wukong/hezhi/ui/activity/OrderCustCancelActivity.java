package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.view.View;
import android.widget.ImageView;

/***
 * 
 * @ClassName: OrderCustCancelActivity
 * @Description: TODO(定制订单取消原因)
 * @author HuangFeiFei
 * @date 2017-8-8 上午11：20
 * 
 */
public class OrderCustCancelActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.order_cancel);
	public String pageCode ="order_cancel";
	
	@ViewInject(R.id.image_right01)
	private ImageView image_right01;
	
	@ViewInject(R.id.image_right02)
	private ImageView image_right02;
	
	@ViewInject(R.id.image_right03)
	private ImageView image_right03;
	
	@ViewInject(R.id.image_right04)
	private ImageView image_right04;
	
	@ViewInject(R.id.image_right05)
	private ImageView image_right05;
	
	@ViewInject(R.id.image_right06)
	private ImageView image_right06;
	
	/**订单取消原因 int类型**/
	private int orderCancelReasonInt = -1;
	/**订单取消原因 String类型**/
	private String orderCancelReasonStr = "";
	private OrderInfo orderInfo;
	
	private List<ImageView> imageViews;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_cancel_reason;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.order_cancel));
		initView();
	}
	
	//初始化控件
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		
		orderInfo = (OrderInfo) getIntent().
				getSerializableExtra(Constant.Extra.ORDER_INFO_CANCEL);
		
		imageViews = new ArrayList(Arrays.asList(image_right01,image_right02,image_right03,
				image_right04,image_right05,image_right06));
		
	}
	
	@OnClick(value = { R.id.layout_order_reason01, R.id.layout_order_reason02, R.id.layout_order_reason03
			 ,R.id.layout_order_reason04, R.id.layout_order_reason05, R.id.layout_order_reason06
			 ,R.id.btn_order_cancel_no, R.id.btn_order_cancel_ok})
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case  R.id.layout_order_reason01://选择“不想买了”原因
			orderCancelReasonInt = Constant.OrderCancelReason.BUY_NOT.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason01);
			imageCancelChange();
			break;
		case  R.id.layout_order_reason02://选择“价格贵”原因
			orderCancelReasonInt = Constant.OrderCancelReason.PRICE_EXPENSIVE.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason02);
			imageCancelChange();
			break;
		case  R.id.layout_order_reason03://选择“付款困难”原因
			orderCancelReasonInt = Constant.OrderCancelReason.PAY_DIFFICULT.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason03);
			imageCancelChange();
			break;
		case  R.id.layout_order_reason04://选择“收货信息有误”原因
			orderCancelReasonInt = Constant.OrderCancelReason.RECEIVE_ERROR.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason04);
			imageCancelChange();
			break;
		case  R.id.layout_order_reason05://选择“发票信心有误”原因
			orderCancelReasonInt = Constant.OrderCancelReason.INVOICE_ERROR.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason05);
			imageCancelChange();
			break;
		case  R.id.layout_order_reason06://选择“其他”原因
			orderCancelReasonInt = Constant.OrderCancelReason.OTHER.ordinal();
			orderCancelReasonStr = ContextUtil.getString(R.string.order_cancel_reason06);
			imageCancelChange();
			break;
		case  R.id.btn_order_cancel_no:
			finish();
			break;
		case  R.id.btn_order_cancel_ok:
			orderCancelOk();
			break;
		default:
			break;
		}
	}
	
	/** 订单取消选择勾选按钮变化**/
	private void imageCancelChange(){
		for(int i=0;i<imageViews.size();i++)
			if(i == orderCancelReasonInt)
				imageViews.get(i).setVisibility(View.VISIBLE);
			else
				imageViews.get(i).setVisibility(View.GONE);
	}
	
	/** 订单取消确认**/
	private void orderCancelOk(){
		if(orderCancelReasonInt == -1){//没有选择取消原因
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_cancel_isnull));
			return;
		}
		
		if(orderInfo == null){//订单信息为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_isnull));
			return;
		}
		
		orderCancelOkHttp();
	}
	
	/** 订单取消确认http接口**/
	private void orderCancelOkHttp(){
		String url = HttpURL.URL1 + HttpURL.ORDER_CANCEL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderInfo.getId());
		map.put("cancelReason", orderCancelReasonStr);
		LogUtil.i(url);
		LogUtil.i(map.toString());
		showProgressDialog();
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_cancel_success));
					
					ObserveManager.getInstance().notifyState(OrderCustDetailActivity.class,
							OrderCustCancelActivity.class,
							orderInfo.getId());// 通知订单详情订单取消
					
					ObserveManager.getInstance().notifyState(OrderCustFragment.class,
							OrderCustCancelActivity.class,
							orderInfo.getId());// 通知订单列表订单取消
					
					if(Constant.isFromPage.isOrderListSearch)
						ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class,
								OrderCustCancelActivity.class,
								orderInfo.getId());// 通知订单搜索列表订单取消
					
					finish();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_cancel_fail));
				}
			}
		});
	}
	
}
