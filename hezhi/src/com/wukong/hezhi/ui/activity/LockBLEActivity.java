package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.BLELockManager;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.manager.BLELockManager.BLE_STATUS;
import com.wukong.hezhi.manager.BLELockManager.BleCallback;
import com.wukong.hezhi.manager.BLELockManager.RESPONSE_DATA;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @ClassName: LockBLEActivity
 * @Description: TODO(开锁蓝牙通讯界面)
 * @author HuangFeiFei
 * @date 2017-12-8
 * 
 */
public class LockBLEActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.lock_open);
	public String pageCode = "lock_open";
	
	@ViewInject(R.id.text_progress)
	private TextView text_progress;
	
	@ViewInject(R.id.text_result)
	private TextView text_result;
	
	@ViewInject(R.id.text_error)
	private TextView text_error;
	
	@ViewInject(R.id.progressbar)
	private ProgressBar progressbar;
	
	@ViewInject(R.id.image_status)
	private ImageView image_status;
	
	/** 界面是否第一次可见 **/
	private boolean isFirst = false;
	
	/** 蓝牙通讯完成 **/
	private boolean isBLEComplete = true;
	
	/** 界面是否退出了 **/
	private boolean isBack = false;
	
	private Handler mHandler;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_lock_ble;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.lock_open));
		
		mHandler = new Handler();
		setBleCallback();
		BLELockManager.getInstance().initBLE();
	}
	
	@OnClick(value = {R.id.header_left})
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			gotoBack();
			break;
		}
	}
	
	 @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	gotoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	 
	 /**界面退出 **/
	private void gotoBack() {
		isBack = true;
		if(isBLEComplete) {
			ActivitiesManager.getInstance().finishActivity(LockSMSActivity.class);
			finish();
		}else{
			new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.lock_open_isexit))
			.setCancelButton(ContextUtil.getString(R.string.cancel), null)
			.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
				@Override
				public void onDialogClick(View v, CustomAlterDialog dialog) {
					ActivitiesManager.getInstance().finishActivity(LockSMSActivity.class);
					finish();
				}
			}).build().show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(isFirst){
			if(!BLELockManager.getInstance().isBLEOK)
				BLELockManager.getInstance().initBLE();
		}
		
		isFirst = true;
	}
	
	 @Override
	protected void onDestroy() {
		 super.onDestroy();
		 BLELockManager.getInstance().close(BLELockManager.getInstance().deviceAddress);
		 mHandler.removeCallbacksAndMessages(null);  
	}
	 
	 /** 蓝牙通讯回调 **/
	 private void setBleCallback(){
		 BLELockManager.getInstance().setBleCallback(new BleCallback() {
			@Override
			public void bleResponse(String result, int code, int status) {
				setView(result, code,status);
			}
		});
	 }
	 
	 /** 蓝牙通讯回调界面更新UI **/
	 @SuppressWarnings("unused")
	private void setView(final String result,final int code,final int status){
		 if(LockBLEActivity.this == null) return;
		 if(isBack) return;
		 
		 this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(code == RESPONSE_DATA.RESPONSE_FAIL){
					isBLEComplete = true;
					text_result.setText(ContextUtil.getString(R.string.lock_open_fail));
					text_error.setText(result);
					progressbar.setVisibility(View.GONE);
					text_progress.setVisibility(View.GONE);
					text_result.setVisibility(View.VISIBLE);
					text_error.setVisibility(View.VISIBLE);
					if(status != BLE_STATUS.BLE_START)
						sendLockResult(-1, ContextUtil.getString(R.string.lock_open_fail) + "," + result);
				}else if(code == RESPONSE_DATA.RESPONSE_GOING){
					isBLEComplete = false;
					text_result.setVisibility(View.GONE);
					text_error.setVisibility(View.GONE);
					text_progress.setText(result);
					if(status == BLE_STATUS.BLE_AUTH){
						progressbar.setProgress(45);
					}else if(status == BLE_STATUS.BLE_INIT){
						progressbar.setProgress(65);
					}else if(status == BLE_STATUS.BLE_OPEN){
						progressbar.setProgress(85);
					}
					writeData(status);
				}else if(code == RESPONSE_DATA.RESPONSE_SUCCESS){
					isBLEComplete = false;
					progressbar.setVisibility(View.VISIBLE);
					text_progress.setVisibility(View.VISIBLE);
					text_result.setVisibility(View.GONE);
					text_error.setVisibility(View.GONE);
					text_progress.setText(result);
					if(status == BLE_STATUS.BLE_SEARCH){
						isBLEComplete = true;
						progressbar.setProgress(0);
					}else if(status == BLE_STATUS.BLE_CONN){
						progressbar.setProgress(25);
					}else if(status == BLE_STATUS.BLE_SERVICE){
					}else if(status == BLE_STATUS.BLE_AUTH){
						progressbar.setProgress(55);
					}else if(status == BLE_STATUS.BLE_INIT){
						progressbar.setProgress(75);
					}else if(status == BLE_STATUS.BLE_OPEN){
						isBLEComplete = true;
						progressbar.setVisibility(View.GONE);
						text_progress.setVisibility(View.GONE);
						text_result.setVisibility(View.VISIBLE);
						progressbar.setProgress(100);
						text_result.setText(ContextUtil.getString(R.string.lock_open_success));
						image_status.setImageResource(R.drawable.icon_unlock_open);
						sendLockResult(1, ContextUtil.getString(R.string.lock_open_success));
					}else if(status == BLE_STATUS.BLE_START){
						isBLEComplete = true;
					}
				}
			}
		});
	 }
	 
	 @SuppressWarnings("unused")
	private void writeData(final int status){
		 
		 mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					 if(LockBLEActivity.this == null) return;
					 if(isBack) return;
					
					BLELockManager.getInstance().write();
				}
			}, 100);//延迟1s去写数据
	 }
	 
	 /** 发送开锁结果到后台
	  * statusCode 1成功，-1失败
	  * resultMessage 成功或失败描述
	  *  
	  *  **/
	@SuppressWarnings("unused")
	private void sendLockResult(int statusCode,String resultMessage){
		if(LockBLEActivity.this == null) return;
		if(isBack) return;
		
		String userName = "";
		String address = "";
		if(UserInfoManager.getInstance().getUserInfo() != null)
			userName = UserInfoManager.getInstance().getUserInfo().getNickName();
		if(LocationManager.getInstance().getAdressInfo() != null)
			address = LocationManager.getInstance().getAdressInfo().getDetailAddr();
//		String url = "http://172.17.10.197:80/remoteAuthenticLock/reportLockStatus";
		String url = HttpURL.URL + HttpURL.REPORTLOCKSTATUS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", BLELockManager.mac);
		map.put("authCode", BLELockManager.authCode);
		map.put("statusCode", statusCode);
		map.put("resultMessage", resultMessage);
		map.put("address", address);
		map.put("userName", userName);
		String URL = url + "?mac=" + BLELockManager.mac + "&authCode=" 
				+ BLELockManager.authCode + "&statusCode=" + statusCode + "&resultMessage=" + resultMessage
				+ "&address=" + address + "&userName=" + userName;
		LogUtil.i(URL);
		showProgressDialog();
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings({"rawtypes" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.lock_open_upload));
				}
			}
		});
	}
}
