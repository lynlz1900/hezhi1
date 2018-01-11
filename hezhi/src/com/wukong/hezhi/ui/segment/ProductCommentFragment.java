package com.wukong.hezhi.ui.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CommentAdapter;
import com.wukong.hezhi.bean.CommentInfo;
import com.wukong.hezhi.bean.CommentInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.activity.ProductCommentActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

/**
 * 
 * @ClassName: ProductCommentFragment
 * @Description: TODO(品牌直播显示评论部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ProductCommentFragment extends BaseFragment {

	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private CommentAdapter adapter;
	private List<CommentInfo> comments = new ArrayList<CommentInfo>();
	private int page = 0;// 当前页
	private int countPage;// 总页数
	private UnityInfo unityInfo = new UnityInfo();


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_product_comment,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		unityInfo = (UnityInfo) getArguments().getSerializable("unityInfo");
		init();
		return rootView;
	}

	public void init() {
		initView();
		setListener();
	}

	private RelativeLayout header_rl;
	private TextView comment_tv;

	public void initView() {
		header_rl = (RelativeLayout) ScreenUtil
				.inflate(R.layout.layout_comment_header);
		comment_tv = (TextView) header_rl.findViewById(R.id.comment_tv);
		refreshListView();
		adapter = new CommentAdapter(comments, (BaseActivity) getActivity());
		lv_scroll.addHeaderView(header_rl);
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
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
		comment_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toActivity(ProductCommentActivity.class, Constant.Extra.PPID,
						unityInfo.getPpId());
			}
		});
	}

	/** 加载数据 */
	private void loadData() {

		String url = HttpURL.URL1 + HttpURL.GET_COMMENT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ppid", unityInfo.getPpId());
		map.put("page", page);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

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
					comment_tv.setText(StringUtil.change2W(commentInfos
							.getCountRecord()) + "条评论");
					if (page == 1) {
						comments.clear();
					}
					comments.addAll(commentInfos.getDataList());
					adapter.notifyDataSetChanged();
				} else {
					ScreenUtil.showToast("数据加载失败");
				}
			}
		});
	}

}
