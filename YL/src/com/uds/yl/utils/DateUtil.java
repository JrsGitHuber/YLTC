package com.uds.yl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * 将日期格式的转化为字符串类型
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date){
		if(date==null){
			return "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	/**
	 * @param date
	 * @param formate 日期转换为字符串的格式的说
	 * @return
	 */
	public static String getDateStr(Date date,String formate){
		if(date==null){
			return "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
}
