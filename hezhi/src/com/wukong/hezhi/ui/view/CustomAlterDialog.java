package com.wukong.hezhi.ui.view;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: CustomAlterDialog
 * @Description: TODO(自定义弹框)
 * @author HuZhiyin
 * @date 2016-11-2 下午2:57:15
 * 
 */
public class CustomAlterDialog extends Dialog implements OnClickListener {
	/** 标题 */
	private TextView title_tv;
	/** 需要显示的内容部分 */
	private FrameLayout content_fl;
	/** 确定按钮 */
	private TextView btn_sure;
	/** 取消按钮 */
	private TextView btn_cancel;
	private View layout;
	/** 按钮布局 */
	private RelativeLayout bt_rl;
	/** 确定按钮监听,取消按钮监听 */
	private OnDialogClickListener dialogSureClickListener, dialogCancelClickListener;
	/** ListView监听 */
	private OnClickListener onItemClickListener;

	private CustomAlterDialog(Context context) {
		super(context, R.style.CustomeDialogStyle);
		initView();
	}

	private void initView() {
		layout = View.inflate(ContextUtil.getContext(), R.layout.dialog_arlter_custom, null);
		title_tv = (TextView) layout.findViewById(R.id.title_tv);
		content_fl = (FrameLayout) layout.findViewById(R.id.content_fl);
		btn_sure = (TextView) layout.findViewById(R.id.btn_sure);
		btn_cancel = (TextView) layout.findViewById(R.id.btn_cancel);
		bt_rl = (RelativeLayout) layout.findViewById(R.id.bt_rl);

	}

	private void setDialog(String title, String msg, String cancel, String sure,
			OnDialogClickListener dialogCancelClickListener, OnDialogClickListener dialogSureClickListener,
			String[] items, OnClickListener onItemClickListener,boolean cancelable) {
		View view = null;
		if (!TextUtils.isEmpty(msg)) {
			view = getTextView(msg);
		}
		this.setDialog(title, view, cancel, sure, dialogCancelClickListener, dialogSureClickListener, items,
				onItemClickListener,cancelable);
	}

