package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfo;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.RedBagOrderManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.OrderWindows;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.OrderWindows.IOrderCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 
 * @ClassName: OrderCustDetailActivity
 * @Description: TODO(定制订单详情)
 * @author HuangFeiFei
 * @date 2017-8-7 下午16：44
 * 
 */
@SuppressWarnings("deprecation")
public class OrderCustDetailActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.order_detail);
	public String pageCode ="order_detail";
	
	@ViewInject(R.id.customization_name_status)
	private TextView customization_name_status;
	
	@ViewInject(R.id.customization_name_statusinfo)
	private TextView customization_name_statusinfo;
	
	@ViewInject(R.id.customization_username)
	private TextView customization_username;
	
	@ViewInject(R.id.customization_userphone)
	private TextView customization_userphone;
	
	@ViewInject(R.id.customization_useraddress)
	private TextView customization_useraddress;
	
	@ViewInject(R.id.customization_name_short)
	private TextView customization_name_short;
	
	@ViewInject(R.id.customization_name_long)
	private TextView customization_name_long;
	
	@ViewInject(R.id.customization_price)
	private TextView customization_price;
	
	@ViewInject(R.id.customization_name_num)
	private TextView customization_name_num;
	
	@ViewInject(R.id.customization_price_info)
	private TextView customization_price_info;
	
	@ViewInject(R.id.customization_order_id)
	private TextView customization_order_id;
	
	@ViewInject(R.id.customization_order_time)
	private TextView customization_order_time;
	
	@ViewInject(R.id.customization_order_remark)
	private TextView customization_order_remark;
	
	@ViewInject(R.id.customization_express_company)
	private TextView customization_express_company;
	
	@ViewInject(R.id.customization_express_number)
	private TextView customization_express_number;
	
	@ViewInject(R.id.customization_express_delivertime)
	private TextView customization_express_delivertime;
	
	@ViewInject(R.id.customization_express_receivedtime)
	private TextView customization_express_receivedtime;
	
	@ViewInject(R.id.customization_get_redbag)
	private TextView customization_get_redbag;
	
	@ViewInject(R.id.btn_customiation_right01)
	private TextView btn_customiation_right01;
	
	@ViewInject(R.id.btn_customiation_right02)
	private TextView btn_customiation_right02;
	
	@ViewInject(R.id.btn_customiation_right03)
	private TextView btn_customiation_right03;
	
	@ViewInject(R.id.imageview_customization_status)
	private ImageView imageview_customization_status;
	
	@ViewInject(R.id.imageview_customization_logo)
	private ImageView imageview_customization_logo;
	
	@ViewInject(R.id.redbag_iv)
	private ImageView redbag_iv;
	
	@ViewInject(R.id.layout_customization_express)
	private LinearLayout layout_customization_express;
	
	@ViewInject(R.id.layout_customization_redbag)
	private LinearLayout layout_customization_redbag;
	
	/**订单状态：待付款，待发货，待收货，已取消，已收货**/
	private int status = -1;
	/**订单信息**/
	private OrderInfo orderInfo;
	/** 订单编号 */
	private String orderNum = "";
	/**订单是否有红包**/
	private int redPackStatus = 0;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_detail;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.order_detail));
		initView();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constant.isFromPage.isOrderDetail = false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!TextUtils.isEmpty(orderNum)){
			getOrderDetailDataHttp();
		}
	}
	
	//初始化控件
	private void initView() {
		
		Constant.isFromPage.isOrderDetail = true;
		orderNum = getIntent().getStringExtra(Constant.Extra.ORDER_NO);
	}
	
	/** 更新界面UI**/
	private void refreshView(){
		 status = orderInfo.getStatus();
		 
		 if(status == Constant.OrderType.OBLIGATIONS.ordinal()){//待付款
			 	imageview_customization_status.setImageDrawable(ContextUtil.getDrawable(R.drawable.icon_oderstatus_withoutpay));
	        	redbag_iv.setVisibility(View.GONE);
	        	btn_customiation_right02.setVisibility(View.VISIBLE);
	        	btn_customiation_right03.setVisibility(View.GONE);
	        	layout_customization_express.setVisibility(View.GONE);
	        	layout_customization_redbag.setVisibility(View.GONE);
	        	customization_name_statusinfo.setVisibility(View.VISIBLE);
	        	
	        	customization_name_status.setText(ContextUtil.getString(R.string.order_status_obligations));
	        	customization_name_statusinfo.setText(ContextUtil.getString(R.string.order_status_obligations_info));  
	        	btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_to));
	        	btn_customiation_right02.setText(ContextUtil.getString(R.string.order_cancel));
	        }else if(status == Constant.OrderType.UNDELIVERED.ordinal()){//待发货
	        	imageview_customization_status.setImageResource(R.drawable.icon_oderstatus_withoutsend);
	        	redbag_iv.setVisibility(View.GONE);
	        	btn_customiation_right02.setVisibility(View.GONE);
	        	btn_customiation_right03.setVisibility(View.GONE);
	        	layout_customization_express.setVisibility(View.GONE);
	        	layout_customization_redbag.setVisibility(View.GONE);
	        	customization_name_statusinfo.setVisibility(View.VISIBLE);
	        	
	        	customization_name_status.setText(ContextUtil.getString(R.string.order_status_undelivered));
	        	customization_name_statusinfo.setText(ContextUtil.getString(R.string.order_status_undelivered_info));  
	        	btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
	        }else if(status == Constant.OrderType.UNRECEIVED.ordinal()){//待收货
	        	imageview_customization_status.setImageResource(R.drawable.icon_oderstatus_recievinggoods);
	        	
	        	redPackStatus = orderInfo.getRedPackStatus();
	        	if(orderInfo.getRedPackStatus() == 1)
	        		redbag_iv.setVisibility(View.VISIBLE);
	        	else
	        		redbag_iv.setVisibility(View.GONE);
	        	
	        	btn_customiation_right02.setVisibility(View.VISIBLE);
	        	btn_customiation_right03.setVisibility(View.VISIBLE);
	        	layout_customization_express.setVisibility(View.VISIBLE);
	        	layout_customization_redbag.setVisibility(View.GONE);
	        	customization_name_statusinfo.setVisibility(View.VISIBLE);
	        	
	        	customization_name_status.setText(ContextUtil.getString(R.string.order_status_unreceived));
	        	
	        	String exCompany = orderInfo.getLogisticsCompany() == null?"":orderInfo.getLogisticsCompany();
	        	String exNumber = orderInfo.getLogisticsNumber() == null?"":orderInfo.getLogisticsNumber();
	        	if(exCompany.equals("") && exNumber.equals("")){
	        		customization_name_statusinfo.setText("");
	        	}else if(exCompany.equals("") && !exNumber.equals("")){
	        		customization_name_statusinfo.setText(
	        				ContextUtil.getString(R.string.express_number)+exNumber);
	        	}else{
	        		customization_name_statusinfo.setText(
	        				exCompany + "  "+
	        						ContextUtil.getString(R.string.express_number)+exNumber);
	        	}
	        		
	        	btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_complete));
	        	btn_customiation_right03.setText(ContextUtil.getString(R.string.order_buy_again));
	        	btn_customiation_right02.setText(ContextUtil.getString(R.string.order_logistics_view));
	        }else if(status == Constant.OrderType.CANCELED.ordinal()){//已取消
	        	imageview_customization_status.setImageResource(R.drawable.icon_oderstatus_canceled);
	        	redbag_iv.setVisibility(View.GONE);
	        	btn_customiation_right02.setVisibility(View.VISIBLE);
	        	btn_customiation_right03.setVisibility(View.GONE);
	        	layout_customization_express.setVisibility(View.GONE);
	        	layout_customization_redbag.setVisibility(View.GONE);
	        	customization_name_statusinfo.setVisibility(View.GONE);
	        	
	        	customization_name_status.setText(ContextUtil.getString(R.string.order_status_canceled));
	        	btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
	        	btn_customiation_right02.setText(ContextUtil.getString(R.string.order_delete));
	        }else if(status == Constant.OrderType.RECEIVED.ordinal()){//已完成
	        	imageview_customization_status.setImageResource(R.drawable.icon_oderstatus_finished);
	        	redbag_iv.setVisibility(View.GONE);
	        	btn_customiation_right02.setVisibility(View.VISIBLE);
	        	layout_customization_express.setVisibility(View.VISIBLE);
	        	layout_customization_redbag.setVisibility(View.VISIBLE);
	        	customization_name_statusinfo.setVisibility(View.GONE);
	        	if(orderInfo != null && orderInfo.getCommentType() == 1)
	        		btn_customiation_right03.setVisibility(View.VISIBLE);
	        	else
	        		btn_customiation_right03.setVisibility(View.GONE);
	        	
	        	customization_name_status.setText(ContextUtil.getString(R.string.order_status_received));
	        	btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
	        	btn_customiation_right03.setText(ContextUtil.getString(R.string.appraise_show));
	        	btn_customiation_right02.setText(ContextUtil.getString(R.string.order_delete));
	        }
	        
		 GlideImgUitl.glideLoaderNoAnimation
		 (ContextUtil.getContext(),  orderInfo.getProductPictureUrl(), imageview_customization_logo, 2);
