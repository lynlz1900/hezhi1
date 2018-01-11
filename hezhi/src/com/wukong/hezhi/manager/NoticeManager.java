package com.wukong.hezhi.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.wukong.hezhi.R;

public class NoticeManager {
	private NoticeManager() {
	}

	private static class Holder {
		private static final NoticeManager SINGLETON = new NoticeManager();
	}

	/**
	 * 单一实例
	 * */
	public static NoticeManager getInstance() {
		return Holder.SINGLETON;
	}

	/**
	 * 发送通知
	 */
	public void sendNotification(Context context, String content, Intent intent) {
		int i = (int) (Math.random() * 100000);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setTicker(content)
				.setAutoCancel(true)
				.setContentText(content)
				.setContentTitle(
						context.getResources().getString(R.string.app_name))
				.setSmallIcon(R.drawable.icon_hezhi_logo);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, i,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(i, builder.build());

		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// 点亮屏幕
		wl.acquire();
		// 释放
		wl.release();
	}
}
