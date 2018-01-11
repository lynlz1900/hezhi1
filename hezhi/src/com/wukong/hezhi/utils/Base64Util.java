package com.wukong.hezhi.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class Base64Util {
	/**
	 * 通将Bitmap转换成Base64字符串
	 */
	public static String Bitmap2StrByBase64(Bitmap bit) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bit.compress(CompressFormat.PNG, 100, bos);// 参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	/**
	 * 通过BASE64Decoder解码，并生成图片
	 */
	public boolean string2Image(String imgStr, String imgFilePath) {
		// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null)
			return false;
		try {
			// Base64解码

			byte[] b = Base64.decode(imgStr, Base64.DEFAULT);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成Jpeg图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/** 解码 */

	public static String decode(String str) {
		String decodedString = new String(Base64.decode(str, Base64.DEFAULT));
		return decodedString;
	}

	/** 编码 */
	public static String encode(String str) {
		String encodedString = Base64.encodeToString(str.getBytes(),
				Base64.DEFAULT);
		return encodedString;
	}

}