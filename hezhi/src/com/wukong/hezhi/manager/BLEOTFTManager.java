package com.wukong.hezhi.manager;

import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.ui.activity.DisResultActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEOTFTManager {
	private final static String TAG = BLEOTFTManager.class.getSimpleName();
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;// 蓝牙适配器
	// private boolean mScanning = true;// 描述扫描蓝牙的状态
	private Handler mHandler;
	private static final long SCAN_PERIOD = 10000; // 蓝牙扫描时间
	private Context mContext;

	// private static BLEOTFTManager blueOTFTManager;
	//
	// private BLEOTFTManager() {
	// mContext = ContextUtil.getContext();
	// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
	// ScreenUtil.showToast(ContextUtil.getString(R.string.blue_version_low_tips));
	// }
	// if (HezhiConfig.IS_BLE_NFC) {
	// init();
	// }
	// }
	//
	// public static BLEOTFTManager getInstance() {
	// if (blueOTFTManager == null) {
	// blueOTFTManager = new BLEOTFTManager();
	// }
	// return blueOTFTManager;
	// }

	private BLEOTFTManager() {
		mContext = ContextUtil.getContext();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			ScreenUtil.showToast(ContextUtil
					.getString(R.string.blue_version_low_tips));
		}
		if (Constant.NFC.IS_BLE_NFC) {
			init();
		}
	}

	private static class Holder {
		private static final BLEOTFTManager SINGLETON = new BLEOTFTManager();
	}

	/**
	 * 单一实例
	 * */
	public static BLEOTFTManager getInstance() {
		return Holder.SINGLETON;
	}

	private void init() {
		mHandler = new Handler();
		// 手机硬件支持蓝牙
		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			ScreenUtil.showToast("不支持BLE");
		}
		// 获取手机本地的蓝牙适配器
		bluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 打开蓝牙权限
		if (mBluetoothAdapter == null && !mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

	}

	/**
	 * 扫描蓝牙
	 */
	public void connect() {
		ScreenUtil.showToast(ContextUtil.getString(R.string.searching_device));
		// 10秒停止扫描
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}, SCAN_PERIOD);
		/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
		mBluetoothAdapter.startLeScan(mLeScanCallback);

	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			if (device.getName() != null && device.getName().contains("RS2016")) {// 如果扫描到了RS2016则连接
				ScreenUtil.showToast("连接到蓝牙RS2016...");
				connectBLE(device);
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			} else {
				ScreenUtil.showToast(ContextUtil
						.getString(R.string.not_find_device));
			}
		}
	};

	private BluetoothGatt mBluetoothGatt;
	public final static String ACTION_GATT_CONNECTED = "com.rising.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.rising.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.rising.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.rising.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.rising.bluetooth.le.EXTRA_DATA";
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private int mConnectionState = STATE_DISCONNECTED;
	/* 连接远程设备的回调函数 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED)// 连接成功
			{
				ScreenUtil.showToast("连接成功");
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				Log.i(TAG, "Connected to GATT server.");
				Log.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED)// 连接失败
			{
				ScreenUtil.showToast("连接失败 ");
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
			}
		}

		/*
		 * 重写onServicesDiscovered，发现蓝牙服务
		 */
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS)// 发现到服务
			{
				ScreenUtil.showToast("发现服务");
				Log.i(TAG, "--onServicesDiscovered called--");
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
				System.out.println("onServicesDiscovered received: " + status);
			}
		}

		/*
		 * 特征值的读写
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.i(TAG, "--onCharacteristicRead called--");
				// 从特征值读取数据
				byte[] sucString = characteristic.getValue();
				String string = new String(sucString);
			}

		}

		/*
		 * 特征值的改变
		 */
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			NFCManager.authCode = "16092117jSAv";
			NFCManager.uid = "1";
			toNFCActivity("16092117jSAv", "1");
		}

		public void toNFCActivity(String result, String uID) {
			Intent check_intent = new Intent(mContext, DisResultActivity.class);
			check_intent.putExtra(Constant.Extra.MAINACTIVITY_RESULT, result);
			check_intent.putExtra(Constant.Extra.MAINACTIVITY_UID, uID);
			check_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 此句是关键
			check_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(check_intent);
		}

		/*
		 * 特征值的写
		 */
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {

			Log.w(TAG, "--onCharacteristicWrite--: " + status);
			ScreenUtil.showToast(ContextUtil
					.getString(R.string.blue_conect_sucess_tips));
		}

		/*
		 * 读描述值
		 */
		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			// TODO Auto-generated method stub
			// super.onDescriptorRead(gatt, descriptor, status);
			Log.w(TAG, "----onDescriptorRead status: " + status);
			byte[] desc = descriptor.getValue();
			if (desc != null) {
				Log.w(TAG, "----onDescriptorRead value: " + new String(desc));
			}

		}

		/*
		 * 写描述值
		 */
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			// TODO Auto-generated method stub
			// super.onDescriptorWrite(gatt, descriptor, status);
			Log.w(TAG, "--onDescriptorWrite--: " + status);
		}

		/*
		 * 读写蓝牙信号值
		 */
		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			// super.onReadRemoteRssi(gatt, rssi, status);
			Log.w(TAG, "--onReadRemoteRssi--: " + status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			// super.onReliableWriteCompleted(gatt, status);
			Log.w(TAG, "--onReliableWriteCompleted--: " + status);
		}

	};

	// 连接远程蓝牙
	public boolean connectBLE(final BluetoothDevice device) {
		/* 调用device中的connectGatt连接到远程设备 */
		mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
		mConnectionState = STATE_CONNECTING;
		if (mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");

			if (mBluetoothGatt.connect())// 连接蓝牙，其实就是调用BluetoothGatt的连接方法
			{
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// UIUtil.showToast2("开始写入特征值");
						writeCharacteristic("c");
					}
				}, 3000);// 1秒后给蓝牙写入数据
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/*
	 * 取消连接
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
		ScreenUtil.showToast(ContextUtil.getString(R.string.diconnect_blue));
	}

	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);

	}

	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled,
			String uuidInter) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		BluetoothGattDescriptor clientConfig = characteristic
				.getDescriptor(UUID.fromString(uuidInter));
		if (enabled) {
			clientConfig
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} else {
			clientConfig
					.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		mBluetoothGatt.writeDescriptor(clientConfig);
	}

	public static String HEART_RATE_ONE = "00002901-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_TWO = "00002902-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_F = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_FIRST = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_SECOD = "0000fff2-0000-1000-8000-00805f9b34fb";
	private List<BluetoothGattCharacteristic> gattCharacteristics = null;

	// 写入特征值
	public void writeCharacteristic(String str) {
		List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

		for (BluetoothGattService gattService : gattServices) {
			if (gattService != null
					&& HEART_RATE_F.equals(gattService.getUuid().toString())) {
				gattCharacteristics = gattService.getCharacteristics();
			}
		}

		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			if (gattCharacteristic.getUuid().toString()
					.equals(HEART_RATE_FIRST)) {
				// setCharacteristicNotification(gattCharacteristic, true,
				// HEART_RATE_ONE);
				gattCharacteristic.setValue(str);
				mBluetoothGatt.writeCharacteristic(gattCharacteristic);
			}
		}

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					if (gattCharacteristic.getUuid().toString()
							.equals(HEART_RATE_SECOD)) {
						setCharacteristicNotification(gattCharacteristic, true,
								HEART_RATE_TWO);
						mBluetoothGatt.writeCharacteristic(gattCharacteristic);
					}
				}
			}
		}, 1000);

	}

}