	private void setDialog(String title, View view, String cancel, String sure,
			OnDialogClickListener dialogCancelClickListener, OnDialogClickListener dialogSureClickListener,
			String[] items, OnClickListener onItemClickListener, boolean cancelable) {

		if (view == null) {// 内容部分显示隐藏
			content_fl.setVisibility(View.GONE);
		} else {
			if (view instanceof ListView) {// 添加listview
				if (items != null && items.length > 0) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContextUtil.getContext(),
							R.layout.item_list_dialog);
					for (int i = 0; i < items.length; i++) {
						adapter.add(items[i]);
					}
					((ListView) view).setAdapter(adapter);
					((ListView) view).setOnItemClickListener(new MyOnItemClickListener());
					ScreenUtil.removeParent(view);
					content_fl.removeAllViews();
					content_fl.addView(view);
					content_fl.setVisibility(View.VISIBLE);
				}
			} else {// 添加view
				content_fl.removeAllViews();
				ScreenUtil.removeParent(view);
				content_fl.addView(view);
				content_fl.setVisibility(View.VISIBLE);
			}
		}

		if (TextUtils.isEmpty(title)) {// 标题部分显示隐藏
			title_tv.setVisibility(View.GONE);
		} else {
			title_tv.setVisibility(View.VISIBLE);
			title_tv.setText(title);
		}

		if (TextUtils.isEmpty(cancel)) {// 取消按钮显示隐藏
			btn_cancel.setVisibility(View.GONE);
		} else {
			btn_cancel.setVisibility(View.VISIBLE);
			btn_cancel.setText(cancel);
		}
		if (TextUtils.isEmpty(sure)) {// 确定按钮显示隐藏
			btn_sure.setVisibility(View.GONE);
		} else {
			btn_sure.setVisibility(View.VISIBLE);
			btn_sure.setText(sure);
		}

		if (TextUtils.isEmpty(sure) && TextUtils.isEmpty(cancel)) {// 按钮布局显示隐藏
			bt_rl.setVisibility(View.GONE);
		} else {
			bt_rl.setVisibility(View.VISIBLE);
		}

		btn_cancel.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		this.dialogCancelClickListener = dialogCancelClickListener;
		this.dialogSureClickListener = dialogSureClickListener;
		this.onItemClickListener = onItemClickListener;
		this.setCancelable(cancelable);
		this.setContentView(layout);
	}

	public class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			onItemClickListener.onClick(CustomAlterDialog.this, position);
			CustomAlterDialog.this.dismiss();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			if (dialogCancelClickListener != null) {
				dialogCancelClickListener.onDialogClick(v, this);
			}
			dismiss();
			break;
		case R.id.btn_sure:
			if (dialogSureClickListener != null) {
				dialogSureClickListener.onDialogClick(v, this);
			}
			dismiss();
			break;
		}
	}

	public interface OnDialogClickListener {
		void onDialogClick(View v, CustomAlterDialog dialog);
	}

	public interface OnClickListener {
		void onClick(DialogInterface dialog, int which);
	}

	/** 获取textview */
	private TextView getTextView(String text) {
		FrameLayout.LayoutParams params = null;
		if (params == null) {
			params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
		}
		TextView tv = null;
		if (tv == null) {
			tv = new TextView(ContextUtil.getContext());
		}
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setLayoutParams(params);
		tv.setTextSize(16);// 默认单位是sp
		tv.setTextColor(ContextUtil.getColor(R.color.text_main));
		tv.setText(text);

		return tv;
	}

	public static class Builder {
		private String textTitle;
		private String textMsg;
		private String textSure;
		private String textCancle;
		private View view;
		private OnDialogClickListener dialogSureClickListener;
		private OnDialogClickListener dialogCancelClickListener;
		private Activity mActivity;
		private OnClickListener onItemClickListener;
		private String[] items;
		private boolean cancelable=true;//默认点击可以隐藏

		public Builder(Activity mActivity) {
			super();
			this.mActivity = mActivity;
		}

		public Builder setTitle(String textTitle) {
			this.textTitle = textTitle;
			return this;
		}

		public Builder setView(View view) {
			this.view = view;
			return this;
		}

		public Builder setItems(String[] items, OnClickListener onItemClickListener) {
			this.items = items;
			this.onItemClickListener = onItemClickListener;
			return this;
		}

		public Builder setCancelButton(String textCancle, OnDialogClickListener dialogCancelClickListener) {
			this.textCancle = textCancle;
			this.dialogCancelClickListener = dialogCancelClickListener;
			return this;
		}

		public Builder setSureButton(String textSure, OnDialogClickListener dialogSureClickListener) {
			this.textSure = textSure;
			this.dialogSureClickListener = dialogSureClickListener;
			return this;
		}

		public Builder setMsg(String textMsg) {
			this.textMsg = textMsg;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public CustomAlterDialog build() {
			CustomAlterDialog dialog = new CustomAlterDialog(mActivity);
			if (view != null) {// 内容部分是一个view
				dialog.setDialog(textTitle, view, textCancle, textSure, dialogCancelClickListener,
						dialogSureClickListener, items, onItemClickListener, cancelable);
			} else {
				if (items != null && items.length > 0) {// 内容部分是一个 listView
					ListView listView = new ListView(ContextUtil.getContext());
					dialog.setDialog(textTitle, listView, textCancle, textSure, dialogCancelClickListener,
							dialogSureClickListener, items, onItemClickListener, cancelable);
				} else {// 内容部分是一个字符串 textMsg
					dialog.setDialog(textTitle, textMsg, textCancle, textSure, dialogCancelClickListener,
							dialogSureClickListener, items, onItemClickListener, cancelable);
				}
			}

			return dialog;
		}

		public void show() {
			CustomAlterDialog dialog = build();
			dialog.show();
		}
	}
}
