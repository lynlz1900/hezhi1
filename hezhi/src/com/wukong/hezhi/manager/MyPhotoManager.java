package com.wukong.hezhi.manager;

import android.app.Activity;
import android.content.Intent;

import com.wukong.hezhi.function.imagepicker.ImagePicker;
import com.wukong.hezhi.function.imagepicker.bean.ImageItem;
import com.wukong.hezhi.function.imagepicker.loader.GlideImageLoader;
import com.wukong.hezhi.function.imagepicker.ui.ImageGridActivity;
import com.wukong.hezhi.function.imagepicker.view.CropImageView;

import java.util.List;

/**
 * @author Huzhiyin
 * @ClassName: MyPhotoManager
 * @Description: TODO(选择照片管理类)
 * @date 2017年12月18日 下午16:24:17
 */
public class MyPhotoManager {
    private Activity mActivity;
    private ImagePicker imagePicker;

    private MyPhotoManager() {
    }

    private static class Holder {
        private static final MyPhotoManager SINGLETON = new MyPhotoManager();
    }

    public static MyPhotoManager getInstance() {
        return Holder.SINGLETON;
    }

    private void initImagePicker() {

        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());//图片加载器
        imagePicker.setMultiMode(false);//是否为多选
        imagePicker.setCrop(true);//是否剪裁
        imagePicker.setFocusWidth(500);//需要剪裁的宽
        imagePicker.setFocusHeight(500);//需要剪裁的高
        imagePicker.setOutPutX(200);
        imagePicker.setOutPutY(200);
        imagePicker.setStyle(CropImageView.Style.CIRCLE);//圆形
        imagePicker.setShowCamera(true);//是否显示摄像

    }

    public void select(Activity activity, ImagePicker imagePicker, OnSelectedMyPhotoListener onSelectedMyPhotoListener) {
        this.mActivity = activity;
        this.mListener = onSelectedMyPhotoListener;
        if (imagePicker != null) {
            this.imagePicker = imagePicker;
        } else {
            initImagePicker();
        }
        Intent intent = new Intent(mActivity, ImageGridActivity.class);
        mActivity.startActivity(intent);
    }

    /**
     * 得到已经选择好的图片，这个方法必须在onActivityResult中进行回调
     */
    public void setSelectedPhoto(List<ImageItem> imageItems) {
        mListener.onSelectedPhoto(imageItems);
    }

    private OnSelectedMyPhotoListener mListener;

    public interface OnSelectedMyPhotoListener {
        public void onSelectedPhoto(List<ImageItem> imageItems);
    }
}
