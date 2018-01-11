package com.wukong.hezhi.adapter;

import java.util.List;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 
 * 商品评论图片列表适配器
 * @author HuangFeiFei
 *
 */
public class CommodityAppraiseImageAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<String> list;
	
	public CommodityAppraiseImageAdapter(Activity mActivity,List<String> data) {
		this.mActivity=mActivity;
		this.list=data;
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
			view = ScreenUtil.inflate(R.layout.item_grid_commodity_appraise);
			holder=new Holder();
			holder.imageView=(ImageView) view.findViewById(R.id.imageView);
			view.setTag(holder);
		}else {
			holder= (Holder) view.getTag();
		}
		
		GlideImgUitl.glideLoaderPic(mActivity, list.get(position), holder.imageView);
		return view;
	}
	
	class Holder{
		ImageView imageView;
	}
	
	
}
