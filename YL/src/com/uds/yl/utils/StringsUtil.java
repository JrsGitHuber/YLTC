package com.uds.yl.utils;


public class StringsUtil {

	/**
	 * @param str
	 * @return
	 * �ж��ַ����Ƿ��Ǻ������ֵ��ַ���
	 */
	public static boolean isNumeric(String str) {
		if(str==null){
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {//����������
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * �ж�str�Ƿ�Ϊnull����Ϊ��
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
	 * ���ַ���ת��Ϊdouble��
	 * @param str
	 * @return
	 */
	public static Double convertStr2Double(String str){
		if(!isNumeric(str)||isEmpty(str)){//���Ǵ����ֻ����ǿ�
			return 0.0;
		}
		
		return Double.valueOf(str);
	}
	
	
	/**
	 *��������˷����ֵ����� 
	 * @param str 
	 * @return
	 */
	public static boolean isNoNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {//����������
				return true;
			}
		}
		return false;
	}
	
}
