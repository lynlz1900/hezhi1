package com.wukong.hezhi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class ReadStreamUtil {

	public static Context mContext = ContextUtil.getContext();

	/**
	 * 从asset中读取json文件
	 */
	public static String getJson(String path) {

		String json = "";
		try {
			AssetManager s = mContext.getAssets();
			try {
				InputStream is = s.open(path);
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				json = new String(buffer, "utf-8");
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;

	}

//	public static void saveImageToGallery(Context context, Bitmap bmp) {
//	    // 首先保存图片
//	    File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
//	    if (!appDir.exists()) {
//	        appDir.mkdir();
//	    }
//	    String fileName = System.currentTimeMillis() + ".png";
//	    File file = new File(appDir, fileName);
//	    try {
//	        FileOutputStream fos = new FileOutputStream(file);
//	        bmp.compress(CompressFormat.JPEG, 100, fos);
//	        fos.flush();
//	        fos.close();
//	    } catch (FileNotFoundException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//		}
//	    
//	    // 其次把文件插入到系统图库
//	    try {
//	        MediaStore.Images.Media.insertImage(context.getContentResolver(),
//					file.getAbsolutePath(), fileName, null);
//	    } catch (FileNotFoundException e) {
//	        e.printStackTrace();
//	    }
//	    // 最后通知图库更新
//	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
//	}
	
}
