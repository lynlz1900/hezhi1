package com.wukong.hezhi.ui.activity;

import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.Result;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.function.zxing.camera.CameraManager;
import com.wukong.hezhi.function.zxing.view.ViewfinderView;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.BLELockManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: QRLockActivity
 * @Description: TODO(扫二维码开锁界面)
 * @author HuangFeiFei
 * @date 2017-12-29
 * 
 */
public class QRLockActivity extends QRBaseActivity{
	public String pageName = ContextUtil.getString(R.string.qr_lock);
	public String pageCode = "qr_lock";
	private ViewfinderView mViewfinderView;
	/** 闪光灯是否开启 **/
	private boolean isLightOpen = false;
	
	@ViewInject(R.id.image_openlight)
	private ImageView image_openlight;
	
	@ViewInject(R.id.layout_openlight)
	private LinearLayout layout_openlight;
	
	@ViewInject(R.id.header_title)
	private TextView header_title;
	
	@ViewInject(R.id.header_left)
	private TextView header_left;
	
	private SurfaceView surfaceView;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		super.isNotAddTitle();
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		super.layoutId();
		return R.layout.activity_qr_lock;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
		layout_openlight.setVisibility(View.VISIBLE);
		header_title.setText(ContextUtil.getString(R.string.qr_lock));
		
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		mViewfinderView.setText("对准保真箱智能锁上的二维码，即可自动扫描");
		
		setMViewfinderView();
		setMSurfaceView();
		
		header_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		image_openlight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isLightOpen){
					image_openlight.setImageResource(R.drawable.icon_light_on);
					isLightOpen = true;
					CameraManager.get().openLight();
				}else{
					image_openlight.setImageResource(R.drawable.icon_light_off);
					isLightOpen = false;
					CameraManager.get().offLight();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void restartQR() {
		// TODO Auto-generated method stub
		super.restartQR();
	}

	@Override
	public void handleDecode(Result result, Bitmap barcode) {
		// TODO Auto-generated method stub
		super.handleDecode(result, barcode);
		if (result == null) {
			return;
		}

		if (result.getText().equals("")) {
			restartQR();
			Toast.makeText(QRLockActivity.this, ContextUtil.getString(R.string.qr_error), Toast.LENGTH_SHORT).show();
		} else {
			sendLockCode(result.getText());
		}
	}

	/** 扫码开锁验证 **/
	private void sendLockCode(final String resultString){
		String mac = "";
		String authCode = "";
		try {
			String strResult = resultString.split("=")[1];
			strResult = Base64Util.decode(strResult);
			mac = strResult.split("&")[0];
			authCode = strResult.split("&")[1];
		} catch (Exception e) {
		}
		
		BLELockManager.mac = mac;
		BLELockManager.authCode = authCode;
		LogUtil.i("resultString:"+resultString);
		LogUtil.i("Mac:"+mac+"  authCode:"+authCode);
//		String url = "http://172.17.10.197:80/remoteAuthenticLock/scanLockCode";
		String url = HttpURL.URL + HttpURL.SCANLOCKCODE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", mac);
		map.put("authCode", authCode);
		map.put("phone", UserInfoManager.getInstance().getPhone());
		String URL = url + "?mac=" + mac + "&authCode=" + authCode + "&phone=" + UserInfoManager.getInstance().getPhone();
		LogUtil.i(URL);
		showProgressDialog();
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
				restartQR();
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
					toActivity(LockSMSActivity.class);
					finish();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
					restartQR();
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.qr_lock_error));
					restartQR();
				}
			}
		});
	}
	
	public void setMViewfinderView() {
		setViewfinderView(mViewfinderView);;
	}
	
	public void setMSurfaceView() {
		setSurfaceView(surfaceView);;
	}
}