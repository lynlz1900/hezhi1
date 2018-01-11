package com.wukong.hezhi.utils;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wukong.hezhi.R;

public class BitmapCombineUtil {

    /**
     * 合成两张图片
     */
    public static Bitmap combineImg2Bmp(ImageView bottom_iv, ImageView templet_iv) {
        int l = 0, t = 0, h = 0, w = 0;
        if (bottom_iv != null) {
            bottom_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
        }
        if (templet_iv != null) {
            templet_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
        }

        if (bottom_iv != null && templet_iv != null) {
            l = (int) (templet_iv.getX() - bottom_iv.getX());
            t = (int) (templet_iv.getY() - bottom_iv.getY());
        }

        Bitmap bottomBM = getViewBitmap(bottom_iv);// 底图
        Bitmap templetBM = getViewBitmap(templet_iv);// 模板

        if (bottomBM != null) {
            h = bottomBM.getHeight();
            w = bottomBM.getWidth();
        }

        int h_distance = (Math.max(h, w) - h) / 2;
        int w_distance = (Math.max(h, w) - w) / 2;

        Bitmap bmp = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);// 正方形图片
        Canvas canvas = new Canvas(bmp);
        if (bottomBM != null) {
            canvas.drawBitmap(bottomBM, w_distance, h_distance, null);
        }
        if (templetBM != null) {
            canvas.drawBitmap(templetBM, l + w_distance, t + h_distance, null);
        }
        return bmp;
    }

    /**
     * 合成三张图片
     */
    public static Bitmap combineBmp(ImageView headerImg, ImageView modelImg, TextView dragText) {
        int h = 1, w = 1;
        if (headerImg != null) {
            headerImg.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
            h = ((ViewGroup) headerImg.getParent()).getHeight();
            w = ((ViewGroup) headerImg.getParent()).getWidth();
        }
        if (modelImg != null) {
            modelImg.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
            h = ((ViewGroup) modelImg.getParent()).getHeight();
            w = ((ViewGroup) modelImg.getParent()).getWidth();
        }
        if (dragText != null) {
            dragText.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
            h = ((ViewGroup) dragText.getParent().getParent()).getHeight();
            w = ((ViewGroup) dragText.getParent().getParent()).getWidth();
        }

        int l = 0, t = 0;
        Bitmap headerBM = getViewBitmap(headerImg);// 头像
        Bitmap modelBM = getViewBitmap(modelImg);// 模板
        Bitmap textBM = getViewBitmap(dragText);// 文字
        if (textBM != null) {// 由于文字外面还有一层布局
            ViewGroup dragTextParent = (ViewGroup) dragText.getParent();
            l = (int) (dragTextParent.getLeft() + dragText.getX());
            t = (int) (dragTextParent.getTop() + dragText.getY());
        }

        Bitmap bmp;
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        if (headerBM != null) {
            canvas.drawBitmap(headerBM, headerImg.getLeft(), headerImg.getTop(), null);
        }
        if (modelBM != null) {
            canvas.drawBitmap(modelBM, modelImg.getLeft(), modelImg.getTop(), null);
        }
        if (textBM != null) {
            canvas.drawBitmap(textBM, l, t, null);
        }
        return bmp;
    }

    /**
     * 合成两张图片
     */
    public static Bitmap combineImg2Bmp2(ImageView bottom_iv, ImageView templet_iv) {
        int l = 0, t = 0, h = 0, w = 0;
//		if (bottom_iv != null) {
//			bottom_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
//		}
//		if (templet_iv != null) {
//			templet_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
//		}

        if (bottom_iv != null && templet_iv != null) {
            l = (int) (templet_iv.getX() - bottom_iv.getX());
            t = (int) (templet_iv.getY() - bottom_iv.getY());
        }

        Bitmap bottomBM = getViewBitmap(bottom_iv);// 底图
        Bitmap templetBM = getViewBitmap(templet_iv);// 模板

        if (bottomBM != null) {
            h = bottomBM.getHeight();
            w = bottomBM.getWidth();
        }

        int h_distance = (Math.max(h, w) - h) / 2;
        int w_distance = (Math.max(h, w) - w) / 2;

        Bitmap bmp = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);// 正方形图片
        Canvas canvas = new Canvas(bmp);
        if (bottomBM != null) {
            canvas.drawBitmap(bottomBM, w_distance, h_distance, null);
        }
        if (templetBM != null) {
            canvas.drawBitmap(templetBM, l + w_distance, t + h_distance, null);
        }
        return bmp;
    }

    /**
     * 合成三张图片
     */
    public static Bitmap combineBmp2(View headerImg, View modelImg, View dragText) {
        int h = 1, w = 1;
        if (modelImg != null) {
            h = modelImg.getHeight();
            w = modelImg.getWidth();
        }

        Bitmap headerBM = getViewBitmap(headerImg);// 头像
        Bitmap modelBM = getViewBitmap(modelImg);// 模板
        Bitmap textBM = getViewBitmap(dragText);// 文字

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        if (headerBM != null) {
            canvas.drawBitmap(headerBM, headerImg.getLeft(), headerImg.getTop(), null);
        }
        if (modelBM != null) {
            canvas.drawBitmap(modelBM, 0, 0, null);
        }
        if (textBM != null) {
            canvas.drawBitmap(textBM, dragText.getLeft(), dragText.getTop(), null);
        }
        return bmp;
    }


    /**
     * 将view转化为bimtap
     */
    private static Bitmap getViewBitmap(View v) {
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

    /**
     * 翻转图片
     *
     * @param isLeftOrRight 垂直还是水平 angle 旋转角度
     */
    public static Bitmap roatePicture(boolean isLeftOrRight, Bitmap bitmap, int angle) {
        Camera camera = new Camera();
        camera.save();
        Matrix matrix = new Matrix();
        // rotate
        if (isLeftOrRight) {
            camera.rotateY(angle);
        } else {
            camera.rotateX(angle);
        }
        camera.getMatrix(matrix);
        // 恢复到之前的初始状态。
        camera.restore();
        // 设置图像处理的中心点
        matrix.preTranslate(bitmap.getWidth() >> 1, bitmap.getHeight() >> 1);
        // matrix.preSkew(10, 10);
        matrix.postTranslate(bitmap.getWidth(), bitmap.getHeight());
        // matrix.postSkew(skewX, skewY);
        // 直接setSkew()，则前面处理的rotate()、translate()等等都将无效。
        // matrix.setSkew(skewX, skewY);
        // 2.通过矩阵生成新图像(或直接作用于Canvas)
        Bitmap newBit = null;
        try {
            // 经过矩阵转换后的图像宽高有可能不大于0，此时会抛出IllegalArgumentException
            newBit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IllegalArgumentException iae) {
            return null;
        }

        return newBit;
    }

    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
