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
	
//	String newSAPCode = (String) JOptionPane.showInputDialog(null,"请输入SAP物料名称：\n","提示",JOptionPane.PLAIN_MESSAGE,null,null,preSAPCode);  

	/**
	 * @param str
	 * @return
	 * 将字符串转化为double类型的数据
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
	 * @return 返回行号
	 */
	public static String getLineInfo() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return "-----" +ste.getLineNumber();
	}
	
	
	/**
	 * @param session 
	 * @return 获取系统的语言环境
	 */
	public static int getTcLanguageEnviroment() {
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		int locale = 1;
		if (session != null) {
			String tcLoc = session.getTcServerLocale();
			if (tcLoc.equalsIgnoreCase("zh_CN")) {
				return 1;//中文
			} else if (tcLoc.equalsIgnoreCase("en_US")) {
				return 0;//英文
			}
		}
		return locale;
	}

	
	/**
	 * @param session
	 * @return 	获取NewStuff文件夹
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
	 * 获取对象的所有者
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
	 * 获取当前登录用户的名字
	 */
	public static String getCurrentUserName(){
		TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		String currentUserName = session.getUserName();
		return currentUserName;
		
	}
	
	/**
	 * @return  当前的Session
	 */
	public static TCSession getSession(){
		TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		return session;
	}

	/*
	 * 获取对象的发布状态
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
     * 获取当前时间  
     *   
     * @return  
     */  
    public static String getCurrentDateStr(String pattern) {  
        Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  
        return sdf.format(date);  
    }  
}
