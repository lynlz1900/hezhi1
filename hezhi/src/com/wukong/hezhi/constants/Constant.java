package com.wukong.hezhi.constants;

import com.wukong.hezhi.bean.CommoditySceneInfo;
import com.wukong.hezhi.bean.TabInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: Constant
 * @Description: TODO(常量类)
 * @author HuZhiyin
 * @date 2016-9-14 下午2:23:28
 * 
 */
public class Constant {

	/**
	 * activity跳转传值，备注：需要类的全名+逻辑名，例如com.wukong.cloud_nfc.activity.MainActivity.
	 */
	public interface Extra {
		String MAINACTIVITY_RESULT = "com.wukong.cloud_nfc.activity.MainActivity.result";
		String MAINACTIVITY_UID = "com.wukong.cloud_nfc.activity.MainActivity.uid";
		String PVRESULTACTIVITY_PRODUCTINFO = "com.wukong.hezhi_maotai.activity.PVResultActivity.productInfo";
		String VIDEO_URL = "com.wukong.hezhi.vuforia.CloudReconition.CloudRecoRenderer.url";
		String NICK_NAME = "com.wukong.hezhi.ui.activity.PersonInfoActivity.nickname";
		String NICK_NAME_RESULT = "com.wukong.hezhi.ui.activity.NicknameActivity.nicknameresult";
		String ADRESS_RESULT = "com.wukong.hezhi.ui.activity.PersonInfoActivity.addressresult";
		String QQ_CONECTSERVER = "com.wukong.hezhi.ui.activity.LoginActivity.qqConectServer";
		String QQ_USER_INFO = "com.wukong.hezhi.ui.activity.QQActivity.qqUserInfo";
		String WEB_URL = "com.wukong.hezhi.ui.activity.UnityPlayerActivity.URL";
		String TEMP_URL = "com.wukong.hezhi.ui.activity.UnityPlayerActivity.tempurl";
		String UNITY_INFO = "com.wukong.hezhi.adapter.UnityRecordAdapter.unityInfo";
		String REDBAG_INFO = "com.wukong.hezhi.manager.RedBagManager.redbagInfo";
		String WALLET = "com.wukong.hezhi.ui.activity.MyWalletActivity.wallet";
		String ISFROMREDBAG = "com.wukong.hezhi.manager.RedBagNfcManager.isfromredbag";
		String MSG_DETAIL = "com.wukong.hezhi.adapter.MsgCenterActivityAdapter.msgDetail";
//		String ISFROMUINTY = "com.wukong.hezhi.manager.RedBagNfcManager.isfromunity";
		String JUMP_URL = "com.wukong.hezhi.manager.AdGlobalManager.jumpurl";
		String VUFORIA = "com.wukong.hezhi.adapter.LiveFragmentAdapter.vuforia";
		String PPID = "com.wukong.hezhi.ui.activity.CommentActivity.ppid";
		String BUSINESS_ID = "com.wukong.hezhi.ui.activity.BusinessAcitivty.business";
		String ARTICLE = "com.wukong.hezhi.ui.activity.ArticleActivity.articleid";
		String WEBVIEW_TITLE = "com.wukong.hezhi.ui.activity.WebViewActivity.title";
		String ATRICLE_ID = "com.wukong.hezhi.ui.segment.ArticleEditCommentFragment.atictleId";
		String ARTICLE_TAB_INFO = "com.wukong.hezhi.ui.fragment.FindFragment.info";
		String CHANGE_PHONE = "com.wukong.hezhi.ui.activity.ChangePhoneActivity.changephone";
		String FROM_ACTIVITY = "com.wukong.hezhi.ui.activity.FromActivity";
		String SHARE_INFO = "com.wukong.hezhi.ui.activity.QQActivity.shareInfo";
		String ORDER_INFO = "com.wukong.hezhi.ui.activity.MyCustomizationOrderAdapter.orderInfo";
		String IMAGE = "com.wukong.hezhi.ui.activity.CommodityTailorActivity.image";
		String CUSTOMIZATION_INFO = "com.wukong.hezhi.ui.activity.MyCustomizationAdapter.custInfo";
		String CUSTOMIZATION_DETAIL = "com.wukong.hezhi.ui.activity.MyCustomizationTobuyActivity.custInfo";
		String ORDER_INFO_CANCEL = "com.wukong.hezhi.ui.activity.OrderCustDetailActivity.orderInfoCancel";
		String ADDR_INFO = "com.wukong.hezhi.ui.activity.AddressReceiptAdapter.addressInfo";
		String ORDER_INFO_PAY = "com.wukong.hezhi.ui.activity.OrderCustDetailActivity.orderInfoPay";
		String ORDER_INFO_Buy = "com.wukong.hezhi.ui.activity.OrderCustDetailActivity.orderInfoBuy";
		String PAY_RESULT = "com.wukong.hezhi.ui.activity.OrderPayActivity.payResult";
		String COMMDITYINFO = "com.wukong.hezhi.ui.activity.CommodityActivity.CommdityInfo";
		String MAILING_ADDRESS = "com.wukong.hezhi.ui.activity.SettingAddressActivity.mailingAddr";
		String ISFROM_COMMODITYPRE = "com.wukong.hezhi.ui.activity.CommodityPreActivity";
		String ISFROM_ORDERCONFIRM = "com.wukong.hezhi.ui.activity.OrderConfirmActivity";
		String PRODUCTID = "com.wukong.hezhi.ui.activity.CommodityDetailActivity";
		// String DISRESULT_ISFROM =
		// "com.wukong.hezhi.ui.activity.DisResultActivity";
		String CUSTOMUPLOADPICINFO = "com.wukong.hezhi.ui.activity.CommodityCustomImageActivity";
		String ISFROM_ADDRESSMANAGE = "com.wukong.hezhi.ui.activity.AddressManageActivity";
		String EXPRESS_COMPANY = "com.wukong.hezhi.ui.activity.ExpressViewActivity.expressCompany";
		String EXPRESS_NUMBER = "com.wukong.hezhi.ui.activity.ExpressViewActivity.expressNumber";
		String VIDEO_PATH = "com.wukong.hezhi.ui.activity.UploadActivity.videoPath";
		String PIC_PATH_LIST = "com.wukong.hezhi.ui.activity.UploadActivity.picPathList";
		String ORDER_NO = "com.wukong.hezhi.ui.activity.OrderPayActivity.orderNum";
		String GOTO_PAY = "com.wukong.hezhi.ui.activity.OrderCustDetailActivity.gotoPay";
		String GOTO_PAY_AGAIN = "com.wukong.hezhi.ui.activity.OrderCustDetailActivity.gotoPayAgain";
		String UNITY_VIDEO_PATH = "com.wukong.hezhi.ui.activity.UnityPlayerActivity.videoPath";
		String UNITY_PPID = "com.wukong.hezhi.ui.activity.UnityPlayerActivity.ppid";
		String SCENE_ID = "com.wukong.hezhi.ui.activity.CustomizationFragment.sceneId";
		String PIC_PATH="com.wukong.hezhi.ui.activity.ComposeActivity.picPath";
		String TEMPLATE="com.wukong.hezhi.ui.activity.CustomTemplateActivity.template";
		String MONEY = "com.wukong.hezhi.ui.activity.GetCrashActivity.money";
		String ISQRBLEACTIVITY = "com.wukong.hezhi.ui.activity.QrActivity.qrLock";
		String ORDERSTATE = "com.wukong.hezhi.ui.fragment.PersonalFragment.orderState";
		String APPRAISE_CENTER_INFO = "com.wukong.hezhi.ui.fragment.AppraiseCenterActivity.appraiseCenterInfo";
	}

