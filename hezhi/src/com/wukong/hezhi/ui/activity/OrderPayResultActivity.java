package com.wukong.hezhi.ui.activity;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.ContextUtil;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName: OrderPayResultActivity
 * @Description: TODO(付款结果页面)
 * @author HuZhiyin
 * @date 2017-8-11 上午9:53:50
 * 
 */
public class OrderPayResultActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.order_pay_result);
	public String pageCode = "order_pay_result";

	@ViewInject(R.id.pay_result_iv)
	private ImageView pay_result_iv;

	@ViewInject(R.id.pay_result_tv)
	private TextView pay_result_tv;

	@ViewInject(R.id.left_tv)
	private TextView left_tv;

	@ViewInject(R.id.right_tv)
	private TextView right_tv;

	@ViewInject(R.id.pay_resultinfo_tv)
	private TextView pay_resultinfo_tv;

	/** 支付成功 */
	private boolean paySucess = true;
	/** 订单编号 */
	private String orderNum = "";

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_pay_result;
	}

	@Override
	protected void init() {
		setSwipeBackEnable(false);// 禁止侧滑消除
		headView.setTitleStr(ContextUtil.getString(R.string.order_pay_result));
		headView.setLeftTitleText(null, -1);// 隐藏左侧的图标
		initView();
	}

	private void initView() {
		paySucess = (Boolean) getIntent().getSerializableExtra(Constant.Extra.PAY_RESULT);
		orderNum = getIntent().getStringExtra(Constant.Extra.ORDER_NO);
		
		SpannableString spannableString = new SpannableString(ContextUtil.getString(R.string.fail_pay_info));
		spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F74D4D")), 5, 9,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		pay_resultinfo_tv.setText(spannableString);

		if (paySucess) {// 支付成功
			pay_result_iv.setImageDrawable(ContextUtil.getDrawable(R.drawable.icon_payresult_success));
			pay_result_tv.setText(ContextUtil.getString(R.string.sucess_pay2));
			right_tv.setText(ContextUtil.getString(R.string.custom_go_on));
//			headView.setRightTitleText(ContextUtil.getString(R.string.text_complete));
			headView.setRihgtTitleText(null, -1);
			pay_resultinfo_tv.setVisibility(View.GONE);
		} else {// 支付失败
			pay_result_iv.setImageDrawable(ContextUtil.getDrawable(R.drawable.icon_payresult_fail));
			pay_result_tv.setText(ContextUtil.getString(R.string.fail_pay2));
			right_tv.setText(ContextUtil.getString(R.string.pay_again));
			headView.setRihgtTitleText("");
			pay_resultinfo_tv.setVisibility(View.VISIBLE);
		}

	}

	@OnClick(value = { R.id.header_right, R.id.left_tv, R.id.right_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			handleRight();
			break;
		case R.id.left_tv:
			handleLeft();
			break;
		case R.id.right_tv:
			handleRight();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			handleRight();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 右侧按钮根据不同状态做相应的处理 */
	private void handleRight() {
		if (paySucess) {// 如果付款成，跳转到继续定制
			toCommodityActivity();
		} else {// 付款失败，则跳转继续付款
			finish();
		}
	}

	/** 跳转至主页，销毁此页面和主页之间的所有页面，并告诉主页跳转到选择商品页面 */
	private void toCommodityActivity() {
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		finish();
//		ObserveManager.getInstance().notifyState(MainActivity.class, OrderPayResultActivity.class,
//				Constant.JumpToAc.ACTIVITY_COMMODITY);// 通知主界面自动跳转到选择商品页面
		ActivitiesManager.getInstance().finishActivity(OrderPayActivity.class);
		finish();  //modify by Huangfeifei 2017-10-16
	}

	/** 跳转至主页，销毁此页面和主页之间的所有页面，并告诉主页跳转到定制订单页面 */
	private void handleLeft() {
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		finish();
//		ObserveManager.getInstance().notifyState(MainActivity.class, OrderPayResultActivity.class,
//				Constant.JumpToAc.ACTIVITY_ORDERCUST);// 通知主界面自动跳转到定制订单页面
		if(Constant.isFromPage.isOrderDetailToCommDetail){
			Constant.isFromPage.isOrderDetailToCommDetail = false;
		}else{
			ActivitiesManager.getInstance().finishActivity(OrderCustDetailActivity.class);
		}
		ActivitiesManager.getInstance().finishActivity(OrderPayActivity.class);
		toActivity(OrderCustDetailActivity.class,Constant.Extra.ORDER_NO,orderNum);
		finish();  //modify by Huangfeifei 2017-10-16
	}
}
