package com.wukong.hezhi.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.BusinessAcitivty;
import com.wukong.hezhi.ui.activity.CommodityInfoActivity;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.MyWalletActivity;
import com.wukong.hezhi.ui.activity.ProductCommentActivity;
import com.wukong.hezhi.ui.activity.QQActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.activity.UnityVideoActivity;
import com.wukong.hezhi.ui.activity.WebARListActivity;
import com.wukong.hezhi.ui.activity.WebPrizeActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PhotoUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;
import com.wukong.hezhi.wxapi.WXShareManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/***
 *
 * @ClassName: UnityManger
 * @Description: TODO(unity向本地发送消息管理类)
 * @author HuZhiyin
 * @date 2017-3-22 上午8:11:48
 *
 */
public class UnityManger {
    private String wxfr = "com.tencent.mm.ui.tools.ShareImgUI";// 分享给微信好友
    private String wxtimeline = "com.tencent.mm.ui.tools.ShareToTimeLineUI";// 分享给朋友圈
    private static final String separator = "@&@";// 与unity约定的分割符号

    private UnityManger() {

    }

    private static class Holder {
        private static final UnityManger SINGLETON = new UnityManger();
    }

    /**
     * 单一实例
     */
    public static UnityManger getInstance() {
        return Holder.SINGLETON;
    }