	/**
	 * 配置文件key
	 */
	public interface Sp {
		String ADDRESS_RECEIPT = "com.wukong.hezhi.adapter.AddressReceiptAdapter.address";
		String USER_ID = "com.wukong.hezhi.manager.UserInfoManager.userId";
		String FIRST = "com.wukong.hezhi.ui.activity.SplashActivity.first";
		String DIRECTION = "direction";
		String WXUSERINFO = "com.wukong.hezhi.wxapi.WXEntryActivity.wxuserinfo";
		String QQUSERINFO = "com.wukong.hezhi.ui.activity.QQActivity.qquserinfo";
		String LOGINUSERINFO = "com.wukong.hezhi.ui.activity.LoginActivity.loginuserinfo";
		String USER_INFOs = "com.wukong.hezhi.ui.activity.PersonInfoActivity.userinfo";
		String URL = "url";
		String PORT = "port";
		String SERVICE_URL = "service.url";
		String URL1 = "url1";
		String PORT1 = "port1";
		String SERVICE_URL1 = "service.url1";
		String REP_POINT = "com.wukong.hezhi.ui.activity.MainActivity.redpoint";
		String TAB_INFO = "com.wukong.hezhi.ui.activity.SplashActivity.tabinfo";
		String ATICLE_TAB_INFO = "com.wukong.hezhi.ui.activity.SplashActivity.articletabinfo";
		String HEZHIFRAGMENTFIRST = "com.wukong.hezhi.ui.fragment.HezhiFragment.HezhiFragmentFirst";
		String LIVEFRAGMENTFIRST = "com.wukong.hezhi.ui.fragment.LiveFragment.LiveFragmentFirst";
		String PERSONALFRAGMENTFIRST = "com.wukong.hezhi.ui.fragment.PersonalFragment.PersonalFragmentFirst";
	}

