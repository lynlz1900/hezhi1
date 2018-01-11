package com.wukong.hezhi.utils;

import java.util.concurrent.ExecutionException;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.view.GlideCircleTransform;
import com.wukong.hezhi.ui.view.GlideRoundTransform;

/**
 * 
 * @ClassName: GlideImgUitl
 * @Description: TODO(圆角图片，圆形图片处理器)
 * @author HuZhiyin
 * @date 2017-6-26 下午3:04:29
 * 
 */
public class GlideImgUitl {
	public static Context mContext = ContextUtil.getContext();

	public static int degree = 10;
	
	public static void glideLoader(Context context, String url, int erroImg, int emptyImg, ImageView iv, int tag) {
		if (0 == tag) {// 圆形图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg)
					.transform(new GlideCircleTransform(context)).into(iv);
		} else if (1 == tag) {// 圆角图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg)
					.transform(new GlideRoundTransform(context, degree)).into(iv);
		} else if (2 == tag) {// 原图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).into(iv);
		}
		degree = 10;
	}

	public static void glideLoader(Context context, String url, ImageView iv, int tag) {
		glideLoader(context, url, R.drawable.bg_loading, R.drawable.bg_loading, iv, tag);
	}

	/** 不带的动画，防止某些情况（比如图片在popupwindow里面）下第一次加载出现变形 */
	public static void glideLoaderNoAnimation(Context context, String url, int erroImg, int emptyImg, ImageView iv,
			int tag) {
		if (0 == tag) {// 圆形图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg)
					.transform(new GlideCircleTransform(context)).dontAnimate().into(iv);
		} else if (1 == tag) {// 圆角图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg)
					.transform(new GlideRoundTransform(context, degree)).dontAnimate().into(iv);
		} else if (2 == tag) {// 原图
			Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).dontAnimate().into(iv);
		}
		degree = 10;
	}

	/** 不带的动画， */
	public static void glideLoaderNoAnimation(Context context, String url, ImageView iv, int tag) {
		glideLoaderNoAnimation(context, url, R.drawable.bg_loading, R.drawable.bg_loading, iv, tag);
	}

	public static void glideLoaderPic(Context context, String url, ImageView iv){
		Glide.with(context).load(url).asBitmap().placeholder(R.drawable.bg_loading)
		.error(R.drawable.bg_loading).into(iv);
	}
	
	public static Bitmap getBitMap(String url) {
		Bitmap thumb = null;
		try {
			thumb = Glide.with(mContext).load(url).asBitmap() // 必须
					.centerCrop().into(500, 500).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return thumb;
	}
}