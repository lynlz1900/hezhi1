package com.wukong.hezhi.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class TitlePopup extends PopupWindow {
	/*
	 * 内部类
	 */
	public class ActionItem {
		// 定义图片对象
		public Drawable mDrawable;
		// 定义文本对象
		public CharSequence mTitle;

		public ActionItem(Drawable drawable, CharSequence title) {
			this.mDrawable = drawable;
			this.mTitle = title;
		}

		public ActionItem(Context context, int titleId, int drawableId) {
			this.mTitle = context.getResources().getText(titleId);
			this.mDrawable = context.getResources().getDrawable(drawableId);
		}

		public ActionItem(Context context, CharSequence title, int drawableId) {
			this.mTitle = title;
			this.mDrawable = context.getResources().getDrawable(drawableId);
		}
	}

	private Context mContext;
	// 弹框在上面显示还是下面显示
	private boolean isdown;

	// 实例化一个矩形
	private Rect mRect = new Rect();

	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];

	// 屏幕的宽度和高度
	private int mScreenWidth, mScreenHeight;

	// popupwindow的高度和宽度
	private int popupwindowHeight, popupwindowWith;

	// 判断是否需要添加或更新列表子类项
	private boolean mIsDirty;

	// 位置不在中心
	private int popupGravity = Gravity.NO_GRAVITY;

	// 弹窗子类项选中时的监听
	private OnItemOnClickListener mItemOnClickListener;

	// 定义列表对象
	private ListView mListView;

	/** 动画显示的位置,0，从左，1，中间，2，最右边 */
	private int animLocation = 0;

	public static final int ANIMATION_LEFT = 0;
	public static final int ANIMATION_CENTER = 1;
	public static final int ANIMATION_RIGHT = 2;

	public void setAnimLocation(int animLocation) {
		this.animLocation = animLocation;
	}

	// 定义弹窗子类项列表
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();
	// popuwindow布局北京
	private LinearLayout layout;
	// 由于listview的高度不好计算，所以在次设置一个固定的item高度。
	int itemHeight = (int) ContextUtil.getResource().getDimension(
			R.dimen.popuwindow_item_height);

	public TitlePopup(Context context) {
		// 设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public TitlePopup(Context context, int width, int height) {
		this.mContext = context;
		// 设置可以获得焦点
		setFocusable(true);
		// 设置弹窗内可点击
		setTouchable(true);
		// 设置弹窗外可点击
		setOutsideTouchable(true);

		// 获得屏幕的宽度和高度
		mScreenWidth = ScreenUtil.getScreenWidth();
		mScreenHeight = ScreenUtil.getScreenHeight();

		// 设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

		setBackgroundDrawable(new BitmapDrawable());
		layout = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.litview_popup, null);
		// 设置弹窗的布局界面
		setContentView(layout);
		initUI();

	}

	/**
	 * 初始化弹窗列表
	 */
	private void initUI() {
		mListView = (ListView) getContentView().findViewById(R.id.title_list);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// 点击子类项后，弹窗消失
				dismiss();
				if (mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index),
							index);
			}
		});
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view) {
		// 获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),
				mLocation[1] + view.getHeight());
		// 判断是否需要添加或更新列表子类项
		if (mIsDirty) {
			populateActions();
		}

		int itemCounts = mActionItems.size();
		// 计算listview的高度(即popwindow高度),itemHeight+1,1是pix单位，listview自身包含分割线的高度值。
		// popupwindowHeight = (itemHeight + 1) * itemCounts;
		popupwindowHeight = (int) ContextUtil.getResource().getDimension(
				R.dimen.popuwindow_height);
		// 计算listview宽度(即popwindow宽度)
		popupwindowWith = (int) ContextUtil.getResource().getDimension(
				R.dimen.popupwindow_red_with);

		/***
		 * 判断动画出来的位置
		 */
		int view_x = view.getLeft() + view.getWidth() / 2;
		if (view_x > mScreenWidth / 2) {
			animLocation = ANIMATION_RIGHT;
		} else if (view_x == mScreenWidth / 2) {
			animLocation = ANIMATION_CENTER;
		} else if (view_x < mScreenWidth / 2) {
			animLocation = ANIMATION_LEFT;
		}

		// 显示弹窗的位置
		if (mScreenHeight - mLocation[1] > popupwindowHeight + itemHeight) {// 判断popupwindow在被点击的控件下方是否能显示的下。如果显示不下，则显示在控件的上方。
			isdown = true;

			switch (animLocation) {
			case ANIMATION_LEFT:
				int x = view.getLeft() + view.getWidth() / 2;
				this.setAnimationStyle(R.style.popwindow_anim_top_left_style);
				showAtLocation(view, popupGravity, x, mRect.bottom);
				break;
			case ANIMATION_CENTER:
				int x1 = view.getLeft() + view.getWidth() / 2 - popupwindowWith
						/ 2;
				this.setAnimationStyle(R.style.popwindow_anim_top_center_style);
				showAtLocation(view, popupGravity, x1, mRect.bottom);
				break;
			case ANIMATION_RIGHT:
				int x2 = view.getLeft() + view.getWidth() / 2 - popupwindowWith;
				this.setAnimationStyle(R.style.popwindow_anim_top_right_style);
				showAtLocation(view, popupGravity, x2, mRect.bottom);
				break;
			}

		} else {
			isdown = false;
			switch (animLocation) {
			case ANIMATION_LEFT:
				int x = view.getLeft() + view.getWidth() / 2;
				this.setAnimationStyle(R.style.popwindow_anim_buttom_left_style);
				showAtLocation(view, popupGravity, x, mLocation[1]
						- popupwindowHeight);
				break;
			case ANIMATION_CENTER:
				int x1 = view.getLeft() + view.getWidth() / 2 - popupwindowWith
						/ 2;
				this.setAnimationStyle(R.style.popwindow_anim_buttom_center_style);
				showAtLocation(view, popupGravity, x1, mLocation[1]
						- popupwindowHeight);
				break;
			case ANIMATION_RIGHT:
				int x2 = view.getLeft() + view.getWidth() / 2 - popupwindowWith;
				this.setAnimationStyle(R.style.popwindow_anim_buttom_right_style);
				showAtLocation(view, popupGravity, x2, mLocation[1]
						- popupwindowHeight);
				break;

			}
		}

	}

	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions() {
		mIsDirty = false;

		// 设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				TextView textView = null;
				if (convertView == null) {
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(
							R.color.text_main));
					/** 屏幕适配需要用到 */
					textView.setTextSize(ScreenUtil
							.getSpSize(R.dimen.text_size_normal));
					// 设置文本居中
					textView.setGravity(Gravity.CENTER);
					int padding = ScreenUtil.dp2px(10);
					// // 设置文本域的范围
					textView.setPadding(padding, 0, padding, 0);
					// 设置文本在一行内显示（不换行）
					textView.setSingleLine(true);
				} else {
					textView = (TextView) convertView;
				}

				ActionItem item = mActionItems.get(position);

				// 设置文本文字
				textView.setText(item.mTitle);
				// 设置文字与图标的间隔
				textView.setCompoundDrawablePadding(ScreenUtil.dp2px(3));
				// 设置在文字的左边放一个图标
				textView.setCompoundDrawablesWithIntrinsicBounds(
						item.mDrawable, null, null, null);
				textView.setHeight(itemHeight);
				return textView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}

			@Override
			public int getCount() {
				return mActionItems.size();
			}
		});

	}

	/**
	 * 添加子类项
	 */
	public void addAction(ActionItem action) {
		if (action != null) {
			mActionItems.add(action);
			mIsDirty = true;
		}
	}

	/**
	 * 清除子类项
	 */
	public void cleanAction() {
		if (mActionItems.isEmpty()) {
			mActionItems.clear();
			mIsDirty = true;
		}
	}

	/**
	 * 根据位置得到子类项
	 */
	public ActionItem getAction(int position) {
		if (position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}

	/**
	 * 设置监听事件
	 */
	public void setItemOnClickListener(
			OnItemOnClickListener onItemOnClickListener) {
		this.mItemOnClickListener = onItemOnClickListener;
	}

	/**
	 * @author huzy 功能描述：弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener {
		public void onItemClick(ActionItem item, int position);
	}

}
