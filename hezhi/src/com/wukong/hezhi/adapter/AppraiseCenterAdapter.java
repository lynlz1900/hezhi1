package com.wukong.hezhi.adapter;

import java.util.List;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.AppraiseAddActivity;
import com.wukong.hezhi.ui.activity.AppraiseDetailActivity;
import com.wukong.hezhi.ui.activity.AppraiseShowActivity;
import com.wukong.hezhi.ui.activity.CommodityInfoActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 评价中心列表适配器
 * @author HuangFeiFei
 *
 */
public class AppraiseCenterAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<CommodityAppraiseCenterInfo> list;
	private int state = 0;
	
	public AppraiseCenterAdapter(Activity mActivity,List<CommodityAppraiseCenterInfo> data,int state) {
		this.mActivity = mActivity;
		this.list = data;
		this.state = state;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		Holder holder;
		if (view==null) {
			view = ScreenUtil.inflate(R.layout.item_list_appraise_center);
			holder=new Holder();
			holder.image_commodity = (ImageView) view.findViewById(R.id.image_commodity);
			holder.text_name = (TextView) view.findViewById(R.id.text_name);
			holder.btn_appraise_show = (TextView) view.findViewById(R.id.btn_appraise_show);
			holder.btn_appraise_add = (TextView) view.findViewById(R.id.btn_appraise_add);
			holder.btn_appraise_view = (TextView) view.findViewById(R.id.btn_appraise_view);
			view.setTag(holder);
		}else {
			holder= (Holder) view.getTag();
		}
		
		final CommodityAppraiseCenterInfo appraiseCenterInfo = list.get(position);
		
		if(state == 0){
			holder.btn_appraise_show.setVisibility(View.VISIBLE);
			holder.btn_appraise_add.setVisibility(View.GONE);
			holder.btn_appraise_view.setVisibility(View.GONE);
		}else if(state == 1){
			holder.btn_appraise_show.setVisibility(View.GONE);
			holder.btn_appraise_add.setVisibility(View.VISIBLE);
			holder.btn_appraise_view.setVisibility(View.VISIBLE);
		}
		
		if(appraiseCenterInfo != null){
			GlideImgUitl.glideLoaderNoAnimation(mActivity, 
					appraiseCenterInfo.getImageUrl(), holder.image_commodity, 2);
			holder.text_name.setText(appraiseCenterInfo.getProductName());
			if(appraiseCenterInfo.getAppraiseStatus() == 1 && state == 1){
				holder.btn_appraise_view.setVisibility(View.VISIBLE);
				holder.btn_appraise_add.setText(ContextUtil.getString(R.string.appraise_add));
			}else if(appraiseCenterInfo.getAppraiseStatus() == 2 && state == 1){
				holder.btn_appraise_view.setVisibility(View.GONE);
				holder.btn_appraise_add.setText(ContextUtil.getString(R.string.appraise_view));
			}
		}	
		
		holder.btn_appraise_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivity == null) return;
				if(appraiseCenterInfo == null) return;
				
				JumpActivityManager.getInstance().toActivity(mActivity, AppraiseShowActivity.class, 
						Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
			}
		});
		
		holder.btn_appraise_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivity == null) return;
				if(appraiseCenterInfo == null) return;
				
				if(appraiseCenterInfo.getAppraiseStatus() == 1){
					JumpActivityManager.getInstance().toActivity(mActivity, AppraiseAddActivity.class, 
							Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
				}else if(appraiseCenterInfo.getAppraiseStatus() == 2){
					JumpActivityManager.getInstance().toActivity(mActivity, AppraiseDetailActivity.class, 
							Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
				}
			}
		});
		
		holder.btn_appraise_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivity == null) return;
				if(appraiseCenterInfo == null) return;
				
				JumpActivityManager.getInstance().toActivity(mActivity, AppraiseDetailActivity.class, 
						Constant.Extra.APPRAISE_CENTER_INFO, appraiseCenterInfo);
			}
		});
		
		holder.image_commodity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mActivity == null) return;
				if(appraiseCenterInfo == null) return;
				
				JumpActivityManager.getInstance().toActivity(mActivity, CommodityInfoActivity.class,
						Constant.Extra.PRODUCTID, appraiseCenterInfo.getProductId());//跳转到商品详情界面
			}
		});
		
		holder.text_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mActivity == null) return;
				if(appraiseCenterInfo == null) return;
				
				JumpActivityManager.getInstance().toActivity(mActivity, CommodityInfoActivity.class,
						Constant.Extra.PRODUCTID, appraiseCenterInfo.getProductId());//跳转到商品详情界面
			}
		});

		return view;
	}
	
	class Holder{
		ImageView image_commodity;
		TextView text_name;
		TextView btn_appraise_show;
		TextView btn_appraise_add;
		TextView btn_appraise_view;
	}
}
