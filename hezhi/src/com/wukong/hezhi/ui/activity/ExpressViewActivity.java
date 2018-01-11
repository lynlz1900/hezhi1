package com.wukong.hezhi.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.ExpressViewAdapter;
import com.wukong.hezhi.bean.KDNiaoInfo;
import com.wukong.hezhi.bean.KDNiaoInfo.Traces;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: ExpressCheckActivity
 * @Description: TODO(物流查看界面)
 * @author HuangFeiFei
 * @date 2017-10-11 上午9:58
 * 
 */
public class ExpressViewActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.express_view);
	public String pageCode = "express_view";
	/** 订单信息 */
	private OrderInfo orderInfo;

	@ViewInject(R.id.text_express_company)
	private TextView text_express_company;

	@ViewInject(R.id.text_express_number)
	private TextView text_express_number;

	@ViewInject(R.id.text_express_no)
	private TextView text_express_no;
	
	@ViewInject(R.id.lv_scroll)
	private ListView lv_scroll;

	private ExpressViewAdapter adapter;
	
	private List<Traces> traces;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_express_view;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.express_view));
		
		try {
			orderInfo = (OrderInfo) getIntent().getSerializableExtra(Constant.Extra.ORDER_INFO);
		} catch (Exception e) {
		}
		
//		companyCode="ZTO";
//		orderNo="445978975202";
		if(orderInfo != null && !TextUtils.isEmpty(orderInfo.getLogisticsCompany()))
			text_express_company.setText(orderInfo.getLogisticsCompany());
		if(orderInfo != null && !TextUtils.isEmpty(orderInfo.getLogisticsNumber()))
			text_express_number.setText(orderInfo.getLogisticsNumber());
		
		setAdapter();
		getExpressData();
	}

	private void setAdapter(){
		traces = new ArrayList<Traces>();
		adapter = new ExpressViewAdapter(traces, ExpressViewActivity.this);
		lv_scroll.setAdapter(adapter);
	}
	
	@OnClick(value = { })
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		}
	}

	/** 获取物流数据 */
	private void getExpressData() {
		
		if(orderInfo == null && TextUtils.isEmpty(orderInfo.getLogisticsCompany())
				== TextUtils.isEmpty(orderInfo.getLogisticsNumber())){
			updateUI(false, getString(R.string.express_view_isfail));
			return;
		}
		
		String url = HttpURL.URL1 + HttpURL.ORDER_TRACE_SEARCH;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderInfo.getOrderNumber());
		LogUtil.d(url);
		LogUtil.d(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.i(arg1);
				updateUI(false, getString(R.string.express_view_isfail));
			}

			@SuppressWarnings({"rawtypes" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				traces.clear();
				ResponseJsonInfo info = (ResponseJsonInfo) JsonUtil.parseJson2Obj(arg0.result, ResponseJsonInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					String kdNiaoInfoStr = "";
					KDNiaoInfo kdNiaoInfo = new KDNiaoInfo();
					try {
						kdNiaoInfoStr = (String)info.getBody();
						kdNiaoInfo = (KDNiaoInfo) JsonUtil.parseJson2Obj(kdNiaoInfoStr, KDNiaoInfo.class);
						List<Traces> list = kdNiaoInfo.getTraces();
						Collections.reverse(list); // 倒序排列
						traces.addAll(list);
					} catch (Exception e) {
						updateUI(false, getString(R.string.express_view_isfail));
					}
					
					if(traces == null || traces.size() <1)
						updateUI(false, getString(R.string.express_view_isnull));
					else
						updateUI(true, null);
					
				}else{
					updateUI(false, getString(R.string.express_view_isfail));
				}
			}
		});
	}
	
	/** 更新界面 **/
	private void updateUI(final boolean expressIsHas,final String nothingType){
		ThreadUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissProgressDialog();
				if(expressIsHas){
					lv_scroll.setVisibility(View.VISIBLE);
					text_express_no.setVisibility(View.GONE);
					adapter = new ExpressViewAdapter(traces, ExpressViewActivity.this);
					lv_scroll.setAdapter(adapter);
				}else{
					lv_scroll.setVisibility(View.GONE);
					text_express_no.setVisibility(View.VISIBLE);
					text_express_no.setText(nothingType);
				}
			}
		});
	}
}
