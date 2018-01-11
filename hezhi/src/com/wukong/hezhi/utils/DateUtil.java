package com.wukong.hezhi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * @ClassName: DataUtil
 * @Description: TODO(日期工具类)
 * @author HuZhiyin
 * @date 2016-12-30 下午5:46:15
 * 
 */
public class DateUtil {
	public static String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	public static String getTimeDay() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	public static String getRadom() {
		long a = System.currentTimeMillis();
		return a + "";
	}

	/**
	 * 时间戳格式转换为朋友圈时间显示的形式
	 */
	public static String getFormatTime(long timesamp) {
		String result = "";
		Calendar todayCalendar = Calendar.getInstance();
		Calendar otherCalendar = Calendar.getInstance();
		otherCalendar.setTimeInMillis(timesamp);
		SimpleDateFormat timeFormat = new SimpleDateFormat("M月d日 HH:mm");
		SimpleDateFormat yearTimeFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm");
		SimpleDateFormat hourAndMinFormat = new SimpleDateFormat("HH:mm");

		boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
		if (yearTemp) {
			int todayMonth = todayCalendar.get(Calendar.MONTH);
			int otherMonth = otherCalendar.get(Calendar.MONTH);
			if (todayMonth == otherMonth) {// 表示是同一个月
				int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
				switch (temp) {
				case 0:
					result = hourAndMinFormat.format(new Date(timesamp));
					break;
				case 1:
					result = "昨天 " + hourAndMinFormat.format(new Date(timesamp));
					break;

				default:
					result = timeFormat.format(new Date(timesamp));
					break;
				}
			} else {
				result = timeFormat.format(new Date(timesamp));
			}
		} else {
			result = yearTimeFormat.format(new Date(timesamp));
		}
		return result;
	}

	public static String getYYHHMM(long timesamp) {
		SimpleDateFormat yearTimeFormat = new SimpleDateFormat("yyyy-M-d HH:mm");
		return yearTimeFormat.format(new Date(timesamp));
	}

	public static String getYMDHMS(long timesamp) {
		SimpleDateFormat yearTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (timesamp == 0)
			return "";

		String date = "";
		try {
			date = yearTimeFormat.format(new Date(timesamp));
		} catch (Exception e) {
		}

		return date;
	}

	public static String getYMD_(long timesamp) {
		SimpleDateFormat yearTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (timesamp == 0)
			return "";

		String date = "";
		try {
			date = yearTimeFormat.format(new Date(timesamp));
		} catch (Exception e) {
		}

		return date;
	}
	
	public static String getYMD(long timesamp) {
		SimpleDateFormat yearTimeFormat = new SimpleDateFormat("yyyy年MM月dd日");

		if (timesamp == 0)
			return "";

		String date = "";
		try {
			date = yearTimeFormat.format(new Date(timesamp));
		} catch (Exception e) {
		}

		return date;
	}
	
	/**
	 * 格式化时间，判断是否今天，昨天
	 * @param time
	 * @return String
	 */
	public static String formatDateTime(String time) {
		try {
			SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy年MM月dd日"); 
			Date date = null;
			date = format.parse(time);
			if(time == null || "".equals(time)){
				return "";
			}
			
			long dateTime = date.getTime();
			
			if(dateTime >= getDayBegin() && dateTime <= getDayEnd()){
				return "今天";
			}else if((dateTime) >= getBeginDayOfYesterday() && (dateTime) <= getEndDayOfYesterDay()){
				return "昨天";
			}
			
		} catch (Exception e) {
			return time;
		}
		
        return time;
	}

	//获取当天的开始时间
    public static long getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }
    //获取当天的结束时间
    public static long getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime().getTime();
    }
    //获取昨天的开始时间
    public static long getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(getDayBegin()));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime().getTime();
    }
    //获取昨天的结束时间
    public static long getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(getDayEnd()));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime().getTime();
    }
    
	/**
	 * 计算两个时间相差
	 * 
	 * @param date
	 * @return
	 */
	public static String dateDiff(long startTime, long endTime) {
		if(startTime == 0 || endTime == 0){
			return "当天";
		}
		
		if(startTime >= endTime){
			return "当天";
		}
		
		//按照传入的格式生成一个simpledateformate对象
		long nd = 1000*24*60*60;//一天的毫秒数
//		long nh = 1000*60*60;//一小时的毫秒数
//		long nm = 1000*60;//一分钟的毫秒数
//		long ns = 1000;//一秒钟的毫秒数long diff;try {
		//获得两个时间的毫秒时间差异
		long diff = endTime - startTime;
		String day = diff/nd + "天后";//计算差多少天
//		String hour = diff/nh + "小时"; //计算相差多少小时
//		String min = diff/nm + "分钟";//计算差多少分钟
//		String sec = diff/ns + "秒";//计算差多少秒
		
		if(diff/nd >0)
			return day;
//		else if(diff/nh >0)
//		return hour;
//	else if(diff/nm >0)
//		return min;
//	else if(diff/ns >0)
//		return sec;
		else
			return "当天";
		
	}
	
	/**
	 * 将日期格式的字符串转换为长整型
	 * 
	 * @param date
	 * @return
	 */
	public static long convert2long(String date) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sf.parse(date).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0l;
	}

}
