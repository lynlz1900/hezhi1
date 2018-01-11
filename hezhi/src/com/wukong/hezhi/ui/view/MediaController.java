package com.wukong.hezhi.ui.view;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
* @ClassName: MediaController 
* @Description: TODO(自定义的MediaController) 
* @author HuZhiyin 
* @date 2017-5-31 上午8:58:11 
*
 */
public class MediaController extends android.widget.MediaController {

	private Activity mActivity;
	private LinearLayout linearLayout;
	private View mView;// 根视图
	private RelativeLayout header;// 头部布局

	public MediaController(Activity activity) {
		super(activity);
		mActivity = activity;
	}

	public MediaController(Activity activity, View view) {
		super(activity);
		this.mActivity = activity;
		this.mView = view;
	}

	@Override
	public void setAnchorView(View view) {
		super.setAnchorView(view);
		setVideoHeaderView();
		setMediaController();
	}

	/** 设置视频头部布局 */
	private void setVideoHeaderView() {
		header = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.layout_video_header, null);
		((ViewGroup) mView).addView(header);
	}

	/** 设置自定义控制器 */
	private void setMediaController() {
		linearLayout = (LinearLayout) LayoutInflater.from(getContext())
				.inflate(R.layout.layout_media_controller, null);
		ImageButton pause = (ImageButton) linearLayout.findViewById(R.id.pause);
		final ImageButton button = (ImageButton) linearLayout
				.findViewById(R.id.is_full_screen);
		TextView time = (TextView) linearLayout.findViewById(R.id.time);
		TextView time_current = (TextView) linearLayout
				.findViewById(R.id.time_current);
		SeekBar mediacontroller_progress = (SeekBar) linearLayout
				.findViewById(R.id.mediacontroller_progress);
		try {
			Field mRoot = android.widget.MediaController.class
					.getDeclaredField("mRoot");
			mRoot.setAccessible(true);
			ViewGroup mRootVg = (ViewGroup) mRoot.get(this);
			mRootVg.removeAllViewsInLayout();
			mRootVg.addView(linearLayout);

			Field mProgress = android.widget.MediaController.class
					.getDeclaredField("mProgress");
			mProgress.setAccessible(true);
			mProgress.set(this, mediacontroller_progress);
			Field mSeekListener = android.widget.MediaController.class
					.getDeclaredField("mSeekListener");
			mSeekListener.setAccessible(true);
			mediacontroller_progress
					.setOnSeekBarChangeListener((OnSeekBarChangeListener) mSeekListener
							.get(this));

			Field mPauseButton = android.widget.MediaController.class
					.getDeclaredField("mPauseButton");
			mPauseButton.setAccessible(true);
			mPauseButton.set(this, pause);

			Field mPauseListener = android.widget.MediaController.class
					.getDeclaredField("mPauseListener");
			mPauseListener.setAccessible(true);
			pause.setOnClickListener((OnClickListener) mPauseListener.get(this));

			Field mEndTime = android.widget.MediaController.class
					.getDeclaredField("mEndTime");
			mEndTime.setAccessible(true);
			mEndTime.set(this, time);

			Field mCurrentTime = android.widget.MediaController.class
					.getDeclaredField("mCurrentTime");
			mCurrentTime.setAccessible(true);
			mCurrentTime.set(this, time_current);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
					button.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_fullscreen));
				} else {// 竖屏

					button.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_halfscreen));
				}
				clickIsFullScreenListener.setOnClickIsFullScreen();

			}
		});
	}

	private onClickIsFullScreenListener clickIsFullScreenListener;

	// 全屏半屏切换接口
	public interface onClickIsFullScreenListener {
		public void setOnClickIsFullScreen();
	}

	public void setClickIsFullScreenListener(
			onClickIsFullScreenListener listener) {
		this.clickIsFullScreenListener = listener;
	}

	private ViewGroup findSeekBarParent(ViewGroup vg) {
		ViewGroup viewGroup = null;
		for (int i = 0; i < vg.getChildCount(); i++) {
			View view = vg.getChildAt(i);
			if (view instanceof SeekBar) {
				viewGroup = (ViewGroup) view.getParent();
				break;
			} else if (view instanceof ViewGroup) {
				viewGroup = findSeekBarParent((ViewGroup) view);
			} else {
				continue;
			}
		}
		return viewGroup;
	}

	@Override
	public void show(int timeout) {
		super.show(timeout);
		((ViewGroup) mView).removeView(header);
		((ViewGroup) mView).addView(header);
	}

	@Override
	public void hide() {
		super.hide();
		((ViewGroup) mView).removeView(header);
	}

}
