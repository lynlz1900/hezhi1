package com.wukong.hezhi.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.scan.lib.camera.CameraManager;
import com.suntech.sdk.CodeType;
import com.suntech.sdk.ScanManager;
import com.suntech.sdk.ScanType;
import com.suntech.sdk.callback.ScanListener;
import com.wukong.hezhi.R;
import com.wukong.hezhi.http.HttpURLUtil;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.SoundUtil;
import com.wukong.hezhi.utils.ThreadUtil;

/***
 * 
 * @ClassName: ScanActivity
 * @Description: TODO(鉴真及二维码扫描界面)
 * @author Huzhiyin
 * @date 2017年9月16日 上午7:57:10
 *
 */
public class ScanActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.scan_qr);
	public String pageCode = "scan_qr";

	@ViewInject(R.id.capture_preview)
	private SurfaceView capture_preview;

	@ViewInject(R.id.scan_line)
	private ImageView scan_line;

	@ViewInject(R.id.buttom_rg)
	private RadioGroup buttom_rg;

	@ViewInject(R.id.light_cb)
	private CheckBox light_cb;

	@ViewInject(R.id.describe_tv)
	private TextView describe_tv;

	@ViewInject(R.id.scan_kuang_rl)
	private RelativeLayout scan_kuang_rl;

	private SurfaceHolder mSurfaceHolder = null;
	/** 扫描动画 */
	private TranslateAnimation mAnimation;
	/** 扫描线动画时间 */
	private final int DELAY_ANI_DURATION = 1500;

	private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			CameraManager.getInstance().openDriver(holder);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			CameraManager.getInstance().destroy();
		}
	};

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_scan;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initView();
		initSurfaceHolder();
		initScan();
		initScanLineAni();
	}

	private void initView() {
		headView.setTitleStr(ContextUtil.getString(R.string.scan_qr));
		buttom_rg.setOnCheckedChangeListener(new BottomCheckChangeListener());// RadioGroup选中状态改变监听
		buttom_rg.check(R.id.lzy_code_rb);

		light_cb.setOnCheckedChangeListener(new LightListener());
	}

	private void initSurfaceHolder() {
		mSurfaceHolder = capture_preview.getHolder();
		mSurfaceHolder.setKeepScreenOn(true);
		mSurfaceHolder.addCallback(mSurfaceHolderCallback);
	}

	private void initScan() {
		ScanManager.getInstance().registerScanListener(onScanListener);// 注册扫码监听
	}

	/** 扫描线动画 */
	private void initScanLineAni() {
		if (mAnimation == null) {
			mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
					TranslateAnimation.RELATIVE_TO_PARENT, -0.5f, TranslateAnimation.RELATIVE_TO_PARENT, 0.5f);
			scan_line.setVisibility(View.VISIBLE);
			mAnimation.setDuration(DELAY_ANI_DURATION);
			mAnimation.setRepeatCount(-1);
			mAnimation.setRepeatMode(Animation.REVERSE);
			mAnimation.setInterpolator(new LinearInterpolator());
		}
		scan_line.setAnimation(mAnimation);
		mAnimation.startNow();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSurfaceHolder != null) {
			CameraManager.getInstance().openDriver(mSurfaceHolder);
		}

		ThreadUtil.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				light_cb.setChecked(true);
			}
		}, 1500);

	}

	@Override
	protected void onPause() {
		super.onPause();
		CameraManager.getInstance().closeDriver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ScanManager.getInstance().unRegisterScanListener(onScanListener);// 解除扫码监听
	}

	/** 扫码模式（量子云码） */
	private void setScanCodeMode() {
		ScanManager.getInstance().setCodeType(CodeType.SUNTECH_CODE.value);// 量子云码
		ScanManager.getInstance().setScanType(ScanType.SCAN_CODE.value);// 扫码查询
	}

	/** 鉴伪模式（量子云码） */
	private void setCheckCodeMode() {
		ScanManager.getInstance().setCodeType(CodeType.SUNTECH_CODE.value);// 量子云码
		ScanManager.getInstance().setScanType(ScanType.CHECK_CODE.value);// 扫码鉴伪
	}

	/** 扫码模式（二维码） */
	private void setScanQRCodeMode() {
		ScanManager.getInstance().setCodeType(CodeType.QR_CODE.value);// 二维码/条形码
		ScanManager.getInstance().setScanType(ScanType.SCAN_CODE.value); // 扫码查询
	}

	/**
	 * 扫码结果回调监听
	 */
	private ScanListener onScanListener = new ScanListener() {
		/** 0:量子云码 1:二维码/条形码 */
		@Override
		public void onScan(int codeType, String data) {
			LogUtil.d("量子云码******codeType:" + codeType + "data:" + data);
			Message msg = mhandler.obtainMessage();
			msg.what = codeType;
			msg.obj = data;
			mhandler.sendMessage(msg);
		}
	};

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ScanListener.LOCATION_ERROR:
			case ScanListener.NO_NETWORK_EXCEPTION:
			case ScanListener.SCAN_MODE_ERROR:
			case ScanListener.SCAN_MODE_EXCEPTION:
			case ScanListener.SCAN_RESULT_CODECOPY:
			case ScanListener.SUNTECH_KEY_ERROR:
			case ScanListener.SCAN_RESULT_EXCEPTION:
			case ScanListener.QCCODE_SUCCESS:// 量子云码
				if (msg.obj != null) {
					resolveQCode(msg.obj.toString());
				}
				break;
			case ScanListener.QRCODE_SUCCESS:// 二维码
				if (msg.obj != null) {
					resolveQRode(msg.obj.toString());
				}
				break;
			case ScanListener.FAIL:
				ScreenUtil.showToast("FAIL");
				break;
			}
		}
	};

	/** 解析二维码 */
	public void resolveQRode(String str) {
		NFCManager.uid = "";
		NFCManager.authCode = "";
		NFCManager.ndefMsg = "";//初始化ndef格式标签里的信息，否则会导致定制信息有缓存
		final String SEPARATOR = "ofid=";
		try {
			String url = "";
			String uid = "";
			String authCode = "";
			url = str.substring(str.indexOf(SEPARATOR) + SEPARATOR.length(), str.length());
			String s = Base64Util.decode(url);
			uid = s.substring(0, s.indexOf("&"));
			authCode = s.substring(s.indexOf("&") + 1, s.length());
			NFCManager.uid = uid;
			NFCManager.authCode = authCode;
		} catch (Exception e) {

		}
		toDistinguishActivity();
	}

	/** 解析量子云码 */
	public void resolveQCode(String str) {
		NFCManager.uid = "";
		NFCManager.authCode = "";
		NFCManager.ndefMsg = "";//初始化ndef格式标签里的信息，否则会导致定制信息有缓存
		String uid = HttpURLUtil.getPara(str, "pid");
		String authCode = HttpURLUtil.getPara(str, "tid");
		// if (!TextUtils.isEmpty(authCode)) {
		// while (uid.length() < 12) {// 12位，不足补零
		// uid = 0 + uid;
		// }
		// }
		if (!TextUtils.isEmpty(authCode)) {// 4位，不足补零
			while (authCode.length() < 4) {
				authCode = 0 + authCode;
			}
		}

		ScreenUtil.showToastByDebug("uid:" + uid + "\nauthCode:" + authCode);
		if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(authCode)) {
			NFCManager.uid = uid;
			NFCManager.authCode = authCode;
		}
		toDistinguishActivity();
	}

	/** 跳转鉴真页面 */
	private void toDistinguishActivity() {
		SoundUtil.playB();
		JumpActivityManager.getInstance().toActivity(this, DisResultActivity.class);
		finish();
	}

	private class BottomCheckChangeListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			// case R.id.query_rb: // 扫码查询
			// setScanCodeMode();
			// break;
			case R.id.lzy_code_rb:// 扫码鉴伪
				checkLZY();
				break;
			case R.id.qr_code_rb: // 二维码/条形码
				chekcQR();
				break;
			}
		}

		private void chekcQR() {
			light_cb.setChecked(false);
			ViewGroup.LayoutParams params = scan_kuang_rl.getLayoutParams();
			params.height = ScreenUtil.dp2px(220);
			params.width = ScreenUtil.dp2px(220);
			scan_kuang_rl.setLayoutParams(params);
			headView.setTitleStr(ContextUtil.getString(R.string.qr_code));
			describe_tv.setText(ContextUtil.getString(R.string.qr_code_tip));
			setScanQRCodeMode();
		}

		private void checkLZY() {
			light_cb.setChecked(false);
			ViewGroup.LayoutParams params = scan_kuang_rl.getLayoutParams();
			params.height = ScreenUtil.dp2px(250);
			params.width = ScreenUtil.dp2px(250);
			scan_kuang_rl.setLayoutParams(params);
			headView.setTitleStr(ContextUtil.getString(R.string.lzy_code));
			describe_tv.setText(ContextUtil.getString(R.string.lzy_code_tip));
			setCheckCodeMode();
		}
	}

	/** 闪光灯监听 */
	private class LightListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				CameraManager.getInstance().openLight();
			} else {
				CameraManager.getInstance().offLight();
			}
		}
	}

}
