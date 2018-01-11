package com.wukong.hezhi.ui.activity;

import java.util.Collections;
import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.KDNiaoInfoAdapter;
import com.wukong.hezhi.bean.KDNiaoInfo;
import com.wukong.hezhi.bean.KDNiaoInfo.Traces;
import com.wukong.hezhi.manager.KdniaoManager;
import com.wukong.hezhi.ui.view.TitlePopup;
import com.wukong.hezhi.ui.view.TitlePopup.ActionItem;
import com.wukong.hezhi.ui.view.TitlePopup.OnItemOnClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ThreadUtil;

/**
 * 
 * @ClassName: ExpressQueryActivity
 * @Description: TODO(快递查询界面)
 * @author HuZhiyin
 * @date 2017-2-20 上午10:11:11
 * 
 */
public class ExpressQueryActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.express_query);
	public String pageCode = "express_query";
	/** 公司代号 */
	private String companyCode;
	/** 快递单号 */
	private String orderNo;
	/** 定义标题栏弹窗按钮 */
	private TitlePopup titlePopup;

	@ViewInject(R.id.company_ll)
	private LinearLayout company_ll;

	@ViewInject(R.id.company_tv)
	private TextView company_tv;

	@ViewInject(R.id.oddnumber_et)
	private EditText oddnumber_et;

	@ViewInject(R.id.qurrey_tv)
	private TextView qurrey_tv;

	@ViewInject(R.id.lv_scroll)
	private ListView lv_scroll;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_receive;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.express_query));
		initPopupWindow();
	}

	@OnClick(value = { R.id.header, R.id.company_ll, R.id.qurrey_tv })
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.company_ll:
			titlePopup.show((View) v);

			break;

		case R.id.qurrey_tv:
			if (TextUtils.isEmpty(companyCode)) {
				ScreenUtil.showToast("请选择快递公司");
				return;
			}
			orderNo = oddnumber_et.getText().toString().trim();
			if (TextUtils.isEmpty(orderNo)) {
				ScreenUtil.showToast("请输入快递单号");
				return;
			}
			getData();
			break;
		}
	}

	private void getData() {
		
		if(TextUtils.isEmpty(companyCode) || TextUtils.isEmpty(orderNo)){
			ScreenUtil.showToast(getString(R.string.express_info_error));
			return;
		}
		
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					KdniaoManager api = KdniaoManager.getInstance();
					String result = api.getOrderTracesByJson(companyCode, orderNo);
					if (!result.endsWith("}")) {//快递鸟api返回的json缺少最后一个括号，未搞清原因。暂且做此处理。
						result = result + "}";
					}
					LogUtil.i(result);
					dismissProgressDialog();
					KDNiaoInfo info = (KDNiaoInfo) JsonUtil.parseJson2Obj(result, KDNiaoInfo.class);
					if (info != null && info.getTraces() != null && info.getTraces().size() > 0) {
						List<Traces> traces = info.getTraces();
						Collections.reverse(traces); // 倒序排列
						adapter = new KDNiaoInfoAdapter(traces, lv_scroll, ExpressQueryActivity.this);
						ThreadUtil.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub

								lv_scroll.setAdapter(adapter);
							}
						});
					} else {
						ScreenUtil.showToast(getString(R.string.express_view_isnull));
					}

				} catch (Exception e) {
					ScreenUtil.showToast(getString(R.string.express_view_isfail));
				}
			}
		}).start();
	}

	private KDNiaoInfoAdapter adapter;

	private void initPopupWindow() {
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 给标题栏弹窗添加子类 (实例化一个内部类：titlePopup.new ActionItem(this,
		// "继续跟进",R.drawable.mm_title_btn_compose_normal);)
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.zhongtong)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.yuantong)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.shentong)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.shunfeng)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.tiantian)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.jd)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.youzheng)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.yunda)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.zhaijisong)));
		titlePopup.addAction(titlePopup.new ActionItem(null, "EMS"));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.amazon)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.baishi)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.guotong)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.uc)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.debang)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.fast)));
		titlePopup.addAction(titlePopup.new ActionItem(null, ContextUtil.getString(R.string.quanfeng)));
		titlePopup.setItemOnClickListener(new OnItemOnClickListener() {
			@Override
			public void onItemClick(ActionItem item, int position) {
				company_tv.setText(item.mTitle.toString());
				switch (position) {
				case 0:
					companyCode = "ZTO";
					break;
				case 1:
					companyCode = "YTO";
					break;
				case 2:
					companyCode = "STO";
					break;
				case 3:
					companyCode = "SF";
					break;
				case 4:
					companyCode = "HHTT";
					break;
				case 5:
					companyCode = "JD";
					break;
				case 6:
					companyCode = "YZPY";
					break;
				case 7:
					companyCode = "YD";
					break;
				case 8:
					companyCode = "ZJS";
					break;
				case 9:
					companyCode = "EMS";
					break;
				case 10:
					companyCode = "AMAZON";
					break;
				case 11:
					companyCode = "HTKY";
					break;
				case 12:
					companyCode = "GTO";
					break;
				case 13:
					companyCode = "UC";
					break;
				case 14:
					companyCode = "DBL";
				case 15:
					companyCode = "FAST";
				case 16:
					companyCode = "QFKD";
					break;
				}
			}
		});
	}

}
