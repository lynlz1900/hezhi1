package com.wukong.hezhi.ui.view;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class CustomAlterProgressDialog extends Dialog {
	private View layout;
	private TextView loading_txt;

	private CustomAlterProgressDialog(Context context) {
		super(context, R.style.CustomeDialogStyle);
		this.setCanceledOnTouchOutside(false);  
	    this.setCancelable(false);  
		initView();
	}

	private void initView() {
		layout = View.inflate(ContextUtil.getContext(), R.layout.dialog_arlter_progress_custom, null);
		loading_txt = (TextView) layout.findViewById(R.id.loading_txt);
	}

	public void setProgressDialog(String msg, boolean cancelable) {
		if (TextUtils.isEmpty(msg)) {
			loading_txt.setVisibility(View.GONE);
		} else {
			loading_txt.setVisibility(View.VISIBLE);
			loading_txt.setText(msg);
		}
		this.setCanceledOnTouchOutside(cancelable);
		this.setContentView(layout);

	}

	public static class Builder {
		private Activity mActivity;
		private String textMsg;
		private boolean cancelable = true;// 默认点击可以隐藏

		public Builder(Activity mActivity) {
			super();
			this.mActivity = mActivity;
		}

		public Builder setMsg(String textMsg) {
			this.textMsg = textMsg;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public CustomAlterProgressDialog build() {
			CustomAlterProgressDialog progressDialog = new CustomAlterProgressDialog(mActivity);
			progressDialog.setProgressDialog(textMsg, cancelable);
			return progressDialog;
		}
	}
}
