package com.wukong.hezhi.manager;

import java.util.List;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.wukong.hezhi.bean.AddressInfo;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;

/**
 * 
 * @ClassName: Location
 * @Description: TODO(定位信息管理类)
 * @author HuZhiyin
 * @date 2016-11-7 下午1:33:23
 * 
 */
public class LocationManager {
	private AddressInfo addressInfo;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = null;

	private LocationManager() {
	}

	private static class Holder {
		private static final LocationManager SINGLETON = new LocationManager();
	}

	/**
	 * 单一实例
	 * */
	public static LocationManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 开启定位 */
	public void startLocation() {
		startLocationListener(null);
	}

	/** 开启定位 ，带回调参数 */
	public void startLocationListener(LocationListener listener) {

		if (mLocationClient == null) {
			mLocationClient = new LocationClient(ContextUtil.getContext()); // 声明LocationClient类
		}
		if (listener != null) {
			myListener = listener;
		} else {
			myListener = new MyLocationListener();
		}
		initLocationOption();
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mLocationClient.start();
	}

	/** 关闭定位 */
	public void stopLocation() {
		if (mLocationClient != null && myListener != null) {
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient.stop();
			mLocationClient = null;
			LogUtil.d("定位关闭");
		}
	}

	private void initLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	public interface LocationListener extends BDLocationListener {
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			if (location.getLocType() == BDLocation.TypeGpsLocation
					|| location.getLocType() == BDLocation.TypeNetWorkLocation) {
				setAdress(location);
			}
		}
	}

	/** 设置地址 */
	private void setAdress(BDLocation location) {

		AddressInfo address = new AddressInfo();
		List<Poi> list = location.getPoiList();// POI数据
		if (list != null && list.size() > 0) {
			int i = 0;
			if (!TextUtils.isEmpty(list.get(i).getName())) {
				String mAdderss = list.get(i).getName();// 地址
				address.setBuilding(mAdderss);
			}
		}
		address.setDetailAddr(location.getAddrStr());
		address.setProvince(location.getProvince());
		address.setCity(location.getCity());
		address.setArea(location.getStreet());
		address.setLatitude(location.getLatitude());
		address.setLongitude(location.getLongitude());
		if (!TextUtils.isEmpty(address.getDetailAddr())
				|| !TextUtils.isEmpty(address.getBuilding())) {// 如果获取到了地址信息,关闭定位
			addressInfo = address;
			stopLocation();
		}
	}

	/** 获取地址信息 */
	public AddressInfo getAdressInfo() {
		return addressInfo;
	}
}
