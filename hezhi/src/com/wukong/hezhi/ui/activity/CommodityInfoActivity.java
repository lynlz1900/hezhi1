package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.segment.CommdityBottomFragment;
import com.wukong.hezhi.ui.segment.CommodityInfoFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName: CommodityInfoActivity
 * @Description: TODO(商品详情界面，包括商品，详情，评论)
 * @author HuangFeiFei
 * @date 2017-12-13
 * 
 */
public class CommodityInfoActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.commodity_detail);
	public String pageCode = "commodity_detail";

	@ViewInject(R.id.content_rl)
	private RelativeLayout content_rl;

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;
	
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 产品id */
	private int productId;

	@Override
	protected boolean isNotAddTitle() {
		return true;
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_commodity_info;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initData();
	}

	private void initData() {
		productId = (int) getIntent().getSerializableExtra(Constant.Extra.PRODUCTID);
		getData();
	}

	private void getData() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", productId);// 产品id
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_DETAIL;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<CommodityInfo> info = JsonUtil.fromJson(arg0.result, CommodityInfo.class);

				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						commodityInfo = info.getBody();
					} catch (Exception e) {
					}

					if (commodityInfo != null) {
						if (commodityInfo.getProductIsDelete() == 1) {// 商品下架
							empty_ll.setVisibility(View.VISIBLE);
							content_rl.setVisibility(View.GONE);
						} else if (commodityInfo.getProductIsDelete() == 0) {
							empty_ll.setVisibility(View.GONE);
							content_rl.setVisibility(View.VISIBLE);
							addFragment();
						}
					}

				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				LogUtil.i(arg1);
				dismissProgressDialog();
			}
		});
	}

	private void addFragment() {
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("commodityInfo", commodityInfo);
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		CommodityInfoFragment commodityInfoFragment = new CommodityInfoFragment();// 产品详细信息
		commodityInfoFragment.setArguments(bundle);
		transaction.replace(R.id.detail_rl, commodityInfoFragment);
		
		if(commodityInfo.getFaceCustom() == 1 || commodityInfo.getNfcCustom() == 1
				|| commodityInfo.getNowBuy() == 1){
			CommdityBottomFragment commdityBottomFragment = new CommdityBottomFragment();// 底部
			commdityBottomFragment.setArguments(bundle);
			transaction.replace(R.id.bottom_rl, commdityBottomFragment);
		}else{
			findViewById(R.id.bottom_rl).setVisibility(View.GONE);
		}
		transaction.commitAllowingStateLoss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.isFromPage.isOrderDetailToCommDetail = false;
	}

	 @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(mKeydown != null)
        		mKeydown.onKeyDown();
        	else
        		finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	 
	 /**  返回回调 接口**/
	 public onKeydown mKeydown;
	 
	 /**  返回回调 **/
	 public interface onKeydown{
		 /**  返回回调 接口**/
		 void onKeyDown();
	 }
	 
	 public void setKeyDown(onKeydown keydown){
		 this.mKeydown = keydown;
	 }
	 
	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MyCustomizationTobuyActivity.class)) {
			}
		}
	}

}
