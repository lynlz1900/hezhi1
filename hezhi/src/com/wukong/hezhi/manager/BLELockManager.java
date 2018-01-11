package com.wukong.hezhi.manager;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

/** 
 * 蓝牙管理类
 * 和智能锁通信
 * 
 *  **/
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLELockManager {
	public BluetoothManager bluetoothManager;
	public BluetoothAdapter mBluetoothAdapter;// 蓝牙适配器
	private Handler mHandler = new Handler();
	private static final long SCAN_PERIOD = 20000; // 蓝牙扫描时间
	private static Context mContext;

	public BluetoothGattService bluetoothGattService;
	private List<BluetoothGattCharacteristic> characteristicLists = new ArrayList<>();;
	private List<BluetoothGattService> bluetoothGattServices = new ArrayList<>();;
	private Map<String, BluetoothGatt> mBluetoothGatts = new HashMap<String, BluetoothGatt>();
	public BluetoothGatt bluetoothGatt;
	
	public static List<String> responseDatas = Arrays.asList(new String[]{"","授权失败","电池电量低，无法解锁","锁卡死，检查是否有重物压住锁"
	    		,"其他原因解锁失败","电路故障","激活指令失败"});
	public static final String INIT_LOCK = "Lock";//激活锁
	public static final String OPEN_LOCK = "Unlock";//开锁
	
	public static final int BLE_AUTHORITY = 0;//授权
	public static final int BLE_INIT = 1;//初始化
	public static final int BLE_OPEM = 2;//开锁
	public static int BLE_TYPE = BLE_AUTHORITY;
	
	/**
     * 手机蓝牙首次初始化
     */
	public static boolean isFirstBleInit = true;
	/**
     * 手机蓝牙是否开启状态
     */
	public static boolean mobile_ble_isopen = false;
	/**
     * 蓝牙Mac地址
     */
	public static String mac = "";
	/**
     * 公司授权码
     */
	public static String authCode = "";
	/**
     * 回调接口
     */
	public BleCallback callback;
	/**
     * 当前连接的设备的address
     */
	public String deviceAddress = "";
    /**
     * 重连的次数
     */
	private int reconnectCount = 0;
    /**
     * 是否进行重连
     */
    private boolean canreconntect = false;
    /**
     * 是否搜索到设备
     */
    private boolean searchDeviceSuccess = false;
    /**
     * 是否已经写数据到设备
     */
    private boolean isWriteDate = false;
    
    /** 是否有蓝牙权限 **/
	public boolean isBLEOK = true;
	
	private static class Holder {
		private static final BLELockManager SINGLETON = new BLELockManager();
	}

	/**
	 * 单一实例
	 * */
	public static BLELockManager getInstance() {
		mContext = ContextUtil.getContext();
		return Holder.SINGLETON;
	}

	public void init() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			Toast.makeText(mContext, "手机系统版本过低", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 手机硬件支持蓝牙
		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(mContext, "系统不支持蓝牙", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 获取手机本地的蓝牙适配器
		bluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		//注：这里通过getSystemService获取BluetoothManager，  
        //再通过BluetoothManager获取BluetoothAdapter。BluetoothManager在Android4.3以上支持(API level 18)。  
        if (bluetoothManager != null) {  
        	mBluetoothAdapter = bluetoothManager.getAdapter();  
        }  
        
        if(mBluetoothAdapter == null){
        	Toast.makeText(mContext, "系统不支持蓝牙", Toast.LENGTH_SHORT).show();
        	return;
        }
        
        if(mBluetoothAdapter != null && isFirstBleInit){
        	mobile_ble_isopen = mBluetoothAdapter.isEnabled();
        	isFirstBleInit = false;
        }
	}

	 /** 初始化蓝牙 **/
	 public void initBLE(){
		 isWriteDate = false;
		 isBLEOK = true;
		init();
		openBLE();
		connectBLE();
	 }
	 
	 /** 打开蓝牙 **/
	 public boolean openBLE(){
		 if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
			return mBluetoothAdapter.enable();
		}
		 return false;
	 }
	 
	 /** 判断蓝牙是否打开 **/
	 public boolean isOpenNo(){
		 if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
				return true;
			}
		 return false;
	 }
	 
	 /** 开启搜索蓝牙 **/
	 public void connectBLE(){
		 if(isOpenNo()){
			 callback.bleResponse("蓝牙开启中...", RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_START);
			 mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(!mBluetoothAdapter.isEnabled()){
							callback.bleResponse("蓝牙权限可能被禁止", RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_START);
							isBLEOK = false;
							return;
						}
						connect();
					}
				}, 2000);//如果蓝牙未开启状态，延迟2s去连接
			}else{
				connect();
			}
	 }
	 
	/**
	 * 扫描蓝牙
	 */
	@SuppressWarnings("deprecation")
	public void connect() {
		callback.bleResponse("正在搜索中...", RESPONSE_DATA.RESPONSE_GOING,BLE_STATUS.BLE_SEARCH);
		// 10秒停止扫描
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				if(!searchDeviceSuccess)
					callback.bleResponse("搜索蓝牙失败", RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_SEARCH);
			}
		}, SCAN_PERIOD);
		/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
		mBluetoothAdapter.startLeScan(mLeScanCallback);

	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@SuppressWarnings("deprecation")
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			 String address = device.getAddress();
			 String name = device.getName();
			 LogUtil.i("搜索到设备----"+"address:" + address+"	name:"+name);
			 String addr = address.replace(":", "");
			if (device.getName() != null && device.getName().equals("SmartLock-JinJiaGrp") && addr.equals(addr)) {
				searchDeviceSuccess = true;
				callback.bleResponse("搜索到设备"+name, RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_SEARCH);
				connect(device.getAddress());
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			} else {
			}
		}
		
	};

	/* 连接远程设备的回调函数 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			 super.onConnectionStateChange(gatt, status, newState);
	            String address = gatt.getDevice().getAddress();
	            String name = gatt.getDevice().getName();
	            LogUtil.i("连接到设备----"+"address:" + address+"  name："+name);
	            if (status == BluetoothGatt.GATT_SUCCESS) {
	                //如果是主动断开的 则断开
	                if (newState == BluetoothProfile.STATE_CONNECTED) {
	                	callback.bleResponse(name+"蓝牙连接成功",  RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_CONN);
	                    reconnectCount = 0;
	                    LogUtil.i("连接成功");
	                    deviceAddress = gatt.getDevice().getAddress();
	                    discoverServices(deviceAddress);
	                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
	                    disconnect(address,true);
	                    deviceAddress = "";
	                    LogUtil.e("断开成功");
	                }
	            }else{
	                //如果是收到莫名其妙的断开状态的话 则重连一次
	                if (newState != BluetoothProfile.STATE_CONNECTED && canreconntect) {
	                    //先断开原有的连接
	                    disconnect(address,true);
	                    if(reconnectCount >= 2){
	                        reconnectCount = 0;
	                        canreconntect = false;
	                        deviceAddress = "";
	                        return;
	                    }
	                    //再次重新连接
	                    boolean connect_resule = requestConnect(gatt,address,true);
	                    reconnectCount++;
	                    LogUtil.i("正在尝试重新连接："+connect_resule);
	                }else{
	                	 deviceAddress = "";
	                    disconnect(address,true);
	                }
	            }
		}

		/*
		 * 重写onServicesDiscovered，发现蓝牙服务
		 */
		@Override
		public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			if (status == BluetoothGatt.GATT_SUCCESS)// 发现到服务
			{
				LogUtil.i("--onServicesDiscovered success--");
				bluetoothGattService = getService(gatt,UUID_DATA.UUID_SERVICE);
				bluetoothGatt = mBluetoothGatts.get(deviceAddress);
//				getCharcteristics(bluetoothGattService);
				
				callback.bleResponse("发现蓝牙服务",RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_SERVICE);
				
				notifyCharacteristic(bluetoothGattService, gatt, UUID_DATA.UUID_RESPONSE);
				
				BLE_TYPE = BLE_AUTHORITY;
				callback.bleResponse("授权中...",RESPONSE_DATA.RESPONSE_GOING,BLE_STATUS.BLE_AUTH);
				
			} else {
				callback.bleResponse("没有找到蓝牙服务蓝牙服务",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_SERVICE);
				disconnect(deviceAddress,false);
				LogUtil.e("onServicesDiscovered fail: " + status);
			}
		}

		/*
		 * 特征值的读写
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				LogUtil.i("read-------address：" + gatt.getDevice().getAddress() + " name：" + gatt.getDevice().getName()
						+"  UUID:"+characteristic.getUuid().toString()
						+"	data："+byteArrayToStr(characteristic.getValue()));
			}else{
				LogUtil.e("onCharacteristicRead fail: " + status);
			}
		}

		/*
		 * 特征值的改变
		 */
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
            byte[] bytes = characteristic.getValue();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02x", bytes[i]));
            }
            LogUtil.i("notify---------address：" + gatt.getDevice().getAddress() + " name：" + gatt.getDevice().getName()
            		+"  UUID:"+characteristic.getUuid().toString()
            		+"	data：" + sb.toString());
            
            if(!isWriteDate) {
            	return;  //在APP写数据到设备之前，不接受设备过来的数据
            }
            
            int responseCode = -100;
            try {
            	responseCode = Integer.valueOf(sb.toString());
			} catch (Exception e) {
			}
           
