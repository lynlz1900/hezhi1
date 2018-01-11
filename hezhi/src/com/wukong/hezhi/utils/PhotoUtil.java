package com.wukong.hezhi.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

/**
 * 
 * @ClassName: PhotoUtil
 * @Description: TODO(头像处理工具)
 * @author HuZhiyin
 * @date 2017-1-11 下午1:43:21
 * 
 */
public class PhotoUtil {

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static String savePhoto(Bitmap photoBitmap, String path,
			String photoName) {
		String localPath = null;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".png");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
							fileOutputStream)) { // 转换完成
						localPath = photoFile.getPath();
						fileOutputStream.flush();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				localPath = null;
				e.printStackTrace();
			} finally {
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
						fileOutputStream = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return localPath;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
		// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	/** 将bitmap保存到相册 */
	public static void insertBitMapToMediaStore(Context context, Bitmap bitmap,
			String filePath) {

		File file = new File(filePath);// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ContentValues values = initCommonContentValues(file);
		values.put(MediaStore.Images.ImageColumns.DATE_TAKEN,
				System.currentTimeMillis());
		values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
		values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			values.put(MediaStore.Images.ImageColumns.WIDTH, 0);
			values.put(MediaStore.Images.ImageColumns.HEIGHT, 0);
		}
		values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
		context.getApplicationContext().getContentResolver()
				.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	/**
	 * 保存到照片到本地，并插入MediaStore以保证相册可以查看到
	 * 
	 * @param context
	 *            上下文
	 * @param filePath
	 *            文件路径
	 * @param createTime
	 *            创建时间 <=0时为当前时间
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 */
	public static void insertImageToMediaStore(Context context,
			String filePath, long createTime, int width, int height) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		if (createTime <= 0)
			createTime = System.currentTimeMillis();
		File saveFile = new File(filePath);
		ContentValues values = initCommonContentValues(saveFile);
		values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
		values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
		values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			if (width > 0)
				values.put(MediaStore.Images.ImageColumns.WIDTH, 0);
			if (height > 0)
				values.put(MediaStore.Images.ImageColumns.HEIGHT, 0);
		}
		values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
		context.getApplicationContext().getContentResolver()
				.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	/**
	 * 保存到视频到本地，并插入MediaStore以保证相册可以查看到
	 * 
	 * @param context
	 *            上下文
	 * @param filePath
	 *            文件路径
	 * @param createTime
	 *            创建时间 <=0时为当前时间
	 * @param duration
	 *            视频长度
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 */
	public static void insertVideoToMediaStore(Context context,
			String filePath, long createTime, long duration, int width,
			int height) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}
		if (createTime <= 0)
			createTime = System.currentTimeMillis();
		File saveFile = new File(filePath);
		ContentValues values = initCommonContentValues(saveFile);
		values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime);
		values.put(MediaStore.Video.VideoColumns.DURATION, duration);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			if (width > 0)
				values.put(MediaStore.Video.VideoColumns.WIDTH, width);
			if (height > 0)
				values.put(MediaStore.Video.VideoColumns.HEIGHT, height);
		}
		values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
		context.getContentResolver().insert(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
	}

	/**
	 * 针对非系统文件夹下的文件,使用该方法 插入时初始化公共字段
	 * 
	 * @param saveFile
	 *            文件
	 * @return ContentValues
	 */
	private static ContentValues initCommonContentValues(File saveFile) {
		ContentValues values = new ContentValues();
		long currentTimeInSeconds = System.currentTimeMillis();
		values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
		values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
		values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeInSeconds);
		values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeInSeconds);
		values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
		return values;
	}

	/** 
     * 获取原始图片的角度（解决三星手机拍照后图片是横着的问题） 
     * @param path 图片的绝对路径 
     * @return 原始图片的角度 
     */  
    public static int getBitmapDegree(String path) {  
        int degree = 0;  
        try {  
        	LogUtil.i("path:" + path);  
            // 从指定路径下读取图片，并获取其EXIF信息  
            ExifInterface exifInterface = new ExifInterface(path);  
            // 获取图片的旋转信息  
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                    ExifInterface.ORIENTATION_NORMAL);  
            LogUtil.i("orientation:" + orientation);  
            switch (orientation) {  
            case ExifInterface.ORIENTATION_ROTATE_90:  
                degree = 90;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_180:  
                degree = 180;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_270:  
                degree = 270;  
                break;  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return degree;  
    }  
    
    /** 
     *	处理旋转角度问题
     * @param path 图片的绝对路径  bitmap 原始图片
     * @return 矫正旋转角度后的图片
     */  
    public static Bitmap getBitmapToMatrix(Bitmap bitmap,int degree){
    	Bitmap returnBm = null;
    	  
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        LogUtil.i("degree:" + degree);  
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
        	bitmap.recycle();
        }
        return returnBm;
    }
    
  //防止图片背景变黑
  	public static Bitmap changeColor(Bitmap bitmap) {
          if (bitmap == null) {
              return null;
          }
          int w = bitmap.getWidth();
          int h = bitmap.getHeight();
          int[] colorArray = new int[w * h];
          int n = 0;
          for (int i = 0; i < h; i++) {
              for (int j = 0; j < w; j++) {
                  int color = getMixtureWhite(bitmap.getPixel(j, i));
                  colorArray[n++] = color;
              }
          }
          return Bitmap.createBitmap(colorArray, w, h,Bitmap.Config.ARGB_8888);
      }

      //获取和白色混合颜色 
      private static int getMixtureWhite(int color) {
          int alpha = Color.alpha(color);
          int red = Color.red(color);
          int green = Color.green(color);
          int blue = Color.blue(color);
          return Color.rgb(getSingleMixtureWhite(red, alpha), getSingleMixtureWhite(green, alpha),
                  getSingleMixtureWhite(blue, alpha));
      }

      // 获取单色的混合值 
      private static int getSingleMixtureWhite(int color, int alpha) {
          int newColor = color * alpha / 255 + 255 - alpha;
          return newColor > 255 ? 255 : newColor;
      }
}
