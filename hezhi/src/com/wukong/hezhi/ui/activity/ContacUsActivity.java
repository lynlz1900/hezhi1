package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: ContacUsActivity
 * @Description: TODO(联系我们)
 * @author HuZhiyin
 * @date 2017-5-19 下午2:19:48
 * 
 */
public class ContacUsActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.contact_us);
	public String pageCode ="contact_us";
	
	@ViewInject(R.id.phone_ll)
	private LinearLayout phone_ll;
	
	@ViewInject(R.id.phone_tv)
	private TextView phone_tv;
	
	private String phoneNum;
	
	@OnClick(value = { R.id.phone_ll })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.phone_ll:
			call(phoneNum);
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
		return R.layout.activity_contact_us;
	}

	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.contact_us));
		phoneNum = phone_tv.getText().toString().trim();
	}
	/** 
	 * 调用拨号功能 
	 * @param phone 电话号码 
	 */  
	private void call(String phone) {  
	    Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));  
	    startActivity(intent);  
	} 
}
