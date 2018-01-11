package com.wukong.hezhi.utils;

/**
 * @author HuZhiyin
 * @ClassName: ${FILE_NAME}
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @date 2017/11/10
 */


public class OrientationUtil {

    /**
     * @param orientation 屏幕旋转的角度
     * @return 屏幕需要旋转的角度（0,90,180,270）
     */
    public static int getAngle(int orientation) {
        int angle = 0;
        if ((orientation >= 0 && orientation <= 45) || orientation >= 315 && orientation <= 360
                || orientation == -1) {// 竖直
            angle = 0;
        } else if (orientation > 45 && orientation < 135) {// 顺时针旋转90度（横屏）
            angle = 90;
        } else if (orientation >= 135 && orientation <= 225) {// 顺时针旋转180度（倒屏）
            angle = 180;
        } else if (orientation > 225 && orientation < 315) {// 顺时针旋转270度（横屏）
            angle = 270;
        }
        return angle;
    }
}
