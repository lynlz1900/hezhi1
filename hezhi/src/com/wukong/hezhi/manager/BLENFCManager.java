package com.wukong.hezhi.manager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.dk.bleNfc.DeviceManager;
import com.dk.bleNfc.DeviceManagerCallback;
import com.dk.bleNfc.Scanner;
import com.dk.bleNfc.ScannerCallback;
import com.dk.bleNfc.card.Ntag21x;
import com.dk.bleNfc.card.Ultralight.onReceiveCmdListener;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.SoundUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import java.nio.charset.Charset;

/**
 * 
 * @ClassName: BLENFCManager
 * @Description: TODO(手机连接蓝牙吊，坠蓝牙吊坠读取nfc)
 * @author HuZhiyin
 * @date 2016-12-9 上午8:56:23
 * 
 */
public class BLENFCManager {
	/** 读标签线程 */
	private Thread readThread;
	/** 控制有效线程的运行 */
	public boolean startFlag = false;
	/** 将子线程无限循坏 */
	private boolean runFlag = true;
	/** 标签uid */
	// private String uid = "";
	private Scanner mScanner;
	private DeviceManager deviceManager;
	private BluetoothDevice mNearestBle = null;
	/** 蓝牙信号值（-100最弱，0最强） */
	private int lastRssi = -100;
	private Context mContext = ContextUtil.getContext();;

	private BLENFCManager() {
		if (Constant.NFC.IS_BLE_NFC) {
			init();
		}
	}

	private static class Holder {
		private static final BLENFCManager SINGLETON = new BLENFCManager();
	}

