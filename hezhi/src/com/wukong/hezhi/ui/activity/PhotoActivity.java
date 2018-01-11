package com.wukong.hezhi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.PhotoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.FileUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.UriUtil;

import java.io.File;

/**
 * @author Huzhiyin
 * @ClassName: PhotoActivity
 * @Description: TODO(处理选择系统相册的activity)
 * @date 2017年9月11日 上午10:26:09
 */
public class PhotoActivity extends Activity {
    /**
     * 选择本地照片
     */
    private static final int CHOOSE_PICTURE = 0;
    /**
     * 拍照
     */
    private static final int TAKE_PICTURE = 1;
    /**
     * 裁剪
     */
    private static final int CROP_BIG_PICTURE = 2;
    /**
     * 选择本地视频
     */
    private static final int CHOOSE_VIDEO = 3;
    /**
     * 头像uri
     */
    private static Uri tempUri;
    /**
     * 打开图片的方式，相册或相机
     */
    private int type;
    private int height;
    private int with;
    private int weightRatio;
    private int heightRatio;
    private boolean isTailor = true;// 是否需要裁剪

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        type = getIntent().getIntExtra("Type", PhotoManager.FROM_ALBUM);// 默认从相册打开
        tempUri = (Uri) getIntent().getParcelableExtra("Uri");
        height = getIntent().getIntExtra("height", 200);// 默认宽高200,200
        with = getIntent().getIntExtra("with", 200);
        weightRatio = getIntent().getIntExtra("weightRatio", 1);
        heightRatio = getIntent().getIntExtra("heightRatio", 1);
        if (weightRatio == heightRatio) {// 默认宽高比设置成10000:9999.趋近于正方形，防止某些手机出现圆的情况
            weightRatio = 10000;
            heightRatio = 9999;
        }
        isTailor = getIntent().getBooleanExtra("isTailor", true);// 默认需要裁剪
        switch (type) {
            case PhotoManager.FROM_ALBUM:
                selectLocalPhoto();
                break;
            case PhotoManager.FROM_CAMERA:
                takePhoto();
                break;
            case PhotoManager.FROM_VIDEO:
                selectLocalvideo();
                break;
        }
    }

    /**
     * 打开系统相册
     */
    private void selectLocalPhoto() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }

    /**
     * 打开系统视频
     */
    private void selectLocalvideo() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
        openAlbumIntent.setType("video/*");
        startActivityForResult(openAlbumIntent, CHOOSE_VIDEO);
    }

    /**
     * 打开系统相机
     */
    private void takePhoto() {
        if (!PermissionUtil.cameraPermission()) {
            ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
            finish();
            ;
        }

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    /**
     * 裁剪图片方法实现
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", "true");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("aspectX", weightRatio);
        intent.putExtra("aspectY", heightRatio);
        intent.putExtra("outputX", with);
        intent.putExtra("outputY", height);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CROP_BIG_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE: // 拍照
                    if (tempUri != null) {
                        if (isTailor) {
                            startPhotoZoom(tempUri);
                        } else {
                            Bitmap photo = FileUtil.decodeUriAsBitmap(tempUri);
                            // FileUtil.updateAlbum(tempUri); // 更新图库
                            PhotoManager.getInstance().getSelectedPhoto(photo, tempUri);
                            finish();
                        }

                    } else {
                        ScreenUtil.showToast(ContextUtil.getString(R.string.photo_get_fail));
                        finish();
                    }
                    break;
                case CHOOSE_PICTURE: // 选择照片

                    if (data == null) {
                        ScreenUtil.showToast(ContextUtil.getString(R.string.photo_get_fail));
                        finish();
                    }
                    if (isTailor) {// 需要裁剪
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())) {
                            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                                    null, null, null);
                            if (null == cursor) {
                                ScreenUtil.showToast(ContextUtil.getString(R.string.photo_get_fail));
                                finish();
                                return;
                            }
                            cursor.moveToFirst();
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                            startPhotoZoom(Uri.fromFile(new File(path)));
                        } else {
                            String path = uri.getPath();
                            startPhotoZoom(Uri.fromFile(new File(path)));
                        }
                    } else {// 不需要裁剪
                        Bitmap photo = FileUtil.decodeUriAsBitmap(data.getData());
                        PhotoManager.getInstance().getSelectedPhoto(photo, data.getData());
                        finish();
                    }

                    break;
                case CROP_BIG_PICTURE:// 裁剪
                    if (tempUri != null && data != null) {
                        Bitmap photo = FileUtil.decodeUriAsBitmap(tempUri);
                        // FileUtil.updateAlbum(tempUri); // 更新图库
                        PhotoManager.getInstance().getSelectedPhoto(photo, data.getData());
                        finish();
                    } else {
                        ScreenUtil.showToast(ContextUtil.getString(R.string.photo_crop_fail));
                        finish();
                    }
                    break;

                case CHOOSE_VIDEO:// 选择本地视频
                    if (data == null) {
                        ScreenUtil.showToast(ContextUtil.getString(R.string.video_get_fail));
                        finish();
                    }
                    Uri uri = data.getData();
                    String path = UriUtil.getImageAbsolutePath(uri);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(path);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    int time = Integer.parseInt(duration);// 视频的时长
                    long size = FileUtil.getFileSize(path);// 视频的大小

                    if (!(path.endsWith(".mp4") || path.endsWith(".mov") || path.endsWith(".mkv") || path.endsWith(".MP4")
                            || path.endsWith(".MOV") || path.endsWith(".MKV"))) {// 支持这三种类型视频
                        ScreenUtil.showToast(ContextUtil.getString(R.string.no_format));
                    } else if (time > HezhiConfig.FIVE_MINUTES) {// 不能大于五分钟
                        ScreenUtil.showToast(ContextUtil.getString(R.string.five_minute));
                    } else if (size > HezhiConfig.TWENTY_M) {// 不能大于20M
                        ScreenUtil.showToast(ContextUtil.getString(R.string.little_video));
                    } else {
                        PhotoManager.getInstance().getSelectedVideo(uri);
                    }
                    finish();
                    break;
            }
        } else {
            finish();
        }
    }
}
