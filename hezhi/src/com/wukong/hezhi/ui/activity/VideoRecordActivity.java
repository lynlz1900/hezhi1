package com.wukong.hezhi.ui.activity;

import android.hardware.SensorManager;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.manager.TakePhotoManager;
import com.wukong.hezhi.ui.view.CircleButtonView;
import com.wukong.hezhi.ui.view.CircleButtonView.OnClickListener;
import com.wukong.hezhi.ui.view.MovieRecorderView;
import com.wukong.hezhi.ui.view.MovieRecorderView.PhotoCallback;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.OrientationUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ViewShowAniUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Huzhiyin
 * @ClassName: VideoRecordActivity
 * @Description: TODO(拍摄界面)
 * @date 2017年10月17日 下午4:43:48
 */
public class VideoRecordActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.record_video);
    public String pageCode = "record_video";

    @ViewInject(R.id.movieRecorderView)
    private MovieRecorderView movieRecorderView;

    @ViewInject(R.id.cancel_bt)
    private Button cancel_bt;// 取消

    @ViewInject(R.id.sure_bt)
    private Button sure_bt;// 确定

    @ViewInject(R.id.cbv)
    private CircleButtonView cbv;// 拍照按钮

    @ViewInject(R.id.close_iv)
    private ImageView close_iv;// 关闭摄像头

    /**
     * 手机旋转角度监听
     */
    private OrientationEventListener mOrientationListener;
    /**
     * 手机旋转的角度（屏幕朝脸，顺时针旋转）
     */
    private int angle;
    /**
     * 是否结束录制
     */
    private boolean isFinish = true;
    /**
     * 视频：true,照片：false
     */
    private boolean isVideo = true;
    /**
     * 拍照图片的路径
     */
    private String picPath;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_video_record;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        initView();
    }

    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setSwipeBackEnable(false);// 禁止侧滑消除
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);//activity进入动画
        /** 长按监听 */
        cbv.setOnLongClickListener(longClickListener);
        /** 单击监听 */
        cbv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick() {
                // TODO Auto-generated method stub
                isVideo = false;// 照片
                movieRecorderView.photo(angle, photoCallback);
            }
        });

        setOrietationListener();
    }

    private void setOrietationListener() {
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                angle = OrientationUtil.getAngle(orientation);
                LogUtil.d("Orientation changed to " + orientation);
                LogUtil.d("向右旋转" + angle);
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    /**
     * 长按监听
     */
    private CircleButtonView.OnLongClickListener longClickListener = new CircleButtonView.OnLongClickListener() {
        @Override
        public void onLongClick() {
            isVideo = true;// 视频
            isFinish = false;// 开始录制
            movieRecorderView.record(angle);// 开始录制
        }

        @Override
        public void onNoMinRecord(int minTime) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.record_min_tips) + minTime + "秒");
            resetData();
        }

        @Override
        public void onRecordFinishedListener() {
            // handler.sendEmptyMessage(RECORD_FINISH);
            isFinish = true;
            recordComplete();
        }

        @Override
        public void onCancelListener() {
            // TODO Auto-generated method stub
            if (!isFinish) {// 如果视频还未录制完，则重置。
                ScreenUtil.showToast(ContextUtil.getString(R.string.record_cancel));
                resetData();
            }
        }
    };

    /**
     * 拍照回调
     */
    private PhotoCallback photoCallback = new PhotoCallback() {

        @Override
        public void onSucess(String path) {
            // TODO Auto-generated method stub
            showViewState(true);
            picPath = path;
        }

        @Override
        public void onFail(String msg) {
            // TODO Auto-generated method stub
            ScreenUtil.showToast(msg);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        resetData();
    }

    /**
     * 按钮状态
     */
    private void showViewState(boolean complete) {
        if (complete) {
            ViewShowAniUtil.playAni(sure_bt, true);
            ViewShowAniUtil.playAni(cancel_bt, true);
            ViewShowAniUtil.playAni(cbv, false);
            ViewShowAniUtil.playAni(close_iv, false);

        } else {
            ViewShowAniUtil.playAni(sure_bt, false);
            ViewShowAniUtil.playAni(cancel_bt, false);
            ViewShowAniUtil.playAni(cbv, true);
            ViewShowAniUtil.playAni(close_iv, true);
        }
    }

    /**
     * 重置状态
     */
    private void resetData() {
        showViewState(false);
        if (!TextUtils.isEmpty(picPath)) {// 删除图片
            File file = new File(picPath);
            if (file != null && file.exists()) {
                file.delete();
                picPath = "";
            }
        }

        if (movieRecorderView.getRecordFile() != null) {// 删除视频
            movieRecorderView.getRecordFile().delete();
        }

        movieRecorderView.stop();
        isFinish = true;
        try {
            movieRecorderView.initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 录制完成
     */
    private void recordComplete() {
        if (isFinish) {
            movieRecorderView.stop();
            showViewState(true);
        }
    }

    @OnClick(value = {R.id.sure_bt, R.id.cancel_bt, R.id.close_iv, R.id.header_right})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.sure_bt:
                save();
                break;
            case R.id.cancel_bt:
                resetData();
                break;
            case R.id.close_iv:
                finish();
                break;
            case R.id.header_right:
                movieRecorderView.changeCamera();
                break;
        }
    }

    private void save() {
        if (isVideo) {
            String videoPath = movieRecorderView.getPath();
            TakePhotoManager.getInstance().setSelected(TakePhotoManager.VIDEO, videoPath);
            finish();
        } else {
            TakePhotoManager.getInstance().setSelected(TakePhotoManager.PHOTO, picPath);
            finish();
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
        mOrientationListener.disable();
    }
}
