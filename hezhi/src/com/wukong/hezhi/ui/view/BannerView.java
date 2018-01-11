package com.wukong.hezhi.ui.view;

import java.util.ArrayList;
import java.util.List;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.LogUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 广告图片自动轮播控件</br>
 * 
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于一般常见的广告图片轮播，具有自动轮播和手动轮播功能 
 *   使用：只需在xml文件中使用{@code <com.minking.imagecycleview.ImageCycleView/>} ，
 *   然后在页面中调用  {@link #setImageResources(ArrayList, ImageCycleViewListener) }即可!
 *   
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播需要进行控制，有利于内存管理
 * </pre>
 * 
 */
public class BannerView extends LinearLayout {

	public int TIME = 3000;
	
	/**
	 * 上下文
	 */
	private Context mContext;

	/**
	 * 无banner显示
	 */
	private ImageView text_banner_no;
	
	/**
	 * 一个banner显示
	 */
	private ImageView image_banner_one;
	
	/**
	 * 图片轮播视图
	 */
	private BannerViewPager mBannerPager = null;

	/**
	 * 滚动图片视图适配器
	 */
	private ImageCycleAdapter mAdvAdapter;

	/**
	 * 图片轮播指示器控件
	 */
	private ViewGroup mGroup;

	/**
	 * 图片轮播指示器-个图
	 */
	private ImageView mImageView = null;

	/**
	 * 滚动图片指示器-视图列表
	 */
	private ImageView[] mImageViews = null;

	/**
	 * 图片滚动当前图片下标
	 */
	private int mImageIndex = 1;

	/**
	 * 手机密度
	 */
	private float mScale;

	/**
	 * @param context
	 */
	public BannerView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	@SuppressLint("ClickableViewAccessibility")
	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mScale = context.getResources().getDisplayMetrics().density;
		LayoutInflater.from(context).inflate(R.layout.layout_banner_content, this);
		mBannerPager = (BannerViewPager) findViewById(R.id.pager_banner);
		mBannerPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						// 开始图片滚动
						if(mImageViews != null && mImageViews.length >1)
							startImageTimerTask();
						break;
					default:
						// 停止图片滚动
						stopImageTimerTask();
						break;
				}
				return false;
			}
		});
		// 滚动图片右下指示器视图
		mGroup = (ViewGroup) findViewById(R.id.viewGroup);
		text_banner_no = (ImageView) findViewById(R.id.text_banner_no);
		image_banner_one = (ImageView) findViewById(R.id.image_banner_one);
	}

	/**
	 * 装填图片数据
	 * 
	 * @param imageUrlList
	 * @param imageCycleViewListener
	 */
	@SuppressWarnings("unused")
	public void setImageResources(final List<AdContentInfo> infoList, final ImageCycleViewListener imageCycleViewListener) {
		// 清除所有子视图
		mGroup.removeAllViews();
		// 图片广告数量
		final int imageCount = infoList.size();
		mImageViews = new ImageView[imageCount];
		
		if(imageCount >0){
			for (int i = 0; i < imageCount; i++) {
				mImageView = new ImageView(mContext);
				int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，适应不同分辨率
				int imagePadding = (int) (mScale * 5 + 0.5f);
				LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				layout.setMargins(6, 0, 6, 0);
				mImageView.setLayoutParams(layout);
				mImageViews[i] = mImageView;
				if (i == 0) {
					mImageViews[i].setBackgroundResource(R.drawable.icon_dot_select);
				} else {
					mImageViews[i].setBackgroundResource(R.drawable.icon_dot_unselect);
				}
				mGroup.addView(mImageViews[i]);
			}
			
			text_banner_no.setVisibility(View.GONE);
			if(imageCount >1){
				mBannerPager.setVisibility(View.VISIBLE);
				mGroup.setVisibility(View.VISIBLE);
				image_banner_one.setVisibility(View.GONE);
				mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());
				
				mAdvAdapter = new ImageCycleAdapter(mContext, infoList, imageCycleViewListener);
				mBannerPager.setAdapter(mAdvAdapter);
				
				startImageTimerTask();
			}else if(imageCount == 1){
				mBannerPager.setVisibility(View.GONE);
				mGroup.setVisibility(View.GONE);
				image_banner_one.setVisibility(View.VISIBLE);
				mBannerPager.setOnPageChangeListener(null);
				
				stopImageTimerTask();
					
				GlideImgUitl.glideLoaderNoAnimation(getContext(), infoList.get(0).getHttpUrl(), image_banner_one, 2);
				image_banner_one.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						LogUtil.i(infoList.get(0).getHttpUrl());
						if(infoList.get(0) != null && !TextUtils.isEmpty(infoList.get(0).getJumpUrl())){
							JumpActivityManager.getInstance().toActivity
							(ActivitiesManager.getInstance().currentActivity(), 
							 WebViewActivity.class, Constant.Extra.WEB_URL,infoList.get(0).getJumpUrl());
						}
					}
				});
			}
		}else{
			mBannerPager.setOnPageChangeListener(null);
			stopImageTimerTask();
			image_banner_one.setVisibility(View.GONE);
			mBannerPager.setVisibility(View.GONE);
			mGroup.setVisibility(View.GONE);
			text_banner_no.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 开始轮播(手动控制自动轮播与否，便于资源控制)
	 */
	public void startImageCycle() {
		startImageTimerTask();
	}

	/**
	 * 暂停轮播——用于节省资源
	 */
	public void pushImageCycle() {
		stopImageTimerTask();
	}

	/**
	 * 开始图片滚动任务
	 */
	private void startImageTimerTask() {
		stopImageTimerTask();
		// 图片每3秒滚动一次
		mHandler.postDelayed(mImageTimerTask, 3000);
	}

	/**
	 * 停止图片滚动任务
	 */
	private void stopImageTimerTask() {
		mHandler.removeCallbacks(mImageTimerTask);
	}

	private Handler mHandler = new Handler();

	/**
	 * 图片自动轮播Task
	 */
	private Runnable mImageTimerTask = new Runnable() {

		@Override
		public void run() {
			if (mImageViews != null) {
				// 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
				if ((++mImageIndex) == mImageViews.length + 1) {
					mImageIndex = 1;
				}
				mBannerPager.setCurrentItem(mImageIndex);
			}
		}
	};

	/**
	 * 轮播图片状态监听器
	 * 
	 * @author minking
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE)
				if(mImageViews != null && mImageViews.length >1)
					startImageTimerTask(); // 开始下次计时
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			
			if (index == 0 || index == mImageViews.length + 1 || mImageViews.length <= 1) {
				return;
			}
			// 设置图片滚动指示器背景
			mImageIndex = index;
			index -= 1;
			mImageViews[index].setBackgroundResource(R.drawable.icon_dot_select);
			for (int i = 0; i < mImageViews.length; i++) {
				if (index != i) {
					mImageViews[i].setBackgroundResource(R.drawable.icon_dot_unselect);
				}
			}

		}

	}

	private class ImageCycleAdapter extends PagerAdapter {

		/**
		 * 图片视图缓存列表
		 */
		private ArrayList<ImageView> mImageViewCacheList;

		/**
		 * 图片资源列表
		 */
		private List<AdContentInfo> mAdList = new ArrayList<AdContentInfo>();

		/**
		 * 广告图片点击监听器
		 */
		private ImageCycleViewListener mImageCycleViewListener;

		private Context mContext;

		public ImageCycleAdapter(Context context, List<AdContentInfo> adList, ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			mAdList = adList;
			mImageCycleViewListener = imageCycleViewListener;
			mImageViewCacheList = new ArrayList<ImageView>();
		}

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			String imageUrl = mAdList.get(position).getHttpUrl();
			ImageView imageView = null;
			if (mImageViewCacheList.isEmpty()) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				
			} else {
				imageView = mImageViewCacheList.remove(0);
			}
			// 设置图片点击监听
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mImageCycleViewListener.onImageClick(mAdList.get(position),position, v);
				}
			});
//			imageView.setTag(imageUrl);
			container.addView(imageView);
			mImageCycleViewListener.displayImage(imageUrl, imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView view = (ImageView) object;
			container.removeView(view);
			mImageViewCacheList.add(view);
		}

	}

	/**
	 * 轮播控件的监听事件
	 * 
	 * @author minking
	 */
	public static interface ImageCycleViewListener {

		/**
		 * 加载图片资源
		 * 
		 * @param imageURL
		 * @param imageView
		 */
		public void displayImage(String imageURL, ImageView imageView);

		/**
		 * 单击图片事件
		 * 
		 * @param position
		 * @param imageView
		 */
		public void onImageClick(AdContentInfo info, int postion, View imageView);
	}
}
