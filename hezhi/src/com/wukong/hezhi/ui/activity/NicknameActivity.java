package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

/**
 * 
 * @ClassName: NicknameActivity
 * @Description: TODO(昵称设置页面)
 * @author HuZhiyin
 * @date 2017-1-13 上午9:16:24
 * 
 */
public class NicknameActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.nickname);
	public String pageCode = "nickname";
	@ViewInject(R.id.nickname_et)
	private EditText nickname_et;

	private String nickname;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_nickname;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.nickname));
		headView.setRightTitleText(ContextUtil.getString(R.string.save));
		nickname = getIntent().getStringExtra(Constant.Extra.NICK_NAME);
		nickname_et.setText(nickname);
		nickname_et.setSelection(nickname_et.getText().length());
	}

	@OnClick(value = { R.id.header_right })
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			saveNickName();
			break;
		}
	}

	private void saveNickName() {
		String name = nickname_et.getText().toString().trim();
		if (StringUtil.isContainsEmoji(name)) {// 是否含有非法字符
			ScreenUtil.showToast(ContextUtil.getString(R.string.nickname_error));
			return;
		}
		if (TextUtils.isEmpty(name)) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.nickname_empty));
			return;
		}
		Intent intent = new Intent(this, PersonInfoActivity.class);
		intent.putExtra(Constant.Extra.NICK_NAME_RESULT, nickname_et.getText().toString().trim());
		setResult(RESULT_OK, intent);
		finish();
	}
}
