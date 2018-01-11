package com.wukong.hezhi.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: AboutUsActivity
 * @Description: TODO(关于我们)
 * @author HuZhiyin
 * @date 2016-9-19 下午4:45:08
 * 
 */
public class AboutUsActivity extends BaseActivity {

	public String  pageName = ContextUtil.getString(R.string.about_us);
	public String pageCode ="about_us";
	@ViewInject(R.id.address)
	private TextView address;
	@OnClick(value = {  R.id.address })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.address:
			toActivity(WebViewActivity.class,Constant.Extra.WEB_URL,HezhiConfig.HEZHI_URL);
			break;
		default:
			break;
		}
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_about_us;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.about_us));
	}


}
