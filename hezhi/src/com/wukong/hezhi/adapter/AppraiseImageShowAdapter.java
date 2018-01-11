package com.wukong.hezhi.adapter;

import java.util.List;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.lIstViewItem;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 
 * 评价晒单本地图片图片显示列表适配器
 * @author HuangFeiFei
 *
 */
public class AppraiseImageShowAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<lIstViewItem> list;
	
	public AppraiseImageShowAdapter(Activity mActivity,List<lIstViewItem> data) {
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
	
	//返回当前布局的样式type
    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    //返回你有多少个不同的布局
    @Override
    public int getViewTypeCount() {
        return 2;
    }

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		int Type = getItemViewType(position);
		HolderFirst holderFirst;
		HolderOther holderOther;
		if(Type == 0){
			if (view==null) {
					view = ScreenUtil.inflate(R.layout.item_grid_commodity_appraise);
					holderFirst=new HolderFirst();
					holderFirst.imageView=(ImageView) view.findViewById(R.id.imageView);
					view.setTag(holderFirst);
			}else {
				holderFirst= (HolderFirst) view.getTag();
			}
		
			holderFirst.imageView.setImageResource(R.drawable.icon_uploadpic);
			holderFirst.imageView.setScaleType(ScaleType.CENTER_INSIDE);
		}else if(Type == 1){
			if (view==null) {
				view = ScreenUtil.inflate(R.layout.item_grid_commodity_appraise);
				holderOther=new HolderOther();
				holderOther.imageView=(ImageView) view.findViewById(R.id.imageView);
				view.setTag(holderOther);
			}else {
				holderOther= (HolderOther) view.getTag();
			}
		
			GlideImgUitl.glideLoaderNoAnimation(mActivity, list.get(position).url, holderOther.imageView, 2);
			holderOther.imageView.setScaleType(ScaleType.CENTER_CROP);
		}
				
		return view;
	}
	
	class HolderFirst{
		ImageView imageView;
	}
	
	class HolderOther{
		ImageView imageView;
	}
}
