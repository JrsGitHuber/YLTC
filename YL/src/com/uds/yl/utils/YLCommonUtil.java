package com.uds.yl.utils;

public class YLCommonUtil {

	/**
	 * @param up
	 *            ����
	 * @param down
	 *            ����
	 * @param upSymbol
	 *            ���޷���
	 * @param downSymbol
	 *            ���޷���
	 * @param detectValue
	 *            ���ֵ����
	 * @return
	 */
	public static String getStandardFormUPAndDown(String up, String down, String upSymbol, String downSymbol,
			String detectValue) {
		String result = "";
		if (strIsEmpty(up) && strIsEmpty(down)) {// up��down��Ϊ����
			result = detectValue;
		}
		if (!strIsEmpty(up) && !strIsEmpty(down)) {// up��down����ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			result = down+downSymbol+"," + upSymbol + up;// ����һ���Ǵ��ڵ���С�ڵ���
		}
		if (!strIsEmpty(up) && strIsEmpty(down)) {// ֻ��up��ֵ
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			result = ","+upSymbol + up;
		}
		if (strIsEmpty(up) && !strIsEmpty(down)) {// ֻ��down��ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			result = down+downSymbol +",";
		}
		return result;
	}
	
	
//	/**
//	 * @param newStandard
//	 * ���ݱ�׼��ֳ��� up  down �Ͷ�Ӧ�ķ��� Ҫ��д������
//	 */
//	public void setUpAndDownBynewStandard(String newStandard,) {
//		//��ʼ������
//		detectValue = "";
//		down = "";
//		up = "";
//		downSymbol = "";
//		upSymbol = "";
//		
//		if(!StringsUtil.isNumeric(newStandard)){//������������
//			detectValue = newStandard;
//			down = "";
//			up = "";
//			downSymbol = "";
//			upSymbol = "";
//			return;
//		}
//		if (!strIsEmpty(newStandard)) {// ��Ϊ��
//			String[] result = newStandard.split(",");
//			
//			//����
//			if(result[0].contains("��")){
//				down = result[0].split("��")[0];//����
//				downSymbol = ">=";
//			}else if(result[0].contains("��")){
//				down = result[0].split("��")[0];//����
//				downSymbol = ">";
//			}
//			
//			//����
//			if(result[1].contains("��")){
//				up = result[1].split("��")[1];//����
//				upSymbol = "<=";
//			}else if(result[1].contains("��")) {
//				up = result[1].split("��")[1];//����
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
	 * @return �ж��ַ����Ƿ�Ϊ��
	 */
	public static boolean strIsEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

}
