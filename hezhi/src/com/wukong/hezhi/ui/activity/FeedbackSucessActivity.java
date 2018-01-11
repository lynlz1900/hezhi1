package com.wukong.hezhi.ui.activity;

import android.view.View;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: FeedbackSucessActivity
 * @Description: TODO(反馈成功)
 * @author HuZhiyin
 * @date 2017-7-5 下午3:56:56
 * 
 */
public class FeedbackSucessActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.feedback_sucess);
	public String pageCode ="feedback_sucess";

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_feedback_sucess;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.feedback_sucess));

	}

	@OnClick(value = { R.id.close_bt })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.close_bt:
			finish();
			break;
		}
	}

}
