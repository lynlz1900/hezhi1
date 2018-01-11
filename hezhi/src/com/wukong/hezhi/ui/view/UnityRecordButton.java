package com.wukong.hezhi.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

public class UnityRecordButton extends View {

	private Paint mBigCirclePaint;
	private Paint mSmallCirclePaint;
	private Paint mProgressCirclePaint;
	private int mHeight;// 当前View的高
	private int mWidth;// 当前View的宽
	private float mBigRadius;
	private float mSmallRadius;
	private boolean isRecording;// 录制状态
	private float mCurrentProgress;// 当前进度

	private int mTime = 15;// 录制最大时间s 
	private float mProgressW = 8f;// 圆环宽度

	private ValueAnimator mProgressAni;// 圆弧进度变化

	public UnityRecordButton(Context context) {
		super(context);
		init(context, null);
	}

	public UnityRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public UnityRecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		// 初始画笔抗锯齿、颜色
		mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBigCirclePaint.setColor(Color.parseColor("#4CFFFFFF"));

		mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSmallCirclePaint.setColor(Color.parseColor("#FFFFFF"));

		mProgressCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mProgressCirclePaint.setColor(Color.parseColor("#ff8b26"));

		mProgressAni = ValueAnimator.ofFloat(0, 360f);
		mProgressAni.setDuration(mTime * 1000);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		mBigRadius = mWidth / 2 * 0.75f;
		mSmallRadius = mBigRadius * 0.75f;
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		// 绘制外圆
		canvas.drawCircle(mWidth / 2, mHeight / 2, mBigRadius, mBigCirclePaint);
		// 绘制内圆
		canvas.drawCircle(mWidth / 2, mHeight / 2, mSmallRadius, mSmallCirclePaint);
		// 录制的过程中绘制进度条
		if (isRecording) {
			drawProgress(canvas);
		}
	}

	/**
	 * 绘制圆形进度
	 *
	 * @param canvas
	 */
	private void drawProgress(Canvas canvas) {
		mProgressCirclePaint.setStrokeWidth(mProgressW);
		mProgressCirclePaint.setStyle(Paint.Style.STROKE);
		// 用于定义的圆弧的形状和大小的界限
		RectF oval = new RectF(mWidth / 2 - (mBigRadius - mProgressW / 2), mHeight / 2 - (mBigRadius - mProgressW / 2),
				mWidth / 2 + (mBigRadius - mProgressW / 2), mHeight / 2 + (mBigRadius - mProgressW / 2));
		// 根据进度画圆弧
		canvas.drawArc(oval, -90, mCurrentProgress, false, mProgressCirclePaint);

	}

	/**
	 * 圆形进度变化动画
	 */

	private long mStartTime;
	public void startProgressAnimation() {

		LogUtil.i("record  start");

		mStartTime = System.currentTimeMillis();

		isRecording = true;

		mProgressAni.start();
		mProgressAni.setInterpolator(new  LinearInterpolator());
		mProgressAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mCurrentProgress = (float) animation.getAnimatedValue();
				invalidate();
			}
		});

		mProgressAni.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {


				if(isRecording)
				{
					LogUtil.i("onAnimationEnd...");
					stopProgressAnimation();
				}

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
	}

	public void stopProgressAnimation()
	{

		LogUtil.i("stopProgressAnimation...");

		if(System.currentTimeMillis() - mStartTime < 4*1000)
		{
			ScreenUtil.showToast("视频录制不得少于4s");
			return;
		}

		isRecording = false;

		mProgressAni.cancel();

		mCurrentProgress = 0;
		invalidate();

		if(onClickListener!=null)
		{
			onClickListener.onRecordEnd();
		}
	}

	/**
	 * 点击监听器
	 */
	public interface OnRecordListener {
		void onRecordEnd();
	}

	public OnRecordListener onClickListener;

	public void setOnRecordListener(OnRecordListener onClickListener) {
		this.onClickListener = onClickListener;
	}


}
