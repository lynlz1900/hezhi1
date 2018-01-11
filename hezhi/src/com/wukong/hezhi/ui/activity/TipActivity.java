package com.wukong.hezhi.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXShareManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuZhiyin
 * @ClassName: TipActivity
 * @Description: TODO(关注微信公众号提示)
 * @date 2017-5-18 下午4:45:03
 */
public class TipActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.tip);
    public String pageCode = "tip";
    @ViewInject(R.id.QRCode_iv)
    private ImageView QRCode_iv;
    private float cashMoney;//提现金额

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_tip;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        headView.setTitleStr(ContextUtil.getString(R.string.tip));
        cashMoney = getIntent().getFloatExtra(Constant.Extra.MONEY, 0);
        QRCode_iv.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                saveAndSendPic();
                return false;
            }
        });
    }

    private String wxfr = "com.tencent.mm.ui.tools.ShareImgUI";// 分享给微信好友

    private void saveAndSendPic() {
        Bitmap bitmap = ((BitmapDrawable) QRCode_iv.getDrawable()).getBitmap();
        String filePath = FileUtil.saveBitmap2File(bitmap, HezhiConfig.PIC_PATH, HezhiConfig.HEZHI_QR_CODE);// 保存二维码
        WXShareManager.getInstance().sharePic(filePath, wxfr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        showProgressDialog();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cashMoney", cashMoney);
        map.put("memberId", UserInfoManager.getInstance().getUserId());
        String URL = HttpURL.URL1 + HttpURL.GET_CASH;
        HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // TODO Auto-generated method stub
                dismissProgressDialog();
                ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, RedBagInfo.class);
                if (info != null) {
                    if (info.getHttpCode().equals(HttpCode.SUCESS)) {
                        RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
                        toHandle(redBagInfo);
                    } else if (info.getHttpCode().equals(HttpCode.FAIL)) {
                        ScreenUtil.showToast(info.getPromptMessage());
                    }
                }

            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                dismissProgressDialog();
                ScreenUtil.showToast(arg1);
            }
        });
    }

    /**
     * 做相应的处理，提示成功， resultCode：0成功 1 没有绑定微信 resultCode：2 没有关注盒知公众号 200的时候 提现
     */
    public void toHandle(RedBagInfo redBagInfo) {
        switch (redBagInfo.getResultCode()) {
            case 0:// 成功
                showSucessDialog("你的提款已由“盒知”公众号发送至你的微信，请在微信中查看");
                break;
            case 1:// 没有绑定微信
                break;
            case 2:// 没有关注盒知公众号
                break;
        }
    }

    /**
     * 提款成功提示
     */
    protected void showSucessDialog(String str) {
        new CustomAlterDialog.Builder(mActivity).setTitle(ContextUtil.getString(R.string.get_cash_sucess)).setMsg(str)
                .setSureButton(ContextUtil.getString(R.string.i_know), new OnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v, CustomAlterDialog dialog) {
                        // TODO Auto-generated method stub
                        finish();
                        ActivitiesManager.getInstance().finishActivity(GetCrashActivity.class);//关闭提现页面
                    }
                }).build().show();
    }
}
