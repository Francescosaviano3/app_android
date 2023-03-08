package com.worldgn.connector;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class TimeUtils {

	/**
	 * 获取当前时区
	 * */
	public static String offSetTimeZone() {
		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();
		String displayName = timeZone.getDisplayName();
		int offset = timeZone.getOffset(System.currentTimeMillis());
		int offsetHour = offset / (1000 * 60 * 60);
		return String.valueOf(offset);
	}

	/**
	 * 夏令时
	 * */
	public static String offSetSummerTimeZone() {
		Calendar cal = Calendar.getInstance();
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		return String.valueOf(dstOffset);
	}
	//: lpy
	public static String stamp2String(long timeStamp) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		String dateStr;
		Timestamp ts = new Timestamp(timeStamp);
		dateStr = sdr.format(ts);
		return dateStr;
	}
	/**
	 * 掉此方法输入所要转换的时间输入例如（"2014-06-14 16:09:00"）返回秒时间戳h
	 * 
	 * @param time
	 * @return
	 */
	public static long String2Stamp(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		Date date;
		long times = 0;
			try {
				date = sdr.parse(time);
				times = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return times/1000;
	}
	public static long String2StampYMD(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date;
		long times = 0;
			try {
				date = sdr.parse(time);
				times = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return times/1000;
	}

	public static long String2Stamp_Today() {
        Date d = new Date();
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdr.format(d);
		sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date;
		long times = 0;
		try {
			date = sdr.parse(dateNowStr + " 00:00:01");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}
	
	public static long String2Stamp_Today_end() {
        Date d = new Date();
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdr.format(d);
		sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date;
		long times = 0;
		try {
			date = sdr.parse(dateNowStr + " 23:59:59");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}

	public static long String2Stamp_Thismonth() {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String first = sdr.format(c.getTime());
 		
		sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

		Date date;
		long times = 0;
		try {
			date = sdr.parse(first + " 00:00:01");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}
	
	public static long String2Stamp_Thismonth_end() {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = sdr.format(ca.getTime());

        sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date;
		long times = 0;
		try {
			date = sdr.parse(last + " 23:59:59");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}

	public static long String2Stamp_Thisweek() {
		Date date;
		long times = 0;
		try {
	        Date curd = new Date();
	        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
	        String dateNowStr = sdr.format(curd);
	        Calendar cal = Calendar.getInstance();
			cal.setTime(sdr.parse(dateNowStr));
			
			int d = 1 - cal.get(Calendar.DAY_OF_WEEK);
			cal.add(Calendar.DAY_OF_WEEK, d);
			//所在周开始日期
			dateNowStr = sdr.format(cal.getTime());
			//cal.add(Calendar.DAY_OF_WEEK, 6);
			//所在周结束日期
			//System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));		sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

			date = sdr.parse(dateNowStr + " 00:00:01");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}
	
	public static long String2Stamp_Thisweek_end() {
		Date date;
		long times = 0;
		try {
	        Date curd = new Date();
	        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
	        String dateNowStr = sdr.format(curd);
	        Calendar cal = Calendar.getInstance();
			cal.setTime(sdr.parse(dateNowStr));
			
			int d = 1 - cal.get(Calendar.DAY_OF_WEEK);
			cal.add(Calendar.DAY_OF_WEEK, d);
			//所在周开始日期
			//dateNowStr = sdr.format(cal.getTime());
			cal.add(Calendar.DAY_OF_WEEK, 6);
			//所在周结束日期
			dateNowStr = sdr.format(cal.getTime());
			sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			date = sdr.parse(dateNowStr + " 23:59:59");
			times = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}

	public static long String2Stampq(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("mm:ss",
				Locale.getDefault());
		Date date;
		long times = 0;
			try {
				date = sdr.parse(time);
				times = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return times/1000;
	}
	public static long String2Stamps(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		Date date;
		long times = 0;
			try {
				date = sdr.parse(time);
				times = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return times/1000;
	}
	
	public static long String2Stampsss(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("mm:ss",
				Locale.getDefault());
		Date date;
		long times = 0;
			try {
				date = sdr.parse(time);
				times = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return times/1000;
	}
	/**
	 * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014-06-14 16:09:00"）
	 * 
	 * @param time
	 * @return
	 */
	public static String times(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}
	
	public static String timees(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}
	
	
	public static String timesss(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("mm:ss", Locale.getDefault());
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		return times;

	}

	
	// 时间格式字符串转换成周几的int类型数据
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}
	
	public static int dayForWeekUs(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		return dayForWeek;
	}
	
	public static String secToTime(long time){
		String timeStr;
		long hour = 0;
		long minute = 0;
		long second = 0;
		if(time<=0){
			return "00:00:00";
		}else{
			hour=time/60;
			minute=time%60;
			timeStr = unitFormat(hour) +":"+unitFormat(minute) + ":" + unitFormat(second);
		}
		return timeStr;
	}
	
	
	
	public static String secToTime(int time){
		String timeStr;
		long hour = 0;
		long minute = 0;
		long second = 0;
		if(time<=0){
			return "00:00:00";
		}else{
			hour=time/60;
			minute=time%60;
			timeStr = unitFormat(hour) +":"+unitFormat(minute) + ":" + unitFormat(second);
		}
		return timeStr;
	}
	/**
		int h = (int) (sleepContentTemp / 60);
		int m = (int) (sleepContentTemp % 60);
		String hours = h >= 10 ? h + "" : "0" + h;
		String min = m >= 10 ? m + "" : "0" + m;
	 */

	public static String unitFormat(long i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Long.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static long sss(String zz){
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		long millionSeconds=0;
		  try {
			 millionSeconds = sdf.parse(zz).getTime();
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//毫秒
		return millionSeconds/1000;
		
	}
	
}
