package com.uds.yl.tcutils;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.common.UserInfoSingleFactory;

public class PreferenceUtil {

	
	/**
	 * @param preferenceName
	 * @return
	 * ��ȡ��ѡ�������
	 */
	public static String getPreference(String preferenceName) {
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		String[] vals = null;
		try {
			// �����ļ���ѡ��
			TCPreferenceService preferService = session.getPreferenceService();
			vals = preferService.getStringValues(preferenceName);
			if ((vals == null) || (vals.length <= 0)) {
				System.out.println("û���ҵ���ѡ������:" + preferenceName);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(vals != null && vals.length > 0)
			return vals[0];
		return "";
	}
	
}
