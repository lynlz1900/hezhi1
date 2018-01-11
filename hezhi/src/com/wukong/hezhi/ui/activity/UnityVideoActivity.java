package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArShareInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.ShareWindows;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;

/***
 *
 * @ClassName: UnityVideoActivity
 * @Description: TODO(Unity分享视频预览界面)
 * @author Huzhiyin
 * @date 2017年10月16日 下午2:01:29
 *
 */
public class UnityVideoActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.unity_video);
    public String pageCode = "unity_video";

    @ViewInject(R.id.videoView_show)
    private VideoView videoViewShow;

    @ViewInject(R.id.imageView_show)
    private ImageView imageViewShow;

    @ViewInject(R.id.button_play)
    private Button buttonPlay;

    @ViewInject(R.id.loading_pb)
    private ProgressBar loading_pb;

    @ViewInject(R.id.share_bt)
    private Button share_bt;

    @ViewInject(R.id.close_iv)
    private ImageView close_iv;

    /**
     * 视频路径
     */
    private String videoPath;
    private String ppid;
    /**
     * 视频文件
     */
    private File file;
    /**
     * 生成分享的Url
     */
    private String shareUrl;
    /**
     * 视频封面图片地址
     */
    private String coverUrl;
    /**
     * 视频封面图片路径
     */
    private String picPath;
    /**
     * 视频封面，第一帧
     */
    private Bitmap bitmap;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_unity_video;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        initData();
        initView();
    }

    public void initData() {
        videoPath = getIntent().getExtras().getString(Constant.Extra.UNITY_VIDEO_PATH);//文件路径
        ppid = getIntent().getExtras().getString(Constant.Extra.UNITY_PPID);//ppid
    }

    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setSwipeBackEnable(false);// 禁止侧滑消除
        loading_pb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                file = new File(videoPath);
                // 获取第一帧图片，预览使用
                if (file.length() != 0) {
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    media.setDataSource(videoPath);
                    bitmap = media.getFrameAtTime();
                    picPath = FileUtil.saveBitmap2File(bitmap, HezhiConfig.PIC_PATH, HezhiConfig.UNITY_VIDEO_IMG_NAME);//保存封面图片
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_pb.setVisibility(View.INVISIBLE);
                        showButton();
                        imageViewShow.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
        videoViewShow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoViewShow.isPlaying()) {
                    videoViewShow.pause();
                    isPause = true;
                    showButton();
                }
                return false;
            }
        });
    }

    private boolean isPause = false;

    /**
     * 播放视频
     */
    private void playVideo() {
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
                    showButton();
                }
            }
        });

    }

    @OnClick(value = {R.id.close_iv, R.id.share_bt, R.id.button_play})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play://播放
                imageViewShow.setVisibility(View.INVISIBLE);
                showButton();
                playVideo();
                break;
            case R.id.close_iv://返回
                finish();
                break;
            case R.id.share_bt://上传数据
                if (!UserInfoManager.getInstance().isLogin()) {
                    toActivity(LoginActivity.class);
                } else {
                    postData();
                }
                break;
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (videoViewShow.isPlaying()) {
            videoViewShow.pause();
            isPause = true;
            showButton();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageViewShow.setVisibility(View.VISIBLE);
    }

    /**
     * 显示隐藏播放按钮和分享按钮
     */
    private void showButton() {
        if (buttonPlay.getVisibility() != View.VISIBLE || share_bt.getVisibility() != View.VISIBLE
                || close_iv.getVisibility() != View.VISIBLE) {
            buttonPlay.setVisibility(View.VISIBLE);
            share_bt.setVisibility(View.VISIBLE);
            close_iv.setVisibility(View.VISIBLE);
        } else {
            buttonPlay.setVisibility(View.INVISIBLE);
            share_bt.setVisibility(View.INVISIBLE);
            close_iv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 上传数据
     */
    private void postData() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("memberId", UserInfoManager.getInstance().getUserId() + "");
        params.addBodyParameter("ppId", ppid);
        params.addBodyParameter("img", new File(picPath));
        params.addBodyParameter("video", new File(videoPath), "video/*");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, HttpURL.URL1 + HttpURL.UPLOAD_RECORD_VIDEO, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgressDialog();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        // TODO Auto-generated method stub
                        super.onLoading(total, current, isUploading);
                        LogUtil.d("total:" + total + "current:" + current);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dismissProgressDialog();
                        ResponseJsonInfo<ArShareInfo> info = JsonUtil.fromJson(responseInfo.result,
                                ArShareInfo.class);
                        if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
                            shareUrl = info.getBody().getShareUrl();
                            coverUrl = info.getBody().getCoverUrl();
                            share();
                        } else {
                            ScreenUtil.showToast(ContextUtil.getString(R.string.post_fail));
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        dismissProgressDialog();
                        ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
                    }
                });

    }

    private void share() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(ContextUtil.getString(R.string.ar_shared));
        shareInfo.setUrl(shareUrl);
        shareInfo.setImagUrl(coverUrl);
        shareInfo.setDescription(ContextUtil.getString(R.string.ar_shared));
        final View view = getWindow().getDecorView().findViewById(
                android.R.id.content);// 获取一个view,popubwindow会用到
        ShareWindows.getInstance().show(view, shareInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoViewShow.stopPlayback();
//        delete();
    }

    /**
     * 删除图片和视频
     */
    private void delete() {
        FileUtil.DeleteFolder(picPath);
        FileUtil.DeleteFolder(videoPath);
    }
}
