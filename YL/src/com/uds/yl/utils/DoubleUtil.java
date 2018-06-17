package com.uds.yl.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DoubleUtil {
	
	/**
	 * @param number f
	 * @return ����ת������Ľ�� Ĭ���� 4λ
	 */
	public static String formatNumber(String number){
		Double f = StringsUtil.convertStr2Double(number);
		DecimalFormat df = new DecimalFormat("0.0000");
		
		return df.format(f);
		
	}
	
	
	/**
	 * @param number f
	 * @return ����ת������Ľ�� Ĭ���� 4λ
	 */
	public static String formatNumber(Double number){
		DecimalFormat df = new DecimalFormat("0.0000");
		return df.format(number);
	}

	
	/**
	 * @param number Ҫ�������
	 * @param size Ҫ������λ
	 * @return ת����Ľ��
	 */
	public static String formatNumber(String number,int size){
		Double f = StringsUtil.convertStr2Double(number);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(size);
		return nf.format(f);
	}
}
