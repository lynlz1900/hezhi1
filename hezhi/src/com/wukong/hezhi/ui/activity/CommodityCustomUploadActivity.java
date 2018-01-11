package com.wukong.hezhi.ui.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CustomPicInfo;
import com.wukong.hezhi.bean.CustomUploadPicInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.manager.PhotoManager.OnSelectedPhotoListener;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.BitmapCombineUtil;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: CommodityCustomImage
 * @Description: TODO(定制上传图片界面)
 * @author HuangFeiFei
 * @date 2017-9-12 14:23
 * 
 */
public class CommodityCustomUploadActivity extends BaseActivity implements OnCheckedChangeListener{
	public String pageName = ContextUtil.getString(R.string.info_upload);
	public String pageCode = "info_upload";
	
	@ViewInject(R.id.image_cover_one)
	private ImageView image_cover_one;
	
	@ViewInject(R.id.image_cover_two)
	private ImageView image_cover_two;
	
	@ViewInject(R.id.image_cover_three)
	private ImageView image_cover_three;
	
	@ViewInject(R.id.image_cover_four)
	private ImageView image_cover_four;
	
	@ViewInject(R.id.image_one_dis)
	private ImageView image_one_dis;

	@ViewInject(R.id.image_two_dis)
	private ImageView image_two_dis;
	
	@ViewInject(R.id.image_three_dis)
	private ImageView image_three_dis;
	
	@ViewInject(R.id.image_one_add)
	private ImageView image_one_add;

	@ViewInject(R.id.image_two_add)
	private ImageView image_two_add;
	
	@ViewInject(R.id.image_three_add)
	private ImageView image_three_add;
	
	@ViewInject(R.id.image_del_one)
	private ImageView image_del_one;
	
	@ViewInject(R.id.image_del_two)
	private ImageView image_del_two;
	
	@ViewInject(R.id.image_del_three)
	private ImageView image_del_three;
	
	@ViewInject(R.id.lay_img_one)
	private RelativeLayout lay_img_one;
	
	@ViewInject(R.id.lay_img_two)
	private RelativeLayout lay_img_two;
	
	@ViewInject(R.id.lay_img_three)
	private RelativeLayout lay_img_three;
	
	@ViewInject(R.id.check_img_one)
	private CheckBox check_img_one;
	
	@ViewInject(R.id.check_img_two)
	private CheckBox check_img_two;
	
	@ViewInject(R.id.check_img_three)
	private CheckBox check_img_three;
	
	@ViewInject(R.id.edit_company_name)
	private TextView edit_company_name;
	
	@ViewInject(R.id.edit_user_name)
	private EditText edit_user_name;
	
	@ViewInject(R.id.header_title)
	private TextView header_title;
	
	@ViewInject(R.id.header_left)
	private TextView header_left;
	
	/** 公司名称*/
	private String companyName;
	/** 姓名*/
	private String userName;
	/** 定制上传图片信息 */
	private CustomUploadPicInfo customUploadPicInfo;
	/** 商品信息 */
	private CommodityInfo commodityInfo;
	/** 选择哪张图片 **/
	private String coverMark = "";
	/** 选择哪张图片 **/
	private int imageType = -1;
	/** 第一张图片 **/
	public final int IMAGETYPE_ONE = 1;
	/** 第二张图片 **/
	public final int IMAGETYPE_TWO = 2;
	/** 第三张图片 **/
	public final int IMAGETYPE_THREE = 3;
	/** 图片地址 **/
	private static Uri tempUri;
	/** 图片宽度**/
	public final int imageWeight = 617;
	/** 图片宽度**/
	public final int imageHeight = 507;
	
