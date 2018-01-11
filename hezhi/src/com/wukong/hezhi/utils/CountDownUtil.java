package com.wukong.hezhi.utils;

import android.os.Handler;
import android.widget.CheckBox;

public class CountDownUtil {
	/** 倒计时长 （秒） */
	private int timeSum = 60;
	private int timeCount = timeSum;
	private boolean start = true;
	private CheckBox checkBox;

	public CountDownUtil(int timeSum, CheckBox checkBox) {
		super();
		this.timeSum = timeSum;
		this.checkBox = checkBox;
		countdown();
	}

	/** 倒计时 */
	public void countdown() {
		start = true;
		checkBox.setClickable(false);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (start && checkBox != null) {
					if (timeCount > 0) {
						timeCount--;
						checkBox.setText(timeCount + "s");
						handler.postDelayed(this, 1000);
					}
					if (timeCount == 0) {
						checkBox.setClickable(true);
						checkBox.setText("重新获取");
						checkBox.setChecked(false);
						timeCount = timeSum;
						start = false;
					}
				}
			}
		}, 1000);
	}
}