    /**
     * 保存unity传来的数据
     */
    public void saveJsonFromUnity(String jsonStr) {
        ResponseJsonInfo info = JsonUtil.fromJson(jsonStr, UnityInfo.class);
        if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
            UnityInfo unityInfo = (UnityInfo) info.getBody();
            String picId = unityInfo.getPicId();
            saveDB(picId, jsonStr);
        }
    }

    /**
     * 将文件保存到hezhi目录下,并返回路径
     */
    public String saveFile2Hezhi(String filePath) {
        String oldPath = filePath.substring(7);
        String newPath = HezhiConfig.VIDEO_PATH + DateUtil.getTime() + ".mp4";
        FileUtil.isFolderExists(HezhiConfig.VIDEO_PATH);// 创建文件夹
        FileUtil.copyFile(oldPath, newPath);// 复制文件
        return newPath;
    }

    /**
     * 在系统相册里显示
     */
    public void showInAlbum(String videoPath) {
        // String path = videoPath.substring(7);
        PhotoUtil.insertVideoToMediaStore(ContextUtil.getContext(), videoPath, 0, 10, 10, 10);
    }

    /**
     * 分享图片, 0.微信好友；1.微信朋友圈；2.qq好友；3.qq空间
     */
    public void sharePic(int socialType, String filePath) {
        filePath = filePath.substring(8);
        switch (socialType) {
            case 0:// 微信好友
                WXShareManager.getInstance().sharePic(filePath, wxfr);
                break;
            case 1:// 朋友圈
                WXShareManager.getInstance().sharePic(filePath, wxtimeline);
                break;
            case 2:// QQ好友
                QQActivity.sharPic(filePath);
                break;
            case 3:// QQ空间
                QQActivity.shareToQQZone(filePath);
                break;
        }
    }

    /**
     * 分享视频
     */
    public void shareVideo(String filePath) {
        WXShareManager.getInstance().shareVideo(filePath, wxfr);// 分享到微信好友
    }

    /**
     * 分享视频, 0.微信好友；1.微信朋友圈；2.qq好友；3.qq空间
     */
    public void shareVideo(int socialType, String filePath) {
        filePath = filePath.substring(7);
        switch (socialType) {
            case 0:// 微信好友
                WXShareManager.getInstance().shareVideo(filePath, wxfr);
                break;
            case 1:// 朋友圈
                WXShareManager.getInstance().shareVideo(filePath, wxtimeline);
                break;
            case 2:// QQ好友
            case 3:// QQ空间
                QQActivity.sharVideo(filePath);
                break;
        }
    }

    /**
     * 获取地址信息
     */
    public AddressInfo getAddressInfo() {
        AddressInfo addressInfo = LocationManager.getInstance().getAdressInfo();
        return addressInfo;
    }

    /**
     * 获取详细地址信息
     */
    public String getDeatailAddr() {
        if (getAddressInfo() == null) {
            return "";
        } else {
            return getAddressInfo().getDetailAddr();
        }
    }

    /**
     * 获取GPS
     */
    public String getGPS() {
        if (getAddressInfo() == null) {
            return "";
        } else {
            double longitude = getAddressInfo().getLongitude();
            double latitude = getAddressInfo().getLatitude();
            String gps = "(" + longitude + "," + latitude + ")";
            return gps;
        }
    }

    /**
     * 保存数据库
     */
    private void saveDB(String picId, String jsonStr) {
        List<String> picIds = DBManager.getInstance().qurreyPicIds();
        if (!picIds.contains(picId)) {// 如果数据库不存在这个数据，则将这条记录加入数据库
            DBManager.getInstance().insert(picId, DBManager.UNITY, jsonStr);
        } else {
            DBManager.getInstance().deleteById(picId);
            DBManager.getInstance().insert(picId, DBManager.UNITY, jsonStr);
        }
    }

    /**
     * 将数据转换成和unity约定的数据格式
     */
    public String toUTFormat(String prefix, String content) {
        return prefix + separator + content;
    }

    public static String SAO_YI_SAO = "localrecord";// 扫一的本地记录
    public static String LIVE = "pinpaizhibo";// 品牌直播
    public static String AR_LIST = "ListHezhi";// AR推荐列表

    /**
     * 跳转至评论页面
     */
    private void toProductCommentActivity(Activity activity, String ppid) {
        Intent intent = new Intent(activity, ProductCommentActivity.class);
        intent.putExtra(Constant.Extra.PPID, ppid);
        activity.startActivity(intent);
    }

    /**
     * 跳转至商户界面
     */
    private void toBusinessAcitivty(Activity activity, String businessId) {
        int businessIdInt = StringUtil.str2Int(businessId);
        Intent intent = new Intent(activity, BusinessAcitivty.class);
        intent.putExtra(Constant.Extra.BUSINESS_ID, businessIdInt);
        activity.startActivity(intent);
    }

    /**
     * 跳转商品详情页面
     */
    private void toCommoditDetailActivity(Activity activity, String secondPara) {
        int productId = 0;
        try {
            productId = Integer.parseInt(secondPara);
        } catch (Exception e) {
            productId = 0;
        }
        Intent intent = new Intent(activity, CommodityInfoActivity.class);
        intent.putExtra(Constant.Extra.PRODUCTID, productId);
        activity.startActivity(intent);
    }

    /**
     * 跳转分享视频界面
     */
    private void shareAR(Activity activity, String ppid, String filePath) {
        String newPath = Environment.getExternalStorageDirectory() + filePath.substring(7);//转化一下unity的文件路径。
        Intent intent = new Intent(activity, UnityVideoActivity.class);
        intent.putExtra(Constant.Extra.UNITY_PPID, ppid);
        intent.putExtra(Constant.Extra.UNITY_VIDEO_PATH, newPath);
        activity.startActivity(intent);
    }

    /**
     * 是否分享弹框
     */
    private void showShareTip(final Activity activity, final String[] argsGroup) {
        new CustomAlterDialog.Builder(activity).setTitle(ContextUtil.getString(R.string.tip))
                .setMsg(ContextUtil.getString(R.string.share_tip))
                .setCancelButton(ContextUtil.getString(R.string.cancel), null)
                .setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {

                    @Override
                    public void onDialogClick(View v, CustomAlterDialog dialog) {
                        // TODO Auto-generated method stub
                        String ppid = argsGroup[1];
                        String filePath = argsGroup[2];
                        shareAR(activity, ppid, filePath);
                    }
                }).show();

    }

    /**
     * 跳转大转盘页面
     */
    private void toWebPrizeActivity(Activity activity, String url) {
        if (!UserInfoManager.getInstance().isLogin()) {//如果没有登录
            Intent loginIntent = new Intent(activity, LoginActivity.class);
            activity.startActivity(loginIntent);
        } else {
            Intent intent = new Intent(activity, WebPrizeActivity.class);
            intent.putExtra(Constant.Extra.WEB_URL, url);
            activity.startActivity(intent);
        }
    }

    /**
     * 若没有登录，则跳到登录页面
     */
    private void toLoginActivity(Activity activity) {
        if (!UserInfoManager.getInstance().isLogin()) {
            JumpActivityManager.getInstance().toActivity(activity, LoginActivity.class);
        }
    }

    /**
     * 跳转我的钱包
     */
    private void toMyWalletActivity(Activity activity) {
        if (!UserInfoManager.getInstance().isLogin()) {
            JumpActivityManager.getInstance().toActivity(activity, LoginActivity.class);
        } else {
            JumpActivityManager.getInstance().toActivity(activity, MyWalletActivity.class);
        }
    }

    /**
     * 跳转webview页面
     */
    private void toWebViewActivity(Activity activity, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Constant.Extra.WEB_URL, url);
        activity.startActivity(intent);
    }

    /**
     * 跳转AR推荐列表
     */
    private void toWebPrizeListActivity(Activity activity, String url) {
        Intent intent = new Intent(activity, WebARListActivity.class);
        intent.putExtra(Constant.Extra.WEB_URL, url);
        activity.startActivity(intent);
    }

    /**
     * unity调用natvie的方法 (约定的格式，A&B，A:表示执行动作的行为。B:表示传递的参数)
     */
    public void utCallNative(Activity activity, String args) {
        try {
            String[] argsGroup = args.split(separator);
            String firstPara = argsGroup[0];
            String secondPara = "";
            if (argsGroup.length == 2) {
                secondPara = argsGroup[1];
            }
            if ("OpenComment".equals(firstPara)) {// 跳转评论页面
                toProductCommentActivity(activity, secondPara);
            } else if ("OpenCompany".equals(firstPara)) {// 跳转商户页面
                toBusinessAcitivty(activity, secondPara);
            } else if ("OpenRedPackage".equals(firstPara)) {// 弹红包
                String ppid = argsGroup[1];
                String modelname = argsGroup[2];
                RedBagGanzManager.getInstance(activity).checkRedBag(ppid, modelname);
                ((UnityPlayerActivity) activity).onAppOpen();
            } else if ("AppClose".equals(firstPara)) {// 关闭红包
                RedBagGanzManager.getInstance(activity).dismissRedBagWindow();
                ((UnityPlayerActivity) activity).onAppClose();

            } else if ("CustomOrder".equals(firstPara)) {
                toCommoditDetailActivity(activity, secondPara);
            } else if ("ShareVideo".equals(firstPara)) {//分享视频
                showShareTip(activity, argsGroup);
            } else if ("OpenWeb".equals(firstPara)) {//大转盘抽奖
                String weburl = argsGroup[2];
                toWebPrizeActivity(activity, weburl);
            } else if ("StartRecord".equals(firstPara)) {//unity录屏
                ((UnityPlayerActivity) activity).startRecord();
            } else if ("ShowToastView".equals(firstPara)) {
                ScreenUtil.showToast(secondPara);
            } else if ("SendLoginIn".equals(firstPara)) {//登录
                toLoginActivity(activity);
            } else if ("GoToNativeMoney".equals(firstPara)) {//我的钱包
                toMyWalletActivity(activity);
            } else if ("ShareAppWebUrl".equals(firstPara)) {//通知原生分享URL
                String ppid = argsGroup[1];
                String url = argsGroup[2];
                toWebViewActivity(activity, url);
            } else if ("OpenARRecommend".equals(firstPara)) {//AR推荐列表
//                String ppid = argsGroup[1];
//                String url = argsGroup[2];
                toWebPrizeListActivity(activity, HttpURL.URL1 + HttpURL.PRIZE_LIST);
            }
        } catch (Exception e) {
//            ScreenUtil.showToastByDebug("解析unity发送的数据异常");
        }
    }


    ///讯飞语音------------------------

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public void InitIat(Context context) {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
    }

    public void onDestroy() {
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    public void Iat_StartRecognizer() {
        mIatResults.clear();

        // 设置参数
        setParam();

        int ret = mIat.startListening(mRecognizerListener);

        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip("请开始说话…");
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("mingyi", "SpeechRecognizer init() code = " + code);
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        private boolean isStartRecord = false;
        private String resultStr = "";

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			showTip("开始说话");

            resultStr = "";
            isStartRecord = true;
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//			showTip("结束说话");

            isStartRecord = false;

            UnityNativeManger.getInstance().NotifyUnityIatResult(resultStr);

        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {

            Log.d("mingyi", results.getResultString());
            StringBuffer ret = new StringBuffer();
            try {
                JSONTokener tokener = new JSONTokener(results.getResultString());
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    // 转写结果词，默认使用第一个结果
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String text = ret.toString();

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            showTip(resultBuffer.toString());
            resultStr = resultBuffer.toString();

            if (isLast) {
                // TODO 最后的结果

            }

        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//			showTip("当前正在说话，音量大小：" + volume);
//			Log.d("mingyi",  "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public void setParam() {

        if (mIat == null)
            showTip("iat 为空");

        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void showTip(final String str) {
        ScreenUtil.showToast(str);
    }
}