	private Bitmap BitMapOne;
	private Bitmap BitMapTwo;
	private Bitmap BitMapThree;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_commodity_design_upload;
	}

	@Override
	protected void init() {
		initView();
		initData();
	}
	
	/** 初始化控件 **/
	private void initView(){
		header_title.setText(ContextUtil.getString(R.string.info_upload));
		Drawable drawable = getResources().getDrawable(R.drawable.icon_back);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		header_left.setCompoundDrawables(drawable, null, null, null);
		
		image_del_one.setVisibility(View.GONE);
		image_del_two.setVisibility(View.GONE);
		image_del_three.setVisibility(View.GONE);
		image_one_dis.setVisibility(View.GONE);
		image_two_dis.setVisibility(View.GONE);
		image_three_dis.setVisibility(View.GONE);
		image_cover_one.setVisibility(View.GONE);
		image_cover_two.setVisibility(View.GONE);
		image_cover_three.setVisibility(View.GONE);
		image_cover_four.setVisibility(View.GONE);
		check_img_one.setOnCheckedChangeListener(this);
		check_img_two.setOnCheckedChangeListener(this);
		check_img_three.setOnCheckedChangeListener(this);
	}
	
	/** 初始化数据 **/
	private void initData(){
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		customUploadPicInfo = new CustomUploadPicInfo();
		customUploadPicInfo.setCompanyName(commodityInfo.getCompanyName());
		customUploadPicInfo.setUserName(commodityInfo.getUsername());
		edit_company_name.setText(customUploadPicInfo.getCompanyName());
		edit_user_name.setText(customUploadPicInfo.getUserName());
		
		tempUri = Uri.fromFile(new File(HezhiConfig.PIC_PATH, HezhiConfig.UPLOAD_CUSTOM_IMG_NAME));
	}
	
	/** 上传三张底图 */
	private void postImages(Map<String, Object> map) {
		String URL = HttpURL.URL1 + HttpURL.SAVECUSTOMIZEPRODUCT;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings({ "unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo<CustomPicInfo> info = JsonUtil.fromJson(arg0.result, CustomPicInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					try {
						CustomPicInfo customPicInfo = info.getBody();
						if (UserInfoManager.getInstance().userLoginStatus
								(CommodityCustomUploadActivity.this,customPicInfo.getOnLineType())){
							commodityInfo.setImageUrl(customPicInfo.getPictureUrl());
							commodityInfo.setCustomId(customPicInfo.getCustomId());
							Intent intent = new Intent(mActivity, MyCustomizationTobuyActivity.class);
							intent.putExtra(Constant.Extra.CUSTOMIZATION_INFO, commodityInfo);
							intent.putExtra(Constant.Extra.ISFROM_COMMODITYPRE, true);
							startActivity(intent);
						}
					} catch (Exception e) {
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				LogUtil.i(arg1);
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}
	
	@OnClick(value = {  R.id.header_left,R.id.btn_save ,R.id.image_del_one,R.id.image_del_two,R.id.image_del_three
			,R.id.lay_img_one,R.id.lay_img_two,R.id.lay_img_three})
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			gotoBack();
			break;
		case R.id.btn_save:
			gotoSave();
			break;
		case R.id.lay_img_one:
			chooseImage(IMAGETYPE_ONE);
			break;
		case R.id.lay_img_two:
			chooseImage(IMAGETYPE_TWO);
			break;
		case R.id.lay_img_three:
			chooseImage(IMAGETYPE_THREE);
			break;
		case R.id.image_del_one:
			closeImage(IMAGETYPE_ONE);
			break;
		case R.id.image_del_two:
			closeImage(IMAGETYPE_TWO);
			break;
		case R.id.image_del_three:
			closeImage(IMAGETYPE_THREE);
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			gotoBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/** 关闭图片 **/
	private void closeImage(int type){
		if(type == IMAGETYPE_ONE){
			image_del_one.setVisibility(View.GONE);
			image_one_dis.setVisibility(View.GONE);
			image_cover_one.setVisibility(View.GONE);
			image_one_add.setVisibility(View.VISIBLE);
			BitMapOne = null;
		}else if(type == IMAGETYPE_TWO){
			image_del_two.setVisibility(View.GONE);
			image_two_dis.setVisibility(View.GONE);
			image_cover_two.setVisibility(View.GONE);
			image_two_add.setVisibility(View.VISIBLE);
			BitMapTwo = null;
		}else if(type == IMAGETYPE_THREE){
			image_del_three.setVisibility(View.GONE);
			image_three_dis.setVisibility(View.GONE);
			image_cover_three.setVisibility(View.GONE);
			image_three_add.setVisibility(View.VISIBLE);
			BitMapThree = null;
		}
		
		setCover();
	}
	
	/** 选择图片 **/
	private void chooseImage(int type){
		imageType = type;
		PhotoManager manager = PhotoManager.getInstance();
		manager.setWandH(imageWeight, imageHeight);
		manager.setWHRatio(imageWeight, imageHeight);
		manager.choicePhoto(mActivity, PhotoManager.FROM_ALBUM, tempUri, new PhotoListener());
	}
	
	/** 显示图片 **/
	private void showImage(int type,Bitmap bitmap) {
		if(bitmap == null){
			ScreenUtil.showToast(ContextUtil.getString(R.string.photo_get_fail));
			return;
		}
		
		LogUtil.i(bitmap.getWidth()+" "+bitmap.getHeight());
		if(bitmap.getWidth() != imageWeight && bitmap.getHeight() != imageHeight){
			bitmap = BitmapCombineUtil.zoomImg(bitmap, imageWeight, imageHeight);
		}
		
		FileUtil.deleteFile(tempUri.getPath());
		if(type == IMAGETYPE_ONE){
			image_del_one.setVisibility(View.VISIBLE);
			image_one_dis.setVisibility(View.VISIBLE);
			image_cover_one.setVisibility(View.VISIBLE);
			image_one_add.setVisibility(View.GONE);
			BitMapOne = bitmap;
			LogUtil.i(BitMapOne.getWidth()+" "+BitMapOne.getHeight());
			image_one_dis.setImageBitmap(bitmap);
			image_cover_one.setImageBitmap(bitmap);
		}else if(type == IMAGETYPE_TWO){
			image_del_two.setVisibility(View.VISIBLE);
			image_two_dis.setVisibility(View.VISIBLE);
			image_cover_two.setVisibility(View.VISIBLE);
			image_two_add.setVisibility(View.GONE);
			BitMapTwo = bitmap;
			LogUtil.i(BitMapTwo.getWidth()+" "+BitMapTwo.getHeight());
			image_two_dis.setImageBitmap(bitmap);
			image_cover_two.setImageBitmap(bitmap);
		}else if(type == IMAGETYPE_THREE){
			image_del_three.setVisibility(View.VISIBLE);
			image_three_dis.setVisibility(View.VISIBLE);
			image_cover_three.setVisibility(View.VISIBLE);
			image_three_add.setVisibility(View.GONE);
			BitMapThree = bitmap;
			LogUtil.i(BitMapThree.getWidth()+" "+BitMapThree.getHeight());
			image_three_dis.setImageBitmap(bitmap);
			image_cover_three.setImageBitmap(bitmap);
		}
		
		setCover();
	}
	
	/** 设置瓶身图 */
	private void setCover(){
		Bitmap bitmap = null;
			
		try {
			if(check_img_one.isChecked() && image_one_dis.getVisibility() == View.VISIBLE)
				bitmap = ((BitmapDrawable)image_one_dis.getDrawable()).getBitmap();
			else if(check_img_two.isChecked() && image_two_dis.getVisibility() == View.VISIBLE)
				bitmap = ((BitmapDrawable)image_two_dis.getDrawable()).getBitmap();
			else if(check_img_three.isChecked() && image_three_dis.getVisibility() == View.VISIBLE)
				bitmap = ((BitmapDrawable)image_three_dis.getDrawable()).getBitmap();
		} catch (Exception e) {
			
		}
		
		if(bitmap != null){
//			Bitmap bitmapAngle = getBitmapAngle(bitmap,-6);//
			image_cover_four.setVisibility(View.VISIBLE);
			image_cover_four.setImageBitmap(bitmap);
		}else{
			image_cover_four.setVisibility(View.GONE);
		}
			
	}
	
	/** 图片绕Y轴倾斜-6度 */
	public Bitmap getBitmapAngle(Bitmap bitmap,int degree) {
		Matrix matrix = new Matrix();
		Camera camera = new Camera();
		float centery = bitmap.getHeight()/2;
		float centerx = bitmap.getWidth();
		camera.save();
		camera.rotateY(degree);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerx, -centery);
		matrix.postTranslate(centerx, centery);
		Bitmap copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(copyBitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap, matrix, paint);
		return copyBitmap;
	}

	/** 选择相册照片或拍照图片的监听 */
	private class PhotoListener implements OnSelectedPhotoListener {
		@Override
		public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo) {
			showImage(imageType,photo);
		}
	}
	
	/** 返回 */
	private void gotoBack(){
		finish();
	}
	
	/** 保存上传图片和信息到服务器*/
	private void gotoSave(){
		if(image_one_dis.getVisibility() == View.GONE || image_two_dis.getVisibility() == View.GONE ||
				image_three_dis.getVisibility() == View.GONE){
			ScreenUtil.showToast(ContextUtil.getString(R.string.image_upload_iserror));
			return;
		}
		
		
		if(BitMapOne == null || BitMapTwo == null || BitMapThree == null){
			ScreenUtil.showToast(ContextUtil.getString(R.string.image_upload_iserror));
			return;
		}
		
		if(!check_img_one.isChecked() && !check_img_two.isChecked() &&
				!check_img_three.isChecked()){
			ScreenUtil.showToast(ContextUtil.getString(R.string.image_upload_cover_iserror));
			return;
		}
		
		userName = edit_user_name.getText().toString().trim();
		if(TextUtils.isEmpty(userName)){
			ScreenUtil.showToast(ContextUtil.getString(R.string.info_user_name_input));
			return;
		}
		
		customUploadPicInfo.setCompanyName(companyName);
		customUploadPicInfo.setUserName(userName);
		customUploadPicInfo.setCoverMark(coverMark);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				showProgressDialog();
				
				Bitmap bitmapRed = BitMapOne;// 压缩图片
				Bitmap bitmapBlue = BitMapTwo;// 压缩图片
				Bitmap bitmapyellow = BitMapThree;// 压缩图片

				String sourceRedPic = Base64Util.Bitmap2StrByBase64(bitmapRed);
				String sourceBluePic = Base64Util.Bitmap2StrByBase64(bitmapBlue);
				String sourceYellowPic = Base64Util.Bitmap2StrByBase64(bitmapyellow);
				customUploadPicInfo.setBitmapOneString(sourceRedPic);
				customUploadPicInfo.setBitmapTwoString(sourceBluePic);
				customUploadPicInfo.setBitmapThreeString(sourceYellowPic);
				
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("memberId", UserInfoManager.getInstance().getUserId());
				map.put("productId", commodityInfo.getId());
				map.put("coverMark", customUploadPicInfo.getCoverMark());
				map.put("name", customUploadPicInfo.getUserName());
				map.put("sourceRedPic", customUploadPicInfo.getBitmapOneString());
				map.put("sourceBluePic", customUploadPicInfo.getBitmapTwoString());
				map.put("sourceYellowPic", customUploadPicInfo.getBitmapThreeString());
				
				runOnUiThread(new Runnable() {
					public void run() {
						postImages(map);
					}
				});
			}
		}).start();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.check_img_one:
			if(isChecked){
				check_img_two.setChecked(false);
				check_img_three.setChecked(false);
				coverMark = "red";
			}
			setCover();
			break;
		case R.id.check_img_two:
			if(isChecked){
				check_img_one.setChecked(false);
				check_img_three.setChecked(false);
				coverMark = "blue";
			}
			setCover();
			break;
		case R.id.check_img_three:
			if(isChecked){
				check_img_two.setChecked(false);
				check_img_one.setChecked(false);
				coverMark = "yellow";
			}
			setCover();
			break;
		default:
			break;
		}
	}
}
