package com.wukong.hezhi.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.BitmapUtils;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;

/**
 * 
 * @ClassName: RedBagDialog
 * @Description: TODO(拆红包弹窗)
 * @author HuZhiyin
 * @date 2017-3-21 上午8:39:24
 * 
 */
public class RedBagDialog extends Dialog {
	private Context context;
	private View layout;
	private ImageView close_iv;// 关闭
	private ImageView company_logo_iv;// 公司logo
	private TextView company_tv;// 公司名称
	private TextView redbug_tips_tv;// 祝福语
	private ImageView open_iv;// 打开红包

	public RedBagDialog(Context context) {
		super(context, R.style.AdDialogStyle);
		this.context = context;
		this.setCanceledOnTouchOutside(false);// 点击空白不消失
		this.setCancelable(false);
		initView();
	}

	private void initView() {
		layout = View.inflate(context, R.layout.layout_redbug, null);
		close_iv = (ImageView) layout.findViewById(R.id.close_iv);
		company_logo_iv = (ImageView) layout.findViewById(R.id.company_logo_iv);
		company_tv = (TextView) layout.findViewById(R.id.company_tv);
		redbug_tips_tv = (TextView) layout.findViewById(R.id.redbug_tips_tv);
		open_iv = (ImageView) layout.findViewById(R.id.open_iv);
	}

	/**
	 * 
	 * @Title: show
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param url 公司Logo链接
	 * @param @param comanpanyName 公司名称
	 * @param @param blessing 祝福语言
	 * @param @param openListener 打开红包监听
	 * @return void 返回类型
	 * @throws
	 */
	public void show(String url, String comanpanyName, String blessing,
			android.view.View.OnClickListener openListener) {
		close_iv.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		GlideImgUitl.glideLoader(context,url,
			 company_logo_iv, 0);
		company_tv.setText(comanpanyName);
		redbug_tips_tv.setText(blessing);
		open_iv.setOnClickListener(openListener);
		this.setContentView(layout);
		this.show();
	}

}
