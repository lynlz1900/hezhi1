package com.wukong.hezhi.manager;

import android.app.Activity;

import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.activity.WebViewActivity;

public class TestManager {
    private TestManager() {
    }

    private static class Holder {
        private static final TestManager SINGLETON = new TestManager();
    }

    public static TestManager getInstance() {
        return Holder.SINGLETON;
    }

    public void test(Activity activity) {

//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("userId", UserInfoManager.getInstance().getUserId());
//        map.put("page", 1);
//        HttpController.post(HttpURL.URL1 + HttpURL.MY_CUST, map, new HttpController.IOnResponseCallback<String>() {
//            @Override
//            public void onSucess(String msg) {
//                ScreenUtil.showToast(msg);
//            }
//            @Override
//            public void onFail(String msg) {
//                ScreenUtil.showToast(msg);
//            }
//        });
//        HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.MY_CUST, map, new RequestCallBack<String>() {
//            @Override
//            public void onFailure(HttpException arg0, String arg1) {
//                // TODO Auto-generated method stub
//            }
//
//            @SuppressWarnings({"unchecked" })
//            @Override
//            public void onSuccess(ResponseInfo<String> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });

//    	JumpActivityManager.getInstance().toActivity(ActivitiesManager.getInstance().currentActivity(), LockBLEActivity.class);
//        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new GlideImageLoader());//图片加载器
//        imagePicker.setSelectLimit(9);//设置可以选择几张
//        imagePicker.setMultiMode(true);//是否为多选
//        imagePicker.setShowCamera(true);//是否显示摄像
//        MyPhotoManager.getInstance().select(activity, imagePicker, new MyPhotoManager.OnSelectedMyPhotoListener() {
//            @Override
//            public void onSelectedPhoto(final List<ImageItem> imageItems) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<String> images = new ArrayList<String>();
//                        for (int i = 0; i < imageItems.size(); i++) {
//                            images.add(imageItems.get(i).path);
//                        }
//                        List<String> imagesCompress = ThumbUtil.compress(images);
//                        ScreenUtil.showToast(imagesCompress.toString());
//                    }
//                }).start();
//            }
//        });
        String url = "http://app.xuanke3d.com/apps/winebottle/";
        JumpActivityManager.getInstance().toActivity(activity,WebViewActivity.class, Constant.Extra.WEB_URL,url);
    }

}
