package com.wukong.hezhi.manager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.unity3d.player.UnityPlayer;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;

/***
 *
 * @ClassName: UnityNativeManger
 * @Description: TODO(本地向unity发送消息管理类)
 * @author HuZhiyin
 * @date 2017-12-04 上午11:30:48
 *
 */
public class UnityNativeManger {
    private Activity mActivity;

    private UnityNativeManger() {
    }

    private static class Holder {
        private static final UnityNativeManger SINGLETON = new UnityNativeManger();
    }

    public static UnityNativeManger getInstance() {
        return Holder.SINGLETON;
    }

    public void init(Activity activity) {
        mActivity = activity;
    }

    private static final String UNITY_OBJECT = "MobileDispatcher";
    private static final String UNITY_METHOD = "NativeNotifyActionToUnity";
    // 视频录制结束
    private static final String RecordStop = "RecordStop";
    //讯飞语音的结果
    private static final String IatResult = "IatResult";

    public void NotifyUnityRecordStop() {
        sendMsg(GetMsg(RecordStop));
    }

    ///向unity发送讯飞语音的结果
    public void NotifyUnityIatResult(String result) {
        sendMsg(GetMsg(IatResult, result));
    }

    private String GetMsg(String action, String... args) {
        StringBuffer msg = new StringBuffer(action);
        msg.append("@&@");

        for (String string : args) {
            msg.append(string + "@&@");
        }
        return msg.toString();
    }

    private void sendMsg(String msg) {
        UnityPlayer.UnitySendMessage(UNITY_OBJECT, UNITY_METHOD, msg);
    }

    /**
     * 获取deviceId
     */
    public String _getDeviceID() {
        return PackageUtil.getPhoneId();
    }

    /**
     * 获取userid
     */
    public String _getUserId() {
        return UserInfoManager.getInstance().getUserId() + "";
    }

    /**
     * 获取地址
     */
    public String _getAddress() {
        return UnityManger.getInstance().getDeatailAddr();
    }

    /**
     * 获取GPS经纬度
     */
    public String _getGPS() {
        return UnityManger.getInstance().getGPS();
    }

    /**
     * 将数据发送给unity
     */
    public void sendData2Unity(Intent intent) {
        String jsonStr = intent.getStringExtra(Constant.Extra.UNITY_INFO);
        if (!TextUtils.isEmpty(jsonStr)) {
            _sendContentJson(jsonStr);
        }
    }

    /**
     * 将数据传送给unity
     */
    public void _sendContentJson(String json) {
        UnityPlayer.UnitySendMessage("MobileDispatcher", "NativeNotifyActionToUnity", json);
    }

    /**
     * 打开一个网页
     */
    public void _openWeb(String url) {
        Intent intent = new Intent(mActivity, WebViewActivity.class);
        intent.putExtra(Constant.Extra.WEB_URL, url);
        mActivity.startActivity(intent);
    }

    /**
     * 分享截图0.微信好友；1.微信朋友圈；2.qq好友；3.qq空间
     */
    public void _shareToSocial(int socialType, String filePath) {
        LogUtil.d("socialType=" + socialType + ";filePath=" + filePath);
        UnityManger.getInstance().sharePic(socialType, filePath);
    }

    /**
     * 保存扫描的结果
     */
    public void _saveScanResult(String jsonStr) {
        // jsonStr = ReadStreamUtil.getJson("TXT/unityjson.txt");
        LogUtil.d(jsonStr);
        UnityManger.getInstance().saveJsonFromUnity(jsonStr);// 保存扫描记录
    }

    /**
     * 分享视频 0.微信好友；1.微信朋友圈；2.qq好友；3.qq空间
     */
    public void _shareVideoToSocial(int socialType, String filePath) {
        LogUtil.d("socialType=" + socialType + ";filePath=" + filePath);
        UnityManger.getInstance().shareVideo(socialType, filePath);
    }

    /**
     * 和unity通信的统一接口（unity端发送过来的数据，按照）
     */
    public void _notifyActionToNative(String args) {
        LogUtil.d(args);
        UnityManger.getInstance().utCallNative(mActivity, args);
    }

    /**
     * 退出unity界面，处理逻辑和onKeyDown一致
     */
    public void _goBackToNative() {
        RedBagGanzManager.getInstance(mActivity).dismissRedBagWindow();// 返回需要销毁领取红包弹框，否则会导致popupwindow一直存在。
        mActivity.finish();
    }
}
