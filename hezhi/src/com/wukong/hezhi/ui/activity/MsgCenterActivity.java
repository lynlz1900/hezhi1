package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MsgCenterActivityAdapter;
import com.wukong.hezhi.bean.JpushInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;

/**
 * 
* @ClassName: MsgCenterActivity 
* @Description: TODO(消息中心) 
* @author HuZhiyin 
* @date 2017-7-28 上午9:45:45 
*
 */
public class MsgCenterActivity extends BaseActivity  {
	public String pageName = ContextUtil.getString(R.string.msg_center);
	public String pageCode = "msg_center";
	@ViewInject(R.id.lv_scroll)
	private ListView lv_scroll;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	// @ViewInject(R.id.empty)
	private ViewStub empty;

	// @ViewInject(R.id.empty_tips_tv)
	private TextView empty_tips_tv;

	private MsgCenterActivityAdapter adapter;
	private List<JpushInfo> jpushInfos = new ArrayList<JpushInfo>();

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_msg_center;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.msg_center));
		setEmptyTip();
		initView();
		setListener();

	}

	private void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				loadData();
			}

		});// 进入页面，自动加载
		adapter = new MsgCenterActivityAdapter(jpushInfos, this);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);

	}

	/** 设置空提醒 */
	private void setEmptyTip() {
		empty = (ViewStub) findViewById(R.id.empty);
		empty.inflate();
		// ViewStub调用inflate()方法后,ViewStub被替换为以inflateId为Id的视图。网络上很多博客都提到了这句话，但在代码示例中并没有如下这句。实际运行中导致使用text时报空指针错误。
		View view = findViewById(R.id.pic_view_id_after_inflate);
		empty_tips_tv = (TextView) view.findViewById(R.id.empty_tips_tv);
		empty_tips_tv.setText("没有消息");
	}

	private void loadData() {
		jpushInfos.clear();
		jpushInfos.addAll(getDataFromDB());
		adapter.notifyDataSetChanged();
		mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
	}

	public void setListener() {
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				loadData();
			}
		});
	}

	private List<JpushInfo> getDataFromDB() {
		List<String> jsonStrs = DBManager.getInstance().qurreyJsonsByTpye(
				DBManager.JGPUSH);
		List<JpushInfo> unityInfos = new ArrayList<JpushInfo>();
		if (jsonStrs != null) {
			for (int i = 0; i < jsonStrs.size(); i++) {
				ResponseJsonInfo info = JsonUtil.fromJson(jsonStrs.get(i),
						JpushInfo.class);
				JpushInfo jpushInfo = (JpushInfo) info.getBody();
				unityInfos.add(jpushInfo);
			}
		}
		return unityInfos;
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MsgCenterActivityAdapter.class)) {
				loadData();
			}

		}

	}

}
