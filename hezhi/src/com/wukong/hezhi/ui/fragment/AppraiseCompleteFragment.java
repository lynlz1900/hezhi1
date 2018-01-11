package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.AppraiseCenterAdapter;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfo;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.AppraiseAddActivity;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 *
 * @ClassName: AppraiseCenterFragment
 * @Description: TODO(评价中心--已完成)
 * @author HuangFeiFei
 * @date 2017-12-19
 *
 */
public class AppraiseCompleteFragment extends NoPreloadFragment {

    @ViewInject(R.id.lv_scroll)
    private LoadMoreListView lv_scroll;

    @ViewInject(R.id.empty)
    private TextView empty;

    @ViewInject(R.id.swprf)
    private SwipeRefreshLayout mSwipeLayout;

    private int page = 0;// 当前页
    @SuppressWarnings("unused")
    private boolean isOver = false;// 是否加载介绍
    /*** 总页数 **/
    private int countPage = 0;

    private AppraiseCenterAdapter appraiseCenterAdapter;
    private List<CommodityAppraiseCenterInfo> appraiseCenterInfos = new ArrayList<CommodityAppraiseCenterInfo>();
    private List<CommodityAppraiseCenterInfo> appraiseCenterInfosSel = new ArrayList<CommodityAppraiseCenterInfo>();

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_appraise_center, container,
                false);// 关联布局文件
        ViewUtils.inject(this, rootView); // 注入view和事件
        init();
        isPrepared = true;
        lazyLoad();
        return rootView;
    }

    protected void init() {
    	empty.setText(ContextUtil.getString(R.string.appraise_complete_isnull));
        initView();
        setListener();
    }

    /**
     * 初始化控件
     **/
    private void initView() {
    	appraiseCenterInfosSel.clear();
    	appraiseCenterInfosSel.addAll(appraiseCenterInfos);
        setAdapter();
        setSwipeLayout();
    }

    /**
     * 初始化adapter
     **/
    private void setAdapter() {
    	appraiseCenterAdapter = new AppraiseCenterAdapter((BaseActivity) getActivity(),
    			appraiseCenterInfosSel,1);
        lv_scroll.setAdapter(appraiseCenterAdapter);
        lv_scroll.setEmptyView(empty);
    }

    /**
     * 初始化加载
     **/
    public void setSwipeLayout() {
        mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
    }

    /**
     * 设置监听
     */
    private void setListener() {

        //下拉刷新
        mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                page = 1;// 刷新将page置为1
                mSwipeLayout.setRefreshing(true);
                loadData();
            }
        });

        //上拉加载更多
        lv_scroll.setLoadListener(new ILoadListener() {
            @Override
            public void onLoad() {
                // TODO Auto-generated method stub
                page++;
                if (page <= countPage) {// 如果加载结束
                    loadData();
                } else {
                    lv_scroll.loadCompleted();
                    ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
                }
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (!isVisible || !isPrepared) {
            return;
        }

        refresListView();
    }

    /**
     * 加载首页数据
     **/
    private void refresListView() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                page = 1;// 刷新将page置为1
                loadData();
            }
        });// 进入页面，自动加载
    }

    /**
     * 追加评价后更新UI
     **/
    private void refresListViewByAppraise(int id) {
    	appraiseCenterInfos.clear();
    	appraiseCenterInfos.addAll(appraiseCenterInfosSel);//先更新appraiseCenterInfos，否则会导致数据不是最新的
    	for(int i=0;i<appraiseCenterInfos.size();i++){
    		if(appraiseCenterInfos.get(i).getId() == id)
    			appraiseCenterInfos.get(i).setAppraiseStatus(2);
    	}
    	
    	updateList();
    }
    
    /**
     * 加载数据
     */
    private void loadData() {
    	  String url = HttpURL.URL1 + HttpURL.APPRAISE_CENTER;
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("memberId", UserInfoManager.getInstance().getUserId());
          map.put("page", page);
          map.put("state", 1+"");
          LogUtil.i(url);
          LogUtil.i(map.toString());
          HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
              @Override
              public void onFailure(HttpException arg0, String arg1) {
                  // TODO Auto-generated method stub
                  mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
                  lv_scroll.loadCompleted();// 上拉加载隐藏
                  LogUtil.i(arg1);
                  ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
              }

              @SuppressWarnings({"unchecked"})
              @Override
              public void onSuccess(ResponseInfo<String> arg0) {
                  // TODO Auto-generated method stub
                  LogUtil.i(arg0.result);
                  appraiseCenterInfos.clear();
                  appraiseCenterInfos.addAll(appraiseCenterInfosSel);//先更新appraiseCenterInfos，否则会导致数据不是最新的
                  mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
                  lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
                  ResponseJsonInfo<CommodityAppraiseCenterInfos> info = JsonUtil.fromJson(
                          arg0.result, CommodityAppraiseCenterInfos.class);
                  if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
                  	CommodityAppraiseCenterInfos infos = new CommodityAppraiseCenterInfos();
                      try {
                          infos = (CommodityAppraiseCenterInfos) info.getBody();
                          List<CommodityAppraiseCenterInfo> list = infos.getDataList();
                          countPage = infos.getCountPage();
                          if (page == 1) {
                          	appraiseCenterInfos.clear();
                          }
                          if (list == null || list.size() == 0) {
                              isOver = true;
                          } else {
                              isOver = false;
                              appraiseCenterInfos.addAll(list);
                          }

                          updateList();
                      } catch (Exception e) {
                      }
                  } else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
                      if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
                          ScreenUtil.showToast(info.getPromptMessage());
                  } else {
                      ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
                  }
              }
          });
    }

    /** 更新listview **/
    private void updateList(){
    	appraiseCenterInfosSel.clear();
        appraiseCenterInfosSel.addAll(appraiseCenterInfos);
        appraiseCenterAdapter.notifyDataSetChanged();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
        super.updateState(notifyTo, notifyFrom, msg);

        if (notifyTo.equals(getClass())) {
            if (notifyFrom.equals(AppraiseAddActivity.class)) {

                try {
                	int id = (int)msg;
                	LogUtil.i(id+"");
                	refresListViewByAppraise(id);
                } catch (Exception e) {
                }
            } 
        }
    }
}
