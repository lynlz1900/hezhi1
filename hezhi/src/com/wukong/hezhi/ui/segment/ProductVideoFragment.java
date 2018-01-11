package com.wukong.hezhi.ui.segment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.bean.AdContentInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.bean.UnityResourceInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.RedBagVideoManager;
import com.wukong.hezhi.ui.activity.ProductMainActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.CustomVideoView;
import com.wukong.hezhi.ui.view.CustomVideoView.PlayPauseListener;
import com.wukong.hezhi.ui.view.MediaController;
import com.wukong.hezhi.ui.view.MediaController.onClickIsFullScreenListener;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: ProductVideoFragment
 * @Description: TODO(品牌直播显示视频部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ProductVideoFragment extends BaseFragment {

	@ViewInject(R.id.video_view)
	private CustomVideoView video_view;

	@ViewInject(R.id.loading_rl)
	private RelativeLayout loading_rl;

	@ViewInject(R.id.ad_rl)
	private RelativeLayout ad_rl;

	@ViewInject(R.id.ad_iv)
	private ImageView ad_iv;

	private View rootView;
	private String video_url;
	private UnityInfo unityInfo = new UnityInfo();
	private MediaController mediaController;
	private boolean hasPopRedbag=false;//已经弹过红包

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.fragment_product_video, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		unityInfo = (UnityInfo) getArguments().getSerializable("unityInfo");
		init();
		return rootView;
	}

	public void init() {
		ArrayList<UnityResourceInfo> list = (ArrayList<UnityResourceInfo>) unityInfo
				.getResourceList();
		if (list != null && list.size() > 0) {
			video_url = list.get(0).getOssUrl();// 获取视频地址
			initVideo();
		}
		watchVideo();// 检测视频
		disPlayAd();// 广告页展示
	}

	private String adUrl;// 视频广告显示的url
	private String jumpUrl;// 点击广告跳转的连接

	private void disPlayAd() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("placement",
				Constant.AdLocation.VIDEOPAGE_PERCEPTION.toString());
		map.put("ppid", unityInfo.getPpId());
		String URL = HttpURL.URL1 + HttpURL.AD;

		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result,
						AdContentInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					AdContentInfos adContentInfos = (AdContentInfos) info
							.getBody();
					List<AdContentInfo> list = adContentInfos.getData();
					adUrl = list.get(0).getHttpUrl();
					jumpUrl = list.get(0).getJumpUrl();
					GlideImgUitl.glideLoader(getActivity(), adUrl, ad_iv, 2);// 显示广告
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private Handler handler;
	private Runnable runnable;

	/** 监测videoView的播放时间,一秒监测一次，有些机型调用mp.setOnBufferingUpdateListener无法回调。故此。 */
	private void watchVideo() {
		handler = new Handler();
		runnable = new MyRunanble();
		handler.postDelayed(runnable, 1000);
	}

	public class MyRunanble implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int currentPosition = video_view.getCurrentPosition();// 获得当前播放时间和当前视频的长度
			if (12 == currentPosition / 1000 ) {// 当播放12秒时候，弹出红包
				popRedbag();
			}
			handler.postDelayed(this, 1000);
		}
	}

	@OnClick(value = { R.id.close_iv, R.id.ad_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.close_iv:
			ad_rl.setVisibility(View.GONE);
			break;
		case R.id.ad_iv:// 跳转广告页
			if (!TextUtils.isEmpty(jumpUrl)) {
				toActivity(WebViewActivity.class, Constant.Extra.WEB_URL,
						jumpUrl);
			}
			break;
		}
	}

	private void initVideo() {
		Uri uri = Uri.parse(video_url);
		// Bitmap bitmap = MediaUtil.createVideoThumbnail(video_url,
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);// 获取第一帧图片
		mediaController = new MediaController(getActivity(), rootView);
		// 设置全屏转换监听
		mediaController
				.setClickIsFullScreenListener(new ControllerOnFullScreenListner());
		video_view.setOnPreparedListener(new VideoOnPreparedListener());
		try {
			video_view.setOnInfoListener(new VideoOnInfoListener());//小米1s找不到此方法。故trycatch起来
		} catch (Exception e) {
			e.printStackTrace();
		}
		video_view.setOnErrorListener(new VideoOnErrorListener());
		video_view.setOnCompletionListener(new VideoOnCompletionListener());
		video_view.setPlayPauseListener(new VideoPlayPauseListener());
		video_view.setMediaController(mediaController);
		video_view.setVideoURI(uri);
		video_view.start();
		video_view.requestFocus();
	}

	private class ControllerOnFullScreenListner implements
			onClickIsFullScreenListener {

		@Override
		public void setOnClickIsFullScreen() {
			// TODO Auto-generated method stub
			change();// 切换横竖屏
		}

	}

	private class VideoOnPreparedListener implements OnPreparedListener {
		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			loading_rl.setVisibility(View.GONE);
		}

	}

	private class VideoOnInfoListener implements OnInfoListener {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub

			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				loading_rl.setVisibility(View.VISIBLE);
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				// 此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
				// if (mp.isPlaying()) {
				// loading_rl.setVisibility(View.GONE);
				// }
				loading_rl.setVisibility(View.GONE);
				break;

			case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
				break;
			}

			return true;
		}

	}

	private class VideoOnErrorListener implements OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub

			switch (what) {
			case MediaPlayer.MEDIA_ERROR_IO:

				break;
			}
			return false;
		}

	}

	/** 控制显示，隐藏广告 */
	private void showAD(boolean show) {
		if (!TextUtils.isEmpty(adUrl)) {
			if (show) {
				ad_rl.setVisibility(View.VISIBLE);
			} else {
				ad_rl.setVisibility(View.GONE);
			}
		} else {
			ad_rl.setVisibility(View.GONE);
		}
	}

	private class VideoOnCompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			popRedbag();//结束时候弹红包。
			showAD(true);
		}

	}
	/**弹红包*/
	private void popRedbag(){
		if(!hasPopRedbag){
			hasPopRedbag = true;
			RedBagVideoManager.getInstance(getActivity()).checkRedBag(
					ProductMainActivity.jsonStr);// 弹出。
		}
	}

	private class VideoPlayPauseListener implements PlayPauseListener {

		@Override
		public void onPlay() {
			// TODO Auto-generated method stub
			// ad_rl.setVisibility(View.GONE);
			showAD(false);
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			showAD(true);
			// ad_rl.setVisibility(View.VISIBLE);
		}

	}

	/** 全屏转换 */
	public void change() {
		Activity activity = getActivity();
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 设置RelativeLayout的全屏模式
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					RelativeLayout.LayoutParams.FILL_PARENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			video_view.setLayoutParams(layoutParams);

		} else {// 设置RelativeLayout的窗口模式

			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT,
					ScreenUtil.dp2px(200));
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			video_view.setLayoutParams(lp);

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RedBagVideoManager.getInstance(getActivity()).dismissRedBagWindow();// 销毁红包弹框，防止空指针
		handler.removeCallbacks(runnable);// 退出时候，置空handler,防止内存泄露
		handler = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (video_view != null && video_view.isShown()) {
			video_view.pause();
		}
		if(mediaController != null){
			mediaController.hide();// 一定要在此处调用这个方法，否则返回上一个界面，会空指针异常。
		}
	
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (video_view != null && !video_view.isPlaying()) {
			video_view.start();
		}
	}

}
