package com.wukong.hezhi.ui.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.ui.activity.TraceActivity;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: IntroduceFragment
 * @Description: TODO(产品PV值介紹)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:47:39
 * 
 */
public class IntroduceFragment extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.pv_introduce);
	public String pageCode = "pv_introduce";
	@ViewInject(R.id.fragment_introduce_iv)
	private ImageView fragment_introduce_iv;

	@ViewInject(R.id.confidence_level)
	private TextView confidence_level;

	@ViewInject(R.id.detection_result)
	private TextView detection_result;

	@ViewInject(R.id.product_name)
	private TextView product_name;

	@ViewInject(R.id.tag_number)
	private TextView tag_number;

	@ViewInject(R.id.produce_data)
	private TextView produce_data;

	@ViewInject(R.id.product_size)
	private TextView product_size;

	@ViewInject(R.id.sale_stus)
	private TextView sale_stus;

	@ViewInject(R.id.bathNo)
	private TextView bathNo;

	@ViewInject(R.id.bathNo_ll)
	private LinearLayout bathNo_ll;
	@ViewInject(R.id.produce_data_ll)
	private LinearLayout produce_data_ll;

	@ViewInject(R.id.chart)
	private HorizontalBarChart mChart;

	private Typeface tf;
	private ProductInfo productInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_introduce,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	public void initView() {
		productInfo = ((TraceActivity) getActivity()).getProductInfo();
		if (productInfo == null) {
			return;
		}

//		if (TextUtils.equals(NFCManager.authCode, HezhiConfig.MAITAO_AUTHCODE)) {// 如果是茅台醇，隐藏信息
//			bathNo_ll.setVisibility(View.GONE);
//			produce_data_ll.setVisibility(View.GONE);
//		} else {
//			bathNo_ll.setVisibility(View.VISIBLE);
//			produce_data_ll.setVisibility(View.VISIBLE);
//		}
		

		// mChart.setOnChartValueSelectedListener(this);

		mChart.setDrawBarShadow(false);
		mChart.setDrawValueAboveBar(true);
		mChart.setDescription("");
		mChart.setMaxVisibleValueCount(60);
		mChart.setPinchZoom(false);

		mChart.setDrawGridBackground(false);
		tf = Typeface.createFromAsset(getActivity().getAssets(),
				"TTF/OpenSans-Regular.ttf");

		XAxis xl = mChart.getXAxis();
		xl.setPosition(XAxisPosition.BOTTOM);
		xl.setTypeface(tf);
		xl.setDrawAxisLine(true);
		xl.setDrawGridLines(true);
		xl.setGridLineWidth(0.3f);

		YAxis yl = mChart.getAxisLeft();
		yl.setTypeface(tf);
		yl.setDrawAxisLine(true);
		yl.setDrawGridLines(true);
		yl.setGridLineWidth(0.3f);
		// yl.setInverted(true);

		YAxis yr = mChart.getAxisRight();
		yr.setTypeface(tf);
		yr.setDrawAxisLine(true);
		yr.setDrawGridLines(false);
		// yr.setInverted(true);

		setChartData(productInfo);
		mChart.animateY(2500);

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);
		l.setFormSize(8f);
		l.setXEntrySpace(4f);

		int currentPV = productInfo.getPvAssessedScore();
		confidence_level.setText(currentPV
				+ ContextUtil.getString(R.string.score));

		if (productInfo.getPvAssessedScore() > 0) {
			detection_result.setText(getResources().getString(R.string.result));
		} else if (productInfo.getPvAssessedScore() == 0) {
			detection_result.setText(getResources().getString(
					R.string.credible_zero));
		}
		product_name.setText(productInfo.getMaterialName());
		tag_number.setText(NFCManager.uid);
		produce_data.setText(productInfo.getCreateDate());
		product_size.setText(productInfo.getMaterialSpecification());
		sale_stus.setText(productInfo.getSale());
		bathNo.setText(productInfo.getBathNo());
		BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils
				.display(fragment_introduce_iv, productInfo.getPicturePath());
	}

	public static final int[] VORDIPLOM_COLORS = { Color.rgb(27, 179, 95),
			Color.rgb(253, 181, 55), Color.rgb(229, 229, 229) };

	public static final int[] VORDIPLOM_COLORS2 = { Color.rgb(255, 00, 00),
			Color.rgb(253, 181, 55), Color.rgb(229, 229, 229) };

	private void setChartData(ProductInfo productInfo) {
		int promisePV = productInfo.getPvPermitedScore();
		int socialAveragePV = productInfo.getPvStandardScore();
		int currentPV = productInfo.getPvAssessedScore();

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		ArrayList<String> xVals = new ArrayList<String>();

		xVals.add(getString(R.string.promisePV));// 承诺pv
		yVals1.add(new BarEntry(currentPV, 2));
		xVals.add(getString(R.string.socialAveragePV));// 基准pv
		yVals1.add(new BarEntry(socialAveragePV, 1));
		xVals.add(getString(R.string.currentPV));// 实测pv
		yVals1.add(new BarEntry(promisePV, 0));

		BarDataSet set1 = new BarDataSet(yVals1,
				ContextUtil.getString(R.string.full_score));
		if (currentPV > socialAveragePV) {
			set1.setColors(VORDIPLOM_COLORS2);
		} else {
			set1.setColors(VORDIPLOM_COLORS);
		}

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);
		BarData data = new BarData(xVals, dataSets);
		data.setValueTextSize(10f);
		data.setValueTypeface(tf);
		mChart.setData(data);
	}
}
