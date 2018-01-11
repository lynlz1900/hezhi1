package com.wukong.hezhi.ui.activity;

import android.graphics.Bitmap;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.Result;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.function.zxing.view.ViewfinderView;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: QRActivity
 * @Description: TODO(扫二维码界面)
 * @author HuZhiyin
 * @date 2017-6-7 下午2:25:00
 * 
 */
public class QRActivity extends QRBaseActivity{
	public String pageName = ContextUtil.getString(R.string.qr_page);
	public String pageCode = "qr_page";
	private ViewfinderView mViewfinderView;
	
	@ViewInject(R.id.image_openlight)
	private ImageView image_openlight;
	
	@ViewInject(R.id.layout_openlight)
	private LinearLayout layout_openlight;
	
	@ViewInject(R.id.header_title)
	private TextView header_title;
	
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
		return R.layout.activity_qr;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		Button mButtonBack = (Button) findViewById(R.id.button_back);
		
		setMViewfinderView();
		setMSurfaceView();
		
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QRActivity.this.finish();
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
			Toast.makeText(QRActivity.this, ContextUtil.getString(R.string.qr_error), Toast.LENGTH_SHORT).show();
		} else {
			toActivity(WebViewActivity.class, Constant.Extra.WEB_URL,result.getText());
			finish();
		}
	}

	private static String SEPARATOR = "ofid=";

	public void resolveQCode(String str) {
		String url = null;
		String uid = null;
		String authCode = null;
		try {
			url = str.substring(str.indexOf(SEPARATOR) + SEPARATOR.length(), str.length());
			String s = Base64Util.decode(url);
			uid = s.substring(0, s.indexOf("&"));
			authCode = s.substring(s.indexOf("&") + 1, s.length());
			NFCManager.uid = uid;
			NFCManager.authCode = authCode;
		} catch (Exception e) {
			// TODO: handle exception
			uid = null;
			authCode = null;

			NFCManager.uid = "";
			NFCManager.authCode = "";
		}
		JumpActivityManager.getInstance().toActivity(this, DisResultActivity.class);
		finish();
	}

	public void setMViewfinderView() {
		setViewfinderView(mViewfinderView);;
	}
	
	public void setMSurfaceView() {
		setSurfaceView(surfaceView);;
	}
}