	/**
	 * 单一实例
	 */
	public static BLENFCManager getInstance() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {// 蓝牙版本过低
			ScreenUtil.showToast(ContextUtil.getString(R.string.blue_version_low_tips));
			return null;
		}
		return Holder.SINGLETON;
	}

	private void init() {
		// 初始设备操作类
		mScanner = new Scanner(ContextUtil.getContext(), scannerCallback);
		deviceManager = new DeviceManager(ContextUtil.getContext());
		deviceManager.setCallBack(deviceManagerCallback);
	}

	// Scanner 回调
	private ScannerCallback scannerCallback = new ScannerCallback() {
		@Override
		public void onReceiveScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
			super.onReceiveScanDevice(device, rssi, scanRecord);
			System.out.println("Activity搜到设备：" + device.getName() + "信号强度：" + rssi);
			// 搜索蓝牙设备并记录信号强度最强的设备
			if ((device.getName() != null)
					&& (device.getName().contains("UNISMES") || device.getName().contains("BLE_NFC"))) {
				if (mNearestBle != null) {
					if (rssi > lastRssi) {
						mNearestBle = device;
					}
				} else {
					mNearestBle = device;
					lastRssi = rssi;
				}
			}
		}

		@Override
		public void onScanDeviceStopped() {
			super.onScanDeviceStopped();
		}
	};

	// 设备操作类回调
	private DeviceManagerCallback deviceManagerCallback = new DeviceManagerCallback() {
		@Override
		public void onReceiveConnectBtDevice(boolean blnIsConnectSuc) {
			super.onReceiveConnectBtDevice(blnIsConnectSuc);
			if (blnIsConnectSuc) {
				System.out.println("Activity设备连接成功");
				// shotTips(ContextUtil
				// .getString(R.string.blue_conect_sucess_tips));

				ThreadUtil.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						bleCallBack.onSucess(ContextUtil.getString(R.string.blue_conect_sucess_tips));
					}
				});

				// 连接上后延时500ms后再开始发指令
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startFlag = true;
			}
		}

		@Override
		public void onReceiveDisConnectDevice(boolean blnIsDisConnectDevice) {
			super.onReceiveDisConnectDevice(blnIsDisConnectDevice);
			System.out.println("Activity设备断开链接");

			ThreadUtil.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					bleCallBack.onFail("断开连接");
				}
			});

			// shotTips(ContextUtil.getString(R.string.device_disconnect));
		}

		@Override
		public void onReceiveConnectionStatus(boolean blnIsConnection) {
			super.onReceiveConnectionStatus(blnIsConnection);
			System.out.println("Activity设备链接状态回调");
		}

		@Override
		public void onReceiveInitCiphy(boolean blnIsInitSuc) {
			super.onReceiveInitCiphy(blnIsInitSuc);
		}

		@Override
		public void onReceiveDeviceAuth(byte[] authData) {
			super.onReceiveDeviceAuth(authData);
		}

		@Override
		public void onReceiveRfnSearchCard(boolean blnIsSus, int cardType, byte[] bytCardSn, byte[] bytCarATS) {
			super.onReceiveRfnSearchCard(blnIsSus, cardType, bytCardSn, bytCarATS);
			if (!blnIsSus) {
				return;
			}
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < bytCardSn.length; i++) {
				stringBuffer.append(String.format("%02x", bytCardSn[i]));
			}

			StringBuffer stringBuffer1 = new StringBuffer();
			for (int i = 0; i < bytCarATS.length; i++) {
				stringBuffer1.append(String.format("%02x", bytCarATS[i]));
			}
			System.out.println("Activity接收到激活卡片回调：UID->" + stringBuffer + " ATS->" + stringBuffer1);
		}

		@Override
		public void onReceiveRfmSentApduCmd(byte[] bytApduRtnData) {
			super.onReceiveRfmSentApduCmd(bytApduRtnData);

			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < bytApduRtnData.length; i++) {
				stringBuffer.append(String.format("%02x", bytApduRtnData[i]));
			}
			System.out.println("Activity接收到APDU回调：" + stringBuffer);
		}

		@Override
		public void onReceiveRfmClose(boolean blnIsCloseSuc) {
			super.onReceiveRfmClose(blnIsCloseSuc);
		}
	};

	private IBleCallBack bleCallBack;

	/** 搜索蓝牙 */
	public void connect(IBleCallBack mBleCallBack) {
		this.bleCallBack = mBleCallBack;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.blue_version_low_tips));
			return;
		}

		runFlag = true;
		startFlag = true;
		if (readThread == null) {
			readThread = new InventoryThread();
			readThread.start();
		}

		if (deviceManager.isConnection()) {
			deviceManager.requestDisConnectDevice();
			return;
		}
		// shotTips(ContextUtil.getString(R.string.searching_device));
		if (!mScanner.isScanning()) {
			mScanner.startScan(0);
			mNearestBle = null;
			lastRssi = -100;
			new Thread(new Runnable() {
				@Override
				public void run() {
					int searchCnt = 0;
					while ((mNearestBle == null) && (searchCnt < 5000) && (mScanner.isScanning())) {
						searchCnt++;
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mScanner.stopScan();
					if (mNearestBle != null) {
						mScanner.stopScan();
						// shotTips(ContextUtil
						// .getString(R.string.connecting_device));
						deviceManager.requestConnectBleDevice(mNearestBle.getAddress());
					} else {
						// shotTips(ContextUtil
						// .getString(R.string.not_find_device));
						ScreenUtil.showToast(ContextUtil.getString(R.string.not_find_device));

						ThreadUtil.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								bleCallBack.onFail(ContextUtil.getString(R.string.not_find_device));
							}
						});

					}
				}
			}).start();
		}
	}

	public interface IBleCallBack {
		/** 连接成功 */
		void onSucess(String sucessTip);

		/** 连接失败 */
		void onFail(String failTip);

		/** 读取成功 */
		void onRead(String uid, String result);

	}

	// 读卡Demo
	private void readCardDemo() {
		System.out.println("Activity发送寻卡/激活指令");
		deviceManager.requestRfmSearchCard((byte) 0x00, new DeviceManager.onReceiveRfnSearchCardListener() {
			@Override
			public void onReceiveRfnSearchCard(final boolean blnIsSus, int cardType, byte[] bytCardSn,
					byte[] bytCarATS) {
				deviceManager.mOnReceiveRfnSearchCardListener = null;
				if (blnIsSus) {
					if (cardType == DeviceManager.CARD_TYPE_ULTRALIGHT) { // 寻到Ultralight卡
						final Ntag21x card = (Ntag21x) deviceManager.getCard();
						if (card != null && !isSameUid(card)) {// 此次读到的uid和上一次读到的不一致
							cmdUid(card);
						}

					}
				}
			}

		});
	}

	private String uid;

	/** 两次读取的uid是否相同 */
	private boolean isSameUid(Ntag21x card) {
		boolean flag = true;
		synchronized (this) {

			if (TextUtils.isEmpty(uid)) {// 第一次读
				LogUtil.e("uidis_EMPTY");
				uid = card.uidToString();
				flag = false;

			} else {
				String currentUid = card.uidToString();
				if (!TextUtils.isEmpty(currentUid) && currentUid.equals(uid)) {// 如果uid相等
					ThreadUtil.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							uid = "";
						}
					}, 3000);// 3秒后将uid置空
					LogUtil.e("uidisSame_YES");
					flag = true;

				} else {
					LogUtil.e("uidisSame_NO");
					flag = false;

				}
			}
		}
		return flag;

	}

	// 读断开状态
	private void cmdBreak(final Ntag21x card) {
		byte[] cmd = { (byte) 0xAF, (byte) 0x00 };// 有断开状态的卡读取

		card.cmd(cmd, new onReceiveCmdListener() {
			@Override
			public void onReceiveCmd(byte[] payload) {
				// TODO Auto-generated method stub

				if (payload.length < 2) {// 如果payload数组长度小于2,可以认为是不可以熔断的标签
					Constant.NFC.IS_BREAK = 1;// 未断开
				} else {
					if ((payload[0] == (byte) 0x00) && (payload[1] == (byte) 0x00)) {
						Constant.NFC.IS_BREAK = 1;// 未断开
					} else if ((payload[0] == (byte) 0xFF) && (payload[1] == (byte) 0xFF)) {
						Constant.NFC.IS_BREAK = 0;// 断开
					}
				}

			}

		});
	}

	// 读取uid
	private void cmdUid(final Ntag21x card) {
		byte[] cmd = new byte[] { (byte) 0x30, (byte) 0x04 };// 卡请求
		card.cmd(cmd, new onReceiveCmdListener() {
			@Override
			public void onReceiveCmd(byte[] arg0) {
				final String msg = new String(arg0, Charset.forName("US-ASCII"));
				cmdBreak(card);// 必须读完才能再读，否则无法读取！！！！

				ThreadUtil.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						SoundUtil.vibrate();
						String uid = card.uidToString();
						String result = msg;
						// ScreenUtil.showToast(ContextUtil.getString(R.string.authorization_code)+":"
						// + result + "\nUID:" + uid);
						if (!TextUtils.isEmpty(result) && result.length() > 12) {
							try {
								result = result.trim().substring(0, 12);
							} catch (Exception e) {
								result = "";
							}

						}
						NFCManager.uid = uid;
						NFCManager.authCode = result;
						bleCallBack.onRead(uid, result);
					}
				});
			}
		});
	}

	private class InventoryThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (runFlag) {
				if (startFlag && Constant.NFC.IS_BLE_NFC) {
					readCardDemo();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void disconnect() {
		// shotTips(ContextUtil.getString(R.string.diconnect_blue));
		runFlag = false;
		startFlag = false;
		readThread = null;
		if (deviceManager != null && mScanner != null) {
			if (deviceManager.isConnection()) {
				deviceManager.requestDisConnectDevice();
			}
			mScanner.stopScan();
			deviceManager.requestRfmClose();
		}
	}

}
