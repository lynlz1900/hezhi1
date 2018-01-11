package com.wukong.hezhi.manager;

import java.io.IOException;
import java.nio.charset.Charset;

import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.function.nfcvreader.DataConvert;
import com.wukong.hezhi.function.nfcvreader.NfcCommands;
import com.wukong.hezhi.http.HttpURLUtil;
import com.wukong.hezhi.ui.activity.DisResultActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.CoverterUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.StringUtil;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @ClassName: NFCManager
 * @Description: TODO(NFC管理类)
 * @author HuZhiyin
 * @date 2017-5-3 上午11:37:12
 * 
 */
public class NFCManager {
	/** 授权码 */
	public static String authCode = "";
	/** 标签uid */
	public static String uid = "";
	/** 标签epc */
	public static String epc = "";
	/** ndef格式标签里的信息 */
	public static String ndefMsg = "";

	public static String UID = "";
	public static String INDEX = "";
	public static String TOKEN = "";
	public static String RANDOM = "";

	/** 手机是否支持nfc */
	public static boolean isNFCPhone = true;
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private boolean isFirst = true;

	private Activity mActivtiy;

	private NFCManager() {
	}

	private static class Holder {
		private static final NFCManager SINGLETON = new NFCManager();
	}

	/**
	 * 单一实例
	 */
	public static NFCManager getInstance() {
		return Holder.SINGLETON;
	}

	public void initNFC(Activity activity) {
		mActivtiy = activity;
		if (Constant.NFC.NFC_ON) {
			// 获取nfc适配器，判断设备是否支持NFC功能
			nfcAdapter = NfcAdapter.getDefaultAdapter(mActivtiy);
			if (nfcAdapter == null) {
				isNFCPhone = false;
				return;
			}
			pendingIntent = PendingIntent.getActivity(ContextUtil.getContext(), 0,
					new Intent(ContextUtil.getContext(), mActivtiy.getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
					0);
			IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
			ndef.addCategory("*/*");
			mFilters = new IntentFilter[] { ndef };// 过滤器
			mTechLists = new String[][] { new String[] { MifareClassic.class.getName() },
					new String[] { NfcA.class.getName() }, new String[] { NfcV.class.getName() } };// 允许扫描的标签类型

		}
	}

	public void onNewIntent(final Intent intent) {
		if (Constant.NFC.NFC_ON && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			toMainActivity(intent);
		}
	}

	public void onResume(Activity activity) {
		if (Constant.NFC.NFC_ON && nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(activity, pendingIntent, mFilters, mTechLists);
			if (isFirst) {
				if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(activity.getIntent().getAction())) {
					toMainActivity(activity.getIntent());
				}
				isFirst = false;
			}
		}
	}

	/**
	 * 无论当前哪个页面，都可跳转至MainActivity。
	 */
	private void toMainActivity(Intent intent) {

		if (readNdefMsg(intent)) {// 如果数据写入了ndef里，则读取ndef里面的数据。
			uid = HttpURLUtil.getPara(ndefMsg, "ud");
			authCode = HttpURLUtil.getPara(ndefMsg, "sqm");

		} else {
			uid = CoverterUtil.getUid(intent);
			authCode = readNFC(intent);
		}

		// ScreenUtil.showToast("授权码:" + authCode + "\nUID:" + uid);
		if (Constant.NFC.NFC_ON) {
			LogUtil.i(mActivtiy.getClass().getSimpleName());
			Intent check_intent = new Intent(mActivtiy, DisResultActivity.class);
			check_intent.putExtra(Constant.Extra.MAINACTIVITY_RESULT, authCode);
			check_intent.putExtra(Constant.Extra.MAINACTIVITY_UID, uid);
			check_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mActivtiy.startActivity(check_intent);
		}
	}

