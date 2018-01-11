package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressReceiptInfo;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.AddressManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: OrderConfirmActivity
 * @Description: TODO(确认订单)
 * @author HuZhiyin
 * @date 2017-8-10 上午11:39:12
 * 
 */
public class OrderConfirmActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.order_confirm);
	public String pageCode = "order_confirm";
	@ViewInject(R.id.commodity_iv)
	private ImageView commodity_iv;

	@ViewInject(R.id.name_tv)
	private TextView name_tv;

	@ViewInject(R.id.discountPrice_tv)
	private TextView discountPrice_tv;

	@ViewInject(R.id.comm_sun_tv)
	private TextView comm_sun_tv;

	@ViewInject(R.id.buy_tv)
	private TextView buy_tv;

	@ViewInject(R.id.customer_name_tv)
	private TextView customer_name_tv;

	@ViewInject(R.id.phone_tv)
	private TextView phone_tv;

	@ViewInject(R.id.default_tv)
	private TextView default_tv;

	@ViewInject(R.id.add_tv)
	private TextView add_tv;

	@ViewInject(R.id.addr_null_tv)
	private TextView addr_null_tv;

	@ViewInject(R.id.remark_et)
	private EditText remark_et;

	@ViewInject(R.id.add_null_rl)
	private RelativeLayout add_null_rl;

	@ViewInject(R.id.add_rl)
	private RelativeLayout add_rl;

	/** 商品信息 */
	private CommodityInfo commodityInfo;
	/** 总价 */
	private double totalPrice;
	/** 收货地址 */
	private AddressReceiptInfo addressReceiptInfo;

	@SuppressWarnings("unused")
	private final static int REQUEST_ADDR = 1001;

	@Override
	protected boolean isNotAddTitle() {
		return false;
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_order_confirm;
	}

	@Override
	protected void init() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);// 防止弹出的软键盘将底部的按钮往上弹
		headView.setTitleStr(ContextUtil.getString(R.string.order_confirm));
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		Constant.isFromPage.isOrderList = getIntent().getBooleanExtra(Constant.Extra.GOTO_PAY_AGAIN, false);
		initView();
		getAddr();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeAddr();
	}

	/** 修改地址 */
	private void changeAddr() {
		if (AddressManager.getInstance().isAddrChange()) {
			addressReceiptInfo = AddressManager.getInstance().getAddr();
			refreshAddr();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AddressManager.getInstance().clean();//清空
	}

	private void initView() {
		add_null_rl.setVisibility(View.VISIBLE);
		add_rl.setVisibility(View.GONE);
		if (commodityInfo != null) {
			totalPrice = commodityInfo.getPrice() * commodityInfo.getOrderNum();
			GlideImgUitl.glideLoaderNoAnimation(ContextUtil.getContext(), commodityInfo.getImageUrl(), commodity_iv, 2);
			name_tv.setText(commodityInfo.getName());
			discountPrice_tv.setText(
					ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(commodityInfo.getPrice()));
			comm_sun_tv.setText("x" + commodityInfo.getOrderNum());
			buy_tv.setText(ContextUtil.getString(R.string.pay_real) + ":" + ContextUtil.getString(R.string.rmb)
					+ DataUtil.double2pointString(totalPrice));
		}
	}

	@OnClick(value = { R.id.add_rl, R.id.submit_order_tv, R.id.add_null_rl })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.add_rl:
			gotoAddr();
			break;
		case R.id.add_null_rl:
			gotoAddr();
			break;
		case R.id.submit_order_tv:
			postOrder();
			break;
		}
	}

	/** 跳转到选择地址界面 */
	private void gotoAddr() {
		toActivity(AddressActivity.class, Constant.Extra.MAILING_ADDRESS, addressReceiptInfo);
	}

	/** 生成订单 */
	private void postOrder() {
		if (addressReceiptInfo == null || commodityInfo == null) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.tip_addr_input));
			return;
		}
		
		if(commodityInfo.getInventory() <1){
			ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_null));
			return;
		}
		
		if(commodityInfo.getInventory() >=1 && commodityInfo.getInventory() <commodityInfo.getOrderNum()){
			ScreenUtil.showToast(ContextUtil.getString(R.string.commdity_number_less)+commodityInfo.getInventory());
			return;
		}
		
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserId()); // 用户id
		map.put("addressId", addressReceiptInfo.getAddId());// 地址id
		map.put("productId", commodityInfo.getId());// 产品id
		map.put("remark", remark_et.getText().toString().trim());// 备注
		map.put("number", commodityInfo.getOrderNum()); // 数量
		map.put("orderType", commodityInfo.getBuyType());// 订单类型,0表示直接购买，1表示定制
		map.put("customId", commodityInfo.getCustomId());// 商品定制的id。
		map.put("htmlFiveUrl", commodityInfo.getPreviewUrl());// 视频定制URL。
		String URL = HttpURL.URL1 + HttpURL.SUBMINT_ORDER;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<OrderInfo> info = JsonUtil.fromJson(arg0.result, OrderInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					OrderInfo orderNoInfo = info.getBody();
					orderNoInfo.setReceiverAddrDetail(addressReceiptInfo.getDetailAddr());
					orderNoInfo.setReceiverAddr(addressReceiptInfo.getDistrict());

					Intent intent = new Intent();
					intent.putExtra(Constant.Extra.ISFROM_ORDERCONFIRM, true);
					intent.putExtra(Constant.Extra.ORDER_INFO, orderNoInfo);
					intent.setClass(OrderConfirmActivity.this, OrderPayActivity.class);
					startActivity(intent);
					ActivitiesManager.getInstance().finishActivity(MyCustomizationTobuyActivity.class);
					finish();  //modify by Huangfeifei 2017-10-16
				}else if (info != null && HttpCode.FAIL.equals(info.getHttpCode())) {
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
					else
						ScreenUtil.showToast(ContextUtil.getString(R.string.order_fail));// 订单生产失败
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.order_fail));// 订单生产失败
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));// 订单生产失败
			}
		});
	}

	/** 获取默认地址信息 */
	private void getAddr() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId()); // 用户id
		String URL = HttpURL.URL1 + HttpURL.DEFAULT_ADDRESS;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissProgressDialog();
				// TODO Auto-generated method stub
				ResponseJsonInfo<AddressReceiptInfo> info = JsonUtil.fromJson(arg0.result, AddressReceiptInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					addressReceiptInfo = info.getBody();
					AddressManager.getInstance().saveAddr(addressReceiptInfo);
					refreshAddr();
				} else {
					refreshAddr();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				refreshAddr();
			}
		});
	}

	/** 刷新收货地址 */
	private void refreshAddr() {
		if (addressReceiptInfo == null) {
			add_null_rl.setVisibility(View.VISIBLE);
			add_rl.setVisibility(View.GONE);
		} else {
			add_rl.setVisibility(View.VISIBLE);
			add_null_rl.setVisibility(View.GONE);
			customer_name_tv.setText(addressReceiptInfo.getReceiver());
			phone_tv.setText(addressReceiptInfo.getReceiverPhonePhone());
			add_tv.setText(addressReceiptInfo.getDistrict() + addressReceiptInfo.getDetailAddr());
			if (addressReceiptInfo.getIsDefault() == 0) {// 是否设为默认地址，0代表否，1代表是
				default_tv.setVisibility(View.GONE);
			} else {
				default_tv.setVisibility(View.VISIBLE);
			}
		}
	}
}
