package com.uds.yl.tcutils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class LOVUtil {
	/*
	 * ��ȡLOV��ֵ��������key=ֵ,val=���� �������û��,��ֵ��Ϊ����
	 */
	public static Map<String, String> getLovPair(TCSession session, String lovId) {
		try {
			TcUtilsLovInfo lovinfo = getLovInfo(session, lovId);
			if (lovinfo != null) {
				// ʹ��LinkedHashMap��֤��ȡ��Lov˳��
				Map<String, String> lovs = new LinkedHashMap<String, String>();
				for (int i = 0; i < lovinfo.vals.length; i++) {
					String key = lovinfo.vals[i];
					String val = key;
					if (lovinfo.descriptions.length > i) {
						// ��������Ϊ��
						if (!lovinfo.descriptions[i].trim().isEmpty())
							val = lovinfo.descriptions[i];
					}
					lovs.put(key, val);
				}
				return lovs;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * ��ȡLOV�ı��ػ�ֵ�� ������key=���ػ�ֵ,val=���� �������û��,��ֵ��Ϊ����
	 */
	public static Map<String, String> getLocaleLovPair(TCSession session, String lovId) {
		try {
			TcUtilsLovInfo lovinfo = getLovInfo(session, lovId);
			if (lovinfo != null) {
				// ʹ��LinkedHashMap��֤��ȡ��Lov˳��
				Map<String, String> lovs = new LinkedHashMap<String, String>();
				for (int i = 0; i < lovinfo.vals.length; i++) {
					String key = lovinfo.vals[i];
					if (lovinfo.displayNames.length > i) {
						String disName = lovinfo.displayNames[i];
						if (disName != null) {
							key = disName;
						}
					}
					String val = key;
					if (lovinfo.descriptions.length > i) {
						// ��������Ϊ��
						if (!lovinfo.descriptions[i].trim().isEmpty())
							val = lovinfo.descriptions[i];
					}
					lovs.put(key, val);
				}
				return lovs;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * ��ȡLOV��ֵ��������key=fullname,val=���� �������û��,��ֵ��Ϊ����
	 */
	public static Map<String, String> getLovFullNames(TCSession session, String lovId) {
		try {
			TcUtilsLovInfo lovinfo = getLovInfo(session, lovId);
			if (lovinfo != null) {
				// ʹ��LinkedHashMap��֤��ȡ��Lov˳��
				Map<String, String> lovs = new LinkedHashMap<String, String>();
				for (int i = 0; i < lovinfo.fullNames.length; i++) {
					String key = lovinfo.fullNames[i];
					String val = key;
					if (lovinfo.descriptions.length > i) {
						// ��������Ϊ��
						if (!lovinfo.descriptions[i].trim().isEmpty())
							val = lovinfo.descriptions[i];
					}
					lovs.put(key, val);
				}
				return lovs;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * ��ȡLOV��ֵ
	 */
	public static List<String> getLovValues(TCSession session, String lovId) {
		try {
			TcUtilsLovInfo lovinfo = getLovInfo(session, lovId);
			if (lovinfo != null) {
				List<String> lovs = new ArrayList<String>();
				for (int i = 0; i < lovinfo.vals.length; i++) {
					lovs.add(lovinfo.vals[i]);
				}
				return lovs;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static TcUtilsLovInfo getLovInfo(TCSession session, String lovId) {
		TcUtilsLovInfo lovinfo = null;
		try {
			TCComponentListOfValuesType componentType = (TCComponentListOfValuesType) session
					.getTypeComponent("ListOfValues");//Fnd0ListOfValuesDynamic
			//ListOfValues
			TCComponentListOfValues componentValues[] = componentType.find(lovId);
			if (componentValues != null && componentValues.length > 0) {
				TCComponentListOfValues compLov = componentValues[0];
				ListOfValuesInfo info = compLov.getListOfValues();
				
				String[] values = info.getStringListOfValues();
				String[] des = info.getDescriptions();
				String[] display = info.getLOVDisplayValues();
				String[] fullNames = info.getValuesFullNames();
				String[] displayDes = info.getDispDescription();

				lovinfo = new TcUtilsLovInfo();
				lovinfo.descriptions = des;
				lovinfo.fullNames = fullNames;
				lovinfo.vals = values;
				lovinfo.displayNames = display;
				lovinfo.displayDescriptions = displayDes;
			}

		} catch (TCException ex) {
			ex.printStackTrace();
		}
		return lovinfo;
	}
	
	
	/**
	 * ��ȡ��̬LOV�ı��ػ�ֵ
	 * @param session
	 * @param lovId
	 * @return
	 */
	public static List<String> getLovDisplayNameList(TCSession session, String lovId) {
		TcUtilsLovInfo lovinfo = null;
		List<String> displayNameList= new ArrayList<String>();
		try {
			TCComponentListOfValuesType componentType = (TCComponentListOfValuesType) session
					.getTypeComponent("ListOfValues");//Fnd0ListOfValuesDynamic
			//ListOfValues
			TCComponentListOfValues componentValues[] = componentType.find(lovId);
			if (componentValues != null && componentValues.length > 0) {
				TCComponentListOfValues compLov = componentValues[0];
				ListOfValuesInfo info = compLov.getListOfValues();
				
				String[] values = info.getStringListOfValues();
				String[] des = info.getDescriptions();
				String[] display = info.getLOVDisplayValues();
				String[] fullNames = info.getValuesFullNames();
				String[] displayDes = info.getDispDescription();

				lovinfo = new TcUtilsLovInfo();
				lovinfo.descriptions = des;
				lovinfo.fullNames = fullNames;
				lovinfo.vals = values;
				lovinfo.displayNames = display;
				lovinfo.displayDescriptions = displayDes;
				
				
				for(String name : display){
					displayNameList.add(name);
				}
			}
			

		} catch (TCException ex) {
			ex.printStackTrace();
		}
		return displayNameList;
	}
	

}

class TcUtilsLovInfo {
	public String[] vals;
	public String[] descriptions;
	public String[] displayNames;
	public String[] displayDescriptions;
	public String[] fullNames; // displayName + desc
}
