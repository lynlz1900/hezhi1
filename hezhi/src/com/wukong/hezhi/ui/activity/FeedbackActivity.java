package com.wukong.hezhi.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuZhiyin
 * @ClassName: AboutActivity
 * @Description: TODO(意见反馈)
 * @date 2017-1-10 下午3:01:22
 */
public class FeedbackActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.feedback);
    public String pageCode = "feedback";

    @ViewInject(R.id.input_et)
    private EditText input_et;

    @ViewInject(R.id.submit_bt)
    private Button submit_bt;

    @OnClick(value = {R.id.input_et, R.id.submit_bt})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {

            case R.id.input_et:
                break;
            case R.id.submit_bt:
                postFeedBack();
                break;
        }
    }

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_feedback;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        headView.setTitleStr(ContextUtil.getString(R.string.feedback));
        input_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(s)) {
                    submit_bt.setEnabled(false);
                } else {
                    submit_bt.setEnabled(true);
                }

            }
        });
    }

    /**
     * 加载数据
     */
    private void postFeedBack() {
        String content = input_et.getText().toString().trim();
        if (StringUtil.isContainsEmoji(content)) {
            ScreenUtil.showToast("不能含有表情");
        }

        if (TextUtils.isEmpty(content)) {
            ScreenUtil.showToast("请输入反馈意见");
            return;
        }
        String url = HttpURL.URL1 + HttpURL.FEED_BACK;
        Map<String, Object> map = new HashMap<String, Object>();
        String userId;
        String user;
        if (UserInfoManager.getInstance().getUserInfo() == null) {
            userId = "";
            user = "";
        } else {
            userId = UserInfoManager.getInstance().getUserId() + "";
            user = UserInfoManager.getInstance().getUserInfo().getNickName();
        }
        map.put("user", user);
        map.put("content", content);
        map.put("userId", userId);
        HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                ResponseJsonInfo<ArticleInfos> info = JsonUtil.fromJson(
                        arg0.result, ArticleInfos.class);
                if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
                    toActivity(FeedbackSucessActivity.class);
                    finish();
                } else {
                    ScreenUtil.showToast("提交失败");
                }
            }
        });
    }

}
