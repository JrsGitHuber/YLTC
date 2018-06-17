package com.uds.yl.tcutils;


import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ItemUtil {
	/**
	 * ����һ��Item
	 */
	public static TCComponentItem createtItem(String type,String name,String desc){
		
		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(type);
			String newID = item_type.getNewID();
			String newRev = item_type.getNewRev(null);
//			String type = "U8_Formula";
//			String name = "����";
//			String desc = "";
			TCComponentItem newItem = item_type.create(newID, newRev,
					type, name, desc, null);
			return newItem;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @param lawID
	 * @param lawName
	 * @param lawRevNum
	 * @return
	 * ����һ������Item
	 */
	public static TCComponentItem createtLawItemWithRevNum(String lawID,String lawName,String lawRevNum,String lawDesc){
		
		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent("U8_Law");
			String newID = lawID;
			String newRev = lawRevNum;
			String type = "U8_Law";
			String name =lawName;
			String desc = lawDesc;
			TCComponentItem newItem = item_type.create(newID, newRev,
					type, name, desc, null);
			return newItem;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * @param name �ļ��е�����
	 * @param desc �ļ��е�����
	 * @return �������ļ���
	 */
	public static TCComponentFolder createFolder(String name,String desc){
		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentFolderType fodlerType = (TCComponentFolderType)session.getTypeComponent("Folder");
			TCComponentFolder folder = fodlerType.create(name,desc,"Folder");
			
		
			return folder;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * �ж϶�����Ƿ���дȨ��
	 * @param compont
	 * @return
	 */
	public static boolean isModifiable(TCComponent compont){
		
		try {
			String isModifyFlag = compont.getProperty("is_modifiable");
			if(isModifyFlag.equals("��")||isModifyFlag.equalsIgnoreCase("no")){
				return false;
			}
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		return true;
	}
	
}
