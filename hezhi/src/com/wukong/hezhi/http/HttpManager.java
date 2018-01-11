package com.wukong.hezhi.http;

import java.io.File;
import org.apache.http.entity.StringEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.NetWorkUitl;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: HttpManager
 * @Description: TODO(http请求管理类)
 * @author HuZhiyin
 * @date 2016-9-31 下午2:14:43
 * 
 */
public class HttpManager {
	private static HttpUtils httpUtils;

	private HttpManager() {
	}

	private static class Holder {
		private static final HttpManager SINGLETON = new HttpManager();
	}

	public static HttpManager getInstance() {
		return Holder.SINGLETON;
	}

	@SuppressWarnings("rawtypes")
	private HttpHandler httpHandler;

	/** 取消请求 */
	public void cancel() {
		if (httpHandler != null) {
			httpHandler.cancel();
		}
	}

	/** get请求 */
	public <T> HttpHandler get(String URL, RequestCallBack<T> requestCallBack) {
		LogUtil.d(URL);
		RequestParams params = setParams(null);
		setHttp();
		httpHandler = httpUtils.send(HttpMethod.GET, URL, params, requestCallBack);
		return httpHandler;
	}

	/** post请求 */
	public HttpHandler post(String URL, Object object, RequestCallBack<String> requestCallBack) {
		LogUtil.d(URL);
		if (object != null) {
			LogUtil.d(object.toString());
		}
		RequestParams params = setParams(object);
		setHttp();
		httpHandler = httpUtils.send(HttpMethod.POST, URL, params, requestCallBack);
		return httpHandler;
	}

	/** download异步请求(下载) */
	public HttpHandler download(String URL, String target, RequestCallBack<File> requestCallBack) {
		LogUtil.d(URL);
		LogUtil.d(target);
		RequestParams params = setParams(null);
		setHttp();
		httpHandler = httpUtils.download(URL, target, params, requestCallBack);
		return httpHandler;
	}

	/** 设置http参数 */
	private void setHttp() {
		if (!NetWorkUitl.isNetworkAvailable()) {// 判断网络是否可用
			ScreenUtil.showToast(ContextUtil.getString(R.string.net_error));
		}

		if (httpUtils == null) {
			httpUtils = new HttpUtils(30 * 1000);// 30秒超时
			httpUtils.configSoTimeout(30 * 1000);
			httpUtils.configTimeout(30 * 1000);
			httpUtils.configCurrentHttpCacheExpiry(1000);// 设置当前请求的缓存时间
			httpUtils.configRequestThreadPoolSize(5);// 设置由几条线程进行下载
		}
	}

	/** 设置params */
	private RequestParams setParams(Object object) {
		RequestParams params = new RequestParams("UTF-8");
		try {
			params.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			params.addHeader("device", "android");// 标记安卓设备
			params.addHeader("deviceId", PackageUtil.getPhoneId());// 设备id
			if (object != null) {
				StringEntity entity = getJsonParameter(object);
				params.setBodyEntity(entity);
				params.setContentType("application/json");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	/** 将对象转换成StringEntity */
	public StringEntity getJsonParameter(Object obj) {
		String json = JsonUtil.parseObj2Json(obj);
		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(json, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringEntity;
	}

}
