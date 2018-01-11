package com.wukong.hezhi.receiver;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.wukong.hezhi.bean.JgExtraInfo;
import com.wukong.hezhi.bean.JpushInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.JgPushManager;
import com.wukong.hezhi.manager.NoticeManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.MsgCenterActivity;
import com.wukong.hezhi.ui.fragment.CustomizationFragment;
import com.wukong.hezhi.ui.fragment.FindFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.SPUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JgPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	public static final int MSG = 10000;
	public static final int ARTICLE = 10100;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.i(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));
		// String id =
		// JPushInterface.getRegistrationID(ContextUtil.getContext());
		// String key = PackageUtil.getAppKey(ContextUtil.getContext());

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.i(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.i(TAG,
					"[MyReceiver] 接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			if ("10000".equals(bundle
					.getString(JPushInterface.EXTRA_CONTENT_TYPE))
					|| "20000".equals(bundle
							.getString(JPushInterface.EXTRA_CONTENT_TYPE))) {// 10000，代表消息中心消息
				String jsonExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
				JgExtraInfo jgExtraInfo = (JgExtraInfo) JsonUtil.parseJson2Obj(
						jsonExtra, JgExtraInfo.class);
				JpushInfo jpushInfo = new JpushInfo();
				jpushInfo.setContent(bundle
						.getString(JPushInterface.EXTRA_MESSAGE));
				jpushInfo.setLogoUrl(jgExtraInfo.getIcon());
				jpushInfo.setTitle(jgExtraInfo.getCorpName());
				String json = JsonUtil.setJson(jpushInfo);
				SPUtil.savaToShared(ContextUtil.getContext(),
						HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.REP_POINT,
						true);
				JgPushManager.getInstance().saveJsonFromJgPush(json);
				ObserveManager.getInstance().notifyState(CustomizationFragment.class,JgPushReceiver.class,
						MSG);// 通知观察者数据发生了变化
				Intent msgIntent = new Intent(context.getApplicationContext(),
						MsgCenterActivity.class);
				NoticeManager.getInstance().sendNotification(
						ContextUtil.getContext(), jpushInfo.getContent(),
						msgIntent);
			}
			
			if ("10200".equals(bundle
					.getString(JPushInterface.EXTRA_CONTENT_TYPE))) {// 10200，代表评价回复信息
				String jsonExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
				JgExtraInfo jgExtraInfo = (JgExtraInfo) JsonUtil.parseJson2Obj(
						jsonExtra, JgExtraInfo.class);
				
				if(jgExtraInfo == null || TextUtils.isEmpty(jgExtraInfo.getUserId()))
					return;
					
				if(jgExtraInfo.getUserId().equals(UserInfoManager.getInstance().getUserId()+"")){
					JpushInfo jpushInfo = new JpushInfo();
					jpushInfo.setContent(bundle
							.getString(JPushInterface.EXTRA_MESSAGE));
					jpushInfo.setLogoUrl(jgExtraInfo.getIcon());
					jpushInfo.setTitle(jgExtraInfo.getCorpName());
					jpushInfo.setUserId(jgExtraInfo.getUserId());
					String json = JsonUtil.setJson(jpushInfo);
					SPUtil.savaToShared(ContextUtil.getContext(),
							HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.REP_POINT,
							true);
					JgPushManager.getInstance().saveJsonFromJgPush(json);
					Intent msgIntent = new Intent(context.getApplicationContext(),
							MsgCenterActivity.class);
					NoticeManager.getInstance().sendNotification(
							ContextUtil.getContext(), jpushInfo.getContent(),
							msgIntent);
				}
			}
			
			if ("10100".equals(bundle
					.getString(JPushInterface.EXTRA_CONTENT_TYPE))) {// 发表文章
				ObserveManager.getInstance().notifyState(FindFragment.class,JgPushReceiver.class,
						ARTICLE);// 通知观察者数据发生了变化
			}

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.i(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.i(TAG, "[MyReceiver] 用户点击打开了通知");
			// JumpActivityManager.getInstance().toActivity(ActivitiesManager.getInstance().currentActivity(),
			// MsgCenterActivity.class);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.i(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.d(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle
						.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(
							bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it = json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" + myKey + " - "
								+ json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
