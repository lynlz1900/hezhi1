package com.wukong.hezhi.utils;

import android.os.SystemClock;

public class NoDoubleClickUtil {

	/** google api 数组数表示点击的次数 */
	static long[] mHits = new long[2];

	/** 是否连续点击 */
	public static boolean isDoubleClk() {
		// 每点击一次 实现左移一格数据
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		// 给数组的最后赋当前时钟值
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();
		// 当0出的值大于当前时间-500时 证明在500秒内点击了2次
		if (mHits[0] > SystemClock.uptimeMillis() - 500) {
			return true;
		} else {
			return false;
		}
	}

}