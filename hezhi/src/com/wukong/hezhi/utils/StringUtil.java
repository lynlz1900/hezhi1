package com.wukong.hezhi.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/** 判断是否是华大芯片 */
	public static boolean isHuadaTag(String uid) {
		if (uid.length() > 4) {
			String uidEnd = uid.substring(uid.length() - 4, uid.length());
			if (uidEnd.equals("81E0")) {// 华大芯片的uid结尾一定是:81E0
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isPhoneNum(String phoneNum) {
		String regEx = "^1[3|4|5|7|8][0-9]{8}$";// 简单的手机判断正则
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(phoneNum);
		// 查找字符串中是否有匹配正则表达式的字符/字符串
		boolean rs = matcher.find();
		return rs;
	}

	/** 字符串倒序 */
	public static String reverseString(String str) {
		StringBuffer stringBuffer = new StringBuffer(str);
		String reverseString = stringBuffer.reverse().toString();
		return reverseString;
	}

	/** 字符中是否含有emoji表情 */
	public static boolean isContainsEmoji(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (isEmojiCharacter(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
	}

	/** 将字符串转换为整型 */
	public static int str2Int(String str) {
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (Exception e) {
			// TODO: handle exception
			i = 0;
		}
		return i;
	}
	
	/** 将字符串转换为long */
	public static long str2Long(String str) {
		long i = 0;
		try {
			i = Long.parseLong(str);
		} catch (Exception e) {
			// TODO: handle exception
			i = 0;
		}
		return i;
	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {

		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {

			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}

	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);
			if (c < 256)// ASC11表中的字符码值不够4位,补00
			{
				unicode.append("\\u00");
			} else {
				unicode.append("\\u");
			}
			// 转换为unicode
			unicode.append(Integer.toHexString(c));
		}

		return unicode.toString();
	}

	public static String change2W(int i) {
		String str = "";
		if (i >= 10000) {
			float j = ((float) i) / 10000;
			String num = new DecimalFormat("###,###,###.#").format(j);// 保留小数点后一位
			str = num + "w";
		} else {
			str = i + "";
		}
		return str;
	}
	/**不区分大小写比较*/
	public static boolean ignoreCaseEquals(String str1,String str2){  
	      return str1 == null ? str2 == null :str1.equalsIgnoreCase(str2);  
	}  
}
