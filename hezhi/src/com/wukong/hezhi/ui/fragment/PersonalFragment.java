package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.OrderStateInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.bean.WalletInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.AppraiseCenterActivity;
import com.wukong.hezhi.ui.activity.BindPhoneActivity;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.ui.activity.MyAttentionActivity;
import com.wukong.hezhi.ui.activity.MyCollectionActivity;
import com.wukong.hezhi.ui.activity.MyCustomizationActivity;
import com.wukong.hezhi.ui.activity.MyWalletActivity;
import com.wukong.hezhi.ui.activity.OrderCustomActivity;
import com.wukong.hezhi.ui.activity.PersonInfoActivity;
import com.wukong.hezhi.ui.activity.SettingActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuZhiyin
 * @ClassName: PreronalFragment
 * @Description: TODO(个人中心)
 * @date 2016-11-7 上午11:51:11
 */
public class PersonalFragment extends BaseFragment {
    public String pageName = ContextUtil.getString(R.string.me);
    public String pageCode = "me";

    @ViewInject(R.id.header_iv)
    private ImageView header_iv;

    @ViewInject(R.id.nickname_tv)
    private TextView nickname_tv;

    @ViewInject(R.id.phone_num_tv)
    private TextView phone_num_tv;

    @ViewInject(R.id.balance_tv)
    private TextView balance_tv;

    @ViewInject(R.id.waipay_num_tv)
    private TextView waipay_num_tv;

    @ViewInject(R.id.waitrecieve_num_tv)
    private TextView waitrecieve_num_tv;

    @ViewInject(R.id.waitcomment_num_tv)
    private TextView waitcomment_num_tv;

