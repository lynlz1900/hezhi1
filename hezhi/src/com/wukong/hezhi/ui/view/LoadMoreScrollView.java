package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 
* @ClassName: LoadMoreScrollView 
* @Description: TODO(可上拉加载的ScrollView) 
* @author HuZhiyin 
* @date 2017-5-31 上午8:58:48 
*
 */
public class LoadMoreScrollView extends ScrollView {
	private int flag = 0; // 并发控制标志位
	
	private boolean isComplete = false;

	private OnZdyScrollViewListener onZdyScrollViewListener;
	
	private OnScrollToBottomListener onScrollToBottomListener;
	
	private OnScollChangedListener onScollChangedListener = null;
	 
	public LoadMoreScrollView(Context context) {
		super(context);
	}

	public LoadMoreScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoadMoreScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	// listview加载完毕，将并发控制符置为0
	public void loadingComponent() {
		flag = 0;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (onScollChangedListener != null) {
            onScollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
		
		View view = this.getChildAt(0);
		// 如果scrollview滑动到底部并且并发控制符为0，回调接口向服务器端请求数据
		if (this.getHeight() + this.getScrollY() == view.getHeight()
				&& flag == 0) {
			flag = 1;// 一进来就将并发控制符置为1，虽然onScrollChanged执行多次，但是由于并发控制符的值为1，不满足条件就不会执行到这
			if(onZdyScrollViewListener != null)
				onZdyScrollViewListener.ZdyScrollViewListener();
		}
	}

	public void setOnScollChangedListener(OnScollChangedListener onScollChangedListener) {
        this.onScollChangedListener = onScollChangedListener;
    }
	
	public interface OnScollChangedListener {

        void onScrollChanged(LoadMoreScrollView scrollView, int x, int y, int oldx, int oldy);

    }
	
	public void setOnZdyScrollViewListener(
			OnZdyScrollViewListener onZdyScrollViewListener) {
		this.onZdyScrollViewListener = onZdyScrollViewListener;
	}

	public interface OnZdyScrollViewListener {
		public void ZdyScrollViewListener();
	}

	@Override  
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {  
	    super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);  
	        if (scrollY>0 && onScrollToBottomListener!=null && clampedY){  
	        	
	        	if(!isComplete){
		            onScrollToBottomListener.onScrollBottomListener();  
		            isComplete = true;
	        	}
	        }  
	}  
	  
	public void setOnScrollToBottomLintener(OnScrollToBottomListener listener){  
	         this.onScrollToBottomListener = listener;  
	}  
	  
	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete() {
		this.isComplete = false;
	}

	public interface OnScrollToBottomListener {  
		public void onScrollBottomListener();  
	}  
}