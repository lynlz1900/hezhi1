package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioGroup;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyFragmentPagerAdapter;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.fragment.AppraiseBeingFragment;
import com.wukong.hezhi.ui.fragment.AppraiseCompleteFragment;
import com.wukong.hezhi.utils.ContextUtil;

import java.util.ArrayList;

/**
 * @author HuangFeiFei
 * @ClassName: AppraiseCenterActivity
 * @Description: TODO(评价中心)
 * @date 2017-12-19
 */
public class AppraiseCenterActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.appraise_center);
    public String pageCode = "appraise_center";

    @ViewInject(R.id.pager)
    private ViewPager viewPager;

    @ViewInject(R.id.main_radiogroup)
    private RadioGroup main_radiogroup;

    /**
     * 当前选中的fragment
     */
    private int currentFragmentState = 0;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_appraise_center;
    }

    @Override
    protected void init() {
        headView.setTitleStr(ContextUtil.getString(R.string.appraise_center));
        headView.setRihgtTitleText("", R.drawable.icon_appraise_explain);;
        initView();
    }

    @SuppressWarnings("deprecation")
    private void initView() {
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new AppraiseBeingFragment());// 待评价
        fragmentList.add(new AppraiseCompleteFragment());// 已评价
        viewPager.setAdapter(new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList));
        viewPager.setOnPageChangeListener(new myOnPageChangeListener());
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        main_radiogroup.setOnCheckedChangeListener(new myCheckChangeListener());// RadioGroup选中状态改变监听
        main_radiogroup.check(R.id.main_rb_appraiseing);//默认选中待评价
    }

    /**
     * RadioButton切换Fragment
     */
    private class myCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.main_rb_appraiseing:
                    currentFragmentState = 0;
                    break;
                case R.id.main_rb_appraised:
                    currentFragmentState = 1;
                    break;
            }
            viewPager.setCurrentItem(currentFragmentState, true);
        }
    }

    /**
     * MapViewPager切换Fragment,RadioGroup做相应变化
     */
    private class myOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    main_radiogroup.check(R.id.main_rb_appraiseing);
                    break;
                case 1:
                    main_radiogroup.check(R.id.main_rb_appraised);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    
    @OnClick(value = { R.id.header_right })
    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	super.onClick(v);
    	switch (v.getId()) {
		case R.id.header_right:// 跳转到商品评价说明H5界面
			String URL = HttpURL.URL1 + HttpURL.APPRAISE_EXPLAIN; 
			Intent intent = new Intent();
			intent.putExtra(Constant.Extra.WEB_URL, URL);
			intent.putExtra(Constant.Extra.WEBVIEW_TITLE, ContextUtil.getString(R.string.appraise_explain));
			intent.setClass(this, WebViewActivity.class);
			startActivity(intent);
			break;
		}
    }
}
