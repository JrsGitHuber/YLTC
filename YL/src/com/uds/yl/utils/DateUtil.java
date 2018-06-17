package com.uds.yl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * �����ڸ�ʽ��ת��Ϊ�ַ�������
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date){
		if(date==null){
			return "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy��MM��dd��");
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	/**
	 * @param date
	 * @param formate ����ת��Ϊ�ַ����ĸ�ʽ��˵
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
