package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.BLENFCManager;
import com.wukong.hezhi.manager.BluetoothManager;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.manager.BLENFCManager.IBleCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: NFCActivity
 * @Description: TODO(NFC鉴真页面)
 * @author HuZhiyin
 * @date 2017-3-28 上午9:46:10
 *
 */
public class NFCActivity extends BaseActivity {

	public String pageName = ContextUtil.getString(R.string.nfc_distinguish);
	public String pageCode = "nfc_distinguish";
	@ViewInject(R.id.connectble_rl)
	private RelativeLayout connectble_rl;

	@ViewInject(R.id.loading_pb)
	private ProgressBar loading_pb;

	@ViewInject(R.id.search_bt)
	private Button search_bt;

	@ViewInject(R.id.tips_tv)
	private TextView tips_tv;

	@ViewInject(R.id.bg_iv)
	private ImageView bg_iv;

	/** 蓝牙 */
	private BLENFCManager blueNFCManager;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_nfc;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.nfc_distinguish));
		initView();
	}

	public void initView() {
		if (NFCManager.isSupporNFC(this) && !NFCManager.isOpenNFC()) {// 支持NFC且nfc没有打开
			ScreenUtil.showToast(getResources().getString(R.string.open_nfc));
		}

		if (NFCManager.isNFCPhone) {
			connectble_rl.setVisibility(View.GONE);
			tips_tv.setText("\n将手机背面NFC感应区靠近商品的NFC芯片区域");
		} else {
			connectble_rl.setVisibility(View.VISIBLE);
			tips_tv.setText("1.开启手机蓝牙,且与蓝牙吊坠配对；\n2.将蓝牙吊坠贴近NFC标签。");
		}

		connectble_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!BluetoothManager.getInstance().isBluetoothEnabled()) {
					BluetoothManager.getInstance().turnOnBluetooth();// 打开蓝牙
					return;
				}

				loading_pb.setVisibility(View.VISIBLE);
				connectble_rl.setClickable(false);
				blueNFCManager = BLENFCManager.getInstance();
				blueNFCManager.connect(new IBleCallBack() {
					@Override
					public void onSucess(String sucessTip) {
						// TODO Auto-generated method stub
						// TODO Auto-generated method stub
						search_bt.setText(ContextUtil.getString(R.string.diconnect_blue));
						loading_pb.setVisibility(View.GONE);
						connectble_rl.setClickable(true);

					}

					@Override
					public void onFail(String failTip) {
						// TODO Auto-generated method stub
						search_bt.setText(ContextUtil.getString(R.string.connect_blue));
						loading_pb.setVisibility(View.GONE);
						connectble_rl.setClickable(true);
					}

					@Override
					public void onRead(String uid, String result) {
						// TODO Auto-generated method stub
						toAc(uid, result);
					}
				});

			}
		});
	}

	private void toAc(String uid, String result) {
		Intent check_intent = new Intent(NFCActivity.this, DisResultActivity.class);
		check_intent.putExtra(Constant.Extra.MAINACTIVITY_RESULT, result);
		check_intent.putExtra(Constant.Extra.MAINACTIVITY_UID, uid);
		check_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 此句是关键
		check_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(check_intent);
	}


}
