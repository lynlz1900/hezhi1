package com.wukong.hezhi.constants;

import android.os.Environment;

/**
 * 
 * @ClassName: HezhiConfig
 * @Description: TODO(配置文件类)
 * @author HuZhiyin
 * @date 2016-12-30 下午4:33:49
 * 
 */
public interface HezhiConfig {
	/** 打印log日志及debug调节模式开关 */
	boolean DEBUG = true;

	/** 数据库版本 */
	int DB_VERSION = 2;

	/** 后台数据库版本 */
	int SERVER_VERSION = 1;

	/** 通用全局配置文件名字 */
	String SP_COMMON_CONFIG = "com.wukong.hezhi_maotai.config";

	/** 盒知logo地址 */
	String HEZHI_LOGO_URL = "http://hezhicloud.oss-cn-shenzhen.aliyuncs.com/image_final/icon_hezhi_logo.png";

	/** 盒知官网 */
	String HEZHI_URL = "http://www.pkgiot.com";

	/** 项目文件路径 */
	String HEZHI_PATH = Environment.getExternalStorageDirectory() + "/hezhi/";

	/** log日志路径 */
	String LOG_PATH = Environment.getExternalStorageDirectory() + "/hezhi/log/";

	/** 图片路径 */
	String PIC_PATH = Environment.getExternalStorageDirectory() + "/hezhi/pic/";

	/** 图片路径 */
	String PIC_THUMBNAIL = Environment.getExternalStorageDirectory() + "/hezhi/thumbnail/";

	/** 视频路径 */
	String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/hezhi/video/";

	/** 截图路径 */
	String SCREENSHOT_PATH = Environment.getExternalStorageDirectory() + "/hezhi/screenshot/";

	/** 日志压缩文件zip */
	String LOG_FILE_ZIP = Environment.getExternalStorageDirectory() + "/hezhi/logfile.zip";

	/** 盒知apk */
	String FILE_APK = Environment.getExternalStorageDirectory() + "/hezhi/hezhi.apk";

	/** 城市json */
	String CITIES_JSON_PATH = "ChineseCities/ChineseCities.txt";

	/** 定制图片文件名 */
	String APPRAISE_UPLOAD_IMAGE = "appraiseImage.zip";
	
	/** 定制图片文件名 */
	String CUSTOM_IMG_NAME = "customImage.png";

	/** 定制后的模板文件名 */
	String CUSTOM_TEMPLET_IMG_NAME = "templetCustomImage.png";

	/** 定图图片裁剪图片文件名 ，此图片文件名必须以jpg结尾，否则在三星手机上拍照后截图会出现横屏的现象 */
	String TAILOR_CUSTOM_IMG_NAME = "tailorCustomImage.jpg";

	/** 定图上传图片裁剪图片文件名 */
	String UPLOAD_CUSTOM_IMG_NAME = "uploadCustomImage.png";

	/** 定图上传图片裁剪图片文件名 */
	String UP_PHOTO = "upPhoto.png";

	/** 定制后的模板文件名 */
	String CUSTOM_TEMPLET_IMG_NAME2 = "templetCustomImage2.png";

	/** unity视频封面 */
	String UNITY_VIDEO_IMG_NAME = "unityVideoImg.png";

	/** 用户头像 ，此图片文件名必须以jpg结尾，否则在三星手机上拍照后截图会出现横屏的现象 */
	String USERHEADER_IMAGE = "userHeaderImage.jpg";

	/** 二维码 */
	String HEZHI_QR_CODE = "hezhi_qr_code.png";

	/** 茅台醇的标签授权码 */
	String MAITAO_AUTHCODE = "17022122M3dx";

	/** 红包弹出延迟时间 */
	int REDBAG_DELAY_TIME = 5000;
	
	/**5分钟*/
	long FIVE_MINUTES = 5 * 60 * 1000;
	
	/**20兆*/
	long TWENTY_M = 20 * 1024 * 1024;
	
	/**0.5兆*/
	long ZERO_POINT_FIVE_M =512 * 1024;
}
