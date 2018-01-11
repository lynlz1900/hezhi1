package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.AppraiseImageLoadAdapter;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.function.imagepicker.Utils;
import com.wukong.hezhi.function.imagepicker.view.ViewPagerFixed;
import com.wukong.hezhi.ui.view.RatingBar;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.StatusBarUtil;
import java.util.ArrayList;
import java.util.List;

/***
 *
 * @ClassName: AppraiseImageShowActivity
 * @Description: TODO(评价图片加载显示)
 * @author HuangFeiFei
 * @date 2017-12-20
 *
 */
public class AppraiseImageShowActivity extends BaseActivity {

    public String pageName = ContextUtil.getString(R.string.pic);
    public String pageCode = "pic";

    @ViewInject(R.id.header_title)
    protected TextView header_title;

    @ViewInject(R.id.back)
    protected TextView back;

    @ViewInject(R.id.viewpager)
    protected ViewPagerFixed mViewPager;

    @ViewInject(R.id.ratingBar)
    private RatingBar ratingBar;

    @ViewInject(R.id.text_appraise)
    protected TextView text_appraise;

    private CommodityAppraiseInfo commodityAppraiseInfo;
    private AppraiseImageLoadAdapter appraiseImageLoadAdapter;

    /**图片列表**/
    private List<String> imageURL;
    /**评论信息**/
    private String message;
     /**跳转进ImagePreviewFragment时的序号，第几个图片**/
    protected int mCurrentPosition = 0;    
    /**是否追加评论**/
    private boolean isAppraiseAdd;
    
    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_appraise_imageshow;
    }

    @Override
    protected void init() {
        initView();
    }

    //初始化控件
    @SuppressWarnings({"deprecation"})
    private void initView() {
        initVisibleState();
        commodityAppraiseInfo = (CommodityAppraiseInfo) getIntent().getSerializableExtra("imageURL");
        mCurrentPosition = getIntent().getIntExtra("position", 0);
        isAppraiseAdd = getIntent().getBooleanExtra("isAppraiseAdd", false);
        
        if (commodityAppraiseInfo == null) {
            commodityAppraiseInfo = new CommodityAppraiseInfo();
        }

        if(!isAppraiseAdd){
        	imageURL = commodityAppraiseInfo.getAppraiseListImages();
        	message = commodityAppraiseInfo.getAppraiseMessage();
        }
        else{
        	imageURL = commodityAppraiseInfo.getAppraiseListImagesAdd();
        	message = commodityAppraiseInfo.getAppraiseAddMessage();
        }
        if (imageURL == null)
            imageURL = new ArrayList<>();

        text_appraise.setText(message);
        text_appraise.setMovementMethod(ScrollingMovementMethod.getInstance());
        ratingBar.setStarMark(Float.valueOf(String.valueOf(commodityAppraiseInfo.getAppraiseStatus())));

        appraiseImageLoadAdapter = new AppraiseImageLoadAdapter(mActivity, imageURL);
        appraiseImageLoadAdapter.setPhotoViewClickListener(new AppraiseImageLoadAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                setVisibleState();
            }
        });

        mViewPager.setAdapter(appraiseImageLoadAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, true);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                LogUtil.i(imageURL.get(mCurrentPosition));
                header_title.setText((mCurrentPosition + 1) + "/" + imageURL.size());
            }
        });
        header_title.setText((mCurrentPosition + 1) + "/" + imageURL.size());
    }
    @ViewInject(R.id.top_rl)
    private RelativeLayout top_rl;
    @ViewInject(R.id.bottom_ll)
    private LinearLayout bottom_ll;

    private void initVisibleState(){
        StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.black));// 设置状态栏颜色
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) top_rl.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            top_rl.setLayoutParams(params);
        }
        setVisible();
    }

    @SuppressLint("InlinedApi")
	private void setVisible() {
        top_rl.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
        top_rl.setVisibility(View.VISIBLE);
        bottom_ll.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        bottom_ll.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 16) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @SuppressLint("InlinedApi")
	private void setInVisible() {
        top_rl.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
        top_rl.setVisibility(View.GONE);
        bottom_ll.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        bottom_ll.setVisibility(View.GONE);
        //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
        if (Build.VERSION.SDK_INT >= 16) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void setVisibleState() {
        if (top_rl.getVisibility() == View.VISIBLE) {
            setInVisible();
        } else {
            setVisible();
        }
    }


    @OnClick(value = {R.id.back})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

}
