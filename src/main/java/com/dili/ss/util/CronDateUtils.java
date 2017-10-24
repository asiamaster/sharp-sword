package com.dili.ss.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 该类提供Quartz的cron表达式与Date之间的转换
 * Created by asiamaster on 2017/10/24 0024.
 */
public class CronDateUtils {
	private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

	/***
	 *
	 * @param date 时间
	 * @return cron类型的日期
	 */
	public static String getCron(final Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
		String formatTimeStr = "";
		if (date != null) {
			formatTimeStr = sdf.format(date);
		}
		return formatTimeStr;
	}

	/***
	 *
	 * @param cron Quartz cron的类型的日期
	 * @return Date日期
	 */
	public static Date getDate(final String cron) throws ParseException {
		if (cron == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
		return sdf.parse(cron);
	}
}
