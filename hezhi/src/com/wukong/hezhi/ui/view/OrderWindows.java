package com.wukong.hezhi.ui.view;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @ClassName: OrderWindows
 * @Description: TODO(订单弹框)
 * @author HuZhiyin
 * @date 2017-8-11 上午11:33:51
 * 
 */
public class OrderWindows extends PopupWindow implements OnClickListener {
	private ImageView commodity_iv;
	private TextView name_tv, discountPrice_tv;
	private EditText sum_et;
	private ImageView minus_iv, plus_iv;
	private TextView money_tv, submit_tv;
	private LinearLayout layout_body;

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 订单的件数 ，默认一件 */
	private int sum = 1;
	/** 单价 */
	private double price = 1;
	/** 总价 */
	private double totalPrice;

	private static class Holder {
		private static final OrderWindows SINGLETON = new OrderWindows();
	}

	/**
	 * 单一实例
	 */
	public static OrderWindows getInstance() {
		return Holder.SINGLETON;
	}

	@SuppressWarnings("deprecation")
	private OrderWindows() {
		View view = View.inflate(ContextUtil.getContext(), R.layout.layout_popupwindow_order, null);
		this.setAnimationStyle(R.style.popwindow_anim_buttomfromparent_style);
		initView(view);
		setListener();
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(view);
	}

	private void initView(View view) {
		commodity_iv = (ImageView) view.findViewById(R.id.commodity_iv);

		name_tv = (TextView) view.findViewById(R.id.name_tv);
		discountPrice_tv = (TextView) view.findViewById(R.id.discountPrice_tv);
		sum_et = (EditText) view.findViewById(R.id.sum_et);

		minus_iv = (ImageView) view.findViewById(R.id.minus_iv);
		plus_iv = (ImageView) view.findViewById(R.id.plus_iv);

		money_tv = (TextView) view.findViewById(R.id.money_tv);
		submit_tv = (TextView) view.findViewById(R.id.submit_tv);

		layout_body = (LinearLayout) view.findViewById(R.id.layout_body);
	}

	private class MyTouchListener implements OnTouchListener {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				OnClick = true;
				Thread t = new Thread() {
					public void run() {
						while (OnClick) {
							try {
								Thread.sleep(TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							downTime++;
							if (downTime > 5) {// 大于5表示长按。
								switch (v.getId()) {
								case R.id.minus_iv:
									if (sum > 1) {
										sum--;
									}
									handler.sendEmptyMessage(PLUS);
									break;
								case R.id.plus_iv:
									if (sum < 200) {
										sum++;
									}
									handler.sendEmptyMessage(MINUS);
									break;
								}
							}
						}
					}
				};
				t.start();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				OnClick = false;
				downTime = 0;
			}
			return false;
		}

	}

