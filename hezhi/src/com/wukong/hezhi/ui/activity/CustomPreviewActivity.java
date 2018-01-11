package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CustomPicInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.BitmapCombineUtil;
import com.wukong.hezhi.utils.BitmapCompressUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ViewShowAniUtil;

import java.io.File;

/**
 * @author HuZhiyin
 * @ClassName: CustomPreviewActivity
 * @Description: TODO(外观定制信息预览)
 * @date 2017-8-8 下午1:59:44
 */
public class CustomPreviewActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.commodity_custom_preview);
    public String pageCode = "commodity_custom_preview";

    @ViewInject(R.id.layout_rl)
    private RelativeLayout layout_rl;

    @ViewInject(R.id.pic_rl)
    private RelativeLayout pic_rl;

    @ViewInject(R.id.templet_iv)
    private ImageView templet_iv;

    @ViewInject(R.id.bottom_iv)
    private ImageView bottom_iv;

    private String picPath;//无底合成图路径
    private String bottomPicPath;//有底合成图路径

    /**
     * 商品信息
     */
    private CommodityInfo commodityInfo;

    @Override
    protected boolean isNotAddTitle() {
        return false;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_commodity_custom_preview;
    }

    @Override
    protected void init() {
        commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
        picPath = getIntent().getStringExtra(Constant.Extra.PIC_PATH);
        if (commodityInfo == null) {
            commodityInfo = new CommodityInfo();
        }
        initView();
    }

    private void initView() {
        headView.setTitleStr(ContextUtil.getString(R.string.cumstom_preview));
        headView.setRightTitleText(ContextUtil.getString(R.string.save));
        showPic();
    }

    /**
     * 展示底图
     */
    private void showPic() {
        showProgressDialog();
        Glide.with(ContextUtil.getContext()).load(commodityInfo.getBottomUrl()).asBitmap().fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation arg1) {
                        /**计算底图大小及缩放比例*/
                        calculateTemplet(arg0);
                        /**设置底图的大小*/
                        setBaseMap(arg0);
                        /**设置合成后模板的大小及位置*/
                        setTemplet();
                        /**设置模板背景的大小*/
                        ViewGroup.LayoutParams parapic_rl = pic_rl.getLayoutParams();
                        parapic_rl.height = h;
                        parapic_rl.width = w;
                        pic_rl.setLayoutParams(parapic_rl);

                        dismissProgressDialog();
                        ViewShowAniUtil.beginAnimSet(templet_iv);
                    }
                });
    }

    /**
     * 设置底图的大小
     */
    private void setBaseMap(Bitmap bitmap) {
        ViewGroup.LayoutParams para_bottom_iv = bottom_iv.getLayoutParams();
        para_bottom_iv.height = h;
        para_bottom_iv.width = w;
        bottom_iv.setLayoutParams(para_bottom_iv);
        bottom_iv.setImageBitmap(bitmap);
    }

    /**
     * 设置合成后模板的大小及位置
     */
    private void setTemplet() {
        Bitmap b = BitmapFactory.decodeFile(picPath);
        float w1 = w * 5 / 8;
        float h2 = b.getHeight();
        float w2 = b.getWidth();

        int w = (int) w1;
        int h = (int) (h2 * (w1 / w2));

        Bitmap bm = Bitmap.createScaledBitmap(b, w,
                h, true);// 压缩图片
        templet_iv.setVisibility(View.VISIBLE);
        templet_iv.setImageBitmap(bm);

        int pic_left = (int) (commodityInfo.getLeft_x() * ratio);//左边距
        int pic_top = (int) (commodityInfo.getLeft_y() * ratio);//上边距
        int pic_with = (int) ((commodityInfo.getRight_x() - commodityInfo.getLeft_x()) * ratio);//宽度
        int pic_height = (int) ((commodityInfo.getRinght_y() - commodityInfo.getLeft_y()) * ratio); //图片高度
        RelativeLayout.LayoutParams para_kuang_iv = (RelativeLayout.LayoutParams) templet_iv.getLayoutParams();
        para_kuang_iv.width = pic_with;
        para_kuang_iv.height = pic_height;
        para_kuang_iv.setMargins(pic_left, pic_top, 0, 0);
        templet_iv.setLayoutParams(para_kuang_iv);

    }

    private float service_templet_h = 1, service_templet_w = 1;//服务器模板的宽高
    private int w = 1, h = 1;//显示在手机上模板的宽高
    private float ratio = 1;//缩放比

    /**
     * 计算底图大小及缩放比例
     */
    private void calculateTemplet(Bitmap bitmap) {
        service_templet_w = bitmap.getWidth();//计算服务器模板的宽度
        service_templet_h = bitmap.getHeight();//计算服务器模板的高度
        w = ScreenUtil.getScreenWidth() - ScreenUtil.dp2px(24);//计算显示在屏幕上模板的宽度(宽度填满屏幕)
        ratio = ((float) w) / service_templet_w;//通过控制宽度，计算缩放比例值
        h = (int) (service_templet_h * ratio);//计算显示在屏幕上模板的高度（根据服务器模板的宽高比，计算屏幕高度）
        float bg_h = layout_rl.getHeight();
        float ratio2 = 1;
        if (h > bg_h) {//如果缩放后的比例高于图片占据屏幕最大的高度，则在原基础上继续以高度缩放。并计算缩放比。
            ratio2 = bg_h / (float) h;
            h = (int) bg_h;
            w = (int) (w * ratio2);
        }
        ratio = ratio * ratio2;
    }


    @OnClick(value = {R.id.header_right})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_right:// 下一步
                next();
                break;
        }
    }

    /**
     * 下一步，合成图片并保存，跳转页面
     */
    private void next() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmapBottom = BitmapCombineUtil.combineImg2Bmp2(bottom_iv, templet_iv);// 合成底图和模板
                bitmapBottom = BitmapCompressUtil.whCompress(bitmapBottom, 750, 750);
                bottomPicPath = FileUtil.saveBitmap2File(bitmapBottom, HezhiConfig.PIC_PATH, HezhiConfig.CUSTOM_IMG_NAME);// 保存模板图片
                runOnUiThread(new Runnable() {
                    public void run() {
                        postPic();
                    }
                });
            }
        }).start();
    }

    private void postPic() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", UserInfoManager.getInstance().getUserId() + "");// 产品id
        params.addBodyParameter("productId", commodityInfo.getId() + "");// 产品id
        params.addBodyParameter("pictureUrl", new File(bottomPicPath));// 定制图片(含背景)
        params.addBodyParameter("customUrl", new File(picPath)); // 定制图片(不含背景)
        String URL = HttpURL.URL1 + HttpURL.SAVE_CUSTOM_NEW;
        new HttpUtils().send(HttpRequest.HttpMethod.POST, URL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                dismissProgressDialog();
                ScreenUtil.showToast(ContextUtil.getString(R.string.save_tip));
                ResponseJsonInfo<CustomPicInfo> info = JsonUtil.fromJson(arg0.result, CustomPicInfo.class);
                if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
                    CustomPicInfo customPicInfo = info.getBody();
                    String picUrl = customPicInfo.getPictureUrl();
                    String customId = customPicInfo.getCustomId();
                    commodityInfo.setImageUrl(picUrl);
                    commodityInfo.setCustomId(customId);
                    commodityInfo.setCustomizationType(Constant.Commodity.TAILOR);// 标记为外观定制
                    Intent intent = new Intent(mActivity, MyCustomizationTobuyActivity.class);
                    intent.putExtra(Constant.Extra.CUSTOMIZATION_INFO, commodityInfo);
                    intent.putExtra(Constant.Extra.ISFROM_COMMODITYPRE, true);
                    startActivity(intent);
                    finish();  //modify by Huangfeifei 2017-10-16
                    ActivitiesManager.getInstance().finishActivity(CustomComposeActivity.class);
                    ActivitiesManager.getInstance().finishActivity(CustomTemplateActivity.class);
                }
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                dismissProgressDialog();
            }
        });
    }

}
