package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.ui.activity.AppraiseImageShowActivity;
import com.wukong.hezhi.ui.view.GridViewForListview;
import com.wukong.hezhi.ui.view.RatingBar;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 商品评论列表适配器
 * @author HuangFeiFei
 *
 */
public class CommodityAppraiseAdapter extends BaseAdapter {
	private List<CommodityAppraiseInfo> infos;
	private Activity mActivity;
	private CommodityAppraiseImageAdapter commodityAppraiseImageAdapter;
	
	public CommodityAppraiseAdapter(List<CommodityAppraiseInfo> infos, Activity mActivity) {
		super();
		if (infos == null) {
			this.infos = new ArrayList<CommodityAppraiseInfo>();
		} else {
			this.infos = infos;
		}
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_commodity_appraise);
			holder = new ViewHolder();
			holder.image_user = (ImageView) convertView.findViewById(R.id.image_user);
			holder.text_user = (TextView) convertView.findViewById(R.id.text_user);
			holder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			holder.text_appraise = (TextView) convertView.findViewById(R.id.text_appraise);
			holder.text_appraise_response = (TextView) convertView.findViewById(R.id.text_appraise_response);
			holder.text_appraiseAdd_response = (TextView) convertView.findViewById(R.id.text_appraiseAdd_response);
			holder.text_add = (TextView) convertView.findViewById(R.id.text_add);
			holder.text_appraise_add = (TextView) convertView.findViewById(R.id.text_appraise_add);
			holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
			holder.gv_images = (GridViewForListview) convertView.findViewById(R.id.gv_images);
			holder.gv_imagesadd = (GridViewForListview) convertView.findViewById(R.id.gv_imagesadd);
			holder.layout_gv_images = (LinearLayout) convertView.findViewById(R.id.layout_gv_images);
			holder.layout_add = (LinearLayout) convertView.findViewById(R.id.layout_add);
			holder.layout_appraise_response = (LinearLayout) convertView.findViewById(R.id.layout_appraise_response);
			holder.layout_appraiseAdd_response = (LinearLayout) convertView.findViewById(R.id.layout_appraiseAdd_response);
			holder.layout_gv_imagesadd = (LinearLayout) convertView.findViewById(R.id.layout_gv_imagesadd);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CommodityAppraiseInfo info = infos.get(position);
		
		if(info != null){
			GlideImgUitl.glideLoaderNoAnimation(mActivity, info.getUserImage(), holder.image_user, 0);
			holder.text_user.setText(info.getUserName());
			holder.text_time.setText(DateUtil.getYMD_(info.getTime()));
			holder.text_appraise.setText(info.getAppraiseMessage());
			holder.ratingBar.setStarMark(Float.valueOf(String.valueOf(info.getAppraiseStatus())));
			
			if(!TextUtils.isEmpty(info.getAppraiseResponseMessage())){
				holder.text_appraise_response.setText(info.getAppraiseResponseMessage());
				holder.layout_appraise_response.setVisibility(View.VISIBLE);
			}else{
				holder.layout_appraise_response.setVisibility(View.GONE);
			}
			
			if(!TextUtils.isEmpty(info.getAppraiseAddMessage())){
				holder.text_add.setText("用户"+DateUtil.dateDiff(info.getTime(), info.getAppraiseAddTime())+"追评");
				holder.text_appraise_add.setText(info.getAppraiseAddMessage());
				holder.layout_add.setVisibility(View.VISIBLE);
			}else{
				holder.layout_add.setVisibility(View.GONE);
			}
			
			if(!TextUtils.isEmpty(info.getAppraiseResponseAddMessage())){
				holder.text_appraiseAdd_response.setText(info.getAppraiseResponseAddMessage());
				holder.layout_appraiseAdd_response.setVisibility(View.VISIBLE);
			}else{
				holder.layout_appraiseAdd_response.setVisibility(View.GONE);
			}
			
			if(TextUtils.isEmpty(info.getAppraiseImages())){
				holder.layout_gv_images.setVisibility(View.GONE);
			}else{
				holder.layout_gv_images.setVisibility(View.VISIBLE);
				initInfoImages(holder.gv_images, info,false);
			}
			
			if(TextUtils.isEmpty(info.getAppraiseImagesAdd())){
				holder.layout_gv_imagesadd.setVisibility(View.GONE);
			}else{
				holder.layout_gv_imagesadd.setVisibility(View.VISIBLE);
				initInfoImages(holder.gv_imagesadd, info,true);
			}
		}


		return convertView;
	}

	static class ViewHolder {
		ImageView image_user;
		TextView text_user;
		TextView text_time;
		RatingBar ratingBar;
		TextView text_appraise;
		TextView text_appraise_response;
		TextView text_appraiseAdd_response;
		TextView text_add;
		TextView text_appraise_add;
		GridViewForListview gv_images;
		GridViewForListview gv_imagesadd;
		LinearLayout layout_gv_images;
		LinearLayout layout_add;
		LinearLayout layout_appraise_response;
		LinearLayout layout_gv_imagesadd;
		LinearLayout layout_appraiseAdd_response;
	}

	/**设置评论图片列表 **/
	public void initInfoImages(GridViewForListview gv_images,final CommodityAppraiseInfo info,final boolean isAppraiseAdd){
		List<String> imgspath = null;
		if(isAppraiseAdd)
			imgspath = info.getAppraiseListImagesAdd();
		else
		    imgspath = info.getAppraiseListImages();
		if(imgspath!=null&&!imgspath.equals("")){
			commodityAppraiseImageAdapter=new CommodityAppraiseImageAdapter(mActivity, imgspath);
			gv_images.setAdapter(commodityAppraiseImageAdapter);
			gv_images.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					Intent intent = new Intent();
					intent.setClass(mActivity, AppraiseImageShowActivity.class);
					intent.putExtra("imageURL", info);
					intent.putExtra("position", arg2);
					intent.putExtra("isAppraiseAdd", isAppraiseAdd);
					mActivity.startActivity(intent);
				}
			});
		}
	}
}
