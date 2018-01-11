package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressInfo;
import com.wukong.hezhi.bean.CustomFlagInfo;
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.http.HttpURLUtil;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.manager.RedBagNfcManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 
 * @ClassName: DisResultActivity
 * @Description: TODO(鉴真结果页面)
 * @author HuZhiyin
 * @date 2016-9-18 上午9:34:17
 * 
 */
public class DisResultActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.trace_result);
	public String pageCode = "trace_result";

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;

	@ViewInject(R.id.fragment_introduce_iv)
	private ImageView fragment_introduce_iv;

	@ViewInject(R.id.zhigou_iv)
	private ImageView zhigou_iv;

	@ViewInject(R.id.cust_iv)
	private ImageView cust_iv;

	@ViewInject(R.id.detection_result)
	private TextView detection_result;

	@ViewInject(R.id.product_name)
	private TextView product_name;

	@ViewInject(R.id.qurrey_time)
	private TextView qurrey_time;

	@ViewInject(R.id.qurrey_addr)
	private TextView qurrey_addr;

	@ViewInject(R.id.company_tv)
	private TextView company_tv;

	@ViewInject(R.id.qurrey_detail_tv)
	private Button qurrey_detail_tv;

	private ProductInfo productInfo = null;// 产品信息
	private AddressInfo addressInfo;// 地址信息
	private String customizeFlag = null;// 是否可定制的标识
	private int customizeId = 0;// 定制的id
	private String url;// 直购链接

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_dis_result;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.trace_result));
		initAddr();
		getData();
	}

	/** 初始化地址信息 */
	private void initAddr() {
		addressInfo = LocationManager.getInstance().getAdressInfo();
		if (addressInfo == null) {
			addressInfo = new AddressInfo();
		}
	}

	/** 从导航拉取产品信息 */
	private void getData() {
		showProgressDialog();
		String URL = getURL();
		LogUtil.i(URL);
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.i(responseInfo.result);
				dismissProgressDialog();
				ResponseJsonInfo<ProductInfo> info = JsonUtil.fromJson(responseInfo.result, ProductInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					productInfo = info.getBody();
					refreshView(productInfo);// 刷新界面
					checkCustom(productInfo);// 检查是否是定制
					RedBagNfcManager.getInstance(DisResultActivity.this).checkRedBag(productInfo);// 弹红包
				} else {
					empty_ll.setVisibility(View.VISIBLE);// 查询失败，显示空见面
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				dismissProgressDialog();
				empty_ll.setVisibility(View.VISIBLE);// 查询失败，显示空见面
			}

		});
	}

	/** 刷新界面 */
	public void refreshView(ProductInfo productInfo) {
		if (productInfo == null)
			return;
		
		
		NFCManager.epc = productInfo.getMaterialFlowNo();// 这句话做什么用的？？？huzhiyin.2017/10/19
		url = productInfo.getGuideUrl();// 直购链接
		if (TextUtils.isEmpty(url)) {// 如果链接存在，则显示直购按钮
			zhigou_iv.setVisibility(View.GONE);
		} else {
			zhigou_iv.setVisibility(View.VISIBLE);
		}
		
		GlideImgUitl.glideLoaderNoAnimation(mActivity, productInfo.getPicturePath(), fragment_introduce_iv, 2);
		detection_result.setText(getResources().getString(R.string.pv_value) + productInfo.getPvAssessedScore());
		product_name.setText(productInfo.getMaterialName());
		company_tv.setText(productInfo.getCompanyName());
		qurrey_time.setText(DateUtil.getTime());
		qurrey_addr.setText(addressInfo.getDetailAddr() + addressInfo.getBuilding());
		
	}

	@OnClick(value = { R.id.qurrey_detail_tv, R.id.zhigou_iv, R.id.cust_iv })
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.qurrey_detail_tv:// 溯源详情
			toTraceActivity();
			break;
		case R.id.zhigou_iv:// 直购
			toActivity(WebViewActivity.class, Constant.Extra.WEB_URL, url);
			break;
		case R.id.cust_iv:// 定制详情
			toActivity(CommodityInfoActivity.class, Constant.Extra.PRODUCTID, customizeId);
			break;
		}
	}

	/** 跳转溯源 */
	private void toTraceActivity() {
		if (productInfo != null && (Constant.TagAcType.ACTIVATED.toString()).equals(productInfo.getTagStateType())) {// 判断标签是否激活
			toActivity(TraceActivity.class, Constant.Extra.PVRESULTACTIVITY_PRODUCTINFO, productInfo);
		} else {
			ScreenUtil.showToast(ContextUtil.getString(R.string.tag_isnot_valid));
		}
	}

	/** 拼装导航地址 。注意：导航请求接口比较奇葩，此处小心拼接。*/
	private String getURL() {
		String isBreak = null;
		String userId;
		if (UserInfoManager.getInstance().isLogin()) {
			userId = UserInfoManager.getInstance().getUserId() + "," + PackageUtil.getPhoneId();
		} else {
			userId = "-1," + PackageUtil.getPhoneId();
		}
		String companyCode = null;
		String address = addressInfo.getDetailAddr() + addressInfo.getBuilding() + ",(" + addressInfo.getLongitude()
				+ "," + addressInfo.getLatitude() + ")";
		String province = addressInfo.getProvince();
		String city = addressInfo.getCity();
		String logSource = "CONSUMER";

		String URLParameter = HttpURLUtil.getURLParameter(NFCManager.authCode, NFCManager.uid, userId, province, city,
				logSource, companyCode, address, isBreak, NFCManager.INDEX, NFCManager.RANDOM, NFCManager.TOKEN,
				PackageUtil.getPhoneInfo());
		String URL = HttpURL.URL + HttpURL.QUERY_PRODUCT + URLParameter;
		LogUtil.d(URL);
		return URL;
	}

	/** 判断商品是否定制 */
	private void checkCustom(ProductInfo info) {
		if (info == null) {
			return;
		}
		String URL = HttpURL.URL1 + HttpURL.GET_CUSTOMIZEFLAG;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authId", info.getAuthId());
		map.put("productCode", info.getProductCode());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings({ "unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				String json = arg0.result;
				ResponseJsonInfo<CustomFlagInfo> info = JsonUtil.fromJson(json, CustomFlagInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CustomFlagInfo customFlagInfo = (CustomFlagInfo) info.getBody();
					customizeFlag = customFlagInfo.getCustomizeFlag();
					customizeId = customFlagInfo.getProductId();
					setImageView();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtil.i(arg1);
			}
		});
	}

	/** 设置定制按钮 **/
	private void setImageView() {
		if (TextUtils.isEmpty(customizeFlag)) {
			cust_iv.setVisibility(View.GONE);
		} else {
			if (customizeFlag.equals("YES")) {
				cust_iv.setVisibility(View.VISIBLE);
			} else {
				cust_iv.setVisibility(View.GONE);
			}
		}
	}
}