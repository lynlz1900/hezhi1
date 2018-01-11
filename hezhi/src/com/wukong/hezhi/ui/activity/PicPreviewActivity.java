package com.wukong.hezhi.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;

public class PicPreviewActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.preview_pic);
    public String pageCode = "preview_pic";

    @ViewInject(R.id.header)
    private RelativeLayout header;

    @ViewInject(R.id.pic_iv)
    private ImageView pic_iv;

    private String picPath;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_pic_preview;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
//        headView.setTitleStr(ContextUtil.getString(R.string.preview_pic));
//        headView.setRihgtTitleText("", R.drawable.icon_delete);
        picPath = getIntent().getStringExtra(Constant.Extra.PIC_PATH_LIST);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setImage(BitmapFactory.decodeFile(picPath));
    }

    private void setImage(Bitmap bitmap) {
        float pic_w = bitmap.getWidth();//计算图片的宽度
        float pic_h = bitmap.getHeight();//计算图片的高度
        int w = ScreenUtil.getScreenWidth();//计算显示在屏幕上模板的宽度(宽度填满屏幕)
        float ratio = ((float) w) / pic_w;//通过控制宽度，计算缩放比例值
        int h = (int) (pic_h * ratio);//计算显示在屏幕上模板的高度（根据服务器模板的宽高比，计算屏幕高度）

        if (h > ScreenUtil.getScreenHeight()) {//如果缩放后的高度还大于屏幕的高度。
            ratio = (float) ScreenUtil.getScreenHeight() / (float) h;
            h = ScreenUtil.getScreenHeight();
            w = (int) (w * ratio);
        }


        /**设置图片大小*/
        ViewGroup.LayoutParams para_pic_iv = pic_iv.getLayoutParams();
        para_pic_iv.height = h;
        para_pic_iv.width = w;
        pic_iv.setLayoutParams(para_pic_iv);
        pic_iv.setImageBitmap(bitmap);// 设置视频封面图

    }

    @OnClick(value = {R.id.header_left, R.id.header_right, R.id.pic_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_left:
                finish();
                break;
            case R.id.header_right:
                showDeleteDialog();
                break;
            case R.id.pic_iv:
                setHeaderVisible();
                break;
        }
    }

    private void setHeaderVisible() {
        if (header.getVisibility() == View.VISIBLE) {
            header.setVisibility(View.INVISIBLE);
        } else {
            header.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否删除
     */
    protected void showDeleteDialog() {
        new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.delete_pic))
                .setCancelButton(ContextUtil.getString(R.string.cancel), null)
                .setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v, CustomAlterDialog dialog) {
                        // TODO Auto-generated method stub
                        File file = new File(picPath);
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                        ObserveManager.getInstance().notifyState(UploadActivity.class, PicPreviewActivity.class, null);// 通知观察者数据发生了变化
                        finish();
                    }
                }).build().show();
    }

}