	private int downTime = 0;
	private boolean OnClick = false;
	private final int PLUS = 1;
	private final int MINUS = 2;
	private final int TIME = 100;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PLUS:
				refreshPrice();
				break;
			case MINUS:
				refreshPrice();
				break;
			}
		}
	};

	private void setListener() {
		sum_et.addTextChangedListener(new MyTextWatcher());
		sum_et.setFocusable(true);
		sum_et.setSelectAllOnFocus(true);

		minus_iv.setOnClickListener(this);
		plus_iv.setOnClickListener(this);

		minus_iv.setOnTouchListener(new MyTouchListener());
		plus_iv.setOnTouchListener(new MyTouchListener());

		submit_tv.setOnClickListener(this);
		layout_body.setOnClickListener(this);
		
		sum_et.clearFocus();
		sum_et.setFocusableInTouchMode(false);
		sum_et.setOnTouchListener(new View.OnTouchListener() {
	      @SuppressLint("ClickableViewAccessibility")
		@Override
	      public boolean onTouch(View view, MotionEvent motionEvent) {
	        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
	        	sum_et.setFocusableInTouchMode(true);
	        	sum_et.requestFocus();
	        	sum_et.selectAll();
	        }else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
	        	sum_et.clearFocus();
	        	sum_et.setFocusableInTouchMode(false);

			}
	        return false;
	      }
	    });

	}

	private class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			try {
				sum = Integer.parseInt(s.toString());
			} catch (Exception e) {
				sum = 0;
			} finally {
				if (sum < 1) {
					showToast(ContextUtil.getString(R.string.commdity_number_over));
					sum_et.setText(1 + "");
					sum_et.setSelection(sum_et.getText().length());
					sum = 1;
					return;
				} else if (sum > 200) {
					showToast(ContextUtil.getString(R.string.commdity_number_over));
					sum_et.setText(200 + "");
					sum_et.setSelection(sum_et.getText().length());
					sum = 200;
					return;
				}else if(sum > commodityInfo.getInventory()){
					showToast(ContextUtil.getString(R.string.commdity_number_less)
							+commodityInfo.getInventory());
					sum_et.setText(commodityInfo.getInventory() + "");
					sum_et.setSelection(sum_et.getText().length());
					sum = commodityInfo.getInventory();
					return;
				}
				totalPrice = sum * price;
				money_tv.setText(
						ContextUtil.getString(R.string.count_total) + ":" + DataUtil.double2pointString(totalPrice));
				commodityInfo.setOrderNum(sum);
			}
		}
	}

	public void show(CommodityInfo commodityInfo, IOrderCallBack orderCallBack) {
		if (commodityInfo == null) {
			this.commodityInfo = new CommodityInfo();
		} else {
			this.commodityInfo = commodityInfo;
		}

		if (orderCallBack != null) {
			this.orderCallBack = orderCallBack;
		}
		init();
		final View parent = ActivitiesManager.getInstance().currentActivity().getWindow().getDecorView()
				.findViewById(android.R.id.content);// 获取一个view,popubwindow会用到

		int heght = 0;
		if (Build.VERSION.SDK_INT >= 24) {// 如果是7.0以上的系统，需要获取状态栏的高度。
			heght = ScreenUtil.getStatusBarHeight();
		}
		showAtLocation(parent, Gravity.BOTTOM, 0, heght);
		update();
	}

	private void init() {
		sum = 1;
		price = commodityInfo.getPrice();
		GlideImgUitl.glideLoaderNoAnimation(ContextUtil.getContext(), commodityInfo.getImageUrl(), commodity_iv, 2);
		name_tv.setText(commodityInfo.getName());
		discountPrice_tv
				.setText(ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(commodityInfo.getPrice()));
		money_tv.setText(ContextUtil.getString(R.string.count_total) + ":"
				+ DataUtil.double2pointString(commodityInfo.getPrice()));
		sum_et.setText(sum + "");// 默认是一件
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.minus_iv:

			if (sum > 1) {
				sum--;
			}
			refreshPrice();
			break;
		case R.id.plus_iv:

			if (sum < 200) {
				sum++;
			}
			refreshPrice();
			break;
		case R.id.submit_tv:
			orderCallBack.response(commodityInfo);
			dismiss();
			break;
		case R.id.layout_body:
			dismiss();
			break;
		}
	}

	private void refreshPrice() {
		if (sum < 1) {
			showToast(ContextUtil.getString(R.string.commdity_number_over));
			sum = 1;
			return;
		}
		else if(sum > 200){
			showToast(ContextUtil.getString(R.string.commdity_number_over));
			sum = 200;
			return;
		}
		else if(sum > commodityInfo.getInventory()){
			showToast(ContextUtil.getString(R.string.commdity_number_less)
					+commodityInfo.getInventory());
			sum = commodityInfo.getInventory();
			return;
		}
		
		showToast("x" + sum);
		sum_et.setText(sum + "");
		totalPrice = sum * price;
		money_tv.setText(ContextUtil.getString(R.string.count_total) + ":" + DataUtil.double2pointString(totalPrice));
		commodityInfo.setOrderNum(sum);
	}

	public static Toast mToast;
	public static TextView view;

	/** 单例吐司，在主线程显示 */
	private void showToast(final String msg) {

		if (mToast == null) {
			Context mContext = ContextUtil.getContext();
			view = new TextView(mContext);
			view.setBackground(mContext.getResources().getDrawable(R.drawable.shape_toast2));
			view.setTextSize(26);
			view.setPadding(80, 80, 80, 80);
			view.setTextColor(mContext.getResources().getColor(R.color.white));
			mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		}
		view.setText(msg);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setView(view);
		mToast.show();
	}

	/** 回调接口 */
	public interface IOrderCallBack {
		void response(Object object);
	}

	private IOrderCallBack orderCallBack = new IOrderCallBack() {

		@Override
		public void response(Object object) {
			// TODO Auto-generated method stub

		}
	};

}