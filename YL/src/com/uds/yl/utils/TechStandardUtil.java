package com.uds.yl.utils;

import com.uds.yl.bean.UpAndDonwBean;

public class TechStandardUtil {
	
	
	/**
	 * @param resultStr
	 * @return 将组成的字符串拆分为数字和符号
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
	 * @return 组合符号和数字 
	 */
	public static UpAndDonwBean initResult(String up, String down, String upSymbol, String downSymbol,
			String detectValue){
		UpAndDonwBean bean = new UpAndDonwBean();
		bean.initResult(up, down, upSymbol, downSymbol, detectValue);
		return bean;
	}
}
