package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleTabInfo;
import com.wukong.hezhi.bean.ArticleTabInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.receiver.JgPushReceiver;
import com.wukong.hezhi.ui.activity.DebugModelActivity;
import com.wukong.hezhi.ui.activity.ExpressQueryActivity;
import com.wukong.hezhi.ui.activity.FocusActivity;
import com.wukong.hezhi.ui.activity.GameCenterActivity;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.ui.activity.QRActivity;
import com.wukong.hezhi.ui.activity.QRLockActivity;
import com.wukong.hezhi.ui.view.GuideView;
import com.wukong.hezhi.ui.view.GuideView.OnClickCallback;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.List;

/**
 * @author HuZhiyin
 * @ClassName: FindFragment
 * @Description: TODO(发现)
 * @date 2017-3-8 上午11:33:58
 */
public class FindFragment extends BaseFragment {
    public String pageName = ContextUtil.getString(R.string.find);
    public String pageCode = "find";

    @ViewInject(R.id.red_point)
    private ImageView red_point;

    @ViewInject(R.id.focus_tv)
    private TextView focus_tv;

    @ViewInject(R.id.commodity_tv)
    private TextView commodity_tv;

    @ViewInject(R.id.foucus_arrow)
    private TextView foucus_arrow;

    @ViewInject(R.id.loading_pb)
    private ProgressBar loading_pb;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_find, container, false);// 关联布局文件
        ViewUtils.inject(this, rootView); // 注入view和事件
        initView();
        return rootView;
    }

    public void initView() {
    }

    /**
     * google api 数组数表示点击的次数
     */
    long[] mHits = new long[2];

    @OnClick(value = {R.id.header_title, R.id.focus_ll,R.id.qrble_ll, R.id.saoyisao_ll, R.id.yuquhudong_ll, R.id.express_qurey_ll})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.focus_ll:// 盒知看点
                getArticleTabData();
                break;
            case R.id.qrble_ll:// 开保真锁
            	toQRLockActivity();
                break;
            case R.id.saoyisao_ll:// 扫一扫
                toQRActivity();
                break;
            case R.id.yuquhudong_ll:// 益趣互动
                toActivity(GameCenterActivity.class);
                break;
            case R.id.express_qurey_ll:// 快递查询
                toActivity(ExpressQueryActivity.class);
                break;
            case R.id.header_title:// debug模式，点击两下计入
                if (HezhiConfig.DEBUG) {
                    // 每点击一次 实现左移一格数据
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    // 给数组的最后赋当前时钟值
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                    // 当0出的值大于当前时间-500时 证明在500秒内点击了3次
                    if (mHits[0] > SystemClock.uptimeMillis() - 500) {
                        toActivity(DebugModelActivity.class);
                    }
                }
                break;
        }
    }

    private void toQRActivity() {
        if (!PermissionUtil.cameraPermission()) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
            return;
        }
        toActivity(QRActivity.class);
    }

    private void toQRLockActivity() {
    	 if (!PermissionUtil.cameraPermission()) {
             ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
             return;
         }
    	 if(!UserInfoManager.getInstance().isLogin()){
    		 toActivity(LoginActivity.class);
    	 }else{
	    	 toActivity(QRLockActivity.class);
    	 }
    }
    
    /**
     * 获取盒知看点分类数据
     */
    public void getArticleTabData() {
        String URL = HttpURL.URL1 + HttpURL.ARTICLE_MENU;
        loading_pb.setVisibility(View.VISIBLE);
        foucus_arrow.setVisibility(View.GONE);
        HttpManager.getInstance().post(URL, null, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                loading_pb.setVisibility(View.GONE);
                foucus_arrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // TODO Auto-generated method stub
                ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, ArticleTabInfos.class);
                if (info != null) {
                    if (info.getHttpCode().equals(HttpCode.SUCESS)) {
                        red_point.setVisibility(View.GONE);// 小红点消失
                        ArticleTabInfos infos = (ArticleTabInfos) info.getBody();
                        List<ArticleTabInfo> articleTabInfos = infos.getDataList();
                        toActivity(FocusActivity.class, Constant.Extra.ARTICLE_TAB_INFO, articleTabInfos);
                    }
                } else {
                    ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
                }
                loading_pb.setVisibility(View.GONE);
                foucus_arrow.setVisibility(View.VISIBLE);
            }

        });
    }

    @Override
    public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
        // TODO Auto-generated method stub
        if (notifyTo.equals(getClass())) {
            if (notifyFrom.equals(MainActivity.class)) {
//				showGuideView();
            }
            if (notifyFrom.equals(JgPushReceiver.class)) {
                if ((int) msg == JgPushReceiver.ARTICLE) {// 极光推送的消息
                    red_point.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showGuideView() {
        final GuideView.Builder builder = new GuideView.Builder(getActivity(), PackageUtil.getVersionName());
        LinearLayout layout_focus = (LinearLayout) ScreenUtil.inflate(R.layout.layout_guide_focus);
        builder.setTextSize(20).addHintView(commodity_tv, layout_focus, GuideView.Direction.RIGHT_BOTTOM,
                GuideView.MyShape.CIRCULAR, 0, 0, new OnClickCallback() {

                    @Override
                    public void onGuideViewClicked() {
                        // TODO Auto-generated method stub
                        builder.showNext();
                    }
                });
        builder.show();
    }
}
