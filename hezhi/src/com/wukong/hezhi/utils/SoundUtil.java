package com.wukong.hezhi.utils;

import com.wukong.hezhi.R;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;

/**
 * 
 * @ClassName: SoundUtil
 * @Description: TODO(声音和震动工具类)
 * @author HuZhiyin
 * @date 2017-1-3 上午8:27:58
 *
 */
public class SoundUtil {
	public static Context mContext = ContextUtil.getContext();

	/** 震动 */
	public static void vibrate() {
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
		vibrator.cancel();
	}

	/** 系统提示音 */
	public static void startAlarm() {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (notification == null)
			return;
		Ringtone r = RingtoneManager.getRingtone(mContext, notification);
		r.play();
	}

	private static SoundPool soundPool;

	/** 初始化声音 ,此过程比较耗时，放在Application中执行，之后只需调用play即可 */
	@SuppressWarnings("deprecation")
	public static void initSound() {
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(mContext, R.raw.msg, 1);
	}

	/** 播放B音 */
	public static void playB() {
		if (soundPool != null) {
			soundPool.play(1, 1, 1, 0, 0, 1);
		}
	}
}
