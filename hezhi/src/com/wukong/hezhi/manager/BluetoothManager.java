package com.wukong.hezhi.manager;

import android.bluetooth.BluetoothAdapter;

/**
 * Bluetooth 管理类
 * 
 */
public class BluetoothManager {

	private BluetoothManager() {
	}

	private static class Holder {
		private static final BluetoothManager SINGLETON = new BluetoothManager();
	}

	/**
	 * 单一实例
	 * */
	public static BluetoothManager getInstance() {
		return Holder.SINGLETON;
	}

	/**
	 * 当前 Android 设备是否支持 Bluetooth
	 * 
	 * @return true：支持 Bluetooth false：不支持 Bluetooth
	 */
	public boolean isBluetoothSupported() {
		return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
	}

	/**
	 * 当前 Android 设备的 bluetooth 是否已经开启
	 * 
	 * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
	 */
	public boolean isBluetoothEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (bluetoothAdapter != null) {
			return bluetoothAdapter.isEnabled();
		}

		return false;
	}

	/**
	 * 强制开启当前 Android 设备的 Bluetooth
	 * 
	 * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
	 */
	public boolean turnOnBluetooth() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		if (bluetoothAdapter != null) {
			return bluetoothAdapter.enable();
		}

		return false;
	}
}