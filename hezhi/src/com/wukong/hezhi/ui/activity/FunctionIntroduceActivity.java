package com.wukong.hezhi.ui.activity;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: FunctionIntroduceActivity
 * @Description: TODO(功能介绍)
 * @author HuZhiyin
 * @date 2017-1-10 下午3:01:22
 * 
 */
public class FunctionIntroduceActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.function_introduce);
	public String pageCode ="function_introduce";
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_function_introduce;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
		headView.setTitleStr(ContextUtil.getString(R.string.function_introduce));
	}



}
