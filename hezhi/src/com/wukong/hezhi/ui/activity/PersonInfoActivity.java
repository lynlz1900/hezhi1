package com.wukong.hezhi.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.function.imagepicker.bean.ImageItem;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.MyPhotoManager;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.manager.PhotoManager.OnSelectedPhotoListener;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnClickListener;
import com.wukong.hezhi.utils.Base64Util;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PhotoUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuZhiyin
 * @ClassName: PersonInfoSettingActivity
 * @Description: TODO(个人信息页面)
 * @date 2017-1-10 下午3:01:22
 */
public class PersonInfoActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.person_info);
    public String pageCode = "person_info";
    /**
     * 头部布局
     */
    @ViewInject(R.id.header_ll)
    private LinearLayout header_ll;
    /**
     * 昵称
     */
    @ViewInject(R.id.nickname_ll)
    private LinearLayout nickname_ll;
    /**
     * 性别
     */
    @ViewInject(R.id.sex_ll)
    private LinearLayout sex_ll;
    /**
     * 地址
     */
    @ViewInject(R.id.address_ll)
    private LinearLayout address_ll;
    /**
     * 头像
     */
    @ViewInject(R.id.personal_iv)
    private ImageView personal_iv;
    /**
     * 性别
     */
    @ViewInject(R.id.sex_tv)
    private TextView sex_tv;
    /**
     * 昵称
     */
    @ViewInject(R.id.nickname_tv)
    private TextView nickname_tv;
    /**
     * 地址
     */
    @ViewInject(R.id.address_tv)
    private TextView address_tv;

    /**
     * 昵称
     */
    private static final int NICK_NAME = 3;
    /**
     * 地址
     */
    private static final int ADRESS = 4;
    /**
     * 头像uri
     */
    private static Uri tempUri;
    /**
     * 用户信息
     */
    private UserInfo userInfo;

    @Override
    protected boolean isNotAddTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected int layoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_personinfo_setting;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        headView.setTitleStr(ContextUtil.getString(R.string.person_info));
        initView();
    }

    public void initView() {
        tempUri = Uri.fromFile(new File(HezhiConfig.PIC_PATH, HezhiConfig.USERHEADER_IMAGE));
        userInfo = UserInfoManager.getInstance().getUserInfo();
        if (userInfo != null) {
            GlideImgUitl.glideLoader(this, userInfo.getShowImageURL(), R.drawable.icon_def_header,
                    R.drawable.icon_def_header, personal_iv, 0);

            nickname_tv.setText(userInfo.getNickName());
            sex_tv.setText(userInfo.getSex());
            address_tv.setText(userInfo.getAddress());
        }
    }

    @OnClick(value = {R.id.header_ll, R.id.nickname_ll, R.id.sex_ll, R.id.address_ll})
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_ll:
//                showChoosePicDialog();
                selectPhoto();
                break;
            case R.id.nickname_ll:
                toNicknameActivity();
                break;
            case R.id.sex_ll:
                showChooseSexDialog();
                break;
            case R.id.address_ll:
                toSettingAddressActivity();
                break;
        }
    }


    /**
     * 设置地址
     */
    private void toSettingAddressActivity() {
        Intent SettingAddressActivityIntent = new Intent(this, SettingAddressActivity.class);
        startActivityForResult(SettingAddressActivityIntent, ADRESS);
    }

    /**
     * 设置昵称
     */
    private void toNicknameActivity() {
        Intent intent = new Intent(this, NicknameActivity.class);
        intent.putExtra(Constant.Extra.NICK_NAME, nickname_tv.getText().toString().trim());
        startActivityForResult(intent, NICK_NAME);
    }

    /**
     * 显示修改头像的对话框
     */
    private void showChoosePicDialog() {
        String[] items = {ContextUtil.getString(R.string.select_album), ContextUtil.getString(R.string.photograph)};
        new CustomAlterDialog.Builder(mActivity).setTitle(ContextUtil.getString(R.string.set_header))
                .setItems(items, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:// 选择本地照片
                                selecltPhoto(PhotoManager.FROM_ALBUM);
                                break;
                            case 1:// 拍照
                                selecltPhoto(PhotoManager.FROM_CAMERA);
                                break;
                        }

                    }

                }).build().show();
    }

    private void selectPhoto() {

        MyPhotoManager.getInstance().select(this, null, new MyPhotoManager.OnSelectedMyPhotoListener() {
            @Override
            public void onSelectedPhoto(List<ImageItem> imageItems) {
                Bitmap bitmapRound =   BitmapFactory.decodeFile(imageItems.get(0).path);
                personal_iv.setImageBitmap(bitmapRound);
                saveUserInfo();
            }
        });
    }

    private void selecltPhoto(int way) {
        PhotoManager photoManager = PhotoManager.getInstance();
        photoManager.choicePhoto(mActivity, way, tempUri, new PhotoListener());
    }

    private class PhotoListener implements OnSelectedPhotoListener {
        @Override
        public void onSelectedPhoto(int way, Uri mPicFilePath, Bitmap photo) {
            // TODO Auto-generated method stub
            Bitmap bitmapRound = PhotoUtil.toRoundBitmap(photo);// 转换图片成圆形
            personal_iv.setImageBitmap(bitmapRound);
            saveUserInfo();
        }
    }

    /**
     * 显示修改性别的对话框
     */
    private CustomAlterDialog dialog = null;

    private void showChooseSexDialog() {
        String sexStr = sex_tv.getText().toString().trim();
        View view = ScreenUtil.inflate(R.layout.layout_sex_select);
        RadioGroup sex_rg = (RadioGroup) view.findViewById(R.id.sex_rg);
        if (ContextUtil.getString(R.string.female).equals(sexStr)) {
            sex_rg.check(R.id.female_rb);
        } else {
            sex_rg.check(R.id.male_rb);
        }
        sex_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.male_rb:
                        sex_tv.setText(ContextUtil.getString(R.string.male));
                        saveUserInfo();
                        break;
                    case R.id.female_rb:
                        sex_tv.setText(ContextUtil.getString(R.string.female));
                        saveUserInfo();
                        break;
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog = new CustomAlterDialog.Builder(mActivity).setView(view).build();
        dialog.show();
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        String imageBase64Str = "";
        try {
            Bitmap image = ((BitmapDrawable) personal_iv.getDrawable()).getBitmap();
            imageBase64Str = Base64Util.Bitmap2StrByBase64(image);
        } catch (Exception e) {
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", UserInfoManager.getInstance().getUserId());
        map.put("photoFile", imageBase64Str);
        map.put("nickName", nickname_tv.getText().toString().trim());
        map.put("sex", sex_tv.getText().toString().trim());
        map.put("address", address_tv.getText().toString().trim());
        HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.POST_USERINFO, map, new RequestCallBack<String>() {

            @SuppressWarnings("rawtypes")
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
                if (info != null) {
                    if (info.getHttpCode().equals(HttpCode.SUCESS)) {
                        UserInfoManager.USERINFO_CHANGE = true;// 刷新个人中心页面
                    } else if (info.getHttpCode().equals(HttpCode.FAIL)) {
                        ScreenUtil.showToast(info.getPromptMessage());
                    }
                }
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                ScreenUtil.showToast(arg1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NICK_NAME:
                    if (data != null) {
                        String nick = data.getStringExtra(Constant.Extra.NICK_NAME_RESULT);
                        nickname_tv.setText(nick);
                        saveUserInfo();
                    }
                    break;
                case ADRESS:
                    if (data != null) {
                        String address = data.getStringExtra(Constant.Extra.ADRESS_RESULT);
                        address_tv.setText(address);
                        saveUserInfo();
                    }
                    break;
            }
        }
    }
}
