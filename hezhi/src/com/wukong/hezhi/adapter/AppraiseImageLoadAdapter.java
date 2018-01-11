package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 
 * 评价图片图片加载显示左右滑动列表适配器
 * @author HuangFeiFei
 *
 */
public class AppraiseImageLoadAdapter extends PagerAdapter {

    private List<String> images = new ArrayList<String>();
    private Activity mActivity;
    public PhotoViewClickListener listener;

    public AppraiseImageLoadAdapter(Activity activity, List<String> images) {
        this.mActivity = activity;
        this.images = images;
    }

    public void setData(List<String> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	View view = ScreenUtil.inflate(R.layout.item_list_appraise_image);
    	PhotoView photoView = (PhotoView) view.findViewById(R.id.imageView);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            }
        });
        String url = images.get(position);
        GlideImgUitl.glideLoaderNoAnimation(mActivity, url,photoView, 2);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
