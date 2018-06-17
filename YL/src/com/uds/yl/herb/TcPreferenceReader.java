 /**************************************************************************************************                                      
 *                                               ��Ȩ��UDS���У�2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Oct-2015    ChenChun               Initial
 * 
 **************************************************************************************************/


package com.uds.yl.herb;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class TcPreferenceReader {
	public static String GetPreference(TCSession session, String preferenceName) {
		String[] vals = GetPreferenceValues(session,preferenceName);
		if(vals != null && vals.length > 0)
			return vals[0];
		return "";
	}
	public static String[] GetPreferenceValues(TCSession session, String preferenceName){
		try {
			// �����ļ���ѡ��
			TCPreferenceService preferService = session.getPreferenceService();
			String[] vals = preferService.getStringValues(preferenceName);
			if ((vals == null) || (vals.length <= 0)) {
				System.out.println("û���ҵ���ѡ������:" + preferenceName);
				return null;
			}
			return vals;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
