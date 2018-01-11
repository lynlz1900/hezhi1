package com.wukong.hezhi.http;

import com.wukong.hezhi.constants.HezhiConfig;

public class HttpURL {
    /**
     * 导航服务器
     */
    public static String URL;
    /**
     * 盒知传媒服务器
     */
    public static String URL1;

    static {
        if (HezhiConfig.DEBUG) {
            URL = "https://test.wukongvision.com";
            URL1 = "https://test.wukongvision.com:8086";
        } else {
            URL = "https://cloud.wukongvision.com";
            URL1 = "https://media.pkgiot.com";
        }
    }

    /**
     * 翟晋慰电脑
     */
    public static String URL_ZHAI = "http://172.17.10.147:9999";
    /**
     * 赵才龙电脑
     */
    public static String URL_ZHAO = "http://172.17.10.197:200";
    /**
     * 张晓电脑
     */
    public static String URL_ZHANG = "http://172.17.10.196:9090";
    /**
     * 马旭电脑
     */
    public static String URL_MA = "https://172.17.10.201:9898";
    /**
     * 李志明电脑
     */
    public static String URL_LI = "https://172.17.10.131:8080";
    /**
     * 8080端口测试服务器
     */
    public static String URL_8080 = "https://test.wukongvision.com:8080";
    /**
     * 产品信息查询
     */
    public static String QUERY_PRODUCT = "/remoteQuery/queryMaterialInfo?materialNo=";
    /**
     * 物流信息查询
     */
    public static String QUERY_LOGISTICS = "/remoteQuery/queryMaterialFlowInfo?materialNo=";
    /**
     * 版本升级
     */
    public static String UP_VISION = "/softwareVersion/getNewestSoftwareVersion?appSystemType=Android&applicationName=HEZHI.APK";
    /**
     * APK下载地址
     */
    public static String APK_DOWN = "/softwareVersion/downloadNewestSoftwareVersion?appSystemType=Android&applicationName=HEZHI.APK";
    /**
     * 授权密匙(Key)
     */
    public static String KUAI_DI_100_KEY = "f0ddcc6cc7964fb8";
    /**
     * 获取验证码
     */
    public static String CHECK_CODE = "/remoteMobile/getSmsCheckCodeByPhone";
    /**
     * 登录
     */
    public static String LOGIN = "/remoteMobile/login";
    /**
     * 退出登录
     */
    public static String LOGOUT = "/remoteMobile/loginOut";
    public static String GET_USERINFO = "/remoteMobile/getMemberInfoById";
    /**
     * 修改用户信息
     */
    public static String POST_USERINFO = "/remoteMobile/updateMemberInfo";
    /**
     * 第三方登录
     */
    public static String THIRDPARTYLOGIN = "/remoteMobile/thirdPartyLogin";
    /**
     * 绑定微信
     */
    public static String BIND_WECHAT = "/remoteMobile/bindWechat";
    /**
     * 绑定微博
     */
    public static String BIND_WEIBO = "/remoteMobile/bindWeibo";
    /**
     * 绑定qq
     */
    public static String BIND_QQ = "/remoteMobile/bindQQ";
    /**
     * 绑定手机
     */
    public static String BIND_PHONE = "/remoteMobile/bindPhone";
    /**
     * 更改手机
     */
    public static String CHANGE_PHONE = "/remoteMobile/changePhone";
    /**
     * （绑定手机号）获取验证码
     */
    public static String CHECK_CODE_BINDPHONE = "/remoteMobile/getCheckCodeForPhoneBinding";
    /**
     * （更换手机号）获取验证码
     */
    public static String CHANGE_CODE_BINDPHONE = "/remoteMobile/getCheckCodeForPhoneChange";
    /**
     * 品牌直播
     */
    public static String LIVE_BAND = "/remoteMobile/bandliveList?";
    /**
     * 品牌直播   修改为post方式
     */
    public static String LIVE_BAND_NEW = "/remoteMobile/bandliveListNew";
    /**
     * 我的动态
     */
    public static String MY_DYNAMIC = "/remoteMobile/receiveCashRecords?";
    /**
     * 检查感知应用是否有红包活动
     */
    public static String CHECK_ACTIVITY_GANZ = "/remoteMobile/checkActivityByPPId?";
    /**
     * 检查感知应用是否领取过红包
     */
    public static String CHECK_RECEIVED_GANZ = "/remoteMobile/perceptionPictureCheckMemberHasRedPack";
    /**
     * 感知应用发红包
     */
    public static String SEND_REDBAG_GANZHI = "/remoteMobile/perceptionPictureSendRedPack";
    /**
     * 检查nfc是否有红包活动
     */
    public static String CHECK_ACTIVITY_NFC = "/remoteMobile/checkActivityByNFC?";
    /**
     * 检查NFC是否领取过红包
     */
    public static String CHECK_RECEIVED_NFC = "/remoteMobile/traceabilityCheckMemberHasRedPack";
    /**
     * NFC发红包
     */
    public static String SEND_REDBAG_NFC = "/remoteMobile/traceabilitySendRedPack";
    /**
     * 提现
     */
    public static String GET_CASH = "/remoteMobile/getCash";
    /**
     * 余额
     */
    public static String BALANCE = "/remoteMobile/getAmount";
    /**
     * 通过感知应用、广告位置类型和广告位置获取广告内容
     */
    public static String AD_GANZ = "/remoteMobile/queryAdvertisementContentByPKGIOT";
    /**
     * 通过公司授权码、产品类型、产品Id、广告位置类型和广告位置获取广告内容
     */
    public static String AD_NFC = "/remoteMobile/queryAdvertisementContentByTRACESOURCE";
    /**
     * 通过广告位置类型和广告位置获取广告内容
     */
    public static String AD_GLOBAL = "/remoteMobile/queryAdvertisementContentByLocation";
    /**
     * 获取感知资源
     */
    public static String GANZ_RESOURCE = "/remoteMobile/getResource?";
    /**
     * 获取广告
     */
    public static String AD = "/remoteMobile/getAd";
    /**
     * 获取产品列表信息
     */
    public static String TABADATA = "/remoteMobile/bandliveListType";
    /**
     * 获取商品评论列表
     */
    public static String GET_COMMENT = "/remoteMobile/getCommentList";
    /**
     * 评论感知应用
     */
    public static String COMMENTPERCEPTION = "/remoteMobile/commentPerception";
    /**
     * 点赞 或取消点赞
     */
    public static String PRAISE = "/remoteMobile/thumbsUpPerception";
    /**
     * 获取感知资源
     */
    public static String GANZ_RESOURCE2 = "/remoteMobile/getResource2";
    /**
     * 关注
     */
    public static String GUANZHU = "/remoteMobile/loveBusiness";
    /**
     * 查看关注的商家
     */
    public static String GUANZHU_BUSINESS = "/remoteMobile/myFocus";
    /**
     * 查看全部关注
     */
    public static String MY_FOUCUS = "/remoteMobile/myFocusList";
    /**
     * 商户信息
     */
    public static String BUSINESS = "/remoteMobile/merchantInfo";
    /**
     * 查询文章菜单
     */
    public static String ARTICLE_MENU = "/remoteMobile/article/getArticleMenus";
    /**
     * 查询文章菜单的文章
     */
    public static String ARTICLE = "/remoteMobile/article/articleMenu/getArticles";
    /**
     * 搜索文章
     */
    public static String SEARCH_ARTICLE = "/remoteMobile/article/search";
    /**
     * 搜索收藏文章
     */
    public static String QUERYMYCOLLECTIONBYKEYWORDS = "/remoteMobile/article/queryMyCollectionByKeyWords";
    /**
     * 查询文章附属信息
     */
    public static String ARTICLE_APPENDINFO = "/remoteMobile/article/getAppendInfo";
    /**
     * 文章点赞收藏等操作
     */
    public static String ATRICLE_OPERATION = "/remoteMobile/article/operation";
    /**
     * 评论文章
     */
    public static String COMMENT_ARTICLE = "/remoteMobile/article/comment";
    /**
     * 查询文章评论
     */
    public static String SEARCHE_COMMENT = "/remoteMobile/article/getComments";
    /**
     * 我收藏的文章
     */
    public static String MY_COLLECT = "/remoteMobile/article/getCollectionRecord";
    /**
     * 意见反馈
     */
    public static String FEED_BACK = "/remoteMobile/commentFeedbackApp";
    /**
     * 用户协议
     */
    public static String PROTOCAL = "/html/UserAgreement/01/word.html";
    /**
     * 分享
     */
    public static String Share = "/remoteMobile/sharePerception?";
    public static String RECORDAPPLOG = "/remoteMobile/recordAppLog";
    /**
     * 文章是否存在
     */
    public static String HAS_ARTICLE = "/remoteMobile/article/queryArticleIsExists";
    /**
     * 分享文章
     */
    public static String SHARE_ARTICLE = "/remoteMobile/article/shareArticle";
    /*** H5视频分享 */
    public static String SHARE_H5 = "/h5/sharevideo/video.html";
    /**
     * H5产品分享定制
     */
    public static String SHARE_CUSTOM = "/h5/sharevideo/product.html";
    /**
     * 统计页面
     */
    public static String STATISTICS_PAGE = "/remoteMobile/recordHezhiAppLog";
    /**
     * 支付宝支付
     */
    public static String PAY_ALI = "/remoteMobile/pay/alipay";
    /**
     * 微信支付
     */
    public static String PAY_WX = "/remoteMobile/pay/weixinpay";
    /**
     * 收货地址列表
     */
    public static String ADDRESS_LIST = "/remoteMobile/listGetGoodsAddress";
    /**
     * 收货地址新增
     */
    public static String ADDRESS_ADD = "/remoteMobile/saveGetGoodsAddress";
    /**
     * 收货地址编辑
     */
    public static String ADDRESS_EDIT = "/remoteMobile/updateGetGoodsAddress";
    /**
     * 收货地址删除
     */
    public static String ADDRESS_DELETE = "/remoteMobile/deleteGetGoodsAddress";
    /**
     * 生成订单
     */
    public static String SUBMINT_ORDER = "/remoteMobile/saveOrders";
    /**
     * 定制保存
     */
    public static String SAVE_CUSTOM = "/remoteMobile/saveCustomOrders";
    /**
     * 定制保存
     */
    public static String SAVE_CUSTOM_NEW = "/remoteMobile/saveCustomOrdersNew";
    /**
     * 定制订单列表
     */
    public static String ORDER_LIST = "/remoteMobile/listOrders";
    /**
     * 定制订单删除
     */
    public static String ORDER_DEL = "/remoteMobile/deleteOrders";
    /**
     * 定制订单确认收货
     */
    public static String ORDER_RECEIVED = "/remoteMobile/confirmGetOrders";
    /**
     * 定制订单取消
     */
    public static String ORDER_CANCEL = "/remoteMobile/cancelOrders";
    /**
     * 定制订单搜索列表
     */
    public static String ORDER_SEARCH = "/remoteMobile/searchOrders";
    /**
     * 我的定制列表
     */
    public static String MY_CUST = "/remoteMobile/listCustomOrders";
    /**
     * 我的定制删除
     */
    public static String MY_CUST_DEL = "/remoteMobile/deleteCustomOrders";
    /**
     * 查看默认地址
     */
    public static String DEFAULT_ADDRESS = "/remoteMobile/getDefaultAddress";
    /**
     * 定制模板查询
     */
    public static String TEMPLETS = "/remoteMobile/listCustomTemplets";
    /**
     * 商品列表
     */
    public static String COMMODITY_LIST = "/remoteMobile/customizeInfo/getCustomizeInfoList";

