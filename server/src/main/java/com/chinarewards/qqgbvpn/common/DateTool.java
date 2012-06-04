package com.chinarewards.qqgbvpn.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {

	private static final String SIMPLE_DATE_FORMAT_STRING = "yyyy-MM-dd";

	/**
	 * Using yyyy-MM-dd to format!
	 * 
	 * @return
	 */
	public static String getSingleStr(Date d) {
		DateFormat fmt = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STRING);
		return fmt.format(d);
	}

	/**
	 * 
	 * @param time
	 *            the time you want to check
	 * @param today
	 *            the current time.
	 * @return
	 */
	public static boolean isToday(Date time, Date currentTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentTime);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date from = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date to = c.getTime();
		System.out.println(from + "," + to);
		if ((time.equals(from) || time.after(from)) && time.before(to)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param time
	 *            the time you want to check
	 * @param today
	 *            the current time.
	 * @return
	 */
	public static boolean isLast7Days(Date time, Date currentTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(currentTime);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date to = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, -7);
		Date from = c.getTime();
		System.out.println(from + "," + to);
		if ((time.equals(from) || time.after(from)) && time.before(to)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(isToday(fmt.parse("2012-04-30 00:01:00"),
				fmt.parse("2012-04-30 22:34:09")));
		System.out.println(isLast7Days(fmt.parse("2012-04-01 00:01:00"),
				fmt.parse("2012-04-07 22:34:09")));
	}
}