//            responseCode = RESPONSE_DATA.RESPONSE_SUCCESS;
            if(BLE_TYPE == BLE_AUTHORITY){
        		if(responseCode == RESPONSE_DATA.RESPONSE_SUCCESS){
        			callback.bleResponse("授权成功",RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_AUTH);
        			mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							BLE_TYPE = BLE_INIT;
			        		callback.bleResponse("智能锁激活中...",RESPONSE_DATA.RESPONSE_GOING,BLE_STATUS.BLE_INIT);
						}
					}, 500);
        		}else if(responseCode >0 && responseCode <7){
        			callback.bleResponse(responseDatas.get(responseCode),RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_AUTH);
        			disconnect(gatt.getDevice().getAddress(),false);
					BLE_TYPE = BLE_AUTHORITY;
        		}else{
        			callback.bleResponse("异常操作失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_AUTH);
        			disconnect(gatt.getDevice().getAddress(),false);
					BLE_TYPE = BLE_AUTHORITY;
        		}
        	}else if(BLE_TYPE == BLE_INIT){
        		if(responseCode == RESPONSE_DATA.RESPONSE_SUCCESS){
        			callback.bleResponse("智能锁激活成功",RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_INIT);
        			mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							BLE_TYPE = BLE_OPEM;
			        		callback.bleResponse("智能锁打开中",RESPONSE_DATA.RESPONSE_GOING,BLE_STATUS.BLE_OPEN);
						}
					}, 500);
        		}else if(responseCode >0 && responseCode <7){
        			callback.bleResponse(responseDatas.get(responseCode),RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_INIT);
        			disconnect(gatt.getDevice().getAddress(),false);
					BLE_TYPE = BLE_AUTHORITY;
        		}else{
        			callback.bleResponse("异常操作失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_INIT);
        			disconnect(gatt.getDevice().getAddress(),false);
					BLE_TYPE = BLE_AUTHORITY;
        		}
        	}else if(BLE_TYPE == BLE_OPEM){
        		if(responseCode == RESPONSE_DATA.RESPONSE_SUCCESS){
        			callback.bleResponse("智能锁打开成功",RESPONSE_DATA.RESPONSE_SUCCESS,BLE_STATUS.BLE_OPEN);
        		}
        		else if(responseCode >0 && responseCode <7){
        			callback.bleResponse(responseDatas.get(responseCode),RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_OPEN);
        		}else{
        			callback.bleResponse("异常操作失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_OPEN);
        		}
        		
        		BLE_TYPE = BLE_AUTHORITY;
				disconnect(gatt.getDevice().getAddress(),false);
        	}
		}

		/*
		 * 特征值的写
		 */
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
            	LogUtil.e("onCharacteristicWrite fail: " + status);
