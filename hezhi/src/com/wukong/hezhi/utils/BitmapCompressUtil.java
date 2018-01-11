package com.wukong.hezhi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 * @ClassName: BitmapCompressUtil
 * @Description: TODO(图片压缩工具类)
 * @author Huzhiyin
 * @date 2017年10月26日 上午9:17:52
 *
 */
public class BitmapCompressUtil {
	/** 质量压缩法 */
	public static Bitmap qualityCompress(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩,(此种压缩法，不能压缩到50kb)
			baos.reset();// 重置baos即清空baos
			// 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/** 二次采样压缩法 */
	public static Bitmap secondCompress(String filePath, int reqHeight, int reqWidth) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// 计算图片的缩放值
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/** 图片按宽，高压缩 */
	public static Bitmap whCompress(Bitmap image, int w, int h) {
		return Bitmap.createScaledBitmap(image, w, h, true);// 压缩图片;
	}

	/** 压缩图片（此方法专门准对NFC视频定制上传图片有效，不具有通用性，写在此处是为了调用时候简单 ） */
	public static Bitmap compress(Bitmap photo) {
		float with = 1000;
		float height = 1800;
		Bitmap compressBitmap = null;// 压缩后的图片
		float h = 1, w = 1;// 图片压缩后的宽高

		float ratio = 1f;// 缩放比例
		float photo_h = photo.getHeight();// 原图的高
		float photo_w = photo.getWidth();// 原图的宽

		if (photo_w > with) {// 如果图片的宽大于1000，则按等比例压缩至宽为1000
			ratio = photo_w / with;
			w = with;
			h = (int) (photo_h / ratio);
			compressBitmap = qualityCompress(whCompress(photo, (int) w, (int) h));// 先按宽高来压缩图片再按质量来压缩，这样能提高压缩速度
		} else if (photo_h > height) {// 如果图片的高大于1800，则按等比例压缩至高为1800
			ratio = photo_h / height;
			h = height;
			w = (int) (photo_w / ratio);
			compressBitmap = qualityCompress(whCompress(photo, (int) w, (int) h));// 先按宽高来压缩图片，再按质量来压缩，这样能提高压缩速度
		} else {
			compressBitmap = photo;
		}
		return compressBitmap;
	}

	/**
	 * 通过图片路径获取图片旋转角度
	 */
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
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
				default:
					break;
				}
			}
		}
		return degree;
	}

	/**
	 * 旋转图片
	 */
	public static Bitmap rotateBitmap(Bitmap b, String filepath) {
		int degrees = getExifOrientation(filepath);
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {

			}
		}
		return b;
	}

}
