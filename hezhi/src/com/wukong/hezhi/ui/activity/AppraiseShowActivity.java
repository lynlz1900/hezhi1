package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.AppraiseImageShowAdapter;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfo;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.lIstViewItem;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.function.imagepicker.ImagePicker;
import com.wukong.hezhi.function.imagepicker.bean.ImageItem;
import com.wukong.hezhi.function.imagepicker.loader.GlideImageLoader;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.MyPhotoManager;
import com.wukong.hezhi.manager.MyPhotoManager.OnSelectedMyPhotoListener;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.fragment.AppraiseBeingFragment;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.GridViewForListview;
import com.wukong.hezhi.ui.view.RatingBar;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;
import com.wukong.hezhi.utils.ThumbUtil;
import com.wukong.hezhi.utils.ZipperUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 *
 * @ClassName: AppraiseShowActivity
 * @Description: TODO(评价晒单)
 * @author HuangFeiFei
 * @date 2017-12-18
 *
 */
public class AppraiseShowActivity extends BaseActivity {

    public String pageName = ContextUtil.getString(R.string.appraise_show);
    public String pageCode = "appraise_show";

    @ViewInject(R.id.image_commodity)
    private ImageView image_commodity;

    @ViewInject(R.id.ratingBar)
    private RatingBar ratingBar;

    @ViewInject(R.id.input_et)
    private EditText input_et;

    @ViewInject(R.id.word_count_tv)
    private TextView word_count_tv;

    @ViewInject(R.id.gv_images)
    private GridViewForListview gv_images;

    /**
     * 评价中心信息
     **/
    private CommodityAppraiseCenterInfo appraiseCenterInfo;

    /**
     * 判断图片是否刚好九张
     **/
    private boolean isComplete = false;

