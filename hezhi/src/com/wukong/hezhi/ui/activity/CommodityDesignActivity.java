package com.wukong.hezhi.ui.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.TempletRecycleViewAdapter;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.bean.TempletInfos;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.function.imagecompose.ImageListener;
import com.wukong.hezhi.function.imagecompose.ViewListenter;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.manager.PhotoManager.OnSelectedPhotoListener;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.TempletWindows;
import com.wukong.hezhi.utils.BitmapCombineUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PhotoUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: CommodityDesignActivity
 * @Description: TODO(设计模板)
 * @author HuZhiyin
 * @date 2017-8-8 下午1:59:44
 * 
 */
public class CommodityDesignActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.commodity_design);
	public String pageCode = "commodity_design";

	@ViewInject(R.id.main_radiogroup)
	private RadioGroup main_radiogroup;

	@ViewInject(R.id.header_iv)
	private ImageView header_iv;

	@ViewInject(R.id.model_iv)
	private ImageView model_iv;

	@ViewInject(R.id.name_tv)
	private TextView name_tv;

	@ViewInject(R.id.name_rl)
	private RelativeLayout name_rl;

	@ViewInject(R.id.custom_area_rl)
	private RelativeLayout custom_area_rl;

	@ViewInject(R.id.text_tip)
	private TextView text_tip;

	@ViewInject(R.id.close_model_iv)
	private ImageView close_model_iv;

	@ViewInject(R.id.close_header_iv)
	private ImageView close_header_iv;

	/** 是否添加文字 */
	private boolean addText = false;
	/** 是否添加头像 */
	private boolean addHeader = false;
	/** 是否添加模板 */
	private boolean addModel = false;
	/** 模板数据 */
	private List<TempletInfo> templetInfos;
	/** 商品信息 */
	private CommodityInfo commodityInfo;
	/** 文字的大小 */
	private float textSize;
	/** 文字的颜色 */
	private int textColor = ContextUtil.getColor(R.color.white);
	private static Uri tempUri;

	@Override
	protected boolean isNotAddTitle() {
		return true;// 此页面加头部是由于弹键盘会将布局顶上去。
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_commodity_design;
	}

	@Override
	protected void init() {
		initData();
		initView();
		initDialogLayout();
	}

	/** 初始化数据 */
	private void initData() {
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		tempUri = Uri.fromFile(new File(HezhiConfig.PIC_PATH, HezhiConfig.TAILOR_CUSTOM_IMG_NAME));
		getTemplets();// 获取模板数据
	}

	/** 初始化控件 */
	private void initView() {
		setSwipeBackEnable(false);// 禁止侧滑消除
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		textSize = name_tv.getTextSize();
		header_iv.setOnTouchListener(new ImageListener());
		name_rl.setOnTouchListener(new ViewListenter());
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}

	/** 获取模板 */
	private void getTemplets() {
		showProgressDialog();
		String URL = HttpURL.URL1 + HttpURL.TEMPLETS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", commodityInfo.getId());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissProgressDialog();
				ResponseJsonInfo<TempletInfos> info = JsonUtil.fromJson(arg0.result, TempletInfos.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					TempletInfos infos = info.getBody();
					templetInfos = infos.getDataList();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
			}
		});
	}

	@OnClick(value = { R.id.header_left, R.id.header_right, R.id.templet_rb, R.id.pic_rb, R.id.photo_rb, R.id.text_rb,
			R.id.close_text_iv, R.id.bigger_iv, R.id.smaller_iv, R.id.close_model_iv, R.id.close_header_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:// 返回
			showClosekTip();
			break;
		case R.id.header_right:// 下一步
			next();
			break;
		case R.id.templet_rb:// 显示模板弹框
			selectTemplet();
			break;
		case R.id.pic_rb:// 选择图片
			selecltPhoto(PhotoManager.FROM_ALBUM);
			break;
		case R.id.photo_rb:// 选择拍照
			selecltPhoto(PhotoManager.FROM_CAMERA);
			break;
		case R.id.text_rb:// 文字
			showEditText();
			break;
		case R.id.close_text_iv:// 隐藏文字
			closeText();
			break;
		case R.id.bigger_iv:// 放大文字
			bigScaleText();
			break;
		case R.id.smaller_iv:// 缩小文字
			smallScaleText();
			break;
		case R.id.close_model_iv:// 关闭画面中的模板
			closeModel();
			break;
		case R.id.close_header_iv:// 关闭画面中的头像
			closeHeader();
			break;
		}
	}

	private void selectTemplet() {
		if (templetInfos == null || templetInfos.size() == 0) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.no_templet));
			return;
		}
		TempletWindows.getInstance().show(this, main_radiogroup, templetInfos);
	}

	private void selecltPhoto(int way) {
		PhotoManager photoManager = PhotoManager.getInstance();
		photoManager.setWandH(ScreenUtil.dp2px(Constant.customImage.WITH), 
				ScreenUtil.dp2px(Constant.customImage.WITH));//设置回显图片大小
		photoManager.choicePhoto(mActivity, way, tempUri, new PhotoListener());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showClosekTip();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 关闭页面提醒 */
	private void showClosekTip() {
		if (header_iv.getVisibility() == View.VISIBLE || model_iv.getVisibility() == View.VISIBLE
				|| name_rl.getVisibility() == View.VISIBLE) {
			new CustomAlterDialog.Builder(this).setTitle(ContextUtil.getString(R.string.tip))
					.setMsg(ContextUtil.getString(R.string.back_tip))
					.setCancelButton(ContextUtil.getString(R.string.cancel), null)
					.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {

						@Override
						public void onDialogClick(View v, CustomAlterDialog dialog) {
							// TODO Auto-generated method stub
							finish();
						}
					}).show();
		} else {
			finish();
		}

	}

	/** 关闭头像 */
	private void closeHeader() {
		addHeader = false;
		close_header_iv.setVisibility(View.GONE);
		header_iv.setVisibility(View.GONE);
		modelTipShow();
	}

	/** 关闭模板 */
	private void closeModel() {
		addModel = false;
		close_model_iv.setVisibility(View.GONE);
		model_iv.setVisibility(View.GONE);
		modelTipShow();
	}

	/** 隐藏文字 */
	private void closeText() {
		addText = false;
		name_rl.setVisibility(View.GONE);
		name_tv.setText("");
		modelTipShow();
	}

	/** 显示文字 */
	private void showText() {
		addText = true;
		name_rl.setVisibility(View.VISIBLE);
		name_tv.setText(editText.getText().toString().trim());
		name_tv.setTextColor(textColor);
		modelTipShow();
	}

	/** 放大文字 */
	private void bigScaleText() {
		textSize = textSize + 5;
		LogUtil.d("before" + name_rl.getLeft());
		name_tv.setTextSize(ScreenUtil.px2dp((int) textSize));
		LogUtil.d("after" + name_rl.getLeft());
	}

	/** 缩小文字 */
	private void smallScaleText() {
		textSize = textSize - 5;
		name_tv.setTextSize(ScreenUtil.px2dp((int) textSize));
	}

	private LinearLayout editText_ll;
	private EditText editText;
	private RadioGroup color_rg;

	/** 弹框布局 */
	private void initDialogLayout() {
		editText_ll = (LinearLayout) ScreenUtil.inflate(R.layout.layout_edittext);
		editText = (EditText) editText_ll.findViewById(R.id.text_et);
		color_rg = (RadioGroup) editText_ll.findViewById(R.id.color_rg);
		color_rg.setOnCheckedChangeListener(new ColorListener());// RadioGroup选中状态改变监听
	}

	/** 显示编辑框 */
	private void showEditText() {

		CustomAlterDialog.Builder builder = new CustomAlterDialog.Builder(mActivity);
		CustomAlterDialog dialog = builder.setTitle(ContextUtil.getString(R.string.input_text)).setView(editText_ll)
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						showText();
					}
				}).build();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);// 显示键盘
		dialog.show();

		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		editText.findFocus();

	}

	private class ColorListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.white_rb:
				textColor = ContextUtil.getColor(R.color.white);
				break;
			case R.id.black_rb:
				textColor = ContextUtil.getColor(R.color.black);
				break;
			}
		}
	}

	/** 显示头像 */
	private void showHeader() {
		addHeader = true;
		close_header_iv.setVisibility(View.VISIBLE);
		header_iv.setVisibility(View.VISIBLE);
		Bitmap bitmap = FileUtil.decodeUriAsBitmap(tempUri);
		Bitmap bitmapRound = PhotoUtil.toRoundBitmap(bitmap);// 转换图片成圆形
		header_iv.setImageBitmap(bitmapRound);
		modelTipShow();
	}

	/** 模板提示语的显示和隐藏 */
	private void modelTipShow() {
		if (model_iv.getVisibility() == View.VISIBLE || header_iv.getVisibility() == View.VISIBLE
				|| name_rl.getVisibility() == View.VISIBLE) {
			text_tip.setVisibility(View.GONE);
			custom_area_rl.setBackground(ContextUtil.getDrawable(R.drawable.bg_kuang));
		} else {
			text_tip.setVisibility(View.VISIBLE);
			custom_area_rl.setBackgroundColor(ContextUtil.getColor(R.color.transparency));
		}
	}

	/** 选择相册照片或拍照图片的监听 */
	private class PhotoListener implements OnSelectedPhotoListener {
		@Override
		public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo) {
			showHeader();
		}
	}

	/** 合成图片并保存至文件 */
	private void next() {
		if (!addHeader && !addText && !addModel) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.nodesign_tips));
			return;
		}

		if (!addHeader) {
			header_iv = null;
		}
		if (!addText) {
			name_tv = null;
		}
		if (!addModel) {
			model_iv = null;
		}

		final Bitmap b = BitmapCombineUtil.combineBmp(header_iv, model_iv, name_tv);// 合成的图片;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileUtil.saveBitmap2File(b, HezhiConfig.PIC_PATH, HezhiConfig.CUSTOM_TEMPLET_IMG_NAME);// 保存模板图片
				runOnUiThread(new Runnable() {
					public void run() {
						ObserveManager.getInstance().notifyState(CommodityCustomActivity.class,
								CommodityDesignActivity.class, null);
						finish();
					}
				});

			}
		}).start();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(TempletRecycleViewAdapter.class)) {
				getModelPic(msg);
			}
		}
	}

	/** 获取模板 */
	private void getModelPic(Object msg) {
		String url = (String) msg;
		showProgressDialog();
		Glide.with(ContextUtil.getContext()).load(url).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onResourceReady(Bitmap arg0, GlideAnimation arg1) {
				dismissProgressDialog();
				addModel = true;
				close_model_iv.setVisibility(View.VISIBLE);
				model_iv.setVisibility(View.VISIBLE);
				model_iv.setImageBitmap(arg0);
				modelTipShow();
			}
		});
	}
}
