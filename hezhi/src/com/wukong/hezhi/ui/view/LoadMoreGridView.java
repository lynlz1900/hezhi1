package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.LogUtil;

/**
 * 
* @ClassName: LoadMoreGridView 
* @Description: TODO(可上拉加载的gridview) 
* @author HuZhiyin 
* @date 2017-7-31 上午8:59:30 
*
 */
public class LoadMoreGridView extends GridView implements OnScrollListener {
	private int lastVisibleItem; // 最后一个可见项
	private int totalItems; // 总的item
	private View footer; // 底部View+头部View;
	private boolean isLoading = false;// 是否正在加载
	private ILoadListener iListener;// 自定义的一个加载接口。暴露给MainActivity让它实现具体加载操作。可以根据需求不同而改写。

	public LoadMoreGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public LoadMoreGridView(Context context) {
		super(context);
		initView(context);
	}

	public LoadMoreGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		// TODO Auto-generated constructor stub
	}
    /** 
     * 设置不滚动 
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                            MeasureSpec.AT_MOST);  
            super.onMeasure(widthMeasureSpec, expandSpec);  

    }  
	// listview加载底部布局
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		this.setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (lastVisibleItem == totalItems && scrollState == SCROLL_STATE_IDLE) {
			// 如果不是在加载
			if (!isLoading && iListener != null) {
				iListener.onLoad();
				isLoading = true;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItems = totalItemCount;

	}

	public void setLoadListener(ILoadListener iListener) {
		this.iListener = iListener;
	}

	// 加载更多数据的回调接口
	public interface ILoadListener {
		public void onLoad();
	}

	// 上拉加载完毕
	public void loadCompleted() {
		isLoading = false;
	}
}