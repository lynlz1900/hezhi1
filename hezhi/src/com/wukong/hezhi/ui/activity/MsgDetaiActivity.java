package com.wukong.hezhi.ui.activity;

import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.JpushInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;

/**
 * 
 * @ClassName: MsgDetaiActivity
 * @Description: TODO(消息详情)
 * @author HuZhiyin
 * @date 2017-5-11 下午2:41:44
 * 
 */
public class MsgDetaiActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.msg_detail);
	public String pageCode ="msg_detail";
	@ViewInject(R.id.time_tv)
	private TextView time_tv;

	@ViewInject(R.id.detail_tv)
	private TextView detail_tv;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_msg_detail;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.msg_detail));
		JpushInfo jpushInfo = (JpushInfo) getIntent().getSerializableExtra(
				Constant.Extra.MSG_DETAIL);
		String time = DateUtil.getYYHHMM(Long.parseLong(jpushInfo
				.getLocalTime()));// 仿朋友圈的时间显示
		time_tv.setText(time);
		detail_tv.setText(jpushInfo.getContent());
	}

}
