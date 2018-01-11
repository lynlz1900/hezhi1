package com.wukong.hezhi.http;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.text.TextUtils;

public class HttpURLUtil {
	public static String separator = "&&&";

	public static String getURLParameter(String... strings) {
		StringBuilder URLParameter = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			URLParameter.append(strings[i]).append(separator);
		}
		String parameter = URLParameter.toString();
		String encodeParameter = Uri.encode(parameter, "UTF-8");
		return encodeParameter;
	}

	/** 解析url并返回对应key的value */
	public static String getPara(String url, String key) {
		String value = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.clear();
		if (!TextUtils.isEmpty(url)) {// 如果URL不是空字符串
			try {
				url = url.substring(url.indexOf('?') + 1);
				String paramaters[] = url.split("&");
				for (String param : paramaters) {
					String values[] = param.split("=");
					if (values.length > 1) {
						paramMap.put(values[0], values[1]);
					} else {
						paramMap.put(values[0], "null");
					}

				}
				if (paramMap != null) {
					value = paramMap.get(key);
				}
			} catch (Exception e) {
				return value;
			}
		}
		return value;
	}
}
