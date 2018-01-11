package com.wukong.hezhi.ui.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.LogisticsFragmentAdapter;
import com.wukong.hezhi.bean.ResponseJsonArrInfo;
import com.wukong.hezhi.bean.LogisticsInfo;
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.http.HttpURLUtil;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.ui.activity.TraceActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: LogisticsFragment
 * @Description: TODO(物流信息)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:53:49
 * 
 */
public class LogisticsFragment extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.logstics);
	public String pageCode = "logstics";
	@ViewInject(R.id.lv_scroll)
	private ListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	private ProductInfo productInfo;

	private LogisticsFragmentAdapter adapter;
	private ArrayList<LogisticsInfo> logisticsInfos = new ArrayList<LogisticsInfo>();// 物流信息

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_logistics,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		initData();
		return rootView;
	}

	public void initView() {
		productInfo = ((TraceActivity) getActivity()).getProductInfo();
		if (productInfo == null) {
			return;
		}
		
		adapter = new LogisticsFragmentAdapter(logisticsInfos, lv_scroll,
				getActivity());
		lv_scroll.addHeaderView(ScreenUtil
				.inflate(R.layout.layout_logistics_header));
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
		// FragmentManager fm = getChildFragmentManager();
		// FragmentTransaction transaction = fm.beginTransaction();
		// MapViewFragment mapViewFragment = new MapViewFragment();
		// transaction.replace(R.id.map_fl, mapViewFragment);
		// transaction.commitAllowingStateLoss();
	}

	public void initData() {

		/***
		 * 物流信息查询
		 */
		final String LOGISTICS_URL = HttpURL.URL
				+ HttpURL.QUERY_LOGISTICS
				+ HttpURLUtil.getURLParameter(NFCManager.authCode,
						NFCManager.uid);
		HttpManager.getInstance().get(LOGISTICS_URL, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				LogUtil.d(LOGISTICS_URL);
				LogUtil.d(json);
				if (!TextUtils.isEmpty(json)) {
					JSONObject resultJson = null;
					String httpcode = null;
					String body = null;
					try {
						resultJson = new JSONObject(json);
						httpcode = resultJson.getString("httpCode");
						body = resultJson.getString("body");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					try {
						if (httpcode.equals(HttpCode.SUCESS)) {
							ResponseJsonArrInfo<LogisticsInfo> commonJsons = JsonUtil
									.fromJsonArr(responseInfo.result,
											LogisticsInfo.class);
							if (commonJsons != null
									&& commonJsons.getBody().size() > 0) {
								ArrayList<LogisticsInfo> infos = commonJsons
										.getBody();
								// adapter = new
								// LogisticsFragmentAdapter(logisticsInfos,
								// lv_scroll, getActivity());
								// lv_scroll.addHeaderView(ScreenUtil.inflate(R.layout.layout_logistics_header));
								// lv_scroll.setAdapter(adapter);
								logisticsInfos.addAll(infos);
								adapter.notifyDataSetChanged();
							}
						} else if (httpcode.equals(HttpCode.FAIL)) {
							ScreenUtil.showToast(ContextUtil
									.getString(R.string.query_failed));
						}
					} catch (Exception e) {
						// TODO: handle exception
						ScreenUtil.showToast(ContextUtil
								.getString(R.string.server_return_empty));
					}

				} else {
					ScreenUtil.showToast(ContextUtil
							.getString(R.string.server_return_empty));
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.d(msg);
				ScreenUtil.showToast(msg);
			}
		});
	}
}
