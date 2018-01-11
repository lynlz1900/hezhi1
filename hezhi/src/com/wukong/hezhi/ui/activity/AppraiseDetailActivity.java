package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CommodityAppraiseImageAdapter;
import com.wukong.hezhi.bean.CommodityAppraiseCenterInfo;
import com.wukong.hezhi.bean.CommodityAppraiseInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.view.GridViewForListview;
import com.wukong.hezhi.ui.view.RatingBar;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/***
 * 
 * @ClassName: AppraiseShowActivity
 * @Description: TODO(评价详情)
 * @author HuangFeiFei
 * @date 2017-12-18
 * 
 */
public class AppraiseDetailActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.appraise_detail);
	public String pageCode ="appraise_detail";
	
	@ViewInject(R.id.image_user)
	private ImageView image_user;
	
	@ViewInject(R.id.ratingBar)
	private RatingBar ratingBar;
	
	@ViewInject(R.id.text_user)
	private TextView text_user;
	
	@ViewInject(R.id.text_time)
	private TextView text_time;
	
	@ViewInject(R.id.gv_images)
	private GridViewForListview gv_images;
	
	@ViewInject(R.id.gv_imagesadd)
	private GridViewForListview gv_imagesadd;
	
	@ViewInject(R.id.text_appraise)
	private TextView text_appraise;
	
	@ViewInject(R.id.text_appraise_response)
	private TextView text_appraise_response;
	
	@ViewInject(R.id.text_appraiseAdd_response)
	private TextView text_appraiseAdd_response;
	
	@ViewInject(R.id.text_add)
	private TextView text_add;
	
	@ViewInject(R.id.text_appraise_add)
	private TextView text_appraise_add;
	
	@ViewInject(R.id.layout_add)
	private LinearLayout layout_add;
	
	@ViewInject(R.id.layout_appraise_response)
	private LinearLayout layout_appraise_response;
	
	@ViewInject(R.id.layout_gv_images)
	private LinearLayout layout_gv_images;
	
	@ViewInject(R.id.layout_gv_imagesadd)
	private LinearLayout layout_gv_imagesadd;
	
	@ViewInject(R.id.layout_appraiseAdd_response)
	private LinearLayout layout_appraiseAdd_response;
	
	/**评价中心信息**/
	private CommodityAppraiseCenterInfo appraiseCenterInfo;
	
	/**评价信息**/
	private CommodityAppraiseInfo commodityAppraiseInfo;
	
	/**评价图片列表**/
	private List<String> listImages;
	/**追加评价图片列表**/
	private List<String> listImagesAdd;
	private CommodityAppraiseImageAdapter commodityAppraiseImageAdapter;
	private CommodityAppraiseImageAdapter commodityAppraiseImageAdapterAdd;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_appraise_detail;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.appraise_detail));
		initView();
	}
	
	//初始化控件
	private void initView() {
		appraiseCenterInfo = (CommodityAppraiseCenterInfo) getIntent().
				getSerializableExtra(Constant.Extra.APPRAISE_CENTER_INFO);
		if(appraiseCenterInfo == null){
			appraiseCenterInfo = new CommodityAppraiseCenterInfo();
		}
		
		listImages = new ArrayList<>();
		listImagesAdd = new ArrayList<>();
		commodityAppraiseImageAdapter = new CommodityAppraiseImageAdapter(mActivity, listImages);
		gv_images.setAdapter(commodityAppraiseImageAdapter);
		commodityAppraiseImageAdapterAdd = new CommodityAppraiseImageAdapter(mActivity, listImagesAdd);
		gv_imagesadd.setAdapter(commodityAppraiseImageAdapterAdd);
		
		gv_images.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				gotoImagePre(position, false);
			}
		});
		
		gv_imagesadd.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				gotoImagePre(position, true);
			}
		});
		
		getAppraiseDetail();
	}
	
	/**
	 * 获取评论详情
	 */
	private void getAppraiseDetail(){
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", appraiseCenterInfo.getId()+"");
		String URL = HttpURL.URL1 + HttpURL.APPRAISE_DETAIL;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<CommodityAppraiseInfo> info = JsonUtil.fromJson(arg0.result, CommodityAppraiseInfo.class);

				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						commodityAppraiseInfo = info.getBody();
					} catch (Exception e) {
					}

					if (commodityAppraiseInfo != null) {
						setData(commodityAppraiseInfo);
					}

				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}
	
	/**
	 * 图片预览
	 */
	private void gotoImagePre(int position,boolean isAppraiseAdd){
		if(mActivity == null) return;
		
		Intent intent = new Intent();
		intent.setClass(mActivity, AppraiseImageShowActivity.class);
		intent.putExtra("imageURL", commodityAppraiseInfo);
		intent.putExtra("position", position);
		intent.putExtra("isAppraiseAdd", isAppraiseAdd);
		mActivity.startActivity(intent);
	}
	
	/**
	 * 设置数据
	 */
	private void setData(CommodityAppraiseInfo appraiseInfo){
		if(appraiseInfo == null) return;
		
		GlideImgUitl.glideLoaderNoAnimation
		 (ContextUtil.getContext(), appraiseInfo.getUserImage(), image_user, 0);
		text_user.setText(appraiseInfo.getUserName());
		text_appraise.setText(appraiseInfo.getAppraiseMessage());
		text_time.setText(DateUtil.getYMD_(appraiseInfo.getTime()));
		ratingBar.setStarMark(Float.valueOf(appraiseInfo.getAppraiseStatus()+""));
		listImages = appraiseInfo.getAppraiseListImages();
		listImagesAdd = appraiseInfo.getAppraiseListImagesAdd();
		if(!TextUtils.isEmpty(appraiseInfo.getAppraiseResponseMessage())){
			text_appraise_response.setText(appraiseInfo.getAppraiseResponseMessage());
			layout_appraise_response.setVisibility(View.VISIBLE);
		}else{
			layout_appraise_response.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(appraiseInfo.getAppraiseResponseAddMessage())){
			text_appraiseAdd_response.setText(appraiseInfo.getAppraiseResponseAddMessage());
			layout_appraiseAdd_response.setVisibility(View.VISIBLE);
		}else{
			layout_appraiseAdd_response.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(appraiseInfo.getAppraiseAddMessage())){
			text_appraise_add.setText(appraiseInfo.getAppraiseAddMessage());
			text_add.setText("用户"+DateUtil.dateDiff(appraiseInfo.getTime(), appraiseInfo.getAppraiseAddTime())+"追评");
			layout_add.setVisibility(View.VISIBLE);
		}else{
			layout_add.setVisibility(View.GONE);
		}
		
		if(listImages == null || listImages.size() <1){
			layout_gv_images.setVisibility(View.GONE);
		}else{
			layout_gv_images.setVisibility(View.VISIBLE);
			commodityAppraiseImageAdapter = new CommodityAppraiseImageAdapter(mActivity, listImages);
			gv_images.setAdapter(commodityAppraiseImageAdapter);
		}
		
		if(listImagesAdd == null || listImagesAdd.size() <1){
			layout_gv_imagesadd.setVisibility(View.GONE);
		}else{
			layout_gv_imagesadd.setVisibility(View.VISIBLE);
			commodityAppraiseImageAdapterAdd = new CommodityAppraiseImageAdapter(mActivity, listImagesAdd);
			gv_imagesadd.setAdapter(commodityAppraiseImageAdapterAdd);
		}
	}
}
