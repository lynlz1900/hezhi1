package com.wukong.hezhi.ui.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.wukong.hezhi.R;

/**
 * 
 * @ClassName: HeadView
 * @Description: TODO(activity页面统一的头部)
 * @author HuZhiyin
 * @date 2017-1-31 上午9:01:59
 * 
 */
public class HeadView {
	private Activity activity;

	/**
	 * 左边的按钮 右边的按钮 标题
	 */
	protected TextView titleLeftTv, titleRightTv, titleTv;

	/**
	 * titleView: 标题栏
	 */
	private View titleView;
	private Integer includeTitleLayoutId;
	private LinearLayout mContentView;
	private View.OnClickListener leftClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (activity != null)
				activity.finish();
		}
	};

	public HeadView(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 包含item_title文件且iten_inlcude增加了Id
	 * 
	 * @param activity
	 * @param includeTitleLayoutId
	 */
	public HeadView(Activity activity, Integer includeTitleLayoutId) {
		this.activity = activity;
		this.includeTitleLayoutId = includeTitleLayoutId;
	}

	private void initContentView() {
		LinearLayout.LayoutParams Viewlp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		// 初始化正文并添加标题栏
		mContentView = new LinearLayout(activity);
		mContentView.setOrientation(LinearLayout.VERTICAL);
		mContentView.setLayoutParams(Viewlp);
	}

	// public void setStatusBar() {
	// StatusBarUtil.setColor(activity, ContextUtil.getColor(R.color.base));//
	// 设置状态栏颜色
	// }

	private void addTitleView() {
		if (includeTitleLayoutId == null) {
			titleView = View.inflate(activity, R.layout.layout_header, null);
			mContentView.addView(titleView);
		}
	}

	/**
	 * 默认增加了滑动，并且自动添加标题栏
	 * 
	 * @param contentLayout
	 */
	public void attachActivity(int contentLayout) {
		attachActivity(contentLayout, true);
	}

	/**
	 * 相当于已经设置了setContentView（）并且加上了标题
	 * 
	 * @param isAddScroll
	 *            是否需要添加满屏后可滑动
	 */
	public void attachActivity(int contentLayout, Boolean isAddScroll) {
		getContentView(contentLayout, isAddScroll);
		activity.setContentView(mContentView);
		// setStatusBar();
	}

	public View getContentView(int contentLayout, Boolean isAddScroll) {
		initContentView();
		addTitleView();
		addCustomView(contentLayout, isAddScroll);
		initTitleView();
		return mContentView;
	}

	private void addCustomView(int contentLayout, Boolean isAddScroll) {
		View contentView = View.inflate(activity, contentLayout, null);
		ScrollView.LayoutParams contentLp = new ScrollView.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		contentView.setLayoutParams(contentLp);
		if (isAddScroll && includeTitleLayoutId == null) {
			ScrollView mScrollView = new ScrollView(activity);
			mScrollView.setScrollbarFadingEnabled(false);
			mScrollView.setVerticalScrollBarEnabled(false);
			mScrollView.setFillViewport(true);
			LinearLayout.LayoutParams scrollView = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 1);
			mScrollView.setLayoutParams(scrollView);
			mScrollView.addView(contentView);
			mContentView.addView(mScrollView);
		} else if (!isAddScroll && includeTitleLayoutId == null) {
			mContentView.addView(contentView);
		}
	}

	/**
	 * 初始化基类的view
	 */
	private void initTitleView() {
		if (titleView == null && includeTitleLayoutId != null)
			titleView = mContentView.findViewById(includeTitleLayoutId);
		titleLeftTv = (TextView) titleView.findViewById(R.id.header_left);
		titleRightTv = (TextView) titleView.findViewById(R.id.header_right);
		titleTv = (TextView) titleView.findViewById(R.id.header_title);
		setLeftTitleText("");
	}

	public void setTitleStr(String titleStr) {
		if (titleTv != null)
			titleTv.setText(titleStr);
	}

	/**
	 * setLeftTitlePic : 设置左边的文字
	 */
	public void setLeftTitleText(String str) {
		setLeftTitleText(str, R.drawable.icon_back);
	}

	/**
	 * setLeftTitlePic : 设置左边的文字与图片
	 */
	public void setLeftTitleText(String str, int res) {
		if (titleLeftTv != null) {
			if(TextUtils.isEmpty(str) && res == -1)
				titleLeftTv.setVisibility(View.GONE);
			else{
				titleLeftTv.setText(str);
				titleLeftTv.setVisibility(View.VISIBLE);
				titleLeftTv.setOnClickListener(leftClick);
				setDrawableLeft(titleLeftTv, res);
			}
		}
	}

	
	public void setRihgtTitleText(String str){
		setRihgtTitleText(str,-1);
	}
	
	/**
	 * setRihgtTitleText : 设置右边的文字与图片
	 */
	public void setRihgtTitleText(String str, int res) {
		if (titleRightTv != null) {
			if(TextUtils.isEmpty(str) && res == -1)
				titleRightTv.setVisibility(View.GONE);
			else{
				titleRightTv.setText(str);
				titleRightTv.setVisibility(View.VISIBLE);
				setDrawableRight(titleRightTv, res);
			}
		}
	}

	public void setDrawableLeft(TextView v, int res) {
		Drawable drawable = null;
		if (res != -1) {
			drawable = activity.getResources().getDrawable(res);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
		}
		v.setCompoundDrawables(drawable, null, null, null);
	}

	public void setDrawableRight(TextView v, int res) {
		Drawable drawable = null;
		if (res != -1) {
			drawable = activity.getResources().getDrawable(res);
			// / 这一步必须要做,否则不会显示.
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
		}
		v.setCompoundDrawables(drawable, null, null, null);
	}

	/**
	 * setRightText : 设置右边的文字
	 */
	// public void setRightTitleText(String str) {
	// setRightTitleText(str, Color.TRANSPARENT);
	// }

	/**
	 * setRightText : 设置右边的文字
	 */
	public void setRightTitleText(String str) {
		if (titleRightTv != null) {
			titleRightTv.setText(str);
			titleRightTv.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * setLeftText : 设置左边的文字和颜色,图标，图标间距，字体大小
	 */
	public void setLeftTitleTextMore(String str,int color,int res,int padding,int textSize){
		if(titleLeftTv != null){
			titleLeftTv.setText(str);
			titleLeftTv.setTextColor(color);
			titleLeftTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					textSize);
			titleLeftTv.setVisibility(View.VISIBLE);
			
			Drawable drawable = null;
			if (res != -1) {
				drawable = activity.getResources().getDrawable(res);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
			}
			titleLeftTv.setCompoundDrawables(drawable, null, null, null);
			titleLeftTv.setCompoundDrawablePadding(padding);
		}
	}
	
	/** 左边监听 */
	public void setLeftLis(View.OnClickListener lis) {
		titleLeftTv.setOnClickListener(lis);
	}

	/** 右边监听 */
	public void setRightLis(View.OnClickListener lis) {
		titleRightTv.setOnClickListener(lis);
	}

	/** 中间 */
	public void setTitleLis(View.OnClickListener lis) {
		titleTv.setOnClickListener(lis);
	}

}