    private UserInfo userInfo;// 用户信息
    private OrderStateInfo orderStateInfo;//订单状态

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);// 关联布局文件
        ViewUtils.inject(this, rootView); // 注入view和事件
        return rootView;
    }

    /**
     * 刷新界面
     */
    public void reFreshView() {
        userInfo = UserInfoManager.getInstance().getUserInfo();
        if (userInfo != null) {
            phone_num_tv.setVisibility(View.VISIBLE);
            GlideImgUitl.glideLoader(getActivity(), userInfo.getShowImageURL(), R.drawable.icon_def_header,
                    R.drawable.icon_def_header, header_iv, 0);
            nickname_tv.setText(userInfo.getNickName());
            if (TextUtils.isEmpty(userInfo.getPhone())) {
                phone_num_tv.setText("未绑定手机");
                phone_num_tv.setClickable(true);
                phone_num_tv.setOnClickListener(this);
            } else {
                phone_num_tv.setClickable(false);
                String phoneNumber = userInfo.getPhone();
                String hidePhoneNumber = phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                phone_num_tv.setText(hidePhoneNumber);// $1、$2、……表示正则表达式里面第一个、第二个、……括号里面的匹配内容
            }
        } else {
            header_iv.setImageDrawable(ContextUtil.getResource().getDrawable(R.drawable.icon_def_header));
            phone_num_tv.setVisibility(View.GONE);
            nickname_tv.setText("登录");
        }

        getBalanceData();//执行完获取用户信息后再去获取余额
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (UserInfoManager.USERINFO_CHANGE) {// 每次返回时，检查是否需要刷新页面
            getUserInfo();// 获取用户信息
            UserInfoManager.USERINFO_CHANGE = false;
        } else {
            getBalanceData();// 每次都刷新余额
        }
        getOrderState();//刷新订单状态数
    }

    /**
     * 余额
     */
    public void getBalanceData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("memberId", UserInfoManager.getInstance().getUserId());
        String URL = HttpURL.URL1 + HttpURL.BALANCE;
        LogUtil.i(map.toString());
        HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // TODO Auto-generated method stub
                LogUtil.i(responseInfo.result);
                ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, WalletInfo.class);
                if (info != null) {
                    if (info.getHttpCode().equals(HttpCode.SUCESS)) {
                        WalletInfo wallet = (WalletInfo) info.getBody();
                        balance_tv.setText("¥"+wallet.getAvailableAmount() );
                    } else if (info.getHttpCode().equals(HttpCode.FAIL)) {
                        balance_tv.setText("");
                    }
                } else {
                    balance_tv.setText("");
                }
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }
        });
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", UserInfoManager.getInstance().getUserId());
        LogUtil.i(map.toString());
        HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.GET_USERINFO, map, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                LogUtil.i(arg0.result);
                ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
                if (info != null) {
                    if (info.getHttpCode().equals(HttpCode.SUCESS)) {
                        userInfo = (UserInfo) info.getBody();
                        // 保存用户信息
                        UserInfoManager.getInstance().setUserInfo(userInfo);
                        UserInfoManager.getInstance().userLoginStatus
                                (getActivity(), userInfo.getOnlineType());
                    } else if (info.getHttpCode().equals(HttpCode.FAIL)) {
                        UserInfoManager.getInstance().cleanUserInfo();// 登录失败，将用户数据置空
                    }
                }
                reFreshView();
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }
        });
    }

    /**
     * 获取订单状态
     */
    private void getOrderState() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("memberId", UserInfoManager.getInstance().getUserId());
        HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.QUERY_ORDEREACH_TOTAL, map, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                LogUtil.i(arg0.result);
                ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, OrderStateInfo.class);
                if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
                    orderStateInfo = (OrderStateInfo) info.getBody();
                }
                refreshOrderState();
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                refreshOrderState();
            }
        });
    }

    /**
     * 刷新订单状态
     */
    private void refreshOrderState() {
        if (orderStateInfo != null) {
            int waipay_num = orderStateInfo.getPayingConut();//待付款数
            int waitrecieve_num = orderStateInfo.getDeliveringCount();//待收货数
            int waitcomment_num = orderStateInfo.getAwaitCommentConut();//带评论数
            if (waipay_num > 100) {
                waipay_num_tv.setVisibility(View.VISIBLE);
                waipay_num_tv.setText(99 + "+");
            } else if (waipay_num == 0) {
                waipay_num_tv.setVisibility(View.INVISIBLE);
            } else {
                waipay_num_tv.setVisibility(View.VISIBLE);
                waipay_num_tv.setText(waipay_num + "");
            }
            if (waitrecieve_num > 100) {
                waitrecieve_num_tv.setVisibility(View.VISIBLE);
                waitrecieve_num_tv.setText(99 + "+");
            } else if (waitrecieve_num == 0) {
                waitrecieve_num_tv.setVisibility(View.INVISIBLE);
            } else {
                waitrecieve_num_tv.setVisibility(View.VISIBLE);
                waitrecieve_num_tv.setText(waitrecieve_num + "");
            }
            if (waitcomment_num > 100) {
                waitcomment_num_tv.setVisibility(View.VISIBLE);
                waitcomment_num_tv.setText(99 + "+");
            } else if (waitcomment_num == 0) {
                waitcomment_num_tv.setVisibility(View.INVISIBLE);
            } else {
                waitcomment_num_tv.setVisibility(View.VISIBLE);
                waitcomment_num_tv.setText(waitcomment_num + "");
            }
        } else {
            waipay_num_tv.setVisibility(View.INVISIBLE);
            waitrecieve_num_tv.setVisibility(View.INVISIBLE);
            waitcomment_num_tv.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(value = {R.id.header_iv, R.id.mywallet_ll, R.id.setting_iv, R.id.my_colletion_ll,
            R.id.talor_order_ll, R.id.my_talor_ll, R.id.my_attention_ll, R.id.waitpay_rl, R.id.waitrecieve_rl, R.id.waitcomment_rl})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_iv:// 用户信息
                toAct(PersonInfoActivity.class);
                break;
            case R.id.mywallet_ll:// 我的钱包
                toAct(MyWalletActivity.class);
                break;
            case R.id.talor_order_ll:// 定制订单
                toAct(OrderCustomActivity.class);
                break;
            case R.id.my_talor_ll:// 我的定制
                toAct(MyCustomizationActivity.class);
                break;
            case R.id.my_attention_ll:// 我的关注
                toAct(MyAttentionActivity.class);
                break;
            case R.id.my_colletion_ll:// 我的收藏
                toAct(MyCollectionActivity.class);
                break;
            case R.id.setting_iv:// 设置
                toActivity(SettingActivity.class);
                break;
            case R.id.phone_num_tv:// 绑定手机
                toAct(BindPhoneActivity.class);
                break;
            case R.id.waitpay_rl://待付款
                OrderCustomActivity(OrderCustomActivity.class, OrderStateInfo.WAIT_PAY);
                break;
            case R.id.waitrecieve_rl:// 待收货
                OrderCustomActivity(OrderCustomActivity.class, OrderStateInfo.WAIT_RECIEVE);
                break;
            case R.id.waitcomment_rl:// 待评论
                OrderCustomActivity(AppraiseCenterActivity.class, OrderStateInfo.WAIT_COMMENT);
                break;
        }
    }

    /**
     * 跳转至订单中心
     */
    public void OrderCustomActivity(Class clazz, int orderState) {
        if (!UserInfoManager.getInstance().isLogin() || userInfo == null) {
            toActivity(LoginActivity.class);
        } else {
            toActivity(clazz, Constant.Extra.ORDERSTATE, orderState);
        }
    }

    /**
     * 跳转至评价中心
     */
    public void AppraiseCenterActivity(Class clazz, int orderState) {
        if (!UserInfoManager.getInstance().isLogin() || userInfo == null) {
            toActivity(LoginActivity.class);
        } else {
            toActivity(clazz, Constant.Extra.ORDERSTATE, orderState);
        }
    }

    /**
     * 若没有登录，则跳到登录页面
     */
    public void toAct(Class clazz) {
        if (!UserInfoManager.getInstance().isLogin() || userInfo == null) {
            toActivity(LoginActivity.class);
        } else {
            toActivity(clazz);
        }
    }

    @Override
    public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
        // TODO Auto-generated method stub
        if (notifyTo.equals(getClass())) {
            if (notifyFrom.equals(MainActivity.class)) {
//				showGuideView();
            }
        }

    }


}
