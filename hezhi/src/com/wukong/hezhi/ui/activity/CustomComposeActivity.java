package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.function.imagecompose.ImageListener;
import com.wukong.hezhi.function.imagepicker.ImagePicker;
import com.wukong.hezhi.function.imagepicker.bean.ImageItem;
import com.wukong.hezhi.function.imagepicker.loader.GlideImageLoader;
import com.wukong.hezhi.function.imagepicker.view.CropImageView;
import com.wukong.hezhi.manager.MyPhotoManager;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.BitmapCombineUtil;
import com.wukong.hezhi.utils.BitmapCompressUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.PhotoUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;
import java.util.List;

/**
 * @author HuZhiyin
 * @ClassName: CustomComposeActivity
 * @Description: TODO(定制模板合成界面)
 * @date 2017-10-8 下午1:59:44
 */
public class CustomComposeActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.custom_compose);
    public String pageCode = "custom_compose";

    @ViewInject(R.id.header)
    private RelativeLayout header;//头布局

    @ViewInject(R.id.reset_bt)
    private ImageView reset_bt;//重置按钮

    @ViewInject(R.id.pic_rl)
    private RelativeLayout pic_rl;//模板背景

    @ViewInject(R.id.pic_iv)
    private ImageView pic_iv;//照片

    @ViewInject(R.id.bg_v)
    private View bg_v;//照片黑底背景

    @ViewInject(R.id.plus_iv)
    private ImageView plus_iv;//添加照片按钮

    @ViewInject(R.id.kuang_iv)
    private ImageView kuang_iv;//照片范围框框

    @ViewInject(R.id.templet_iv)
    private ImageView templet_iv;//模板

    @ViewInject(R.id.dicr_tv)
    private TextView dicr_tv;//文字说明

    private int w = 1, h = 1;//显示在手机上模板的宽高
    private float service_templet_h = 1, service_templet_w = 1;//服务器模板的宽高
    private boolean isRetangle = true;//是否是矩形
    private boolean isAddPic = true;//是否添加图片
    private boolean isAppWenzi = true;//是否添加文字
    private int charCount = 20;//文字的字数
    /**
     * 商品信息
     */
    private CommodityInfo commodityInfo;
    /**
     * 模板信息
     */
    private TempletInfo templetInfo;

    @Override
    protected boolean isNotAddTitle() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_custom_compose;
    }

    @Override
    protected void init() {
        initData();
        initView();
    }

    private void initData() {
        commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
        templetInfo = (TempletInfo) getIntent().getSerializableExtra(Constant.Extra.TEMPLATE);
        if (commodityInfo == null) {
            commodityInfo = new CommodityInfo();
        }
        if (templetInfo == null) {
            templetInfo = new TempletInfo();
        }
        isAppWenzi = templetInfo.isAppWenzi();
        isAddPic = templetInfo.isAddPic();
        if (templetInfo.getPictureShape() == 1) {
            isRetangle = true;
        } else if (templetInfo.getPictureShape() == 2) {
            isRetangle = false;
        }
        charCount = templetInfo.getCharCount();
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
        setSwipeBackEnable(false);// 禁止侧滑消除

        pic_iv.setOnTouchListener(new ImageListener(new ImageListener.ImageCallback() {
            @Override
            public void move(boolean isMove) {
                if (isMove) {
                    kuang_iv.setVisibility(View.VISIBLE);
                    bg_v.setVisibility(View.VISIBLE);
                } else {
                    kuang_iv.setVisibility(View.INVISIBLE);
                    bg_v.setVisibility(View.INVISIBLE);
                }
            }
        }));
        showPic();
    }

    private void showView() {
        if (isAddPic) {//需要添加图片
            plus_iv.setVisibility(View.VISIBLE);
            kuang_iv.setVisibility(View.VISIBLE);
            bg_v.setVisibility(View.VISIBLE);
        } else {
            plus_iv.setVisibility(View.INVISIBLE);
            kuang_iv.setVisibility(View.INVISIBLE);
            bg_v.setVisibility(View.INVISIBLE);
        }
        if (isAppWenzi) {//需要添加文字
            dicr_tv.setVisibility(View.VISIBLE);
        } else {
            dicr_tv.setText("");
            dicr_tv.setVisibility(View.INVISIBLE);
        }
    }

    private float ratio = 1;//缩放比
    private int pic_left;//图片的左边距
    private int pic_top;//图片的上边距
    private int pic_with;//图片的宽度
    private int pic_height;//图片的高度

    /**
     * 展示底图
     */
    private void showPic() {
        showProgressDialog();
        Glide.with(ContextUtil.getContext()).load(templetInfo.getTempletUrl()).asBitmap().fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation arg1) {
                        /**计算模板的宽高及缩放比例*/
                        calculateTemplet(arg0);

                        /**计算照片的宽高及位置*/
                        calculatePic();

                        /**设置模板背景的大小*/
                        ViewGroup.LayoutParams parapic_rl = pic_rl.getLayoutParams();
                        parapic_rl.height = h;
                        parapic_rl.width = w;
                        pic_rl.setLayoutParams(parapic_rl);

                        /**设置模板的大小*/
                        ViewGroup.LayoutParams para_templet_iv = templet_iv.getLayoutParams();
                        para_templet_iv.height = h;
                        para_templet_iv.width = w;
                        templet_iv.setLayoutParams(para_templet_iv);
                        templet_iv.setImageBitmap(arg0);

                        /**计算并设置文字的位置，大小，颜色*/
                        int wenzi_left_x = (int) (templetInfo.getWenzi_left_x() * ratio);
                        int wenzi_left_y = (int) (templetInfo.getWenzi_left_y() * ratio);
                        int wenzi_w = (int) ((templetInfo.getWenzi_right_x() - templetInfo.getWenzi_left_x()) * ratio);
                        int wenzi_h = (int) ((templetInfo.getWenzi_right_y() - templetInfo.getWenzi_left_y()) * ratio);
                        int wenzi_size = (int) (templetInfo.getWenzi_size() * ratio);

                        RelativeLayout.LayoutParams para_dicr_tv = (RelativeLayout.LayoutParams) dicr_tv.getLayoutParams();
                        para_dicr_tv.width = wenzi_w;
                        para_dicr_tv.height = wenzi_h;
                        para_dicr_tv.setMargins(wenzi_left_x, wenzi_left_y, 0, 0);

                        dicr_tv.setLayoutParams(para_dicr_tv);
                        try {
                            dicr_tv.setTextColor(Color.parseColor(templetInfo.getWenzi_color()));//设置文字的颜色
                            dicr_tv.setHintTextColor(Color.parseColor(templetInfo.getWenzi_color()));//设置hint的颜色
                        }catch (Exception e){
                        }
                        dicr_tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, wenzi_size);//设置文字的大小
                        dicr_tv.setCursorVisible(false);//去掉光标

                        /**设置照片框框的大小及位置*/
                        RelativeLayout.LayoutParams para_kuang_iv = (RelativeLayout.LayoutParams) kuang_iv.getLayoutParams();
                        para_kuang_iv.width = pic_with;
                        para_kuang_iv.height = pic_height;
                        para_kuang_iv.setMargins(pic_left, pic_top, 0, 0);
                        kuang_iv.setLayoutParams(para_kuang_iv);

                        /**设置照片黑底背景的大小*/
                        RelativeLayout.LayoutParams para_bg_v = (RelativeLayout.LayoutParams) bg_v.getLayoutParams();
                        para_bg_v.height = pic_height;
                        para_bg_v.width = pic_with;
                        para_bg_v.setMargins(pic_left, pic_top, 0, 0);
                        bg_v.setLayoutParams(para_bg_v);

                        /**设置添加图片加号的大小*/
                        RelativeLayout.LayoutParams para_plus_iv = (RelativeLayout.LayoutParams) plus_iv.getLayoutParams();
                        para_plus_iv.height = pic_height;
                        para_plus_iv.width = pic_with;
                        para_plus_iv.setMargins(pic_left, pic_top, 0, 0);
                        plus_iv.setLayoutParams(para_plus_iv);

                        showView();
                        dismissProgressDialog();
                    }
                });
    }

    /**
     * 计算模板的宽高及缩放比例
     */
    private void calculateTemplet(Bitmap bitmap) {
        service_templet_w = bitmap.getWidth();//计算服务器模板的宽度
        service_templet_h = bitmap.getHeight();//计算服务器模板的高度

        w = ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(24);//计算显示在屏幕上模板的宽度(宽度填满屏幕)
        ratio = ((float) w) / service_templet_w;//通过控制宽度，计算缩放比例值
        h = (int) (service_templet_h * ratio);//计算显示在屏幕上模板的高度（根据服务器模板的宽高比，计算屏幕高度）
        float bg_h = ScreenUtil.getScreenHeight() - ScreenUtil.dp2px(24) - header.getHeight();//图片最大占据屏幕的高度，上下间距24dp,头布局的高度
        float ratio2 = 1;
        if (h > bg_h) {//如果缩放后的比例高于图片占据屏幕最大的高度，则在原基础上继续以高度缩放。并计算缩放比。
            ratio2 = bg_h / (float) h;
            h = (int) bg_h;
            w = (int) (w * ratio2);
        }
        ratio = ratio * ratio2;
    }

    /**
     * 计算照片的宽高及位置
     */
    private void calculatePic() {
        /**计算相框里图片的大小以及位置*/
        pic_left = (int) (templetInfo.getLeft_x() * ratio);//左边距
        pic_top = (int) (templetInfo.getLeft_y() * ratio);//上边距
        pic_with = (int) ((templetInfo.getRight_x() - templetInfo.getLeft_x()) * ratio);//宽度
        pic_height = (int) ((templetInfo.getRinght_y() - templetInfo.getLeft_y()) * ratio); //图片高度
    }

    private void selectPhoto() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());//图片加载器
        imagePicker.setMultiMode(false);//是否为多选
        imagePicker.setCrop(true);//是否剪裁
        imagePicker.setFocusWidth(pic_with);//需要剪裁的宽
        imagePicker.setFocusHeight(pic_height);//需要剪裁的高
        imagePicker.setOutPutX(pic_with);
        imagePicker.setOutPutY(pic_height);
        if (isRetangle) {
            imagePicker.setStyle(CropImageView.Style.RECTANGLE);//方形
        } else {
            imagePicker.setStyle(CropImageView.Style.CIRCLE);//圆形
        }

        imagePicker.setShowCamera(true);//是否显示摄像
        MyPhotoManager.getInstance().select(this, imagePicker, new MyPhotoManager.OnSelectedMyPhotoListener() {
            @Override
            public void onSelectedPhoto(List<ImageItem> imageItems) {
                Bitmap photo = BitmapFactory.decodeFile(imageItems.get(0).path);
                if (photo != null) {
                    if (photo.getWidth() < pic_with || photo.getHeight() < pic_height) {//如果图片不够高，则将图片拉伸
                        photo = BitmapCombineUtil.zoomImg(photo, pic_with, pic_height);
                    }
                    /**设置模板的大小*/
                    RelativeLayout.LayoutParams para1 = (RelativeLayout.LayoutParams) pic_iv.getLayoutParams();
                    para1.width = pic_with;
                    para1.height = pic_height;
                    para1.setMargins(pic_left, pic_top, 0, 0);
                    pic_iv.setLayoutParams(para1);
                    pic_iv.setVisibility(View.VISIBLE);
                    pic_iv.setImageBitmap(photo);
                    reset_bt.setVisibility(View.VISIBLE);
                } else {
                    pic_iv.setVisibility(View.INVISIBLE);
                    ScreenUtil.showToast(ContextUtil.getString(R.string.tail_fail));
                }
                showAddPic();
            }
        });
    }

    /**
     * 选择图片
     **/
    private void chooseImage(int type) {
        deletFile(HezhiConfig.PIC_PATH + HezhiConfig.UP_PHOTO);//先删除
        Uri tempUri = Uri.fromFile(new File(HezhiConfig.PIC_PATH, HezhiConfig.UP_PHOTO));
        PhotoManager manager = PhotoManager.getInstance();
        manager.setWandH(pic_with, pic_height);
        manager.setWHRatio(pic_with, pic_height);
        manager.choicePhoto(mActivity, type, tempUri, new PhotoListener());
    }

    /**
     * 选择相册照片或拍照图片的监听
     */
    private class PhotoListener implements PhotoManager.OnSelectedPhotoListener {
        @Override
        public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo) {
            if (photo != null) {
                if (!isRetangle) {
                    photo = PhotoUtil.toRoundBitmap(photo);// 如果是模板是圆形镂空，则转换图片成圆形
//                    pic_iv.setClipToOutline(true);
//                    pic_iv.setOutlineProvider(new ViewOutlineProvider() {
//                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//                        @Override
//                        public void getOutline(View view, Outline outline) {
//                            outline.setOval(0, 0,
//                                    pic_with, pic_height);
//                        }
//                    });
                }
                if (photo.getWidth() < pic_with || photo.getHeight() < pic_height) {//如果图片不够高，则将图片拉伸
                    photo = BitmapCombineUtil.zoomImg(photo, pic_with, pic_height);
                }

                /**设置模板的大小*/
                RelativeLayout.LayoutParams para1 = (RelativeLayout.LayoutParams) pic_iv.getLayoutParams();
                para1.width = pic_with;
                para1.height = pic_height;
                para1.setMargins(pic_left, pic_top, 0, 0);
                pic_iv.setLayoutParams(para1);
                pic_iv.setVisibility(View.VISIBLE);
                pic_iv.setImageBitmap(photo);
                reset_bt.setVisibility(View.VISIBLE);
            } else {
                pic_iv.setVisibility(View.INVISIBLE);
                ScreenUtil.showToast(ContextUtil.getString(R.string.tail_fail));
            }
            showAddPic();
        }
    }

    @OnClick(value = {R.id.close_bt, R.id.dicr_tv, R.id.reset_bt, R.id.sure_bt, R.id.plus_iv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.close_bt://关闭页面
                showClosekTip();
                break;
            case R.id.reset_bt://重置
                reset();
                break;
            case R.id.sure_bt://确定保存
                save();
                break;
            case R.id.plus_iv://添加图片
//                chooseImage(PhotoManager.FROM_ALBUM);
                selectPhoto();
                break;
            case R.id.dicr_tv://弹出文字编辑框
                showEditText();
                break;
        }
    }

    private void reset() {
        pic_iv.setImageMatrix(new Matrix());
        pic_iv.setVisibility(View.INVISIBLE);//隐藏
        reset_bt.setVisibility(View.INVISIBLE);
        showAddPic();
    }

    /**
     * 显示和隐藏添加图片按钮
     */
    private void showAddPic() {
        if (pic_iv.getVisibility() == View.VISIBLE) {
            plus_iv.setVisibility(View.INVISIBLE);
            kuang_iv.setVisibility(View.INVISIBLE);
            bg_v.setVisibility(View.INVISIBLE);
        } else {
            plus_iv.setVisibility(View.VISIBLE);
            kuang_iv.setVisibility(View.VISIBLE);
            bg_v.setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout editText_ll;
    private EditText editText;
    private TextView word_count_tv;

    /**
     * 显示编辑框
     */
    private void showEditText() {

        editText_ll = (LinearLayout) ScreenUtil.inflate(R.layout.layout_edittext2);
        editText = (EditText) editText_ll.findViewById(R.id.text_et);
        word_count_tv = (TextView) editText_ll.findViewById(R.id.word_count_tv);

        if (noInput()) {
            editText.setText("");
        } else {
            editText.setText(dicr_tv.getText().toString());
        }
        if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
            word_count_tv.setText(editText.getText().toString().trim().length() + "/" + charCount);
        } else {
            word_count_tv.setText("");
        }
        CustomAlterDialog.Builder builder = new CustomAlterDialog.Builder(mActivity);
        CustomAlterDialog dialog = builder.setTitle(ContextUtil.getString(R.string.input_text)).setView(editText_ll)
                .setCancelButton(ContextUtil.getString(R.string.cancel), null)
                .setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v, CustomAlterDialog dialog) {
                        showText();
                    }
                }).build();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);// 显示键盘
        dialog.show();

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charCount)});
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(s)) {
                    word_count_tv.setText("");
                } else {
                    word_count_tv.setText(s.length() + "/" + charCount);
                }
            }
        });
    }

    /**
     * 显示文字
     */
    private void showText() {
        if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
            dicr_tv.setText(editText.getText().toString().trim());
        } else {
            dicr_tv.setText(ContextUtil.getString(R.string.click_input));
        }
    }

    /**
     * 关闭页面提醒
     */
    private void showClosekTip() {
        if (pic_iv.getVisibility() == View.VISIBLE
                || !noInput()) {
            new CustomAlterDialog.Builder(this).setTitle(ContextUtil.getString(R.string.tip))
                    .setMsg(ContextUtil.getString(R.string.back_tip))
                    .setCancelButton(ContextUtil.getString(R.string.cancel), null)
                    .setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {

                        @Override
                        public void onDialogClick(View v, CustomAlterDialog dialog) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    }).show();
        } else {
            finish();
        }
    }

    /**
     * 是否输入了文字
     */
    private boolean noInput() {
        if (dicr_tv.getVisibility() == View.VISIBLE) {
            String text = dicr_tv.getText().toString().trim();
            return text.equals(ContextUtil.getString(R.string.click_input));
        } else {
            return true;
        }
    }

    private String picPath;

    /**
     * 合成图片并保存至文件
     */
    private void save() {
        if (pic_iv.getVisibility() != View.VISIBLE && isAddPic) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.select_pic));
            return;
        }
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap composeBm = null;
                if (!noInput()) {
                    composeBm = BitmapCombineUtil.combineBmp2(pic_iv, templet_iv, dicr_tv);// 合成的图片;
                } else {
                    composeBm = BitmapCombineUtil.combineBmp2(pic_iv, templet_iv, null);// 合成的图片;
                }

                Bitmap outPut = BitmapCompressUtil.whCompress(composeBm, (int) service_templet_w, (int) service_templet_h);//将合成图片统一输出大小
                deletFile(picPath);//先删除
                picPath = FileUtil.saveBitmap2File(outPut, HezhiConfig.PIC_PATH, HezhiConfig.CUSTOM_TEMPLET_IMG_NAME2);// 保存模板图片
                runOnUiThread(new Runnable() {
                    public void run() {
                        dismissProgressDialog();
                        Intent intent = new Intent(CustomComposeActivity.this, CustomPreviewActivity.class);
                        intent.putExtra(Constant.Extra.COMMDITYINFO, commodityInfo);
                        intent.putExtra(Constant.Extra.PIC_PATH, picPath);
                        startActivity(intent);
                    }
                });

            }
        }).start();
    }

    private void deletFile(String path) {
        if (!TextUtils.isEmpty(path)) {// 删除图片
            File file = new File(path);
            if (file != null && file.exists()) {
                file.delete();
            }
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showClosekTip();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}