    /**
     * 商品详细信息
     */
    public static String COMMODITY_DETAIL = "/remoteMobile/customizeInfo/getCustomizeDetail";
    /**
     * 检查订单是否有红包
     */
    public static String ORDER_REDBAG_CHECK = "/remoteMobile/checkOrderActivity";
    /**
     * 订单发红包
     */
    public static String ORDER_REDBAG = "/remoteMobile/orderProductSendRedPack";
    /**
     * 鉴真结果界面判断是否有定制
     */
    public static String GET_CUSTOMIZEFLAG = "/remoteMobile/customizeInfo/getCustomizeByAuthCodeAndProductId";
    /**
     * 关于我们
     */
    public static String ABOUTUS = "/h5/sharevideo/aboutus.html";
    /**
     * 功能介绍
     */
    public static String FUNCTION = "/h5/sharevideo/function.html";
    /**
     * 检测会员身份
     */
    public static String CHECKIDENTITY = "/remoteMobile/customizeInfo/checkMemberIdentity";
    /**
     * 检测员工是否购买过20周年产品
     */
    public static String CHECKISBOUGHT = "/remoteMobile/checkIsBought";
    /**
     * 保存周年庆定制产品
     */
    public static String SAVECUSTOMIZEPRODUCT = "/remoteMobile/customizeInfo/saveCustomizeProduct";
    /**
     * 上传视频及照片信息
     */
    public static String UPLOAD = "/remoteMobile/fileService/nfcCustomUpload";
    /**
     * 保存nfc视频定制接口
     */
    public static String SAVE_NFC = "/remoteMobile/customizeInfo/saveNfcCustomInfo";
    /**
     * 获取订单详情
     */
    public static String ORDER_DETAIL_GET = "/remoteMobile/searchOrderByOrderNo";
    /**
     * 定制信息
     */
    public static String NFC_CUSTOM = "/h5/Order/second.html";
    /**
     * 订单物流信息查看
     */
    public static String ORDER_TRACE_SEARCH = "/remoteMobile/searchOrderTraces";
    /**
     * 感知应用AR录制的视频上传和分享接口
     */
    public static String UPLOAD_RECORD_VIDEO = "/remoteMobile/uploadRecordVideo";
    /**
     * 定制场景信息接口
     */
    public static String CUSTOM_SCENE = "/remoteMobile/listCustomizationCases";
    /**
     * 转盘抽奖H5
     */
    public static String PRIZE_ZP = "/h5/Luckdraw/shareindex.html";
    /**
     * 扫码开锁验证
     */
    public static String SCANLOCKCODE = "/remoteAuthenticLock/scanLockCode";
    /**
     * 扫码开锁短信验证
     */
    public static String SMSOPENLOCK = "/remoteAuthenticLock/smsOpenLock";
    /**
     * 扫码开锁获取短信
     */
    public static String GETVERIFICATIONCODEBYPHONE = "/remoteMobile/getVerificationCodeByPhone";
    /**
     * 扫码开锁结果上报
     */
    public static String REPORTLOCKSTATUS = "/remoteAuthenticLock/reportLockStatus";
    /**
     * 获取订单待付款、待收货、待评价总数
     */
    public static String QUERY_ORDEREACH_TOTAL = "/remoteMobile/queryOrderEachTotal";
    /**
     * 评价中心
     */
    public static String APPRAISE_CENTER = "/remoteMobile/listCommentCentre";
    /**
     * 评价晒单
     */
    public static String APPRAISE_SHOW = "/remoteMobile/saveProductComment";
    /**
     * 追加评价
     */
    public static String APPRAISE_ADD = "/remoteMobile/addProductComment";
    /**
     * 评价详情
     */
    public static String APPRAISE_DETAIL = "/remoteMobile/queryOrderComment";
    /**
     * 商品评价列表
     */
    public static String APPRAISE_LIST = "/remoteMobile/listProductComments";
    /**
     * 获取商品最新一条评论、总行数、好评率
     */
    public static String APPRAISE_NEW_INFO = "/remoteMobile/queryProductCommentLatest";
    /**
     * 获取商品评价全部、好评、中评、差评、有图评价、追加评价相对应的总数和总页数
     */
    public static String APPRAISE_NUMBER = "/remoteMobile/queryProductCommentEachTotalAndPage";
    /**
     * 评价说明H5
     */
    public static String APPRAISE_EXPLAIN = "/h5/VR/Reviews.html";
    /**
     * 抽奖游戏H5
     */
    public static String PRIZE_LIST = "/h5/VR/list.html";
}