	/**
	 * 物流相关
	 */
	public interface LOGISTICS {
		int INSTORAGE = 0;// 入库
		int OUTSTORAGE = 1;// 出库
		int CHECK = 2;// 盘点
		int QURREY = 3;// 查询
		int SALE = 4;// 销售
		int STOWAGE = 3;// 装车
		int UNLOADING = 4;// 卸货
		int WHOLESALE = 5;// 批发
	}

	/** 广告的位置 */
	public enum AdLocation {
		REDPACK_NFC, // nfc红包广告
		REDPACK_PERCEPTION, // 感知应用红包广告
		REDPACK_CUSTOMIZE, // 定制产品广告
		SPLASH_SCREEN, // 插屏广告
		VIDEOPAGE_PERCEPTION, // 视频页广告
		CUSTOMIZATION, // 定制页广告
	}

	/** 广告内容的类型 */
	public enum AdContentType {
		VEDIO, PICTURE, LINK, WORD
	}

	/** 激活状态 */
	public enum TagAcType {
		ACTIVATED, // 已经激活
		NONACTIVATED// 未激活
	}

	/** 感知应用类型 */
	public enum GanzType {
		MP4, UNITY3D, PICTURE, URL, PDF
	}

	/** 订单状态 */
	public enum OrderType {
		// 待付款，待发货，待收货，已取消，已收货
		OBLIGATIONS, UNDELIVERED, UNRECEIVED, CANCELED, RECEIVED
	}

	/** 分享类型 */
	public enum SharedType {
		// 微信，QQ，QQ空间，朋友圈
		SHARE_WECHAT, SHARE_WECHATCIRCLE, SHARE_QQ, SHARE_QZONE
	}

	/** 订单取消原因 */
	public enum OrderCancelReason {
		// 不想买，价格贵，付款困难，收货信息错误，发票信息错误，其他原因
		BUY_NOT, PRICE_EXPENSIVE, PAY_DIFFICULT, RECEIVE_ERROR, INVOICE_ERROR, OTHER
	}

	public static class isFromPage{
		/** 是否来自定制订单搜索界面 */
		public static boolean isOrderListSearch = false;

		/** 是否来自定制订单详情界面 */
		public static boolean isOrderDetail = false;
		
		/** 是否来自定制订单界面 */
		public static boolean isOrderList = false;
		
		/** 是否来自定制订单详情界面跳转到商品详情界面 */
		public static boolean isOrderDetailToCommDetail = false;
	}

	/** 登录的几种状态 */
	public interface loginType {
		int LOGIN_ONLINE = 1;// 正常在线
		int LOGIN_OVER = 2;// 登录过期
		int LOGIN_PUSH = 3;// 被挤下线
	}

	/** NFC相关设置 */
	public static class NFC {
		/** 标签是否断开的标识,0断开，1未断开 */
		public static int IS_BREAK = -1;
		/** NFC开关,若为false。可以感应nfc但不做处理 */
		public static boolean NFC_ON = true;
		/** 显示产品溯源，还是产品信息的开关 */
		public static boolean IS_TRACE = true;
		/** 蓝牙开关 */
		public static boolean IS_BLE_NFC = true;
	}

	/** 品牌直播tab信息 */
	public static List<TabInfo> TABINFOS = new ArrayList<TabInfo>();

	public static class Commodity {
		public static int BUY = 0;// 直接购买
		public static int TAILOR = 1;// 定制
	}

	public static class customImage {
		public static int HEIGHT = 500;// 高度
		public static int WITH = 150;// 宽度
	}

//	/** 视频定制图片 */
//	public static class VideoCustomImage {
//		public static  int IMG_NFC_W = 900;// 视频定制图片的的宽度
//	}

	/** 跳转activity标记 */
	public interface JumpToAc {
		/** 选择定制的商品界面标记 */
		String ACTIVITY_COMMODITY = "CommodityActivity";
		/** 我的定制订单标记 */
		String ACTIVITY_ORDERCUST = "OrderCustActivity";
	}
	
	/** 定制场景管理 */
	public static class customScene{
		
		/** 是否来自定制场景 */
		public static boolean isFromCustomScene = false;
		
		/** 定制场景id */
		public static int customSceneId = -1;
		
		/** 定制场景id */
		public static CommoditySceneInfo customSceneInfo = null;
	}
}
