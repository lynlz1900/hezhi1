package com.wukong.hezhi.utils;

import java.lang.reflect.Field;


public  class ReflectUtil {
	/** 通过反射获取类的pageName */
	public static String getPageName(Object obj) {
		String result = "";
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if ("pageName".equals(fieldName)) {// 页面的名称
				try {
					result = (String) field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}

	/** 通过反射获取类的pageCode */
	public static String getPageCode(Object obj) {
		String result = "";
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if ("pageCode".equals(fieldName)) {// 页面的编码
				try {
					result = (String) field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}
}
