package com.wukong.hezhi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * 
 * @ClassName: SPUtil
 * @Description: TODO(sharepreferences)
 * @author HuZhiyin
 * @date 2017-1-3 上午8:28:27
 * 
 */
public class SPUtil {

	/**
	 * 保存数据到sharepreferences
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            保存键名
	 * @param value
	 *            保存值
	 */
	public static void savaToShared(Context context, String spName, String key,
			Object value) {
		SharedPreferences sharedPreferencesWrite = context
				.getSharedPreferences(spName, Context.MODE_WORLD_WRITEABLE);
		Editor editor = sharedPreferencesWrite.edit();
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Object) {
			try {
				// 先将序列化结果写到byte缓存中，其实就分配一个内存空间
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(bos);
				// 将对象序列化写入byte缓存
				os.writeObject(value);
				// 将序列化的数据转为16进制保存
				String bytesToHexString = bytesToHexString(bos.toByteArray());
				// 保存该16进制数组
				editor.putString(key, bytesToHexString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (value == null) {
			editor.putString(key, "");
		}
		editor.commit();
	}

	/**
	 * 得到保存的string的值
	 * 
	 * @param context
	 * @param spName
	 *            配置文件名
	 * @param key
	 * @return
	 */
	public static String getShareStr(Context context, String spName, String key) {
		SharedPreferences sharedPreferencesRead = context.getSharedPreferences(
				spName, Context.MODE_WORLD_READABLE);
		return sharedPreferencesRead.getString(key, "");
	}

	public static int getShareInt(Context context, String spName, String key) {
		SharedPreferences sharedPreferencesRead = context.getSharedPreferences(
				spName, Context.MODE_WORLD_READABLE);
		return sharedPreferencesRead.getInt(key, -1);
	}

	public static Boolean getShareBoolean(Context context, String spName,
			String key) {
		SharedPreferences sharedPreferencesRead = context.getSharedPreferences(
				spName, Context.MODE_WORLD_READABLE);
		return sharedPreferencesRead.getBoolean(key, false);
	}

	/**
	 * 得到参数保存SharedPreferences----long
	 */
	public static Long getShareLong(Context context, String spName, String key) {
		Long value = 0l;
		SharedPreferences sharedPreferencesRead = context.getSharedPreferences(
				spName, Context.MODE_WORLD_READABLE);
		value = sharedPreferencesRead.getLong(key, 0);
		return value;
	}

	/**
	 * 获取保存的Object对象
	 */
	public static Object getShareObject(Context context, String spName,
			String key) {
		try {
			SharedPreferences sharedata = context.getSharedPreferences(spName,
					Context.MODE_WORLD_READABLE);
			if (sharedata.contains(key)) {
				String string = sharedata.getString(key, "");
				if (TextUtils.isEmpty(string)) {
					return null;
				} else {
					// 将16进制的数据转为数组，准备反序列化
					byte[] stringToBytes = StringToBytes(string);
					ByteArrayInputStream bis = new ByteArrayInputStream(
							stringToBytes);
					ObjectInputStream is = new ObjectInputStream(bis);
					// 返回反序列化得到的对象
					Object readObject = is.readObject();
					return readObject;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 所有异常返回null
		return null;

	}

	/**
	 * desc:将数组转为16进制
	 */
	public static String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			return null;
		}
		if (bArray.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * desc:将16进制的数据转为数组
	 */
	public static byte[] StringToBytes(String data) {
		String hexString = data.toUpperCase().trim();
		if (hexString.length() % 2 != 0) {
			return null;
		}
		byte[] retData = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length(); i++) {
			int int_ch; // 两位16进制数转化后的10进制数
			char hex_char1 = hexString.charAt(i); // //两位16进制数中的第一位(高位*16)
			int int_ch1;
			if (hex_char1 >= '0' && hex_char1 <= '9')
				int_ch1 = (hex_char1 - 48) * 16; // // 0 的Ascll - 48
			else if (hex_char1 >= 'A' && hex_char1 <= 'F')
				int_ch1 = (hex_char1 - 55) * 16; // // A 的Ascll - 65
			else
				return null;
			i++;
			char hex_char2 = hexString.charAt(i); // /两位16进制数中的第二位(低位)
			int int_ch2;
			if (hex_char2 >= '0' && hex_char2 <= '9')
				int_ch2 = (hex_char2 - 48); // // 0 的Ascll - 48
			else if (hex_char2 >= 'A' && hex_char2 <= 'F')
				int_ch2 = hex_char2 - 55; // // A 的Ascll - 65
			else
				return null;
			int_ch = int_ch1 + int_ch2;
			retData[i / 2] = (byte) int_ch;// 将转化后的数放入Byte里
		}
		return retData;
	}
}
