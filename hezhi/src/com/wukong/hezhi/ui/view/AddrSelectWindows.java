package com.wukong.hezhi.ui.view;

import java.util.ArrayList;

import com.bigkoo.pickerview.OptionsPickerView;
import com.wukong.hezhi.bean.CityInfo;
import com.wukong.hezhi.bean.ProvinceInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ReadStreamUtil;
/***
 * 
* @ClassName: AddrSelectWindows 
* @Description: TODO(地址的三级联动选择器) 
* @author HuZhiyin 
* @date 2017-8-11 下午4:14:10 
*
 */
public class AddrSelectWindows {

	private ArrayList<String> options1Items = new ArrayList<String>();;
	private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<ArrayList<ArrayList<String>>>();
	private OptionsPickerView<String> pvOptions;
	private ArrayList<ProvinceInfo> provinces;

	private AddrSelectWindows() {
	}

	private static class Holder {
		private static final AddrSelectWindows SINGLETON = new AddrSelectWindows();
	}

	public static AddrSelectWindows getInstance() {
		return Holder.SINGLETON;
	}

	public void show(IAddrCallBack addrCallBack) {
		if (addrCallBack != null) {
			this.addrCallBack = addrCallBack;
		}
		initPickView();
		pvOptions.show();
	}

	private void initPickView() {
		// 选项选择器
		pvOptions = new OptionsPickerView<String>(ActivitiesManager
				.getInstance().currentActivity());
		String jsonData = ReadStreamUtil.getJson(HezhiConfig.CITIES_JSON_PATH);
		provinces = JsonUtil.parseJson2ArrayList(jsonData, ProvinceInfo.class);

		for (ProvinceInfo province : provinces) {
			ArrayList<String> cities = new ArrayList<String>();
			ArrayList<ArrayList<String>> areas = new ArrayList<>();
			for (CityInfo city : province.getCity()) {
				cities.add(city.getName());
				areas.add(city.getArea());
			}
			options1Items.add(province.getName());
			options2Items.add(cities);
			options3Items.add(areas);
		}
		// 三级联动效果
		pvOptions.setPicker(options1Items, options2Items, options3Items, true);
		// 设置选择的三级单位
		// pwOptions.setLabels("省", "市", "区");
		pvOptions.setTitle("选择城市");
		pvOptions.setCyclic(false, true, true);
		// 设置默认选中的三级项目
		// 监听确定选择按钮
		pvOptions.setSelectOptions(0, 0, 0);
		pvOptions
				.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

					@Override
					public void onOptionsSelect(int options1, int option2,
							int options3) {
						// 返回的分别是三个级别的选中位置
						// 返回的分别是三个级别的选中位置
						String tx = options1Items.get(options1)
								+ "\t"
								+ options2Items.get(options1).get(option2)
								+ "\t"
								+ options3Items.get(options1).get(option2)
										.get(options3);
						addrCallBack.reponse(tx);
					}

				});
	}

	/** 回调接口 */
	public interface IAddrCallBack {
		void reponse(String reponse);

	}

	private IAddrCallBack addrCallBack = new IAddrCallBack() {
		@Override
		public void reponse(String reponse) {
		}
	};

}
