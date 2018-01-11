package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.TestManager;
import com.wukong.hezhi.ui.activity.NFCActivity;
import com.wukong.hezhi.ui.activity.QRActivity;
import com.wukong.hezhi.ui.activity.ScanActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 *
 * @ClassName: DistingiushFragment
 * @Description: TODO(鉴真)
 * @author HuZhiyin
 * @date 2017-6-14 下午5:16:25
 *
 */
public class DistingiushFragment extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.distinguish);
	public String pageCode = "distinguish";

	@OnClick(value = { R.id.nfc_rl, R.id.ofid_rl,R.id.liangziyun_rl,R.id.header_title })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nfc_rl:
			toActivity(NFCActivity.class);
			break;
		case R.id.liangziyun_rl:
			if (!PermissionUtil.cameraPermission()) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
				return;
			}
			toActivity(ScanActivity.class);
			break;
		case R.id.ofid_rl:
			// toActivity(QRActivity.class);
			toQRActivity();
			break;
		case R.id.header_title:
			if (HezhiConfig.DEBUG) {
				TestManager.getInstance().test(getActivity());
			}
			break;
		}
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_distinguish2, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		init();
		return rootView;
	}

	private void toQRActivity() {
		if (!PermissionUtil.cameraPermission()) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
			return;
		}
		toActivity(QRActivity.class);
	}

	public void init() {
	}

}
