package com.wukong.hezhi.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: KeyboardManager
 * @Description: TODO(键盘弹出收缩监听)
 * @author HuZhiyin
 * @date 2017-7-7 上午11:57:33
 * 
 */
public class KeyboardManager implements ViewTreeObserver.OnGlobalLayoutListener {

	private KeyboardManager() {
	}

	/** 此处没有用到单例，当前后两个页面都需要setKeyBoardListener的时候，从后一个页面回到前一个页面，会出问题，故此 */
	public static KeyboardManager getInstance() {
		return new KeyboardManager();
	}

	private static final String TAG = "ListenerHandler";
	private View mContentView;
	private int mOriginHeight;
	private int mPreHeight;
	private KeyBoardListener mKeyBoardListen;

	public interface KeyBoardListener {
		/**
		 * call back
		 * 
		 * @param isShow
		 *            true is show else hidden
		 * @param keyboardHeight
		 *            keyboard height
		 */
		void onKeyboardChange(boolean isShow, int keyboardHeight);
	}

	public void setKeyBoardListener(Activity contextObj,
			KeyBoardListener keyBoardListen) {
		if (contextObj == null) {
			Log.i(TAG, "contextObj is null");
			return;
		}
		mContentView = findContentView(contextObj);
		if (mContentView != null) {
			addContentTreeObserver();
		}

		this.mKeyBoardListen = keyBoardListen;
	}

	private View findContentView(Activity contextObj) {
		return contextObj.findViewById(android.R.id.content);
	}

	private void addContentTreeObserver() {
		mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		int currHeight = mContentView.getHeight();
		if (currHeight == 0) {
			Log.i(TAG, "currHeight is 0");
			return;
		}
		boolean hasChange = false;
		if (mPreHeight == 0) {
			mPreHeight = currHeight;
			mOriginHeight = currHeight;
		} else {
			if (mPreHeight != currHeight) {
				hasChange = true;
				mPreHeight = currHeight;
			} else {
				hasChange = false;
			}
		}
		if (hasChange) {
			boolean isShow;
			int keyboardHeight = 0;
			if (mOriginHeight == currHeight) {
				// hidden
				isShow = false;
			} else {
				// show
				keyboardHeight = mOriginHeight - currHeight;
				isShow = true;
			}

			if (mKeyBoardListen != null) {
				mKeyBoardListen.onKeyboardChange(isShow, keyboardHeight);
			}
		}
	}

	public void destroy() {
		if (mContentView != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				mContentView.getViewTreeObserver()
						.removeOnGlobalLayoutListener(this);
			}
		}
	}

	/** 隐藏键盘 */
	public void hideKeyboard(Activity activity) {
		if(activity == null || activity.getCurrentFocus() == null) return;
		InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(), 0); // 强制隐藏键盘
	}

	/** 如果输入法在窗口上已经显示，则隐藏，反之则显示 */
	public void showKeybord(Activity activity) {
		InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
