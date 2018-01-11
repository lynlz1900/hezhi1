package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
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
import com.wukong.hezhi.ui.activity.AppraiseShowActivity;
import com.wukong.hezhi.ui.activity.ExpressViewActivity;
import com.wukong.hezhi.ui.activity.OrderConfirmActivity;
import com.wukong.hezhi.ui.activity.OrderCustDetailActivity;
import com.wukong.hezhi.ui.activity.OrderCustomActivity;
import com.wukong.hezhi.ui.activity.OrderPayActivity;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.OrderWindows;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.OrderWindows.IOrderCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 定制订单ListView的适配器
 *
 * @ClassName: MyCustomizationOrderAdapter
 * @Description: TODO(定制订单ListView的适配器类)
 * @author HuangFeiFei
 * @date 2017-8-7 上午10：12
 *
 */
public class MyCustomizationOrderAdapter extends BaseAdapter{
	
    private List<OrderInfo> list;
    private Activity mActivity;
    /**订单是否有红包**/
	private int redPackStatus = 0;
	
    public MyCustomizationOrderAdapter(List<OrderInfo> list,Activity mActivity){
    	super();
        if(list==null){
            this.list = new ArrayList<OrderInfo>();
        }else{
            this.list = list;
        }
        this.mActivity = mActivity;
    }
    @Override
    public int getCount() {
    	return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null ) {
            convertView =  ScreenUtil.inflate(R.layout.item_list_my_customizationorder);
            vh = new ViewHolder();
            vh.customization_name_short=(TextView)convertView.findViewById(R.id.customization_name_short);
            vh.customization_name_status=(TextView)convertView.findViewById(R.id.customization_name_status);
            vh.customization_name_long=(TextView)convertView.findViewById(R.id.customization_name_long);
            vh.customization_price=(TextView)convertView.findViewById(R.id.customization_price);
            vh.customization_price_info=(TextView)convertView.findViewById(R.id.customization_price_info);
            vh.btn_customiation_right01=(TextView)convertView.findViewById(R.id.btn_customiation_right01);
            vh.btn_customiation_right02=(TextView)convertView.findViewById(R.id.btn_customiation_right02);
            vh.btn_customiation_right03=(TextView)convertView.findViewById(R.id.btn_customiation_right03);
            vh.redbag_iv=(ImageView)convertView.findViewById(R.id.redbag_iv);
            vh.imageview_customization_delete=(ImageView)convertView.findViewById(R.id.imageview_customization_delete);
            vh.imageview_customization_logo=(ImageView)convertView.findViewById(R.id.imageview_customization_logo);
            vh.layout_customization = (LinearLayout)convertView.findViewById(R.id.layout_customization);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder)convertView.getTag();
        }
        
        final OrderInfo orderInfo = list.get(position);
        int status = 0;
        if(orderInfo != null)
        	status = orderInfo.getStatus();
        if(status == Constant.OrderType.OBLIGATIONS.ordinal()){//待付款
        	vh.imageview_customization_delete.setVisibility(View.GONE);
        	vh.redbag_iv.setVisibility(View.GONE);
        	vh.btn_customiation_right02.setVisibility(View.GONE);
        	vh.btn_customiation_right03.setVisibility(View.GONE);
        	
        	vh.customization_name_status.setText(ContextUtil.getString(R.string.order_status_obligations));
        	vh.customization_name_status.setTextColor(ContextUtil.getResource().getColor(R.color.red));  
        	vh.btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_to));
        }else if(status == Constant.OrderType.UNDELIVERED.ordinal()){//待发货
        	vh.imageview_customization_delete.setVisibility(View.GONE);
        	vh.redbag_iv.setVisibility(View.GONE);
        	vh.btn_customiation_right02.setVisibility(View.GONE);
        	vh.btn_customiation_right03.setVisibility(View.GONE);
        	
        	vh.customization_name_status.setText(ContextUtil.getString(R.string.order_status_undelivered));
        	vh.customization_name_status.setTextColor(ContextUtil.getResource().getColor(R.color.red));  
        	vh.btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
        }else if(status == Constant.OrderType.UNRECEIVED.ordinal()){//待收货
        	vh.imageview_customization_delete.setVisibility(View.GONE);
        	
        	if(list.get(position).getRedPackStatus() == 1)
        		vh.redbag_iv.setVisibility(View.VISIBLE);
        	else
        		vh.redbag_iv.setVisibility(View.GONE);
        	vh.btn_customiation_right02.setVisibility(View.VISIBLE);
        	vh.btn_customiation_right03.setVisibility(View.VISIBLE);
        	
        	vh.customization_name_status.setText(ContextUtil.getString(R.string.order_status_unreceived));
        	vh.customization_name_status.setTextColor(ContextUtil.getResource().getColor(R.color.red));  
        	vh.btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_complete));
        	vh.btn_customiation_right02.setText(ContextUtil.getString(R.string.order_logistics_view));
        }else if(status == Constant.OrderType.CANCELED.ordinal()){//已取消
        	vh.imageview_customization_delete.setVisibility(View.VISIBLE);
        	vh.redbag_iv.setVisibility(View.GONE);
        	vh.btn_customiation_right02.setVisibility(View.GONE);
        	vh.btn_customiation_right03.setVisibility(View.GONE);
        	
        	vh.customization_name_status.setText(ContextUtil.getString(R.string.order_status_canceled));
        	vh.customization_name_status.setTextColor(ContextUtil.getResource().getColor(R.color.red));  
        	vh.btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
        }else if(status == Constant.OrderType.RECEIVED.ordinal()){//已完成
        	vh.imageview_customization_delete.setVisibility(View.VISIBLE);
        	vh.redbag_iv.setVisibility(View.GONE);
        	if(orderInfo != null && orderInfo.getCommentType() == 1)
        		vh.btn_customiation_right02.setVisibility(View.VISIBLE);
        	else
        		vh.btn_customiation_right02.setVisibility(View.GONE);
        	vh.btn_customiation_right03.setVisibility(View.GONE);
        	
        	vh.customization_name_status.setText(ContextUtil.getString(R.string.order_status_received));
        	vh.customization_name_status.setTextColor(ContextUtil.getResource().getColor(R.color.green));  
        	vh.btn_customiation_right01.setText(ContextUtil.getString(R.string.order_buy_again));
        	vh.btn_customiation_right02.setText(ContextUtil.getString(R.string.appraise_show));
        }
        
        if(orderInfo != null){
	        vh.customization_name_short.setText(orderInfo.getCompanyRealName());
	    	vh.customization_name_long.setText(orderInfo.getProductName());
	    	vh.customization_price.setText(ContextUtil.getString(R.string.money_type)
	    			+DataUtil.double2pointString(orderInfo.getPrice()));
	    	
	    	if(status == Constant.OrderType.OBLIGATIONS.ordinal()){//待付款
	    		vh.customization_price_info.setText("共"+orderInfo.getNumber()+"件商品 , 需付款 : ¥"
	    	    	    +(orderInfo.getPriceTotal()==null?"0.00":orderInfo.getPriceTotal()));
	    	}else{
		    	vh.customization_price_info.setText("共"+orderInfo.getNumber()+"件商品 , 实付款 : ¥"
		    	    +(orderInfo.getPriceTotal()==null?"0.00":orderInfo.getPriceTotal()));
	    	}
	    	
	    	 GlideImgUitl.glideLoader
			 (ContextUtil.getContext(), orderInfo.getProductPictureUrl(), vh.imageview_customization_logo, 2);
//	    	GlideImgUitl.glideLoader(mActivity, orderInfo.getProductPictureUrl(),
//	    			vh.imageview_customization_logo, 2);
        }else{
        	vh.customization_name_short.setText("");
 	    	vh.customization_name_long.setText("");
 	    	vh.customization_price.setText(ContextUtil.getString(R.string.money_type)
 	    			+"0.00");
 	    	vh.customization_price_info.setText("共0件商品 , 实付款 : ¥0.00");
        }
    	
        vh.btn_customiation_right01.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onclickRight(position);
			}
		});
        
        vh.btn_customiation_right02.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onclickMid(position);
			}
		});
        
        vh.btn_customiation_right03.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				onclickLeft(position);
			}
        });
        
        vh.imageview_customization_delete.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				deleteOrReceive(OrderCustDetailActivity.ISDELETE,ContextUtil.getString(R.string.order_isdelete_true),position);
			}
        });
        
        vh.layout_customization.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				
				if(orderInfo == null || TextUtils.isEmpty(orderInfo.getOrderNumber()))
					return;
				
				JumpActivityManager.getInstance().toActivity(mActivity,
						OrderCustDetailActivity.class,Constant.Extra.ORDER_NO,orderInfo.getOrderNumber());
			}
        });
        
        return convertView;
    }
    /**
     * 封装ListView中item控件以优化ListView
     * @author HuangFeiFei
     *
     */
    public static class ViewHolder{
        TextView customization_name_short;
        TextView customization_name_status;
        TextView customization_name_long;
        TextView customization_price;
        TextView customization_price_info;
        TextView btn_customiation_right01;
        TextView btn_customiation_right02;
        TextView btn_customiation_right03;
        ImageView redbag_iv;
        ImageView imageview_customization_delete;
        ImageView imageview_customization_logo;
        LinearLayout layout_customization;
    }
    
  /**底部最右侧按钮事件**/
    private void onclickRight(int position){
    	 final OrderInfo orderInfo = list.get(position);
         int status = -1;
         if(orderInfo != null)
        	 status = orderInfo.getStatus();
    	if(status == Constant.OrderType.OBLIGATIONS.ordinal()){
    		getCommodityInfoDataHttp(position,OrderCustDetailActivity.BUY_NOW);
		}else if(status == Constant.OrderType.UNDELIVERED.ordinal() ||
				status == Constant.OrderType.CANCELED.ordinal() ||
				status == Constant.OrderType.RECEIVED.ordinal()){
			getCommodityInfoDataHttp(position,OrderCustDetailActivity.BUY_AGAIN);
		}else if(status == Constant.OrderType.UNRECEIVED.ordinal()){
			deleteOrReceive(OrderCustDetailActivity.ISRECEIVE, ContextUtil.getString(R.string.order_isreceive_true),position);
		}
    }
    
  /**底部中间按钮事件**/
    private void onclickMid(int position){
    	 final OrderInfo orderInfo = list.get(position);
         int status = -1;
         if(orderInfo != null)
        	 status = orderInfo.getStatus();
    	 if(status == Constant.OrderType.UNRECEIVED.ordinal()){
    		 gotoExpressView(position);
		}else if(status == Constant.OrderType.RECEIVED.ordinal()){
			gotoAppraiseShow(position);
		}
    }
    
  /**底部最左侧按钮事件**/
    private void onclickLeft(int position){
    	 final OrderInfo orderInfo = list.get(position);
         int status = -1;
         if(orderInfo != null)
        	 status = orderInfo.getStatus();
    	 if(status == Constant.OrderType.UNRECEIVED.ordinal()){
    		 getCommodityInfoDataHttp(position,OrderCustDetailActivity.BUY_AGAIN);
			}
    }
    
    /** 
	* 跳转到确认订单界面
	 * @param 跳转 activity
	 */ 
    private void gotoPayConfirm(final int position){
    	if(commodityInfo == null) return;
    	
    	if(commodityInfo.getInventory() <1){
			ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
			return;
		}
    	
		commodityInfo.setImageUrl(list.get(position).getProductPictureUrl());
		commodityInfo.setBuyType(list.get(position).getOrderType());
		commodityInfo.setCustomId(list.get(position).getCustomId());
		commodityInfo.setPreviewUrl(list.get(position).getHtmlFiveUrl());
		commodityInfo.setOrderNum(1);
		
		if(commodityInfo.getIsAnniversary() == 1){
			if(Constant.isFromPage.isOrderListSearch){
				JumpActivityManager.getInstance().toActivity(mActivity,
						OrderConfirmActivity.class, Constant.Extra.COMMDITYINFO,
						commodityInfo);
			}else{
				Intent intent = new Intent();
				intent.putExtra(Constant.Extra.COMMDITYINFO, commodityInfo);
		    	intent.putExtra(Constant.Extra.GOTO_PAY_AGAIN, true);
		    	intent.setClass(mActivity, OrderConfirmActivity.class);
		    	mActivity.startActivity(intent);
			}
		}else{
			OrderWindows.getInstance().show(commodityInfo, new IOrderCallBack() {
				@Override
				public void response(Object object) {

					commodityInfo = (CommodityInfo) object;
					if(Constant.isFromPage.isOrderListSearch){
						JumpActivityManager.getInstance().toActivity(mActivity,
								OrderConfirmActivity.class, Constant.Extra.COMMDITYINFO,
								commodityInfo);
					}else{
						Intent intent = new Intent();
						intent.putExtra(Constant.Extra.COMMDITYINFO, commodityInfo);
				    	intent.putExtra(Constant.Extra.GOTO_PAY_AGAIN, true);
				    	intent.setClass(mActivity, OrderConfirmActivity.class);
				    	mActivity.startActivity(intent);
					}
				}
			});// 弹出数量选择对话框
		}
    }
    
    /** 
	* 跳转到支付订单界面
	 * @param 跳转 activity
	 */ 
    private void gotoPayNow(final int position){
//    	JumpActivityManager.getInstance().toActivity(mActivity,
//				OrderPayActivity.class, Constant.Extra.ORDER_INFO,
//					list.get(position));
    	Intent intent = new Intent();
    	intent.putExtra(Constant.Extra.ORDER_INFO, list.get(position));
    	intent.putExtra(Constant.Extra.GOTO_PAY, true);
    	intent.setClass(mActivity, OrderPayActivity.class);
    	mActivity.startActivity(intent);
    }
    
    /** 
   	* 跳转到评价晒单界面
   	 * @param 跳转 activity
   	 */ 
   private void gotoAppraiseShow(final int position){
	   	if(mActivity == null) return;
	
	   	CommodityAppraiseCenterInfo appraiseCenterInfo = new CommodityAppraiseCenterInfo();
	   	appraiseCenterInfo.setId(list.get(position).getId());
	   	appraiseCenterInfo.setImageUrl(list.get(position).getProductPictureUrl());
		Intent intent = new Intent();
	   	intent.putExtra(Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
	   	intent.setClass(mActivity,AppraiseShowActivity.class);
	   	mActivity.startActivity(intent);
   }
       
    /** 
	* 跳转到查看物流界面
	 * @param 跳转 activity
	 */ 
    private void gotoExpressView(final int position){
    	if(mActivity == null) return;
    	
    	Intent intent = new Intent();
    	intent.putExtra(Constant.Extra.ORDER_INFO, list.get(position));
    	intent.setClass(mActivity,ExpressViewActivity.class);
    	mActivity.startActivity(intent);
    }
    
  /**弹出是否删除订单或确认收货对话框**/
    private void deleteOrReceive(final int type,String msg,final int positon){
		new CustomAlterDialog.Builder(mActivity).setMsg(msg)
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				// TODO Auto-generated method stub
				if(type == OrderCustDetailActivity.ISRECEIVE){
					orderReceivedHttp(positon);
				}
				else if(type == OrderCustDetailActivity.ISDELETE){
					orderDeleteHttp(positon);
				}
			}
		}).build().show();
    }
    
    /**确认收货http接口**/
    private void orderReceivedHttp(final int positon){
    	String url = HttpURL.URL1 + HttpURL.ORDER_RECEIVED;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", list.get(positon).getId());
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings({ "unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo<OrderInfo> info = JsonUtil.fromJson(
						arg0.result, OrderInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					
					OrderInfo orderInfo02 = (OrderInfo)info.getBody();
					
					redPackStatus = list.get(positon).getRedPackStatus();
					
					if(Constant.isFromPage.isOrderListSearch)
						ObserveManager.getInstance().notifyState(OrderCustFragment.class,
								MyCustomizationOrderAdapter.class,
								orderInfo02);// 当在搜索界面时通知订单搜索列表订单确认收货
					
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_receive_success));
				
					if(redPackStatus == 1)
						RedBagOrderManager.getInstance(mActivity).checkRedBag(list.get(positon));//领红包
					
					if(Constant.isFromPage.isOrderListSearch){
						Collections.replaceAll(list, list.get(positon), orderInfo02);
						notifyDataSetChanged();
					}else{
						if(OrderCustomActivity.status.equals("ALL"))
							Collections.replaceAll(list, list.get(positon), orderInfo02);
						else
							list.remove(positon);
						notifyDataSetChanged();
					}
					
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
    private void orderDeleteHttp(final int positon){
    	String url = HttpURL.URL1 + HttpURL.ORDER_DEL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", list.get(positon).getId());
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					
					if(Constant.isFromPage.isOrderListSearch)
						ObserveManager.getInstance().notifyState(OrderCustFragment.class,
								MyCustomizationOrderAdapter.class,
								"isDelete,"+list.get(positon).getId());// 当在搜索界面时通知订单列表订单删除
					
					list.remove(positon);
					notifyDataSetChanged();
					
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_delete_success));;
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
    private void getCommodityInfoDataHttp(final int positon,final int type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", list.get(positon).getProductId());// 产品id
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_DETAIL;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
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
							if(type == OrderCustDetailActivity.BUY_AGAIN){
								if(commodityInfo.getIsAnniversary() == 1){
									CheckIsBought(positon);
								}
								else{
									gotoPayConfirm(positon);
								}
							}else if(type == OrderCustDetailActivity.BUY_NOW){
								gotoPayNow(positon);
							}
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
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}
    
    /** 检测员工是否购买过20周年产品*/
   	private void CheckIsBought(final int positon) {
   		String URL = HttpURL.URL1 + HttpURL.CHECKISBOUGHT;
   		Map<String, Object> map = new HashMap<String, Object>();
   		map.put("productId", commodityInfo.getId());
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
   					gotoPayConfirm(positon);
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
}