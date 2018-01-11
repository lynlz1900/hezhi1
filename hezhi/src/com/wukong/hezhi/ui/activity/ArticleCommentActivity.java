package com.wukong.hezhi.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CommentAdapter;
import com.wukong.hezhi.bean.CommentInfo;
import com.wukong.hezhi.bean.CommentInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 
 * @ClassName: ArticleCommentActivity
 * @Description: TODO(文章评论页面)
 * @author HuZhiyin
 * @date 2017-7-7 下午1:40:33
 * 
 */
public class ArticleCommentActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.comment);
	public String pageCode ="comment";

	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	@ViewInject(R.id.comment_et)
	private EditText comment_et;

	@ViewInject(R.id.send_bt)
	private Button send_bt;

	private CommentAdapter adapter;
	private List<CommentInfo> comments = new ArrayList<CommentInfo>();
	private int page = 0;// 当前页
	private int countPage;// 总页数
	private String atricleId;


	@OnClick(value = { R.id.send_bt, R.id.left })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.send_bt:
			KeyboardManager.getInstance().hideKeyboard(this);// 隐藏键盘
			if (!UserInfoManager.getInstance().isLogin()) {// 如果没有登录
				JumpActivityManager.getInstance().toActivity(this,
						LoginActivity.class);
			} else {
				sendData();
			}
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_comment;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.comment));
		initView();
		setListener();
	}

	public void initView() {
		atricleId = getIntent().getStringExtra(Constant.Extra.ATRICLE_ID);
		refreshListView();
		adapter = new CommentAdapter(comments, this);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
	}

	public void refreshListView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				// mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}

	/** 设置监听 */
	private void setListener() {
		KeyboardManager.getInstance().setKeyBoardListener(this,new KeyboardManager.KeyBoardListener() {
			@Override
			public void onKeyboardChange(boolean isShow,
					int keyboardHeight) {
				if (isShow) {// 如果输入了内容
					send_bt.setVisibility(View.VISIBLE);
				} else {
					send_bt.setVisibility(View.GONE);
				}

			}
		});
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
				LogUtil.d("OnRefreshListener" + page);

			}
		});
		lv_scroll.setLoadListener(new ILoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				LogUtil.d("setOnLoadListener" + page);
				if (page <= countPage) {// 如果加载的页数小于或等于总页数，则加载
					loadData();
				} else {
					lv_scroll.loadCompleted();
				}
			}
		});
	comment_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
					JumpActivityManager.getInstance().toActivity(ArticleCommentActivity.this,
							LoginActivity.class);
					touch_flag = 0;
				}	
				return false;
			}
		});
	
	}


	/** 加载数据 */
	private void loadData() {

		String url = HttpURL.URL1 + HttpURL.SEARCHE_COMMENT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", atricleId);
		map.put("page", page);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CommentInfos> info = JsonUtil.fromJson(
						arg0.result, CommentInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CommentInfos commentInfos = info.getBody();
					countPage = commentInfos.getCountPage();// 获取总页数
					headView.setRightTitleText(StringUtil.change2W(commentInfos
							.getCountRecord()) + "条评论");
					LogUtil.d("countPage" + countPage);
					ArrayList<CommentInfo> list = (ArrayList<CommentInfo>) commentInfos
							.getDataList();
					if(page == 1){
						comments.clear();// 清空数据
					}
					for (CommentInfo commentInfo : list) {
						comments.add(commentInfo);
					}
					adapter.notifyDataSetChanged();
				} 
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
		map.put("id", atricleId);
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("content", comment);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<Map> info = JsonUtil.fromJson(arg0.result,
						Map.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					comment_et.setText("");
					refreshListView();
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
