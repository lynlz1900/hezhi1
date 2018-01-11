package com.wukong.hezhi.ui.segment;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.PraiseInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.ProductMainActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.ShareWindows;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

/**
 * 
 * @ClassName: ProductEditCommentFragment
 * @Description: TODO(品牌直播显示编辑评论部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ProductEditCommentFragment extends BaseFragment {
	@ViewInject(R.id.edit_ll)
	private LinearLayout edit_ll;

	@ViewInject(R.id.comment_et)
	private EditText comment_et;

	@ViewInject(R.id.send_bt)
	private Button send_bt;

	@ViewInject(R.id.praise_cb)
	private CheckBox praise_cb;

	@ViewInject(R.id.praise_tv)
	private TextView praise_tv;

	@ViewInject(R.id.share_iv)
	private ImageView share_iv;

	private static int GET_PRAISE = 3;// 获取点赞
	private static int PRAISE = 1;// 点赞
	private static int CANCEL_PRAISE = 2;// 取消点赞
	private int praiseTotal = 0;// 点赞次数
	private boolean isPrased = false;// 是否已经点赞

	private UnityInfo unityInfo = new UnityInfo();


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(
				R.layout.fragment_product_edit_comment, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		unityInfo = (UnityInfo) getArguments().getSerializable("unityInfo");
		init();
		return rootView;
	}

	public void init() {
//		initView();
		setListener();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
	
	public void initView() {
		praise(GET_PRAISE);
	}

	boolean isClick = false;

	public void setListener() {
		KeyboardManager.getInstance().setKeyBoardListener(getActivity(),
				new KeyboardManager.KeyBoardListener() {
					@Override
					public void onKeyboardChange(boolean isShow,
							int keyboardHeight) {
						if (isShow) {// 如果输入了内容
							send_bt.setVisibility(View.VISIBLE);
							praise_cb.setVisibility(View.GONE);
							share_iv.setVisibility(View.GONE);
							praise_tv.setVisibility(View.GONE);
						} else {
							send_bt.setVisibility(View.GONE);
							praise_cb.setVisibility(View.VISIBLE);
							share_iv.setVisibility(View.VISIBLE);
							praise_tv.setVisibility(View.VISIBLE);
						}

					}
				});

		praise_cb.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				isClick = true;
				return false;
			}
		});
		praise_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isClick) {
					if (isChecked) {// 点赞
						praise(PRAISE);
					} else {// 取消点赞
						praise(CANCEL_PRAISE);
					}
					isClick = false;
				}
			}
		});
		comment_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(s)) {
					send_bt.setEnabled(false);
				} else {
					send_bt.setEnabled(true);
				}
			}
		});

		comment_et.setOnTouchListener(new OnTouchListener() {
			int touch_flag = 0;  
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touch_flag++;  
				if (!UserInfoManager.getInstance().isLogin() && touch_flag == 2) {// 如果没有登录
					JumpActivityManager.getInstance().toActivity(getActivity(),
							LoginActivity.class);
					touch_flag = 0;
				}	
				return false;
			}
		});
			
	}

	/** 点赞,1 检查有没有点赞这个感知应用 2 点赞这个感知应用 3 取消点赞这个感知应用 */
	public void praise(final int type) {
		String URL = HttpURL.URL1 + HttpURL.PRAISE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ppid", unityInfo.getPpId());
		map.put("memberId", UserInfoManager.getInstance().getUserIdOrDeviceId());
		map.put("op", type);// 1检查有没有点赞这个感知应用 2
							// 点赞这个感知应用 3 取消点赞这个感知应用
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				ResponseJsonInfo<PraiseInfo> info = JsonUtil.fromJson(
						arg0.result, PraiseInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					PraiseInfo praiseInfo = info.getBody();
					praiseTotal = praiseInfo.getTotal();
					isPrased = praiseInfo.isThumbsUp();
					refreshView();
					// if (type == GET_PRAISE) {
					// refreshView();
					// }
					// if (type == PRAISE || type == CANCEL_PRAISE) {//
					// 如果是点赞或取消点赞，循环调用获取点赞次数
					// praise(GET_PRAISE);
					// }
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void refreshView() {
		if (isPrased) {// 已经点赞过。
			praise_cb.setChecked(true);
		} else {// 没有点赞过。
			praise_cb.setChecked(false);
		}
		String num = StringUtil.change2W(praiseTotal);
		praise_tv.setText(num);// 点赞总数
	}

	@OnClick(value = { R.id.send_bt, R.id.share_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.send_bt:
			KeyboardManager.getInstance().hideKeyboard(getActivity());// 隐藏键盘
			if (!UserInfoManager.getInstance().isLogin()) {// 如果没有登录
				JumpActivityManager.getInstance().toActivity(getActivity(),
						LoginActivity.class);
			} else {
				sendData();
			}
			break;
		case R.id.share_iv:
			share();
			break;
		}
	}

	private void share() {
		if (unityInfo != null) {
			String url = HttpURL.URL1 + HttpURL.SHARE_H5 + "?id="
					+ unityInfo.getPpId();

			ShareInfo shareInfo = new ShareInfo();
			shareInfo.setUrl(url);
			shareInfo.setTitle(unityInfo.getName());
			shareInfo.setDescription(unityInfo.getName());
			shareInfo.setImagUrl(HezhiConfig.HEZHI_LOGO_URL);
			final View view = getActivity().getWindow().getDecorView()
					.findViewById(android.R.id.content);// 获取一个view,popubwindow会用到
			ShareWindows.getInstance().show(view, shareInfo);
		}
	}

	public void sendData() {
		String comment = comment_et.getText().toString().trim();
		if (TextUtils.isEmpty(comment)) {
			ScreenUtil.showToast("请输入评论内容");
			return;
		}
		if (StringUtil.isContainsEmoji(comment)) {// 是否含有非法字符
			ScreenUtil.showToast("评论不能含有表情");
			return;
		}

		String URL = HttpURL.URL1 + HttpURL.COMMENTPERCEPTION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ppid", unityInfo.getPpId());
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		map.put("content", comment);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<Map> info = JsonUtil.fromJson(arg0.result,
						Map.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					ProductMainActivity.productCommentFragment
							.refreshListView();
					comment_et.setText("");
				} else {
					ScreenUtil.showToast("评论失败");
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

}
