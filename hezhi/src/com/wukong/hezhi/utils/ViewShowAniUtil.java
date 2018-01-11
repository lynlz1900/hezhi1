package com.wukong.hezhi.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.wukong.hezhi.ui.view.Rotate3dAnimation;

public class ViewShowAniUtil {
	public interface  IAnimCallbakc{
		void animOver();
	}
	public static void playAni(final View view, final boolean show,final IAnimCallbakc iAnimCallbakc){
		if (view == null) {
			return;
		}
		if (view.getHeight() == 0) {
			return;
		}
		Animation animation;
		if (show) {
			animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else {
			animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		}
		animation.setDuration(500);// 设置动画持续时间
		animation.setFillAfter(false);// 动画执行完后是否停留在执行完的状态
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if (show) {
					view.setVisibility(View.VISIBLE);
				} else {
					view.setVisibility(View.INVISIBLE);
				}
				iAnimCallbakc.animOver();
			}
		});
		view.startAnimation(animation);
	}


	/** 执行显示或隐藏动画 */
	public static void playAni(final View view, final boolean show) {
		playAni(view,show,new IAnimCallbakc(){
			@Override
			public void animOver() {
			}
		});
	}

	// 启动组合动画
	public static void beginAnimSet(View view) {
		// 创建动画集合
		AnimationSet aniSet = new AnimationSet(false);
		// 透明度动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(1000);
		aniSet.addAnimation(alpha);

//        // 旋转动画
//        RotateAnimation rotate = new RotateAnimation(0, 360,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(1000);
//        aniSet.addAnimation(rotate);

		// 缩放动画
		ScaleAnimation scale = new ScaleAnimation(3f, 1.0f, 3f, 1.0f);
		scale.setDuration(1000);
		aniSet.addAnimation(scale);

		// 位移动画
		TranslateAnimation translate = new TranslateAnimation(50, 0, 100, 0);
		translate.setDuration(1000);
		aniSet.addAnimation(translate);

		//3d动画
		Rotate3dAnimation threeD = new Rotate3dAnimation(Rotate3dAnimation.ROLL_BY_Y,40f,0f);
		threeD.setDuration(1000);
		aniSet.addAnimation(threeD);

		// 把动画设置给view
		view.startAnimation(aniSet);
	}
}