    /**
     * 评价图片列表
     **/
    private ArrayList<lIstViewItem> imgs;
    private AppraiseImageShowAdapter appraiseShowImageAdapter;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_appraise_show;
    }

    @Override
    protected void init() {
        headView.setTitleStr(ContextUtil.getString(R.string.appraise_show));
        headView.setRightTitleText(ContextUtil.getString(R.string.submit));
        initView();
    }

    //初始化控件
    private void initView() {

        appraiseCenterInfo = (CommodityAppraiseCenterInfo) getIntent().
                getSerializableExtra(Constant.Extra.APPRAISE_CENTER_INFO);
        if (appraiseCenterInfo == null) {
            appraiseCenterInfo = new CommodityAppraiseCenterInfo();
        }

        ratingBar.setStarMark(5.0f);
        imgs = new ArrayList<>();
        word_count_tv.setText("0/500");
        GlideImgUitl.glideLoaderNoAnimation
                (ContextUtil.getContext(), appraiseCenterInfo.getImageUrl(), image_commodity, 2);

        appraiseShowImageAdapter = new AppraiseImageShowAdapter(mActivity, imgs);
        gv_images.setAdapter(appraiseShowImageAdapter);

        setListener();

        imgs.add(new lIstViewItem(0, ""));
        appraiseShowImageAdapter.notifyDataSetChanged();
    }

    @OnClick(value = {R.id.header_right})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_right:
                appraiseShow();
                break;
            default:
                break;
        }
    }

    /**
     * 设置监听
     **/
    private void setListener() {
        input_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    word_count_tv.setText("0/500");
                } else {
                    word_count_tv.setText(s.length() + "/500");
                }
            }
        });

        gv_images.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imgs.size() - 1 && !isComplete)
                    getImagePath(10 - imgs.size());
                else
                    gotoImageDel(position);
            }
        });
    }

    /**
     * 删除本地图片
     **/
    private void gotoImageDel(int position) {
        Intent intent = new Intent();
        intent.setClass(this, AppraiseImageDelActivity.class);
        intent.putExtra("isComplete", isComplete);
        intent.putExtra("imageURL", imgs);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    /**
     * 添加图片
     **/
    private void getImagePath(int num) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());//图片加载器
        imagePicker.setSelectLimit(num);//设置可以选择几张
        imagePicker.setMultiMode(true);//是否为多选
        imagePicker.setShowCamera(true);//是否显示摄像
        imagePicker.setCrop(false);//不要裁剪
        MyPhotoManager.getInstance().select(mActivity, imagePicker, new OnSelectedMyPhotoListener() {
            @Override
            public void onSelectedPhoto(List<ImageItem> imageItems) {
                if ((imageItems.size() + imgs.size() - 1) > 9) {
                    ScreenUtil.showToast(ContextUtil.getString(R.string.image_is_over));
                    return;
                }

                if (imgs.size() > 0)
                    imgs.remove(imgs.size() - 1);

                for (ImageItem imageItem : imageItems) {
                    imgs.add(new lIstViewItem(1, imageItem.path));
                }
                if (imgs.size() < 9) {
                    imgs.add(new lIstViewItem(0, ""));
                    isComplete = false;
                } else if (imgs.size() == 9) {
                    isComplete = true;
                }

                appraiseShowImageAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 评价晒单
     **/
    public void appraiseShow() {

        if (TextUtils.isEmpty(input_et.getText().toString())) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.appraise_content_input));
            return;
        }

        if (StringUtil.isContainsEmoji(input_et.getText().toString())) {// 判断是否含有表情
            ScreenUtil.showToast(ContextUtil.getString(R.string.no_emoji));
            return;
        }

        int appraiseStatus = (int) ratingBar.getStarMark();
        final String score = CommodityAppraiseInfo.getScoreString(appraiseStatus);
        if (appraiseStatus == 0 || TextUtils.isEmpty(score)) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.appraise_status_error));
            return;
        }

        final List<String> images = new ArrayList<>();
        for (int i = 0; i < imgs.size(); i++) {
            if (i != imgs.size() - 1) {
                images.add(imgs.get(i).url);
            } else if (isComplete && i == imgs.size() - 1) {
                images.add(imgs.get(i).url);
            }
        }

        showProgressDialog();
        if (images.size() < 1) {
            appraiseShowHttp(score, "");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> imagesCompress = ThumbUtil.compress(images);
                        ZipperUtil.zip(HezhiConfig.HEZHI_PATH + HezhiConfig.APPRAISE_UPLOAD_IMAGE, imagesCompress);
                        appraiseShowHttp(score, HezhiConfig.HEZHI_PATH + HezhiConfig.APPRAISE_UPLOAD_IMAGE);
                    } catch (Exception e) {
                        dismissProgressDialog();
                        ScreenUtil.showToast(ContextUtil.getString(R.string.appraise_fail));
                    }
                }
            }).start();
        }
    }

    /**
     * 评价晒单http接口
     **/
    public void appraiseShowHttp(String score, final String imageURL) {
        String url = HttpURL.URL1 + HttpURL.APPRAISE_SHOW;
        RequestParams params = new RequestParams();
        params.addBodyParameter("memberId", UserInfoManager.getInstance().getUserId() + "");
        params.addBodyParameter("orderId", appraiseCenterInfo.getId() + "");
        params.addBodyParameter("score", score);
        params.addBodyParameter("commentContent", input_et.getText().toString());
        if (!TextUtils.isEmpty(imageURL) && new File(imageURL) != null)
            params.addBodyParameter("pictureUrls", new File(imageURL));
//        else
//        	params.addBodyParameter("pictureUrls", new File(""));
        LogUtil.i(url);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
                dismissProgressDialog();
            }

            @SuppressWarnings("rawtypes")
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LogUtil.i(arg0.result);
                dismissProgressDialog();
                ResponseJsonInfo info = JsonUtil.fromJson(
                        arg0.result, ResponseJsonInfo.class);

                if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
                    ObserveManager.getInstance().notifyState(AppraiseBeingFragment.class,
                            AppraiseShowActivity.class,
                            appraiseCenterInfo.getId());// 已评价通知
                    ObserveManager.getInstance().notifyState(OrderCustFragment.class,
                            AppraiseShowActivity.class,
                            appraiseCenterInfo.getId());// 已评价通知
                    if (Constant.isFromPage.isOrderDetail)
                        ObserveManager.getInstance().notifyState(OrderCustDetailActivity.class,
                                AppraiseShowActivity.class,
                                appraiseCenterInfo.getId());// 已评价通知
                    if (Constant.isFromPage.isOrderListSearch)
                        ObserveManager.getInstance().notifyState(SearchOrderCustActivity.class,
                                AppraiseShowActivity.class,
                                appraiseCenterInfo.getId());// 已评价通知
                    FileUtil.deleteFile(imageURL);
                    ScreenUtil.showToast(ContextUtil.getString(R.string.appraise_success));
                    finish();
                } else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
                    if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
                        ScreenUtil.showToast(info.getPromptMessage());
                } else {
                    ScreenUtil.showToast(ContextUtil.getString(R.string.appraise_fail));
                }
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
        super.updateState(notifyTo, notifyFrom, msg);

        if (notifyTo.equals(getClass())) {
            if (notifyFrom.equals(AppraiseImageDelActivity.class)) {
                try {
                    ArrayList<lIstViewItem> list = (ArrayList<lIstViewItem>) msg;
                    imgs.clear();
                    imgs.addAll(list);
                    imgs.add(new lIstViewItem(0, ""));
                    appraiseShowImageAdapter.notifyDataSetChanged();
                    isComplete = false;
                } catch (Exception e) {
                }
            }
        }
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FileUtil.deleteDirectory(HezhiConfig.PIC_THUMBNAIL);
	}
}
