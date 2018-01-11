package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.LocationManager.LocationListener;
import com.wukong.hezhi.ui.view.AddrSelectWindows;
import com.wukong.hezhi.ui.view.AddrSelectWindows.IAddrCallBack;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: SettingAdressActivity
 * @Description: TODO(地区页面)
 * @author HuZhiyin
 * @date 2017-1-13 上午10:52:17
 * 
 */
public class SettingAddressActivity extends BaseActivity implements
		LocationListener {
	public String pageName = ContextUtil.getString(R.string.area);
	public String pageCode ="area";
	@ViewInject(R.id.location_address_ll)
	private LinearLayout location_address_ll;

	@ViewInject(R.id.all_address_ll)
	private LinearLayout all_address_ll;

	@ViewInject(R.id.area_tv)
	private TextView area_tv;

	@ViewInject(R.id.select_area_tv)
	private TextView select_area_tv;

	@ViewInject(R.id.loading_pb)
	private ProgressBar loading_pb;


	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting_address;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.area));
		// addressInfo = LocationManager.getInstance().getAdressInfo();
		initView();
	}

	public void initView() {

		loading_pb.setVisibility(View.VISIBLE);
		LocationManager.getInstance().startLocationListener(this);// 开启定位
	}



	private void finishResult(String address) {
		Intent intent = new Intent(SettingAddressActivity.this,
				PersonInfoActivity.class);
		intent.putExtra(Constant.Extra.ADRESS_RESULT, address);
		// 将数据根据特定键值的意图事件导入
		setResult(RESULT_OK, intent);
		// 关闭后返回主Activity
		finish();
	}

	@OnClick(value = { R.id.location_address_ll, R.id.all_address_ll })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.location_address_ll:
			finishResult(area_tv.getText().toString().trim());
			break;
		case R.id.all_address_ll:
			selectAddr();
			break;
		}
	}

	private void selectAddr() {
		AddrSelectWindows.getInstance().show(new IAddrCallBack() {
			
			@Override
			public void reponse(String reponse) {
				// TODO Auto-generated method stub
				select_area_tv.setText(reponse);
				finishResult(reponse);
			}
		});
	}

	
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location.getLocType() == BDLocation.TypeGpsLocation
				|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
			area_tv.setText(location.getProvince() + "\t" + location.getCity());
		} else {
			area_tv.setText("定位失败");
		}
		loading_pb.setVisibility(View.INVISIBLE);
		LocationManager.getInstance().stopLocation();// 关闭定位
	}
}