//            	callback.bleResponse("发现蓝牙服务失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_CONN);
//				disconnect(deviceAddress,false);
            }else{
            	LogUtil.i("write--------address：" + gatt.getDevice().getAddress() + " name：" + gatt.getDevice().getName()
            			+"  UUID:"+characteristic.getUuid().toString()
            			+ "	data："+byteArrayToStr(characteristic.getValue()));
            }
		}
	};

	/**
     * 获取服务（必须先连接成功）
     * @param address：目标设备的address
     */
    public boolean discoverServices(String address) {
        BluetoothGatt gatt = mBluetoothGatts.get(address);
        if (gatt == null) {
            return false;
        }
        boolean ret = gatt.discoverServices();
        if (!ret) {
        	LogUtil.i("没有搜索到对应的设备服务");
            disconnect(address,true);
        }
        return ret;
    }
    
    /**
     *断开连接
     */
    public synchronized void disconnect(String address,boolean isNotify) {
    	if(mBluetoothGatts == null) return;
        if (mBluetoothGatts.containsKey(address)) {
            BluetoothGatt gatt = mBluetoothGatts.remove(address);
            if (gatt != null) {
            	if(isNotify && callback != null)
            		callback.bleResponse("蓝牙断开",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_CONN);
                gatt.disconnect();
                gatt.close();
                bluetoothGatt = null;
                deviceAddress = "";
                LogUtil.e("断开连接");
            }
        }else{
        	if(isNotify && callback != null)
        		callback.bleResponse("蓝牙断开",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_CONN);
        }
    }
    
    /** 关闭蓝牙连接 **/
    @SuppressWarnings("deprecation")
	public void close(String address){
    	disconnect(address, false);
    	if(mBluetoothGatts != null)
    		mBluetoothGatts.clear();
    	if(mBluetoothAdapter != null && mLeScanCallback != null)
    		mBluetoothAdapter.stopLeScan(mLeScanCallback);
    	if(mHandler != null)
    		mHandler.removeCallbacksAndMessages(null);  
    	if(!mobile_ble_isopen && mBluetoothAdapter != null){
    		mBluetoothAdapter.disable();
    	}
    }
    
    /**
     *发起连接（供外部调用）
     * @param address:目标设备的address地址
     * @param connectionStateCallback:设备连接状态的callback
     * @param canreconntect:是否启用重连机制 true：重连三次 false：不进行重连
     */
    public boolean requestConnect(BluetoothGatt gatt,String address,boolean canreconntect) {
    	this.canreconntect = canreconntect;
        if (gatt != null && gatt.getServices().size() == 0) {
            return false;
        }
        return connect(address);
    }
    /**
     *连接到设备
     * @param address:目标设备的address地址
     */
    private boolean connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        String name = device.getName() == null ? "":device.getName();
        callback.bleResponse(name +"蓝牙连接中",RESPONSE_DATA.RESPONSE_GOING,BLE_STATUS.BLE_CONN);
        BluetoothGatt gatt = device.connectGatt(mContext, false, mGattCallback);
        //为了防止重复的gatt，连接成功先检查是否有重复的，有则断开
        BluetoothGatt last = mBluetoothGatts.remove(address);
        if(last != null){
            last.disconnect();
            last.close();
        }
        if (gatt == null) {
            mBluetoothGatts.remove(address);
            return false;
        } else {
            // 将连接的gatt存起来
            mBluetoothGatts.put(address, gatt);
            return true;
        }
    }
    
	/***获取服务**/
	public List<BluetoothGattService> getServices(BluetoothGatt gatt) {
		if (mBluetoothAdapter == null || gatt == null) {
			return null;
		}
		
		bluetoothGattServices = gatt.getServices();
		for(BluetoothGattService service : bluetoothGattServices)
			LogUtil.i(service.getUuid().toString());
		return bluetoothGattServices;
	}
	
	/***获取服务列表**/
	public BluetoothGattService getService(BluetoothGatt mBluetoothGatt,String uuid) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return null;
		}
		
		BluetoothGattService bluetoothGattService = null;
		try {
			bluetoothGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
		} catch (Exception e) {
			return null;
		}
		
		return bluetoothGattService;
	}

	/***获取服务下的特征列表**/
	public List<BluetoothGattCharacteristic> getCharcteristics(BluetoothGattService service){
		if(service == null) {
			LogUtil.e("getCharcteristics-BluetoothGattService为空");
			return null;
		}
		
		characteristicLists = service.getCharacteristics();
		if(characteristicLists == null || characteristicLists.size() <1){
			LogUtil.e("获取的BluetoothGattCharacteristic列表为空");
			return null;
		}
		
		for(BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristicLists){
			LogUtil.e(bluetoothGattCharacteristic.getUuid().toString()+" "+bluetoothGattCharacteristic.getProperties());
			  // 可读
	        if ((bluetoothGattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
	        	LogUtil.e("可读");
	        }
	       // 可写，注：要 & 其可写的两个属性
	        if ((bluetoothGattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
	        	LogUtil.e("可写");
	        }
	       // 可通知，可指示
	        if ((bluetoothGattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
	        	LogUtil.e("可通知");
	        }
		}
		return characteristicLists;
	}
		
	/***获取服务下的某个特征**/
	public BluetoothGattCharacteristic getCharcteristic(BluetoothGattService service, String characteristicUUID) {
		try {
			//得到此服务结点下Characteristic对象
			final BluetoothGattCharacteristic gattCharacteristic = service.getCharacteristic(UUID.fromString(characteristicUUID));
			if (gattCharacteristic != null) {
				return gattCharacteristic;
			} else {
				LogUtil.e("获取的BluetoothGattCharacteristic为空");
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/***读数据**/
	public boolean readCharacteristic(BluetoothGattService service,BluetoothGatt gatt,
			String uuid,String address) {  
        if (mBluetoothAdapter == null) {  
            LogUtil.e("read-mBluetoothAdapter为空");  
            return false;  
        }  
        
        else if(service == null) {
			LogUtil.e("read-BluetoothGattService为空");
			return false;
		}
		
        else if(gatt == null){
        	LogUtil.e("read-bluetoothGatt为空");
			return false;
        }
        
        BluetoothGattCharacteristic gattCharacteristic = getCharcteristic(service
     		   , uuid);  //这个UUID都是根据协议号的UUID;

        if(gattCharacteristic == null){
        	LogUtil.e("read-BluetoothGattCharacteristic为空");
			return false;
        }
		
		LogUtil.i("read数据");
		boolean isRead = gatt.readCharacteristic(gattCharacteristic);  
		LogUtil.i("read数据："+isRead);
		return isRead;
    }
	
	/***写数据**/
	public boolean write(){
		String uuid = "";
		boolean isWrite = false;
		if(bluetoothGatt == null || bluetoothGatt.getDevice() == null){
			isWrite = false;
			disconnect("",false);
			return isWrite;
		}
			
		String data = "";
		if(BLE_TYPE == BLE_AUTHORITY){
			data = EncoderByMd5(getMac(bluetoothGatt.getDevice().getAddress()));//授权
			uuid = UUID_DATA.UUID_AUTHORITY;
		}
		else if(BLE_TYPE == BLE_INIT){
			data = INIT_LOCK;//锁激活
			uuid = UUID_DATA.UUID_INIT_OPEN;
		}
		else if(BLE_TYPE == BLE_OPEM){
			data = OPEN_LOCK;//开锁
			uuid = UUID_DATA.UUID_INIT_OPEN;
		}
		
		isWrite = writeCharacteristic(data, uuid, bluetoothGattService,bluetoothGatt);
		
		if(!isWrite){
	    	if(BLE_TYPE == BLE_AUTHORITY){
	    		callback.bleResponse("授权失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_AUTH);
	    	}else if(BLE_TYPE == BLE_INIT){
	    		callback.bleResponse("智能锁激活失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_AUTH);
	    	}else if(BLE_TYPE == BLE_OPEM){
	    		callback.bleResponse("智能锁打开失败",RESPONSE_DATA.RESPONSE_FAIL,BLE_STATUS.BLE_AUTH);
	    	}
	    	
	    	disconnect(bluetoothGatt.getDevice().getAddress(),false);//关闭连接
	    }
		
		return isWrite;
	}
	
	/***写数据**/
	public boolean writeCharacteristic(String data,String uuid,BluetoothGattService service,final BluetoothGatt gatt) {   //一般都是传string
	   if (mBluetoothAdapter == null) {  
           LogUtil.e("write-mBluetoothAdapter为空");  
           return false;  
       }  
       
	   else if(service == null) {
			LogUtil.e("write-BluetoothGattService为空");
			return false;
		}
		
	   else if(gatt == null){
       	   LogUtil.e("write-bluetoothGatt为空");
		   return false;
       }
	   
	   BluetoothGattCharacteristic writeCharacteristic = getCharcteristic(service
    		   , uuid);  //这个UUID都是根据协议号的UUID
       if(writeCharacteristic == null){
    	   LogUtil.e("write-BluetoothGattCharacteristic为空");
           return false;
       }

       byte[] bs = data.getBytes();
       byte[] value = new byte[data.getBytes().length];  
       value[0] = (byte) 0x00; 
       LogUtil.i("write数据："+data +"   BLE_TYPE："+BLE_TYPE + " length："+data.getBytes().length
    		   +" writeCharacteristic："+writeCharacteristic.getUuid().toString());
       writeCharacteristic.setValue(value[0],BluetoothGattCharacteristic.FORMAT_UINT8, 0); 
       writeCharacteristic.setValue(bs);
        boolean isWrite = gatt.writeCharacteristic(writeCharacteristic);
        isWriteDate = true;
	    LogUtil.i("write数据："+isWrite);
	    
	    return isWrite;
	}
	
	/***数据通知，从设备传过来的数据**/
	public boolean notifyCharacteristic(BluetoothGattService service,BluetoothGatt gatt,String uuid){
		if (mBluetoothAdapter == null) {
			LogUtil.e("notify-mBluetoothAdapter为空");  
	        return false;
	    }
		
		else if (service == null) {
			LogUtil.e("notify-BluetoothGattService为空");  
	        return false;
	    }
		
		else if (gatt == null) {
			LogUtil.e("notify-bluetoothGatt为空");  
	         return false;
	    }
		
		BluetoothGattCharacteristic notifyCharacteristic = getCharcteristic(service
	    		   , uuid);  //这个UUID都是根据协议号的UUID
        if(notifyCharacteristic == null){
        	LogUtil.e("notify-BluetoothGattCharacteristic为空");
           return false;
        }
        
        boolean isNotifyCharacteristic = gatt.setCharacteristicNotification(notifyCharacteristic, true);
        boolean isSetDescriptor = false;
        if(isNotifyCharacteristic) {
        	List<BluetoothGattDescriptor> bluetoothGattDescriptors = notifyCharacteristic.getDescriptors();
        	for(BluetoothGattDescriptor dp: bluetoothGattDescriptors){
                if (dp != null) {
                	dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    isSetDescriptor = gatt.writeDescriptor(dp);
                }
            }
        }
        
        LogUtil.i("notifyCharacteristic:"+isNotifyCharacteristic + "   isSetDescriptor:"+isSetDescriptor);
        return isNotifyCharacteristic;
	}
	
	public static String byteArrayToStr(byte[] byteArray) {
	    if (byteArray == null) {
	        return null;
	    }
	    String str = new String(byteArray);
	    return str;
	}
	
	/*
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s){
        byte[] r = s.getBytes();
        return r;
    }
    
    /*
     * 字节转10进制
     */
    public static int byte2Int(byte b){
        int r = (int) b;
        return r;
    }
    
    public static String EncoderByMd5(String str){
    	String result = "";
    	try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            result = result.substring(8, 24);
        } catch (Exception e) {
        	result = "";
        }

    	return result;
    }
    
    public static String getMac(String mac){
    	String result = "";
    	
    	try {
    		String[] strings = mac.split(":");
    		result = strings[4] + strings[5];
		} catch (Exception e) {
			result = "";
		}
    	
    	LogUtil.i("mac:"+result);
    	return result;
    }
    
    /** UUID校验  **/
    public static class UUID_DATA{
    	public static String UUID_SERVICE = "0000FFF0-0000-1000-8000-00805F9B34FB";
    	public static String UUID_AUTHORITY = "0000FFF1-0000-1000-8000-00805F9B34FB";
    	public static String UUID_TEST = "0000FFF2-0000-1000-8000-00805F9B34FB";
    	public static String UUID_INIT_OPEN = "0000FFF3-0000-1000-8000-00805F9B34FB";
    	public static String UUID_RESPONSE = "0000FFF4-0000-1000-8000-00805F9B34FB";
    }
    
    /** 返回值校验  **/
    public static class RESPONSE_DATA{
    	public static int RESPONSE_SUCCESS = 0;//当前指令执行成功
    	public static int RESPONSE_AUTHORITY_ERROR = 1;//授权失败
    	public static int RESPONSE_BATTERY_LOW = 2;//电池电量低，无法解锁
    	public static int RESPONSE_LOCK_BLOCK = 3;//锁卡死，检查是否有重物压住锁
    	public static int RESPONSE_LOCK_OTHER = 4;//其他原因解锁失败 
    	public static int RESPONSE_CIRCUIT_ERROR = 5;//电路故障
    	public static int RESPONSE_INIT_ERROR = 6;//激活指令失败
    	public static int RESPONSE_GOING = -1;//操作中
    	public static int RESPONSE_FAIL = -2;//操作失败
    }
    
    /** 通讯状态  **/
    public static class BLE_STATUS{
    	public static int BLE_START = 999;//蓝牙开启
    	public static int BLE_SEARCH = 1000;//搜索
    	public static int BLE_CONN = 1001;//连接
    	public static int BLE_AUTH = 1002;//授权
    	public static int BLE_INIT = 1003;//锁激活
    	public static int BLE_OPEN = 1004;//锁打开
    	public static int BLE_SERVICE = 1005;//发现服务
    }
    
    /** 回调接口  **/
    public interface BleCallback{
    	/** 蓝牙回调接口  **/
    	void bleResponse(String result,int code,int status);
    }
    
    public void setBleCallback(BleCallback callback){
    	this.callback = callback;
    }
}
