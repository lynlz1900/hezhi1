package com.wukong.hezhi.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.viewpagerindicator.TabPageIndicator;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

/***
 *
 * @ClassName: OrderCustActivity
 * @Description: TODO(我的定制订单)
 * @author HuangFeiFei
 * @date 2017-8-4 下午16：12
 *
 */
public class OrderCustomActivity extends BaseActivity {

    public String pageName = ContextUtil.getString(R.string.order_customization);
    public String pageCode = "order_customization";

    @ViewInject(R.id.pager)
    private ViewPager pager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator indicator;

    /*** 订单列表状态  String**/
    public static String status = "ALL";
    /*** 订单列表状态 int **/
    public int orderState = 0;

    public static final List<String> ORDER_STATUS = Arrays.asList(new String[]{
            ContextUtil.getString(R.string.order_status_all),
            ContextUtil.getString(R.string.order_status_obligations),
            ContextUtil.getString(R.string.order_status_undelivered),
            ContextUtil.getString(R.string.order_status_unreceived),
            ContextUtil.getString(R.string.order_status_received)});

    public static final List<String> ORDER_STATUS_TYPE = Arrays.asList(new String[]{
            "ALL", "PAYING", "DELIVERING", "RECEIVING", "END"});

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_order_custom;
    }

    @Override
    protected void init() {
        headView.setTitleStr(ContextUtil.getString(R.string.order));
        headView.setRihgtTitleText("", R.drawable.icon_search);
        orderState = getIntent().getIntExtra(Constant.Extra.ORDERSTATE, 0);
        initView();
    }

    /**
     * 初始化控件
     **/
    private void initView() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();// 这句是关键，fragment的里面嵌套fragment需要使用getChildFragmentManager()而不能用getSupportFragmentManager()
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(
                fragmentManager);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        pager.setCurrentItem(orderState, true);

        status = ORDER_STATUS_TYPE.get(orderState);
        LogUtil.i(status);
        
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                status = ORDER_STATUS_TYPE.get(arg0);
                LogUtil.i("setOnPageChangeListener:"+status);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * ViewPager适配器
     *
     * @author len
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new OrderCustFragment();
            Bundle bundle = new Bundle();
            bundle.putString("state", ORDER_STATUS_TYPE.get(position));
            fragment.setArguments(bundle);
            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ORDER_STATUS.get(position % ORDER_STATUS.size());
        }

        @Override
        public int getCount() {
            return ORDER_STATUS.size();
        }
    }

    @OnClick(value = {R.id.header_right})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_right:
                toActivity(SearchOrderCustActivity.class);
                break;
        }
    }
}