	// NFC标签读取
	private String readNFC(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String msg = "";
		final NfcA mNfcA = NfcA.get(tag);
		final NfcV mNfcv = NfcV.get(tag);
		try {
			if (mNfcA != null) {// 如果是1443a协议
				mNfcA.connect();
				byte[] cmd = new byte[] { (byte) 0x30, (byte) 0x04 };// 卡请求
				byte[] result = mNfcA.transceive(cmd);
				msg = new String(result, Charset.forName("US-ASCII"));
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// try {
				// byte[] cmd = { (byte) 0xAF, (byte) 0x00 };// 有断开状态的卡读取
				// final byte[] payload = mNfcA.transceive(cmd);
				// runOnUiThread(new Runnable() {
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				// if ((payload[0] == (byte) 0x00)
				// && (payload[1] == (byte) 0x00)) {
				// HezhiConfig.IS_BREAK = 1;// 未断开
				// } else if ((payload[0] == (byte) 0xFF)
				// && (payload[1] == (byte) 0xFF)) {
				// HezhiConfig.IS_BREAK = 0;// 断开
				// }
				// }
				// });
				// } catch (IOException e) {
				// HezhiConfig.IS_BREAK = -1;
				// e.printStackTrace();
				// }
				// }
				// }).start();

			} else if (mNfcv != null) {// 如果是15693协议
				if (StringUtil.isHuadaTag(uid)) {// 如果是华大芯片，则需要获取token和随机数，华大芯片的uid结尾一定是:81E0（普通15693不要走这条方法，否则读取token的时候会很慢）
					mNfcv.connect();
					byte[] tmpCmd = new byte[3];
					System.arraycopy(NfcCommands.ReadSingleBlock, 0, tmpCmd, 0, 2);
					tmpCmd[2] = 0x00;// 第0块区域,索引参数
					byte[] reslut0 = mNfcv.transceive(tmpCmd);
					String msg0 = DataConvert.bytesToHexString(reslut0);// index索引
					msg0 = msg0.substring(2, 4);
					LogUtil.d("index索引:" + msg0);

					tmpCmd[2] = 0x02;// 第二块区域
					byte[] reslut2 = mNfcv.transceive(tmpCmd);
					String msg2 = new String(reslut2, Charset.forName("US-ASCII"));
					tmpCmd[2] = 0x03;// 第三块区域
					byte[] reslut3 = mNfcv.transceive(tmpCmd);
					String msg3 = new String(reslut3, Charset.forName("US-ASCII"));
					tmpCmd[2] = 0x04;// 第四块区域
					byte[] reslut4 = mNfcv.transceive(tmpCmd);
					String msg4 = new String(reslut4, Charset.forName("US-ASCII"));
					msg = msg2.substring(1) + msg3.substring(1) + msg4.substring(1);// 授权码
					LogUtil.d("授权码:" + msg);

					byte[] selectCmd = new byte[10];
					System.arraycopy(NfcCommands.Select, 0, selectCmd, 0, 2);
					System.arraycopy(DataConvert.hexStringToBytes(uid), 0, selectCmd, 2, 8);
					mNfcv.transceive(selectCmd);
					byte[] tokenCmd = new byte[7];
					String random = (int) (Math.random() * 8999) + 1000 + "";
					LogUtil.d("随机数:" + random);
					System.arraycopy(NfcCommands.ActAu, 0, tokenCmd, 0, 3);
					System.arraycopy(random.getBytes(), 0, tokenCmd, 3, 4);
					byte[] token = mNfcv.transceive(tokenCmd);
					String tokenStr = DataConvert.bytesToHexString(token);
					tokenStr = tokenStr.substring(2);
					LogUtil.d("token:" + tokenStr);
					TOKEN = tokenStr;
					RANDOM = random;
					INDEX = msg0;
					UID = uid;
				} else {// 如果是其他芯片
					uid = CoverterUtil.getreverseUid(intent);
					mNfcv.connect();
					byte[] tmpCmd = new byte[3];
					System.arraycopy(NfcCommands.ReadSingleBlock, 0, tmpCmd, 0, 2);

					tmpCmd[2] = 0x00;// 第0块区域,索引参数
					byte[] reslut0 = mNfcv.transceive(tmpCmd);
					String msg0 = DataConvert.bytesToHexString(reslut0);// index索引
					msg0 = msg0.substring(2, 4);
					LogUtil.d("index索引:" + msg0);

					tmpCmd[2] = 0x02;// 第二块区域
					byte[] reslut2 = mNfcv.transceive(tmpCmd);
					String msg2 = new String(reslut2, Charset.forName("US-ASCII"));
					tmpCmd[2] = 0x03;// 第三块区域
					byte[] reslut3 = mNfcv.transceive(tmpCmd);
					String msg3 = new String(reslut3, Charset.forName("US-ASCII"));
					tmpCmd[2] = 0x04;// 第四块区域
					byte[] reslut4 = mNfcv.transceive(tmpCmd);
					String msg4 = new String(reslut4, Charset.forName("US-ASCII"));
					msg = msg2.substring(1) + msg3.substring(1) + msg4.substring(1);// 授权码
					LogUtil.d("授权码:" + msg);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (mNfcA != null) {
				try {
					mNfcA.close();
				} catch (IOException e) {
				}
			}
			if (mNfcv != null) {
				try {
					mNfcv.close();
				} catch (IOException e) {
				}
			}
		}

		if (!TextUtils.isEmpty(msg) && msg.length() > 12) {
			try {
				msg = msg.trim().substring(0, 12);// 取前12位
			} catch (Exception e) {
				msg = "";
			}
		}
		return msg;
	}

	/** 读取ndef里面的msg */
	private boolean readNdefMsg(Intent in) {
		resetData();// 每次从新读的时候将数据还原
		try {
			Parcelable[] rawArray = in.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// we just have noly one NdefMessage，如果你不止一个的话，那么你要遍历了。
			NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
			// we just have only one NdefRecord，如果你不止一个record，那么你也要遍历出来所有的record
			NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];

			if (mNdefRecord != null) {
				ndefMsg = new String(mNdefRecord.getPayload(), "UTF-8");
				LogUtil.d(ndefMsg);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/** 还原数据 */
	private void resetData() {
		authCode = "";
		uid = "";
		epc = "";
		ndefMsg = "";
		UID = "";
		INDEX = "";
		TOKEN = "";
		RANDOM = "";
	}

	/** 是否开启NFC */
	public static boolean isOpenNFC() {
		boolean isOpen = false;
		NfcManager manager = (NfcManager) ContextUtil.getContext().getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) { // adapter存在，能启用
			isOpen = true;
		}
		return isOpen;
	}

	/** 是否支持nfc */
	public static boolean isSupporNFC(Activity activity) {
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		if (nfcAdapter == null) {
			isNFCPhone = false;
			return false;
		} else {
			isNFCPhone = true;
			return true;
		}
	}
}
