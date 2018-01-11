package com.wukong.hezhi.function.imagecompose;

import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: ComposeLayout
 * @Description: TODO(合成图片)
 * @author HuZhiyin
 * @date 2017-8-8 下午2:10:08
 * 
 */
public class ComposeLayout extends RelativeLayout {

	private ImageView zoomImage;// 头像部分
	private ImageView layor;// 模板
	private TextView dragText;// 文字

	public ComposeLayout(Context context) {
		super(context);
	}

	public ComposeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public void setLayout(Context context, ImageView header, ImageView model, TextView text) {
		zoomImage = header;
		layor = model;
		dragText = text;

		this.removeAllViews();
		if (zoomImage != null) {
			LayoutParams zoomImageLP = new LayoutParams(ScreenUtil.dp2px(Constant.customImage.WITH),
					ScreenUtil.dp2px(Constant.customImage.WITH));
			zoomImageLP.addRule(CENTER_IN_PARENT);
			zoomImage.setOnTouchListener(new ImageListener());
			zoomImage.setScaleType(ScaleType.MATRIX);
			zoomImage.layout(zoomImage.getLeft(), zoomImage.getTop(), zoomImage.getRight(), zoomImage.getBottom());
			this.addView(zoomImage, zoomImageLP);
		}
		if (layor != null) {
			LayoutParams layorLP = new LayoutParams(ScreenUtil.dp2px(Constant.customImage.WITH),
					ScreenUtil.dp2px(Constant.customImage.HEIGHT));
			layorLP.addRule(CENTER_IN_PARENT);

			layor.setScaleType(ScaleType.CENTER_CROP);
			this.addView(layor, layorLP);
		}
		if (dragText != null) {
			LayoutParams dragTextLP = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			dragText.setPadding(40, 40, 40, 40);
			dragText.setOnTouchListener(new TextListenter());
			this.addView(dragText, dragTextLP);
		}

	}

	/** 合成图片 */
	public Bitmap combineBmp() {
		int h1 = 1, h2 = 1, h3 = 1, w1 = 1, w2 = 1, w3 = 1;//默认值为1
		Bitmap b1 = getViewBitmap(zoomImage);
		if (b1 != null) {
			h1 = b1.getHeight();
			w1 = b1.getWidth();
		}

		Bitmap b2 = getViewBitmap(layor);
		if (b2 != null) {
			h2 = b2.getHeight();
			w2 = b2.getWidth();
		}

		Bitmap b3 = getViewBitmap(dragText);
		if (b3 != null) {
			h3 = b3.getHeight();
			w3 = b3.getWidth();
		}

		int h = Math.max(Math.max(h1, h2), h3);
		int w = Math.max(Math.max(w1, w2), w3);

		Bitmap bmp;
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		if (b1 != null) {
			if (b2 != null) {// 如果模板存在
				canvas.drawBitmap(b1, zoomImage.getLeft(), zoomImage.getTop(), null);
			} else {// 如果模板不存在，则将其置为零，防止黑边。
				canvas.drawBitmap(b1, 0, 0, null);
			}
		}
		if (b2 != null) {
			canvas.drawBitmap(b2, 0, 0, null);
		}
		if (b3 != null) {
			if (b2 != null) {
				canvas.drawBitmap(b3, dragText.getLeft(), dragText.getTop(), null);
			} else {
				canvas.drawBitmap(b3, 0, 0, null);
			}
		}
		return bmp;
	}

	/** 将view转化为bimtap */
	private Bitmap getViewBitmap(View v) {
		if (v == null) {
			return null;
		}
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

}
