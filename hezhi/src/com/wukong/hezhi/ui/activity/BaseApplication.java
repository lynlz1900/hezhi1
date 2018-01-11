package com.wukong.hezhi.ui.activity;

import android.app.Application;
import android.os.Handler;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.suntech.sdk.SDKManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.CashCaseManager;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.LogFileManager;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.SoundUtil;
import com.wukong.hezhi.wxapi.WXConstant;

import cn.jpush.android.api.JPushInterface;

public class BaseApplication extends Application {
    private static BaseApplication application;
    private static int mainTid;
    private static Handler handler;
    public static IWXAPI wxApi;
    public static Tencent mTencent;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        application = this;
        mainTid = android.os.Process.myTid();
        handler = new Handler();
        init();
    }

    private void init() {
        initLiangziyun();// 量子云码识别
        initWeChat();// 微信
        initQQ();// QQ
        initUMENG();// 友盟统计
        initJgPush();// 极光推送
        initBDLocation();// 百度定位
        initSound();// 初始化声音
        initFile();// 创建文件夹
        initCashCase();// 捕获异常
        initLogCat();// 日志文件分析
        initSpeech();//讯飞语音
    }

    /**
     * 讯飞语音
     */
    private void initSpeech() {
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=5a30776c");
    }

    /**
     * 日志文件分析
     */
    private void initLogCat() {
        LogFileManager.getInstance().start();
    }


    /**
     * 捕获异常
     */
    private void initCashCase() {
        if (!HezhiConfig.DEBUG) {
            CashCaseManager.getInstance().setCashCaseIsGet(true);
            CashCaseManager.getInstance().initCashCase();
        }
    }

    /**
     * 文件夹
     */
    private void initFile() {
        FileUtil.isFolderExists(HezhiConfig.PIC_PATH);
        FileUtil.isFolderExists(HezhiConfig.VIDEO_PATH);
    }

    /**
     * 二维码扫描声音
     */
    private void initSound() {
        SoundUtil.initSound();
    }

    /**
     * 量子云识别
     */
    private void initLiangziyun() {
        SDKManager.initialize(this);// 注册量子云码
    }

    /**
     * 微信
     */
    private void initWeChat() {
        wxApi = WXAPIFactory.createWXAPI(this, WXConstant.APP_ID, true);// 注册微信
        wxApi.registerApp(WXConstant.APP_ID);
    }

    /**
     * QQ
     */
    private void initQQ() {
        mTencent = Tencent.createInstance(QQActivity.APP_ID, this);// 注册qq
    }

    /**
     * 友盟统计
     */
    private void initUMENG() {
        MobclickAgent.setDebugMode(HezhiConfig.DEBUG);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setSessionContinueMillis(5000);
        MobclickAgent.enableEncrypt(!HezhiConfig.DEBUG);// 加密日志
        MobclickAgent.startWithConfigure(new UMAnalyticsConfig(this, "58c0b3c8310c935129001fda", "www.pkgiot.com",
                EScenarioType.E_UM_NORMAL, true));
        MobclickAgent.setScenarioType(this, EScenarioType.E_UM_NORMAL);
    }

    /**
     * 极光推送
     */
    private void initJgPush() {
        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);// 极光推送
    }

    /**
     * 百度定位
     */
    private void initBDLocation() {
        LocationManager.getInstance().startLocation();// 开启定位
    }

    public static BaseApplication getApplication() {
        return application;
    }

    public static int getMainTid() {
        return mainTid;
    }

    public static Handler getHandler() {
        return handler;
    }

}
