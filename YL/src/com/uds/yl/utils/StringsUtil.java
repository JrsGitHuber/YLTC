package com.uds.yl.utils;


public class StringsUtil {

	/**
	 * @param str
	 * @return
	 * 判断字符串是否是含有数字的字符串
	 */
	public static boolean isNumeric(String str) {
		if(str==null){
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {//包含了数字
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 判断str是否为null或者为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		
		if(str==null){
			return true;
		}
		if("".equals(str)){
			return true;
		}
		
		return false;
	}

	
	
	/**
	 * 将字符串转化为double形
	 * @param str
	 * @return
	 */
	public static Double convertStr2Double(String str){
		if(!isNumeric(str)||isEmpty(str)){//不是纯数字或者是空
			return 0.0;
		}
		
		return Double.valueOf(str);
	}
	
	
	/**
	 *如果包含了非数字的内容 
	 * @param str 
	 * @return
	 */
	public static boolean isNoNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {//包含了数字
				return true;
			}
		}
		return false;
	}
	
}
