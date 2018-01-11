package com.wukong.hezhi.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Debug;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wukong.hezhi.bean.ResponseJsonArrInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;

/**
 * 
 * @ClassName: JsonUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author huzy
 * @date 2015-11-6 下午3:12:53
 */
public class JsonUtil {

	/**
	 * json通过Gson框架生成对象
	 * 
	 * @param jsonData
	 * @param c
	 * @return
	 */
	public static <T> Object parseJson2Obj(String jsonData, Class<T> c) {
		if (null == jsonData) {
			return null;
		}
		Gson gson = new Gson();
		Object obj = gson.fromJson(jsonData, c);
		return obj;
	}

	/**
	 * json通过Gson框架生成对象
	 * 
	 * @param jsonData
	 * @param c
	 * @return
	 */
	public static <T> Object parseJson2Obj(String jsonData, TypeToken<T> type) {
		if (null == jsonData) {
			return null;
		}
		Gson gson = new Gson();
		Object obj = gson.fromJson(jsonData, type.getType());
		return obj;
	}

	/**
	 * 将java对象转换成json对象
	 * 
	 * @param obj
	 * @return
	 */
	public static String parseObj2Json(Object obj) {

		if (null == obj) {
			return null;
		}
		Gson gson = new GsonBuilder().serializeNulls().create();
		// Gson gson = new Gson();
		String objstr = gson.toJson(obj);
		if (Debug.isDebuggerConnected()) {
			Log.i("parseObj2Json", objstr);
		}
		return objstr;
	}

	/** 将一对象赋值给另一个类，并生产对象，属性不同部分的忽略。 */
	public static Object Obj2Obj(Object objFrom, Class objTo) {
		String jsonFrom = parseObj2Json(objFrom);
		Object object = parseJson2Obj(jsonFrom, objTo);
		return object;
	}

	public static <T> ArrayList<T> parseJson2ArrayList(String json,
			Class<T> clazz) {
		Type type = new TypeToken<ArrayList<JsonObject>>() {
		}.getType();
		ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

		ArrayList<T> arrayList = new ArrayList<>();
		for (JsonObject jsonObject : jsonObjects) {
			arrayList.add(new Gson().fromJson(jsonObject, clazz));
		}
		return arrayList;
	}

	public static <T> List<T> parseJson2ToList(String json, Class<T[]> clazz) {
		Gson gson = new Gson();
		T[] array = gson.fromJson(json, clazz);
		return Arrays.asList(array);
	}

	/**
	 * 解析泛型
	 */
	public static ResponseJsonInfo fromJson(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(ResponseJsonInfo.class, clazz);
		try {
			return gson.fromJson(json, objectType);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 解析泛型数组
	 */
	public static ResponseJsonArrInfo fromJsonArr(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(ResponseJsonArrInfo.class, clazz);
		try {
			return gson.fromJson(json, objectType);
		} catch (Exception e) {
			return null;
		}
	}

	static ParameterizedType type(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}

	/** 将一个对象封装精ResponseJsonInfo对象中,极光推送会用到 */
	public static String setJson(Object object) {
		ResponseJsonInfo responseJsonInfo = new ResponseJsonInfo<>();
		responseJsonInfo.setHttpCode("200");
		responseJsonInfo.setBody(object);
		String jsonStr = JsonUtil.parseObj2Json(responseJsonInfo);
		return jsonStr;
	}

}
