package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.AppraiseImageDelAdapter;
import com.wukong.hezhi.bean.lIstViewItem;
import com.wukong.hezhi.function.imagepicker.Utils;
import com.wukong.hezhi.function.imagepicker.view.ViewPagerFixed;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.StatusBarUtil;
import java.util.ArrayList;

/***
 * 
 * @ClassName: AppraiseImageDelActivity
 * @Description: TODO(评价晒单图片删除)
 * @author HuangFeiFei
 * @date 2017-12-18
 * 
 */
public class AppraiseImageDelActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.pic);
	public String pageCode ="pic";
	
	@ViewInject(R.id.header_title)
	protected TextView header_title;
	
	@ViewInject(R.id.back)
	protected TextView back;
	
	@ViewInject(R.id.delete)
	protected TextView delete;
	
	@ViewInject(R.id.viewpager)
	protected ViewPagerFixed mViewPager;

	private AppraiseImageDelAdapter appraiseImagePageAdapter;
	
	private ArrayList<lIstViewItem> imageURL;
	protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
	private boolean isComplete;
	
	private boolean isdelete = false;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_appraise_imagedel;
	}

	@Override
	protected void init() {
		StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.black));// 设置状态栏颜色
		initView();
	}
	
	//初始化控件
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void initView() {
		initVisibleState();
		imageURL = (ArrayList<lIstViewItem>) getIntent().getSerializableExtra("imageURL");
		mCurrentPosition = getIntent().getIntExtra("position", 0);
		isComplete = getIntent().getBooleanExtra("isComplete", false);
		if(imageURL == null){
			imageURL = new ArrayList<>();
		}else{
			if(!isComplete)
				imageURL.remove(imageURL.size()-1);
		}
		
		appraiseImagePageAdapter = new AppraiseImageDelAdapter(mActivity, imageURL);
		appraiseImagePageAdapter.setPhotoViewClickListener(new AppraiseImageDelAdapter.PhotoViewClickListener() {
			@Override
			public void OnPhotoTapListener(View view, float v, float v1) {
				setVisibleState();
			}
		});
		mViewPager.setAdapter(appraiseImagePageAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, true);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                header_title.setText((mCurrentPosition+1)+"/"+imageURL.size());
            }
        });
        
        header_title.setText((mCurrentPosition+1)+"/"+imageURL.size());
	}
	
	@OnClick(value = { R.id.delete,R.id.back})
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case  R.id.delete:
			appraiseImageDel();
			break;
		case  R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(isdelete)
			appraiseImageDelNotify();
	}
	
	/*** 删除图片 **/
	private void appraiseImageDel() {
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.image_del))
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				if(imageURL.size() >1){
					imageURL.remove(mCurrentPosition);
					appraiseImagePageAdapter.notifyDataSetChanged();
					header_title.setText((mCurrentPosition+1)+"/"+imageURL.size());
						
					isdelete = true;
				}else if(imageURL.size() == 1){
					isdelete = true;
					imageURL.remove(mCurrentPosition);
					appraiseImageDelNotify();
					finish();
				}
			}
		}).build().show();
	}

	/*** 通知图片被删除 **/
	private void appraiseImageDelNotify() {
		ObserveManager.getInstance().notifyState(AppraiseAddActivity.class, 
				AppraiseImageDelActivity.class, imageURL);
		ObserveManager.getInstance().notifyState(AppraiseShowActivity.class, 
				AppraiseImageDelActivity.class, imageURL);
	}
	
	@ViewInject(R.id.top_rl)
	private RelativeLayout top_rl;
	@SuppressLint("InlinedApi")
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
		if (Build.VERSION.SDK_INT >= 16) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	@SuppressLint("InlinedApi")
	private void setInVisible() {
		top_rl.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
		top_rl.setVisibility(View.GONE);
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
}
