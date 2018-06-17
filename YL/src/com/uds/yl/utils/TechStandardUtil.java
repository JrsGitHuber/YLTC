package com.uds.yl.utils;

import com.uds.yl.bean.UpAndDonwBean;

public class TechStandardUtil {
	
	
	/**
	 * @param resultStr
	 * @return ����ɵ��ַ������Ϊ���ֺͷ���
	 */
	public static UpAndDonwBean initUpAndDonwBean(String resultStr){
		UpAndDonwBean bean = new UpAndDonwBean();
		bean.initUpAndDown(resultStr);
		return bean;
	}
	
	
	/**
	 * @param up
	 * @param down
	 * @param upSymbol
	 * @param downSymbol
	 * @param detectValue
	 * @return ��Ϸ��ź����� 
	 */
	public static UpAndDonwBean initResult(String up, String down, String upSymbol, String downSymbol,
			String detectValue){
		UpAndDonwBean bean = new UpAndDonwBean();
		bean.initResult(up, down, upSymbol, downSymbol, detectValue);
		return bean;
	}
}
