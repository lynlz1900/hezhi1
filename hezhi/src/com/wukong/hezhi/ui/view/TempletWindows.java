package com.wukong.hezhi.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.TempletRecycleViewAdapter;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class TempletWindows extends PopupWindow {
	private RecyclerView recyclerview_horizontal;
	private RelativeLayout ll_popup;
	private RelativeLayout layout_rl;
	private List<TempletInfo> templetInfos = new ArrayList<TempletInfo>();;
	private TempletRecycleViewAdapter adapter;

	private TempletWindows() {
		View view = View.inflate(ContextUtil.getContext(), R.layout.layout_popupwindow_templet, null);
		this.setAnimationStyle(R.style.popwindow_anim_buttomfromparent_style);

		layout_rl = (RelativeLayout) view.findViewById(R.id.layout_rl);
		ll_popup = (RelativeLayout) view.findViewById(R.id.ll_popup);
		recyclerview_horizontal = (RecyclerView) view.findViewById(R.id.recyclerview_horizontal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ContextUtil.getContext());  
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);  
        recyclerview_horizontal.setLayoutManager(linearLayoutManager);  
		
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		layout_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		setContentView(view);
	}

	private static class Holder {
		private static final TempletWindows SINGLETON = new TempletWindows();
	}

	public static TempletWindows getInstance() {
		return Holder.SINGLETON;
	}

	public void show(Activity activity, View parent, List<TempletInfo> infos) {

		adapter = new TempletRecycleViewAdapter(templetInfos, activity);
		recyclerview_horizontal.setAdapter(adapter);

		if (infos == null || infos.size() == 0) {
			return;
		}
		templetInfos.clear();
		templetInfos.addAll(infos);
		adapter.notifyDataSetChanged();
		int height = 0;
		if (Build.VERSION.SDK_INT >= 24) {// 如果是7.0以上的系统，需要获取状态栏的高度。
			height = ScreenUtil.getStatusBarHeight() - parent.getHeight();
		}
		showAtLocation(parent, Gravity.BOTTOM, 0, height);
		update();
	}
}