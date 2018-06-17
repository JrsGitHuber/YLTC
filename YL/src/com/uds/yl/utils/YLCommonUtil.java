package com.uds.yl.utils;

public class YLCommonUtil {

	/**
	 * @param up
	 *            上限
	 * @param down
	 *            下限
	 * @param upSymbol
	 *            上限符号
	 * @param downSymbol
	 *            下限符号
	 * @param detectValue
	 *            检测值描述
	 * @return
	 */
	public static String getStandardFormUPAndDown(String up, String down, String upSymbol, String downSymbol,
			String detectValue) {
		String result = "";
		if (strIsEmpty(up) && strIsEmpty(down)) {// up和down都为空则
			result = detectValue;
		}
		if (!strIsEmpty(up) && !strIsEmpty(down)) {// up和down都有值
			downSymbol = ">=".equals(downSymbol) ? "≤" : "＜";
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			result = down+downSymbol+"," + upSymbol + up;// 这里一定是大于等于小于等于
		}
		if (!strIsEmpty(up) && strIsEmpty(down)) {// 只有up有值
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			result = ","+upSymbol + up;
		}
		if (strIsEmpty(up) && !strIsEmpty(down)) {// 只有down有值
			downSymbol = ">=".equals(downSymbol) ? "≤" : "＜";
			result = down+downSymbol +",";
		}
		return result;
	}
	
	
//	/**
//	 * @param newStandard
//	 * 根据标准拆分出来 up  down 和对应的符号 要回写的内容
//	 */
//	public void setUpAndDownBynewStandard(String newStandard,) {
//		//初始化数据
//		detectValue = "";
//		down = "";
//		up = "";
//		downSymbol = "";
//		upSymbol = "";
//		
//		if(!StringsUtil.isNumeric(newStandard)){//不包含有数字
//			detectValue = newStandard;
//			down = "";
//			up = "";
//			downSymbol = "";
//			upSymbol = "";
//			return;
//		}
//		if (!strIsEmpty(newStandard)) {// 不为空
//			String[] result = newStandard.split(",");
//			
//			//下限
//			if(result[0].contains("≤")){
//				down = result[0].split("≤")[0];//数字
//				downSymbol = ">=";
//			}else if(result[0].contains("＜")){
//				down = result[0].split("＜")[0];//数字
//				downSymbol = ">";
//			}
//			
//			//上限
//			if(result[1].contains("≤")){
//				up = result[1].split("≤")[1];//数字
//				upSymbol = "<=";
//			}else if(result[1].contains("＜")) {
//				up = result[1].split("＜")[1];//数字
//				upSymbol = "<";
//			}
//		
//
//		}else if(newStandard.equals("")||newStandard==null){
//			detectValue = "";
//			down = "";
//			up = "";
//			downSymbol = "";
//			upSymbol = "";
//		}
//	}
	
	
	/**
	 * @param str
	 * @return 判断字符串是否为空
	 */
	public static boolean strIsEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

}
