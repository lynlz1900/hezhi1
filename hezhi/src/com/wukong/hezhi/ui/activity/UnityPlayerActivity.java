package com.wukong.hezhi.ui.activity;

import com.unity3d.player.UnityPlayer;
import com.wukong.hezhi.R;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.manager.UnityNativeManger;
import com.wukong.hezhi.ui.view.MyUnityPlayer;
import com.wukong.hezhi.ui.view.UnityRecordButton;
import com.wukong.hezhi.ui.view.UnityRecordButton.OnRecordListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.unity3d.player.UnityPlayer;
import com.wukong.hezhi.R;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.manager.UnityNativeManger;
import com.wukong.hezhi.ui.view.MyUnityPlayer;
import com.wukong.hezhi.ui.view.UnityRecordButton;
import com.wukong.hezhi.ui.view.UnityRecordButton.OnRecordListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;


/***
 *
 * @ClassName: UnityPlayerActivity
 * @Description: TODO(扫描识别图片，UNITY页面)
 * @author HuZhiyin
 * @date 2017-4-28 上午9:49:46
 *
 */
public class UnityPlayerActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.unity);
    public String pageCode = "unity";

    private LinearLayout mParent;
    private UnityPlayer myUnityPlayer;

    @Override
    protected boolean isNotAddTitle() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_android4unity;
    }

    @Override
    protected void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
        setSwipeBackEnable(false);// 禁止侧滑消除
        getWindow().setFormat(PixelFormat.RGBX_8888);
        initView();
        UnityManger.getInstance().InitIat(getApplicationContext());
        UnityNativeManger.getInstance().init(this);
        addRecordBtn();
        initIatBtn();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        myUnityPlayer = new MyUnityPlayer(this);
        // 获取显示Unity视图的父控件
        mParent = (LinearLayout) findViewById(R.id.UnityView);
        // 获取Unity视图
        View mView = myUnityPlayer.getView();
        // 将Unity视图添加到Android视图中
        mParent.addView(mView);
        myUnityPlayer.requestFocus();
        UnityNativeManger.getInstance().sendData2Unity(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        UnityNativeManger.getInstance().sendData2Unity(intent);
        LogUtil.d("onNewIntent");
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        myUnityPlayer.quit();
        super.onDestroy();
        LogUtil.d("onDestroy");
        UnityManger.getInstance().onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        myUnityPlayer.pause();
        LogUtil.d("onPause");
    }


    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        myUnityPlayer.resume();
        LogUtil.d("onResume");
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myUnityPlayer.configurationChanged(newConfig);
        LogUtil.d("onConfigurationChanged");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        myUnityPlayer.windowFocusChanged(hasFocus);
        LogUtil.d("onWindowFocusChanged");

        if (hasFocus) {
            myUnityPlayer.resume();
        } else {
            myUnityPlayer.pause();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return myUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return myUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return myUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return myUnityPlayer.injectEvent(event);
    }

    /* API12 */
    public boolean onGenericMotionEvent(MotionEvent event) {
        return myUnityPlayer.injectEvent(event);
    }

    private UnityRecordButton recordButton;

    private void addRecordBtn() {
        recordButton = (UnityRecordButton) findViewById(R.id.unityrecordbtn);
        recordButton.setVisibility(View.GONE);
        recordButton.setOnRecordListener(new OnRecordListener() {

            @Override
            public void onRecordEnd() {
                // TODO Auto-generated method stub
                LogUtil.i("record  end");
                recordButton.setVisibility(View.GONE);
                UnityNativeManger.getInstance().NotifyUnityRecordStop();
            }
        });
        recordButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                recordButton.stopProgressAnimation();

            }
        });
    }

    public void startRecord() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                recordButton.startProgressAnimation();
                recordButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onAppOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
//                iatBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onAppClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iatBtn.setVisibility(View.GONE);
            }
        });
    }

    private Button iatBtn;

    private void initIatBtn() {
        iatBtn = (Button) findViewById(R.id.iatbtn);
        iatBtn.setVisibility(View.GONE);
        iatBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UnityManger.getInstance().Iat_StartRecognizer();
            }
        });
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
    }
}
