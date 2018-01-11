package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CustomPicInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.CustomSuccessWindows;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.BitmapCombineUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName: CommodityCustomPreActivity
 * @Description: TODO(商品定制)
 * @author HuZhiyin
 * @date 2017-8-8 下午1:59:44
 * 
 */
public class CommodityCustomActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.commodity_custom);
	public String pageCode = "commodity_custom";

	@ViewInject(R.id.templet_iv)
	private ImageView templet_iv;

	@ViewInject(R.id.bottom_iv)
	private ImageView bottom_iv;

	@ViewInject(R.id.close_model_iv)
	private ImageView close_model_iv;

	@ViewInject(R.id.design_iv)
	private ImageView design_iv;

	@ViewInject(R.id.custom_rl)
	private RelativeLayout custom_rl;

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	protected boolean isNotAddTitle() {
		return false;
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_commodity_custom;
	}

	@Override
	protected void init() {
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		initView();
		// watchKuang();
	}

	// private Handler handler;
	// private Runnable runnable;
	//
	// /** 如果框显示状态，着3秒后关闭框 */
	// private void watchKuang() {
	// handler = new Handler();
	// runnable = new MyRunanble();
	// handler.postDelayed(runnable, 3000);
	// }

	// public class MyRunanble implements Runnable {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// if (close_model_iv.getVisibility() == View.VISIBLE) {
	// hideKuang();
	// }
	// handler.postDelayed(this, 3000);
	// }
	// }

	private void initView() {
		headView.setTitleStr(ContextUtil.getString(R.string.commodity_custom));
		headView.setRightTitleText(ContextUtil.getString(R.string.save));
		// templet_iv.setOnTouchListener(new ImageListener());
		// templet_iv.setScaleType(ScaleType.MATRIX);
		showPic();
	}

	/** 展示底图 */
	private void showPic() {
		showProgressDialog();
		Glide.with(ContextUtil.getContext()).load(commodityInfo.getBottomUrl()).asBitmap().fitCenter()
				.into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(Bitmap arg0, GlideAnimation arg1) {
						bottom_iv.setImageBitmap(arg0);
						custom_rl.setVisibility(View.VISIBLE);
						dismissProgressDialog();
					}
				});

	}

	@OnClick(value = { R.id.header_right, R.id.design_iv, R.id.close_model_iv, R.id.layout_rl, R.id.templet_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:// 下一步
			next();
			break;
		case R.id.design_iv:// 去设计
			toActivity(CommodityDesignActivity.class, Constant.Extra.COMMDITYINFO, commodityInfo);// 去定制
			break;
		case R.id.close_model_iv:// 关闭模板
			close();
			break;
		// case R.id.layout_rl:// 隐藏框
		// hideKuang();
		// break;
		// case R.id.templet_iv:// 显示框
		// showKuang();
		// break;
		}
	}

	/** 显示框和关闭按钮 */
	private void showKuang() {
		templet_iv.setBackground(ContextUtil.getDrawable(R.drawable.bg_kuang));
		close_model_iv.setVisibility(View.VISIBLE);
	}

	/** 隐藏框和关闭按钮 */
	private void hideKuang() {
		templet_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
		close_model_iv.setVisibility(View.GONE);
	}

	/** 关闭模板 */
	private void close() {
		templet_iv.setVisibility(View.GONE);
		close_model_iv.setVisibility(View.GONE);
		design_iv.setVisibility(View.VISIBLE);
	}

	/** 合成后的图片 ，带底图 */
	private Bitmap bitmapBottom;
	/** 合成后的图片 ，不带带底图 */
	private Bitmap bitmapNoBottom;
	private String picture;
	private String custom;

	/** 下一步，合成图片并保存，跳转页面 */
	private void next() {
		if (!templet_iv.isShown()) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.nodesign_tips));
			return;
		}
		hideKuang();
		showProgressDialog();
		bitmapBottom = BitmapCombineUtil.combineImg2Bmp(bottom_iv, templet_iv);// 合成地图和模板
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileUtil.saveBitmap2File(bitmapBottom, HezhiConfig.PIC_PATH, HezhiConfig.CUSTOM_IMG_NAME);// 保存模板图片
				// bitmapBottom = BitmapFactory.decodeFile(HezhiConfig.PIC_PATH
				// + "/" + HezhiConfig.CUSTOM_IMG_NAME);// 获取保存在文件下的bitmap
				bitmapNoBottom = BitmapFactory
						.decodeFile(HezhiConfig.PIC_PATH + HezhiConfig.CUSTOM_TEMPLET_IMG_NAME);
				Bitmap bitmapBottomCompress = Bitmap.createScaledBitmap(bitmapBottom, 750, 750, true);// 压缩图片
				Bitmap bitmapNoBottomCompress = Bitmap.createScaledBitmap(bitmapNoBottom, 600, 1500, true);// 压缩图片

				picture = Base64Util.Bitmap2StrByBase64(bitmapBottomCompress);
				custom = Base64Util.Bitmap2StrByBase64(bitmapNoBottomCompress);
				runOnUiThread(new Runnable() {
					public void run() {
						postPic();
					}
				});
			}
		}).start();

	}

	private void postPic() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId());// 产品id
		map.put("productId", commodityInfo.getId());// 产品id
		map.put("pictureUrl", picture);// 定制图片(含背景)
		map.put("customUrl", custom); // 定制图片(不含背景)
		String URL = HttpURL.URL1 + HttpURL.SAVE_CUSTOM;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ResponseJsonInfo<CustomPicInfo> info = JsonUtil.fromJson(arg0.result, CustomPicInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					CustomPicInfo customPicInfo = info.getBody();
					String picUrl = customPicInfo.getPictureUrl();
					String customId = customPicInfo.getCustomId();
					commodityInfo.setImageUrl(picUrl);
					commodityInfo.setCustomId(customId);
					commodityInfo.setCustomizationType(Constant.Commodity.TAILOR);// 标记为外观定制
					showToastSuccess();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
			}
		});
	}

	/*** 提示定制保存成功 ***/
	private void showToastSuccess(){
		CustomSuccessWindows.getInstance().show();
		
		new Handler().postDelayed(new Runnable(){  
            public void run() { 
            	CustomSuccessWindows.getInstance().dismiss();
                gotoBugNowActivity();
            } 
        }, 3000);   //5秒
	}
	
	/*** 跳转到购买界面 ***/
	private void gotoBugNowActivity(){
		Intent intent = new Intent(mActivity, MyCustomizationTobuyActivity.class);
		intent.putExtra(Constant.Extra.CUSTOMIZATION_INFO, commodityInfo);
		startActivity(intent);
		finish();  //modify by Huangfeifei 2017-10-16
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (templet_iv.getVisibility() == View.VISIBLE) {
			design_iv.setVisibility(View.GONE);
			templet_iv.setBackground(ContextUtil.getDrawable(R.drawable.bg_kuang));
			close_model_iv.setVisibility(View.VISIBLE);
		} else {
			design_iv.setVisibility(View.VISIBLE);
			templet_iv.setBackgroundColor(ContextUtil.getColor(R.color.transparency));// 去掉背景
			close_model_iv.setVisibility(View.GONE);
		}
	}

	// @Override
	// public void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// handler.removeCallbacks(runnable);// 退出时候，置空handler,防止内存泄露
	// handler = null;
	// }

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(CommodityDesignActivity.class)) {
				Bitmap b = BitmapFactory.decodeFile(HezhiConfig.PIC_PATH + HezhiConfig.CUSTOM_TEMPLET_IMG_NAME);
				if (b != null) {
					Bitmap bm = Bitmap.createScaledBitmap(b, (int) ((float) b.getWidth() / 2.4),
							(int) ((float) b.getHeight() / 2.4), true);// 压缩图片至一半
					templet_iv.setVisibility(View.VISIBLE);
					templet_iv.setImageBitmap(bm);
				} else {
					templet_iv.setVisibility(View.GONE);
				}
			}
		}
	}

}
