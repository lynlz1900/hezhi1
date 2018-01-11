package com.wukong.hezhi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.wukong.hezhi.constants.HezhiConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HuZhiyin
 * @ClassName: ${FILE_NAME}
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @date 2018/1/10
 */


public class ThumbUtil {
    private static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                             int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static List<String> compress(List<String> images) {
        List<String> imagesCompress = new ArrayList<String>();
        for (int i = 0; i < images.size(); i++) {
            Bitmap bitmap = getSmallBitmap(images.get(i));
            bitmap = BitmapCompressUtil.rotateBitmap(bitmap, images.get(i));
            String path = saveBitmapFile(bitmap);
            imagesCompress.add(path);
        }
        if (imagesCompress.size() == 0) {
            imagesCompress.addAll(images);
        }
        return imagesCompress;
    }

    private static String saveBitmapFile(Bitmap bitmap) {
        FileUtil.isFolderExists(HezhiConfig.PIC_THUMBNAIL);
        File file = new File(HezhiConfig.PIC_THUMBNAIL + System.currentTimeMillis() + ".png");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }
}
