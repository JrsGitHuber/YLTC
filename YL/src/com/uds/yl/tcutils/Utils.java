package com.uds.yl.tcutils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.uds.yl.common.UserInfoSingleFactory;

public class Utils {
	
//	String newSAPCode = (String) JOptionPane.showInputDialog(null,"������SAP�������ƣ�\n","��ʾ",JOptionPane.PLAIN_MESSAGE,null,null,preSAPCode);  

	/**
	 * @param str
	 * @return
	 * ���ַ���ת��Ϊdouble���͵�����
	 */
	public static Double convertStr2Double(String str){
		Double d = 0.0d;
		if("".equals(str)||str==null){
			d = 0.0d;
		}else {
			try{
				d = Double.valueOf(str);	
			}
			catch(NumberFormatException e){
				d = 0.0d;
			}
		}
		return d;
	}
	
	
	/**
	 * @return �����к�
	 */
	public static String getLineInfo() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return "-----" +ste.getLineNumber();
	}
	
	
	/**
	 * @param session 
	 * @return ��ȡϵͳ�����Ի���
	 */
	public static int getTcLanguageEnviroment() {
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		int locale = 1;
		if (session != null) {
			String tcLoc = session.getTcServerLocale();
			if (tcLoc.equalsIgnoreCase("zh_CN")) {
				return 1;//����
			} else if (tcLoc.equalsIgnoreCase("en_US")) {
				return 0;//Ӣ��
			}
		}
		return locale;
	}

	
	/**
	 * @param session
	 * @return 	��ȡNewStuff�ļ���
	 */
	public static TCComponentFolder getNewStuff() {
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		try {
			TCComponentUser currentUser = session.getUser();
			TCComponentFolder newStuff = currentUser.getNewStuffFolder();
			return newStuff;
		} catch (TCException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * ��ȡ�����������
	 */
	public static TCComponentUser getComponentOwnerUser(TCComponent targetComp) {
		TCComponentUser ownerUser = null;
		if (targetComp != null) {
			Property propOwnerObj;
			try {
				propOwnerObj = targetComp.getPropertyObject("owning_user");
				ModelObject ownerObj = propOwnerObj.getModelObjectValue();
				if (ownerObj != null && ownerObj instanceof TCComponentUser) {
					ownerUser = (TCComponentUser) ownerObj;
				}
			} catch (NotLoadedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return ownerUser;
	}
	
	/**
	 * @return
	 * ��ȡ��ǰ��¼�û�������
	 */
	public static String getCurrentUserName(){
		TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		String currentUserName = session.getUserName();
		return currentUserName;
		
	}
	
	/**
	 * @return  ��ǰ��Session
	 */
	public static TCSession getSession(){
		TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		return session;
	}

	/*
	 * ��ȡ����ķ���״̬
	 */
	public static ModelObject[] getComponentReleasedList(TCComponent targetComp) {
		if (targetComp != null) {
			Property propObj;
			try {
				propObj = targetComp.getPropertyObject("release_status_list");
				ModelObject[] releaseObjs = propObj.getModelObjectArrayValue();
				return releaseObjs;
			} catch (NotLoadedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**  
     * ��ȡ��ǰʱ��  
     *   
     * @return  
     */  
    public static String getCurrentDateStr(String pattern) {  
        Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  
        return sdf.format(date);  
    }  
}
