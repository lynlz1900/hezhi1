package com.wukong.hezhi.function.imagepicker.ui;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.view.swipeview.SwipeBackActivity;
import com.wukong.hezhi.ui.view.swipeview.SwipeBackLayout;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.StatusBarUtil;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageBaseActivity extends SwipeBackActivity {

//    protected SystemBarTintManager tintManager;
    /** 在基类中实现滑动结束activity */
    private SwipeBackLayout mSwipeBackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintResource(R.color.status_bar);  //设置上方状态栏的颜色
        StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
        setSwipeBackLayout();// 侧滑关闭当前页面
    }

    /** 侧滑关闭当前页面 */
    private void setSwipeBackLayout() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);// 设置滑动方向
        mSwipeBackLayout.setEdgeSize(100);// 设置滑动范围
        setSwipeBackEnable(true); // 默认开启滑动删除，如需禁止滑动删除，在子类中将setSwipeBackEnable(false);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public void showToast(String toastText) {
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}
