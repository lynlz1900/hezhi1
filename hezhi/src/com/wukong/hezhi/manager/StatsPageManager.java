package com.wukong.hezhi.manager;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.wukong.hezhi.bean.AddressInfo;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;

/***
 * 
 * @ClassName: StatsPageManager
 * @Description: TODO(统计页面管理类)
 * @author HuZhiyin
 * @date 2017-7-27 下午3:50:53
 * 
 */
public class StatsPageManager {
	private StatsPageManager() {
	}

	private static class Holder {
		private static final StatsPageManager SINGLETON = new StatsPageManager();
	}

	public static StatsPageManager getInstance() {
		return Holder.SINGLETON;
	}

	/**
	 * 
	 * @Title: getPageData
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param pageName 页面的名称
	 * @param @param pageNumber 页面的编码
	 * @param @param enterDate 进入页面的时间
	 * @param @param exitDate 离开页面的时间
	 * @param @return 设定文件
	 * @return Map<Object,Object> 返回类型
	 * @throws
	 */
	public Map<Object, Object> getPageData(String pageName, String pageNumber,
			String enterDate, String exitDate) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		try {

			String address = "";
			String cityName = "";
			String GPS = "";
			long memberId = 0;
			String hardwareAddress = "";
			String memo = "";
			AddressInfo addressInfo = LocationManager.getInstance()
					.getAdressInfo();
			if (addressInfo != null && addressInfo.getLatitude() != 0) {
				address = addressInfo.getDetailAddr();
				cityName = addressInfo.getCity();
				GPS = "(" + addressInfo.getLongitude() + ","
						+ addressInfo.getLatitude() + ")";
			}

			memberId = UserInfoManager.getInstance().getUserId();
			hardwareAddress = PackageUtil.getPhoneId();

			if (address != null) {
				map.put("address", address);
			}
			if (!TextUtils.isEmpty(cityName)) {
				map.put("cityName", cityName);
			}
			if (!TextUtils.isEmpty(GPS)) {
				map.put("GPS", GPS);
			}
			map.put("memberId", memberId);
			if (!TextUtils.isEmpty(hardwareAddress)) {
				map.put("hardwareAddress", hardwareAddress);
			}
			if (!TextUtils.isEmpty(enterDate)) {
				map.put("enterDate", enterDate);
			}
			if (!TextUtils.isEmpty(exitDate)) {
				map.put("exitDate", exitDate);
			}
			if (!TextUtils.isEmpty(pageNumber)) {
				map.put("pageNumber", pageNumber);
			}
			if (!TextUtils.isEmpty(pageName)) {
				map.put("pageName", pageName);
			}
			if (!TextUtils.isEmpty(memo)) {
				map.put("memo", memo);
			}
			LogUtil.d(map.toString());

		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.d("获取页面基础信息异常");
		}
		return map;
	}
	
	/**
	 * 
	 * @Title: getPageSearchData
	 * @Description: TODO(获取对应搜索界面点击搜索时接口调取数据)
	 * @param  map 接口调取数据
	 * @return Map<String,Object> 返回类型
	 * @throws
	 */
	public Map<String, Object> getPageSearchData(Map<String, Object> map) {
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2 = map;
		try {

			String address = "";
			String cityName = "";
			String GPS = "";
			AddressInfo addressInfo = LocationManager.getInstance()
					.getAdressInfo();
			if (addressInfo != null && addressInfo.getLatitude() != 0) {
				address = addressInfo.getDetailAddr();
				cityName = addressInfo.getCity();
				GPS = "(" + addressInfo.getLongitude() + ","
						+ addressInfo.getLatitude() + ")";
			}

			if (address != null) {
				map2.put("address", address);
			}
			if (!TextUtils.isEmpty(cityName)) {
				map2.put("cityName", cityName);
			}
			if (!TextUtils.isEmpty(GPS)) {
				map2.put("GPS", GPS);
			}
			LogUtil.d(map.toString());

		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.d("获取搜索信息异常");
		}
		return map2;
	}
}
