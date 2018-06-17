package com.uds.yl.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DoubleUtil {
	
	/**
	 * @param number f
	 * @return 返回转化过后的结果 默认是 4位
	 */
	public static String formatNumber(String number){
		Double f = StringsUtil.convertStr2Double(number);
		DecimalFormat df = new DecimalFormat("0.0000");
		
		return df.format(f);
		
	}
	
	
	/**
	 * @param number f
	 * @return 返回转化过后的结果 默认是 4位
	 */
	public static String formatNumber(Double number){
		DecimalFormat df = new DecimalFormat("0.0000");
		return df.format(number);
	}

	
	/**
	 * @param number 要处理的数
	 * @param size 要保留几位
	 * @return 转化后的结果
	 */
	public static String formatNumber(String number,int size){
		Double f = StringsUtil.convertStr2Double(number);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(size);
		return nf.format(f);
	}
}
