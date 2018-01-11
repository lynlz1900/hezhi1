package com.wukong.hezhi.function.imagecompose;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageListener implements OnTouchListener {


    public ImageListener(ImageCallback imageCallback) {

        if (imageCallback == null) {
            imageCallback = new ImageCallback() {
                @Override
                public void move(boolean isMove) {

                }
            };
        }
        this.imageCallback = imageCallback;
    }

    public ImageListener() {
        this(null);
    }

    /**
     * 记录是拖拉照片模式还是放大缩小照片模式
     */
    private int mode = 0;// 初始状态
    /**
     * 拖拉照片模式
     */
    private static final int MODE_DRAG = 1;
    /**
     * 放大缩小照片模式
     */
    private static final int MODE_ZOOM = 2;

    /**
     * 用于记录开始时候的坐标位置
     */
    private PointF startPoint = new PointF();
    /**
     * 用于记录拖拉图片移动的坐标位置
     */
    private Matrix matrix = new Matrix();
    /**
     * 用于记录图片要进行拖拉时候的坐标位置
     */
    private Matrix currentMatrix = new Matrix();

    /**
     * 两个手指的开始距离
     */
    private float startDis;
    /**
     * 两个手指的中间点
     */
    private PointF midPoint;

    ImageView imageView = null;
    private float startx;// down事件发生时，手指相对于view左上角x轴的距离
    private float starty;// down事件发生时，手指相对于view左上角y轴的距离
    private float endx; // move事件发生时，手指相对于view左上角x轴的距离
    private float endy; // move事件发生时，手指相对于view左上角y轴的距离
    private int left; // DragTV左边缘相对于父控件的距离
    private int top; // DragTV上边缘相对于父控件的距离
    private int right; // DragTV右边缘相对于父控件的距离
    private int bottom; // DragTV底边缘相对于父控件的距离
    private int hor; // 触摸情况下，手指在x轴方向移动的距离
    private int ver; // 触摸情况下，手指在y轴方向移动的距离

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        imageView = (ImageView) v;
        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN:
                imageCallback.move(true);
                mode = MODE_DRAG;
                // 记录ImageView当前的移动位置
                currentMatrix.set(imageView.getImageMatrix());
                startPoint.set(event.getX(), event.getY());

                startx = event.getX();
                starty = event.getY();
                break;
            // 手指在屏幕上移动，改事件会被不断触发
            case MotionEvent.ACTION_MOVE:
                imageCallback.move(true);
                // 拖拉图片
                if (mode == MODE_DRAG) {
                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                    // 在没有移动之前的位置上进行移动
                    matrix.set(currentMatrix);
                    matrix.postTranslate(dx, dy);
                }
                // 移动图片
//			if (mode == MODE_DRAG) {
//				// 手指停留在屏幕或移动时，手指相对与View左上角水平和竖直方向的距离:endX endY
//				endx = event.getX();
//				endy = event.getY();
//				// 获取此时刻 View的位置。
//				left = imageView.getLeft();
//				top = imageView.getTop();
//				right = imageView.getRight();
//				bottom = imageView.getBottom();
//				// 手指移动的水平距离
//				hor = (int) (endx - startx);
//				// 手指移动的竖直距离
//				ver = (int) (endy - starty);
//				// 当手指在水平或竖直方向上发生移动时，重新设置View的位置（layout方法）
//				if (hor != 0 || ver != 0) {
//					imageView.layout(left + hor, top + ver, right + hor, bottom + ver);
//				}
//			}

                // 放大缩小图片
                else if (mode == MODE_ZOOM) {
                    float endDis = distance(event);// 结束距离
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        float scale = endDis / startDis;// 得到缩放倍数
                        matrix.set(currentMatrix);
                        matrix.postScale(scale, scale, imageView.getWidth() / 2, imageView.getHeight() / 2);//图片从中间放大
//					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
                imageCallback.move(false);
                // 当触点离开屏幕，但是屏幕上还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_ZOOM;
                /** 计算两个手指间的距离 */
                startDis = distance(event);
                /** 计算两个手指间的中间点 */
                if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                    midPoint = mid(event);
                    // 记录当前ImageView的缩放倍数
                    currentMatrix.set(imageView.getImageMatrix());
                }
                break;
        }
        imageView.setImageMatrix(matrix);
        return true;
    }

    /**
     * 计算两个手指间的距离
     */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return FloatMath.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两个手指间的中间点
     */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    private ImageCallback imageCallback;

    public interface ImageCallback {
        void move(boolean isMove);
    }
}
