package com.wukong.hezhi.ui.segment;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleAppendInfo;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.ArticleCommentActivity;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.MyCollectionActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.fragment.FocusFragment;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

/**
 * 
 * @ClassName: ArticleEditCommentFragment
 * @Description: TODO(文章显示评论部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ArticleEditCommentFragment extends BaseFragment {
	@ViewInject(R.id.comment_et)
	private EditText comment_et;

	@ViewInject(R.id.send_bt)
	private Button send_bt;

	@ViewInject(R.id.comment_tv)
	private TextView comment_tv;

	@ViewInject(R.id.praise_cb)
	private CheckBox praise_cb;

	@ViewInject(R.id.praise_tv)
	private TextView praise_tv;

	@ViewInject(R.id.collect_cb)
	private CheckBox collect_cb;

	@ViewInject(R.id.collect_tv)
	private TextView collect_tv;

	private ArticleInfo info = new ArticleInfo();
	private int praiseTotal = 0;// 点赞次数
	private int collectTotal = 0;// 收藏次数
	private int commentTotal = 0;// 评论次数
	private static final String THUMBUP = "THUMBUP";// 点赞
	private static final String THUMBCANCEL = "THUMBCANCEL";// 取消电池
	private static final String COLLECTION = "COLLECTION";// 收藏
	private static final String COLLECTIONCANCEL = "CCANCEL";// 取消收藏
	private int operatType = 0;// 1,点赞成，2，取消点赞成，3，收藏成功，4，取消收藏成功

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(
				R.layout.fragment_article_edit_comment, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		init();
		return rootView;
	}

	public void init() {
		initView();
		setListener();
	}

	private boolean isPraised = false;// 是否点赞
	private boolean isCollecte = false;// 是否收藏

	public void initView() {
		info = (ArticleInfo) getArguments().get("info");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadAppendData();
	}

	boolean isPraiseClick = false;
	boolean isCollectClick = false;

	public void setListener() {

		KeyboardManager.getInstance().setKeyBoardListener(getActivity(),
				new KeyboardManager.KeyBoardListener() {
					@Override
					public void onKeyboardChange(boolean isShow,
							int keyboardHeight) {
						if (isShow) {// 如果弹出键盘
							send_bt.setVisibility(View.VISIBLE);
							praise_cb.setVisibility(View.GONE);
							praise_tv.setVisibility(View.GONE);
							comment_tv.setVisibility(View.GONE);
							collect_cb.setVisibility(View.GONE);
							collect_tv.setVisibility(View.GONE);
						} else {
							send_bt.setVisibility(View.GONE);
							praise_cb.setVisibility(View.VISIBLE);
							praise_tv.setVisibility(View.VISIBLE);
							comment_tv.setVisibility(View.VISIBLE);
							collect_cb.setVisibility(View.VISIBLE);
							collect_tv.setVisibility(View.VISIBLE);
						}

					}
				});

		praise_cb.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				isPraiseClick = true;
				return false;
			}
		});
		collect_cb.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (!UserInfoManager.getInstance().isLogin()) {// 如果没有登录
					JumpActivityManager.getInstance().toActivity(getActivity(),
							LoginActivity.class);
				} else {
					isCollectClick = true;
				}

				return false;
			}
		});
		praise_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isPraiseClick) {
					if (isChecked) {// 点赞
						loadData(THUMBUP);
					} else {// 取消点赞
						loadData(THUMBCANCEL);
					}
					isPraiseClick = false;
				}

			}
		});

		collect_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isCollectClick) {
					if (isChecked) {// 收藏
						loadData(COLLECTION);
					} else {// 取消收藏
						loadData(COLLECTIONCANCEL);
					}
					isCollectClick = false;
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

	public void refreshView() {
		String num = StringUtil.change2W(praiseTotal);
		praise_tv.setText(num);// 点赞总数
		String collectNum = StringUtil.change2W(collectTotal);
		collect_tv.setText(collectNum);
		if ("YES".equals(appendInfo.getThumbupStatus())) {// 已点赞
			praise_cb.setChecked(true);
		} else {// 未点赞
			praise_cb.setChecked(false);
		}
		if ("YES".equals(appendInfo.getCollectionStatus())) {// 已收藏
			collect_cb.setChecked(true);
		} else {
			collect_cb.setChecked(false);
		}
		// comment_tv.setText(appendInfo.getCommentCount());
		comment_tv.setText(StringUtil.change2W(StringUtil.str2Int(appendInfo
				.getCommentCount())));
		ObserveManager.getInstance().notifyState(FocusFragment.class,
				ArticleEditCommentFragment.class,
				appendInfo.getId() + "," + appendInfo.getCommentCount());// 通知评论数发生了变化，格式文章id,评论数（id，commentCount）
		ObserveManager.getInstance().notifyState(MyCollectionActivity.class,
				ArticleEditCommentFragment.class,
				appendInfo.getId() + "," + appendInfo.getCommentCount());// 通知评论数发生了变化，格式文章id,评论数（id，commentCount）
	}

	public static ArticleAppendInfo appendInfo = new ArticleAppendInfo();

	public void loadAppendData() {
		String url = HttpURL.URL1 + HttpURL.ARTICLE_APPENDINFO;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", info.getId());
		map.put("userId", UserInfoManager.getInstance().getUserIdOrDeviceId());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<ArticleAppendInfo> info = JsonUtil.fromJson(
						arg0.result, ArticleAppendInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					appendInfo = info.getBody();
					String count = appendInfo.getThumbupCount();
					praiseTotal = StringUtil.str2Int(count);
					String collectCount = appendInfo.getCollectionCount();
					collectTotal = StringUtil.str2Int(collectCount);
					String commentCount = appendInfo.getCommentCount();
					commentTotal = StringUtil.str2Int(commentCount);
					refreshView();
				}

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	@OnClick(value = { R.id.send_bt, R.id.comment_tv })
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
		case R.id.comment_tv:
			toActivity(ArticleCommentActivity.class, Constant.Extra.ATRICLE_ID,
					appendInfo.getId());
			break;
		}
	}

	public static String COLLECT_CHANGE = "COLLECT_CHANGE";

	/** 点赞与收藏 */
	private void loadData(final String type) {
		String URL = HttpURL.URL1 + HttpURL.ATRICLE_OPERATION;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", appendInfo.getId());
		map.put("userId", UserInfoManager.getInstance().getUserIdOrDeviceId());
		map.put("operation", type);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<Map> info = JsonUtil.fromJson(arg0.result,
						Map.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					if (THUMBUP.equals(type)) {// 点赞
						praise_cb.setChecked(true);
						operatType = 1;
						praiseTotal++;
						// ScreenUtil.showToast("点赞成功");
					} else if (THUMBCANCEL.equals(type)) {// 取消点赞
						praise_cb.setChecked(false);
						operatType = 2;
						praiseTotal--;
						// ScreenUtil.showToast("取消点赞成功");
					} else if (COLLECTION.equals(type)) {// 收藏
						collect_cb.setChecked(true);
						operatType = 3;
						collectTotal++;
						ObserveManager.getInstance().notifyState(
								MyCollectionActivity.class,
								ArticleEditCommentFragment.class, true);// 通知收藏发生了变化
						// ScreenUtil.showToast("收藏成功");
					} else if (COLLECTIONCANCEL.equals(type)) {// 取消收藏
						collect_cb.setChecked(false);
						operatType = 4;
						collectTotal--;
						ObserveManager.getInstance().notifyState(
								MyCollectionActivity.class,
								ArticleEditCommentFragment.class, true);// 通知收藏发生了变化
						// ScreenUtil.showToast("取消收藏成功");
					}
					praise_tv.setText(StringUtil.change2W(praiseTotal));// 点赞总数
					collect_tv.setText(StringUtil.change2W(collectTotal));// 收藏次数
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	/** 发送评论 */
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
		String URL = HttpURL.URL1 + HttpURL.COMMENT_ARTICLE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", appendInfo.getId());
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("content", comment);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<Map> info = JsonUtil.fromJson(arg0.result,
						Map.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					comment_et.setText("");
					loadAppendData();// 发送评论后，需要刷新评论数
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
