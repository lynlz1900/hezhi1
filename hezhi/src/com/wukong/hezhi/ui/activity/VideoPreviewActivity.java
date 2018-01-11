package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;

import java.io.File;

/***
 *
 * @ClassName: VideoPreviewActivity
 * @Description: TODO(视频预览界面)
 * @author Huzhiyin
 * @date 2017年10月16日 下午2:01:29
 *
 */
public class VideoPreviewActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.preview_video);
    public String pageCode = "preview_video";

    @ViewInject(R.id.header)
    private RelativeLayout header;

    @ViewInject(R.id.videoView_show)
    private VideoView videoViewShow;

    @ViewInject(R.id.imageView_show)
    private ImageView imageViewShow;

    @ViewInject(R.id.button_play)
    private Button buttonPlay;

    @ViewInject(R.id.textView_count_down)
    private TextView textViewCountDown;

    private String videoPath;// 视频路径
    private File file;// 视频文件
    private boolean isPause = false;//是否停止播放

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_video_preview;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        initView();
        initData();
    }

    public void initView() {
        videoViewShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoViewShow.isPlaying()) {
                    videoViewShow.pause();
                    isPause = true;
                    visible();
                }
                return false;
            }
        });
    }

    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getExtras().getString(Constant.Extra.VIDEO_PATH);
            file = new File(videoPath);
        }
        // 获取第一帧图片，预览使用
        if (file.length() != 0) {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            Bitmap bitmap = media.getFrameAtTime();
            imageViewShow.setImageBitmap(bitmap);
        }
    }

    /**
     * 显示头布局和播放按钮
     */
    private void visible() {
        buttonPlay.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏头布局和播放按钮
     */
    private void invisible() {
        buttonPlay.setVisibility(View.INVISIBLE);
        header.setVisibility(View.INVISIBLE);
        imageViewShow.setVisibility(View.INVISIBLE);
    }

    /**
     * 播放视频
     */
    private void playVideo() {
        invisible();
        if (isPause && videoViewShow != null && !videoViewShow.isPlaying()) {//如果是暂停状态，则直接播放。
            videoViewShow.start();
            return;
        }
        isPause = false;
        videoViewShow.setVideoPath(videoPath);
        videoViewShow.start();
        videoViewShow.requestFocus();
        videoViewShow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!videoViewShow.isPlaying()) {
                    visible();
                }
            }
        });
    }

    @OnClick(value = {R.id.header_left, R.id.button_play, R.id.header_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                playVideo();
                break;
            case R.id.header_right:
                showDeleteDialog();
                break;
            case R.id.header_left:
                finish();
                break;
        }
    }

    /**
     * 是否删除
     */
    protected void showDeleteDialog() {
        new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.delete_video))
                .setCancelButton(ContextUtil.getString(R.string.cancel), null)
                .setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v, CustomAlterDialog dialog) {
                        // TODO Auto-generated method stub
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                        ObserveManager.getInstance().notifyState(UploadActivity.class, VideoPreviewActivity.class,
                                null);// 通知观察者数据发生了变化
                        finish();
                    }
                }).build().show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoViewShow.stopPlayback();
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (videoViewShow != null && videoViewShow.isShown()) {
            imageViewShow.setVisibility(View.VISIBLE);
            videoViewShow.pause();
            visible();
        }
    }
}
