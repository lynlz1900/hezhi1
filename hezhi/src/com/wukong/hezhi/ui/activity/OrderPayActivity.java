package com.wukong.hezhi.ui.activity;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.PayManager;
import com.wukong.hezhi.manager.PayManager.IPayCallBack;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

/***
 * 
 * @ClassName: OrderPayActivity
 * @Description: TODO(支付订单)
 * @author HuZhiyin
 * @date 2017-8-11 上午7:59:05
 * 
 */
public class OrderPayActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.order_pay);
	public String pageCode = "order_pay";
	/** 订单号 */
	private String orderNum;
	/** 支付方式 ,默认微信 */
	private int payWay = PayManager.PAY_WX;
	@ViewInject(R.id.radioGroup)
	private RadioGroup radioGroup;

	@ViewInject(R.id.order_tv)
	private TextView order_tv;

	@ViewInject(R.id.add_tv)
	private TextView add_tv;

	/** 订单信息 */
	private OrderInfo orderInfo;
	/** 来自定制订单去支付功能 */
	private boolean isPayNow = false;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_pay;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		setSwipeBackEnable(false);
		headView.setLeftTitleText("", R.drawable.icon_close1);
		headView.setLeftLis(this);
		headView.setTitleStr(ContextUtil.getString(R.string.order_pay));
		orderInfo = (OrderInfo) getIntent().getSerializableExtra(Constant.Extra.ORDER_INFO);
		isPayNow = (boolean) getIntent().getBooleanExtra(Constant.Extra.GOTO_PAY, false);
		initView();
	}

	private void initView() {

		radioGroup.setOnCheckedChangeListener(new BottomCheckChangeListener());// RadioGroup选中状态改变监听
		if (orderInfo != null) {
			order_tv.setText(ContextUtil.getString(R.string.rmb) + orderInfo.getPriceTotal());
			orderNum = orderInfo.getOrderNumber();
			add_tv.setText((orderInfo.getReceiverAddr() == null ? "" : orderInfo.getReceiverAddr())
					+ (orderInfo.getReceiverAddrDetail() == null ? "" : orderInfo.getReceiverAddrDetail()));
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}

	private class BottomCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.wx_rb:
				payWay = PayManager.PAY_WX;
				break;
			case R.id.zhifubao_rb:
				payWay = PayManager.PAY_ZHIFUBAO;
				break;
			}
		}
	}

	@OnClick(value = { R.id.header_left, R.id.pay_bt })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			showTips();
			break;
		case R.id.pay_bt:
			if (!TextUtils.isEmpty(orderNum)) {
				pay();
			} else {
				ScreenUtil.showToast(ContextUtil.getString(R.string.order_empty));
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showTips();
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 弹出提示是否继续支付对话框 **/
	private void showTips() {
		new CustomAlterDialog.Builder(mActivity).setTitle(ContextUtil.getString(R.string.live_sure))
				.setMsg(ContextUtil.getString(R.string.live_tip))
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {

					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						handle();
					}
				}).setCancelButton(ContextUtil.getString(R.string.cancel), null).build().show();
	}

	/** 跳转至主页，销毁此页面和主页之间的所有页面，并告诉主页跳转到定制订单页面 */
	private void handle() {
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		finish();
//		ObserveManager.getInstance().notifyState(MainActivity.class, OrderPayActivity.class,
//				Constant.JumpToAc.ACTIVITY_ORDERCUST);// 通知主界面自动跳转到定制订单页面
		if(isPayNow){
			finish();
		}
		else if(Constant.isFromPage.isOrderList){
			ObserveManager.getInstance().notifyState(OrderCustFragment.class, OrderPayActivity.class,
					null);// 通知定制订单页面更新数据
			Constant.isFromPage.isOrderList = false;
			finish();
		}
		else{
			if(Constant.isFromPage.isOrderListSearch)
				ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class, OrderPayActivity.class,
						null);// 通知定制订单搜索页面更新数据
			toActivity(OrderCustomActivity.class);
			finish();  //modify by Huangfeifei 2017-10-16
		}
	}

	/** 支付 */
	private void pay() {
		showProgressDialog();
		PayManager.getInstance().pay(mActivity, payWay, orderNum, new IPayCallBack() {

			@Override
			public void sucess(String sucess) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				gotoPayResult(true);
			}

			@Override
			public void fail(String fail) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				gotoPayResult(false);
			}
		});
	}

	/** 跳转到支付结果界面
	 * date:modify by Huangfeifei 2017-10-17
	 *  */
	private void gotoPayResult(boolean isSucess){
		
		ObserveManager.getInstance().notifyState(OrderCustFragment.class, OrderPayActivity.class,
				null);// 通知定制订单页面更新数据
		if(Constant.isFromPage.isOrderListSearch)
			ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class, OrderPayActivity.class,
					null);// 通知定制订单搜索页面更新数据
		
		Intent intent = new Intent();
		intent.putExtra(Constant.Extra.PAY_RESULT, isSucess);
		intent.putExtra(Constant.Extra.ORDER_NO, orderNum);
		intent.setClass(this, OrderPayResultActivity.class);
		startActivity(intent);
	}
}
