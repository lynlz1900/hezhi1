package com.wukong.hezhi.utils;

import java.math.BigDecimal;

public class DataUtil {
	/** 將double转化为两位小数点 */
	public static double double2point(double f) {
		BigDecimal bg = new BigDecimal(f);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
	
	/** 將double转化为两位小数点String */
	public static String double2pointString(double f) {
		try {
			BigDecimal bg = new BigDecimal(f);
			bg = bg.setScale(2, BigDecimal.ROUND_HALF_UP);
			return bg.toString();
		} catch (Exception e) {
			return "0.00";
		}
		
	}
}