//		 	GlideImgUitl.glideLoader(ContextUtil.getContext(), orderInfo.getProductPictureUrl(),
//		 			imageview_customization_logo, 2);
		 
	        customization_name_short.setText(orderInfo.getCompanyRealName());
	    	customization_name_long.setText(orderInfo.getProductName());
	    	customization_price.setText("¥"+DataUtil.double2pointString(orderInfo.getPrice()));
	    	customization_name_num.setText("×"+orderInfo.getNumber());
	    	customization_price_info.setText(ContextUtil.getString(R.string.money_total)+"¥"
	    	    +(orderInfo.getPriceTotal()==null?
	    	    	"0.00":orderInfo.getPriceTotal()));
	    	
	    	customization_username.setText(orderInfo.getReceiverName());
	    	customization_userphone.setText(orderInfo.getReceiverPhone());
	    	customization_useraddress.setText((orderInfo.getReceiverAddr()==null?"":orderInfo.getReceiverAddr())
	    			+(orderInfo.getReceiverAddrDetail()==null?"":orderInfo.getReceiverAddrDetail()));
	    	
	    	customization_order_id.setText(orderInfo.getOrderNumber());
	    	customization_order_time.setText(DateUtil.getYMDHMS(orderInfo.getTimePut()));
	    	customization_order_remark.setText(orderInfo.getRemark());
	    	
	    	if(status == Constant.OrderType.UNRECEIVED.ordinal()
	    			|| status == Constant.OrderType.RECEIVED.ordinal()){//待收货和已收货
	    		if(orderInfo.getLogisticsCompany() != null){
		    		customization_express_company.setText(orderInfo.getLogisticsCompany());
	    		}
		    	customization_express_number.setText(orderInfo.getLogisticsNumber());
		    	customization_express_delivertime.setText(DateUtil.getYMDHMS(orderInfo.getTimeSend()));
	        }
	    	
	    	if(status == Constant.OrderType.RECEIVED.ordinal()){//已收货
	    		customization_express_receivedtime.setText(DateUtil.getYMDHMS(orderInfo.getTimeReceive()));
		    	customization_get_redbag.setText((orderInfo.getRedbag()==null?"0.00":orderInfo.getRedbag())
		    			+ContextUtil.getString(R.string.money_unit));
	    	}
	}
	
	/*** 确认收货 ***/
	public final static int ISRECEIVE = 10001;
	/*** 删除订单 ***/
	public final static int ISDELETE = 10002;
	
	/*** 去支付 ***/
	public final static int BUY_NOW = 100001;
	/*** 再次购买 ***/
	public final static int BUY_AGAIN = 100002;
	
	@OnClick(value = { R.id.btn_customiation_right01, R.id.btn_customiation_right02, R.id.btn_customiation_right03
			 ,R.id.layout_call_service,R.id.customization_number_copy,R.id.layout_customization})
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case  R.id.btn_customiation_right01:
			onclickRight();
			break;
		case  R.id.btn_customiation_right02:
			onclickMid();
			break;
		case  R.id.btn_customiation_right03:
			onclickLeft();
			break;
		case  R.id.layout_call_service:
			call();
			break;
		case R.id.customization_number_copy:
			copy();
			break;
		case R.id.layout_customization:
			Constant.isFromPage.isOrderDetailToCommDetail = true;
			JumpActivityManager.getInstance().toActivity(mActivity, CommodityInfoActivity.class,
					Constant.Extra.PRODUCTID, orderInfo.getProductId());//跳转到商品详情界面
			break;
		default:
			break;
		}
	}
	
	/** 底部最右侧按钮事件监听**/
	private void onclickRight(){
		if(orderInfo == null){//订单不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_isnull));
			return;
		}
		
		if(status == Constant.OrderType.OBLIGATIONS.ordinal()){//去支付
			getCommodityInfoDataHttp(BUY_NOW);
		}else if(status == Constant.OrderType.UNDELIVERED.ordinal() ||
				status == Constant.OrderType.CANCELED.ordinal() ||
				status == Constant.OrderType.RECEIVED.ordinal()){//再次购买
			getCommodityInfoDataHttp(BUY_AGAIN);
		}else if(status == Constant.OrderType.UNRECEIVED.ordinal()){//确认收货
			deleteOrReceive(ISRECEIVE, ContextUtil.getString(R.string.order_isreceive_true));
		}
	}
	
	/** 底部中间按钮事件监听**/
	private void onclickMid(){
		if(orderInfo == null){//订单不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_isnull));
			return;
		}
		
		if(status == Constant.OrderType.OBLIGATIONS.ordinal()){//取消订单
			JumpActivityManager.getInstance().toActivity(mActivity,
					OrderCustCancelActivity.class, Constant.Extra.ORDER_INFO_CANCEL,
					orderInfo);
		}else if(status == Constant.OrderType.CANCELED.ordinal()){//删除订单
			
			deleteOrReceive(ISDELETE,ContextUtil.getString(R.string.order_isdelete_true));
		}if(status == Constant.OrderType.UNRECEIVED.ordinal()){//查看物流
			
			gotoExpressView();
		}
		else if(status == Constant.OrderType.RECEIVED.ordinal()){//删除订单
			
			deleteOrReceive(ISDELETE,ContextUtil.getString(R.string.order_isdelete_true));
		}
	}
	
	/** 底部最左侧按钮事件监听**/
	private void onclickLeft(){
		if(orderInfo == null){//订单不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_isnull));
			return;
		}
		
		if(status == Constant.OrderType.UNRECEIVED.ordinal()){//再次购买
			getCommodityInfoDataHttp(BUY_AGAIN);
		}else if(status == Constant.OrderType.RECEIVED.ordinal()){//查看物流
			gotoAppraiseShow();
		}
	}
	
	/** 弹出确认对话框 **/
    private void deleteOrReceive(final int type,String msg){
		new CustomAlterDialog.Builder(mActivity).setMsg(msg)
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				// TODO Auto-generated method stub
				if(type == ISRECEIVE){
					orderReceivedHttp();;//确认收货
				}
				else if(type == ISDELETE){
					orderDeleteHttp();//删除订单
				}
			}
		}).build().show();

    }
    
    /** 订单删除**/
    private void orderDel(){
    	ScreenUtil.showToast(ContextUtil.getString(R.string.order_delete_success));
		
		ObserveManager.getInstance().notifyState(OrderCustFragment.class,
				OrderCustDetailActivity.class,
				"isDelete,"+orderInfo.getId());// 通知订单列表订单删除
		if(Constant.isFromPage.isOrderListSearch)
			ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class,
					OrderCustDetailActivity.class,
					"isDelete,"+orderInfo.getId());// 通知订单搜索列表订单删除
		
		finish();
    }
    
    /** 订单确认**/
    private void orderReceive(){
		refreshView();
		
		ScreenUtil.showToast(ContextUtil.getString(R.string.order_receive_success));
		
		ObserveManager.getInstance().notifyState(OrderCustFragment.class,
				OrderCustDetailActivity.class,
				orderInfo);// 通知订单列表订单确认收货
		if(Constant.isFromPage.isOrderListSearch)
			ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class,
					OrderCustDetailActivity.class,
					orderInfo);// 通知订单搜索列表订单确认收货
		
		if(redPackStatus == 1)
			RedBagOrderManager.getInstance(mActivity).checkRedBag(orderInfo);//领红包
			
    }
    
    /**确认收货http接口**/
    private void orderReceivedHttp(){
    	String url = HttpURL.URL1 + HttpURL.ORDER_RECEIVED;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderInfo.getId());
		LogUtil.i(url);
		LogUtil.i(map.toString());
		showProgressDialog();
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings({"unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<OrderInfo> info = JsonUtil.fromJson(
						arg0.result, OrderInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					OrderInfo orderInfo02 = (OrderInfo)info.getBody();
					orderInfo = orderInfo02;
					orderReceive();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_receive_fail));
				}
			}
		});
    }
    
    /**删除订单http接口**/
    private void orderDeleteHttp(){
    	String url = HttpURL.URL1 + HttpURL.ORDER_DEL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderInfo.getId());
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
					orderDel();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_delete_fail));
				}
			}
		});
    }
    
    CommodityInfo commodityInfo = null;
    
    /**通过productId获取商品信息http接口**/
    private void getCommodityInfoDataHttp(final int type) {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", orderInfo.getProductId());// 产品id
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_DETAIL;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<CommodityInfo> info = 
						JsonUtil.fromJson(arg0.result, CommodityInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						commodityInfo = info.getBody();
					} catch (Exception e) {
					}
					
					if(commodityInfo != null){
						if(commodityInfo.getProductIsDelete() == 1){
							ScreenUtil.showToast(ContextUtil.getString(R.string.custom_delete));
						}
						else if(commodityInfo.getProductIsDelete() == 0){
							if(type == BUY_AGAIN){
								if(commodityInfo.getIsAnniversary() == 1){
									CheckIsBought();
								}
								else{
									gotoPayConfirm();
								}
							}
							else if(type == BUY_NOW)
								gotoPayNow();
								
						}
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.i(arg1);
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}

    /** 检测员工是否购买过20周年产品*/
   	private void CheckIsBought() {
   		String URL = HttpURL.URL1 + HttpURL.CHECKISBOUGHT;
   		Map<String, Object> map = new HashMap<String, Object>();
   		map.put("productId", orderInfo.getProductId());
   		map.put("memberId", UserInfoManager.getInstance().getUserId());
   		LogUtil.i(URL);
   		LogUtil.i(map.toString());
   		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
   			@SuppressWarnings("unchecked")
   			@Override
   			public void onSuccess(ResponseInfo<String> arg0) {
   				LogUtil.i(arg0.result);
   				ResponseJsonInfo<CommodityInfo> info = JsonUtil.fromJson(arg0.result, CommodityInfo.class);
   				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
   					gotoPayConfirm();
   				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
   					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
   						ScreenUtil.showToast(info.getPromptMessage());
   				}
   			}

   			@Override
   			public void onFailure(HttpException arg0, String arg1) {
   				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
   			}
   		});
   	}
   	
    /**获取订单详情信息**/
    private void getOrderDetailDataHttp() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNum);// 订单号
		String URL = HttpURL.URL1 + HttpURL.ORDER_DETAIL_GET;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<OrderInfo> info = 
						JsonUtil.fromJson(arg0.result, OrderInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						orderInfo = info.getBody();
					} catch (Exception e) {
					}
					
					if(orderInfo != null){
						refreshView();
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.i(arg1);
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}
    
    /** 
	* 跳转到确认订单界面
	 * @param 跳转 activity
	 */ 
    private void gotoPayConfirm(){
    	if(commodityInfo == null) return;
    	
    	if(commodityInfo.getInventory() <1){
			ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
			return;
		}
    	
		commodityInfo.setImageUrl(orderInfo.getProductPictureUrl());
		commodityInfo.setBuyType(orderInfo.getOrderType());
		commodityInfo.setCustomId(orderInfo.getCustomId());
		commodityInfo.setPreviewUrl(orderInfo.getHtmlFiveUrl());
		commodityInfo.setOrderNum(1);
		
		if(commodityInfo.getIsAnniversary() == 1){
			JumpActivityManager.getInstance().toActivity(mActivity,
					OrderConfirmActivity.class, Constant.Extra.COMMDITYINFO,
					commodityInfo);
		}else{
			OrderWindows.getInstance().show(commodityInfo, new IOrderCallBack() {
				@Override
				public void response(Object object) {

					commodityInfo = (CommodityInfo) object;
					JumpActivityManager.getInstance().toActivity(mActivity,
							OrderConfirmActivity.class, Constant.Extra.COMMDITYINFO,
							commodityInfo);
				}
			});// 弹出数量选择对话框
		}
    }
    
    /** 
	* 跳转到支付订单界面
	 * @param 跳转 activity
	 */ 
    private void gotoPayNow(){
//    	JumpActivityManager.getInstance().toActivity(mActivity,
//				OrderPayActivity.class, Constant.Extra.ORDER_INFO,
//					orderInfo);
    	Intent intent = new Intent();
    	intent.putExtra(Constant.Extra.ORDER_INFO, orderInfo);
    	intent.putExtra(Constant.Extra.GOTO_PAY, true);
    	intent.setClass(mActivity, OrderPayActivity.class);
    	startActivity(intent);
    }
    
    /** 
   	* 跳转到评价晒单界面
   	 * @param 跳转 activity
   	 */ 
   private void gotoAppraiseShow(){
		CommodityAppraiseCenterInfo appraiseCenterInfo = new CommodityAppraiseCenterInfo();
	   	appraiseCenterInfo.setId(orderInfo.getId());
	   	appraiseCenterInfo.setImageUrl(orderInfo.getProductPictureUrl());
		Intent intent = new Intent();
	   	intent.putExtra(Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
	   	intent.setClass(this,AppraiseShowActivity.class);
	   	startActivity(intent);
   }
       
    /** 
	* 跳转到查看物流界面
	 * @param 跳转 activity
	 */ 
    private void gotoExpressView(){
    	Intent intent = new Intent();
    	intent.putExtra(Constant.Extra.ORDER_INFO, orderInfo);
    	intent.setClass(this,ExpressViewActivity.class);
    	startActivity(intent);
    }
    
    /** 
	 * 调用拨号功能 
	 * @param phone 电话号码 
	 */  
	private void call() {  
		
		if(orderInfo.getCompanyPhone() == null || orderInfo.getCompanyPhone().length() <= 0)
			return;
		
	    Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+orderInfo.getCompanyPhone()));  
	    startActivity(intent);  
	} 
	
	 /** 
		* 复制运单编号
		 * @param copy 
		 */ 
	public void copy() {
		
		if(customization_express_number.getText().toString().trim() == null ||
				customization_express_number.getText().toString().trim().length() <= 0){
			ScreenUtil.showToast(ContextUtil.getString(R.string.order_ex_number));
			return;
		}
		
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(customization_express_number.getText().toString().trim());
        ScreenUtil.showToast(ContextUtil.getString(R.string.order_expressnumber_copyok));
    }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		super.updateState(notifyTo, notifyFrom, msg);
		
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(OrderCustCancelActivity.class)) {
				orderInfo.setState("CANCELED");
				refreshView();//接到通知更新UI
			}
			else if (notifyFrom.equals(RedBagOrderManager.class)) {
				try {
					String redbag = (String)msg;
					orderInfo.setRedbag(redbag);
					refreshView();//接到红包领取通知更新UI
				} catch (Exception e) {
				}
			}else if (notifyFrom.equals(AppraiseShowActivity.class)) {
				try {
					orderInfo.setCommentType(0);
					refreshView();
				} catch (Exception e) {
				}
			}
		}
	}
}
