package com.uds.yl.tcutils;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.common.UserInfoSingleFactory;

public class PreferenceUtil {

	
	/**
	 * @param preferenceName
	 * @return
	 * 获取首选项的配置
	 */
	public static String getPreference(String preferenceName) {
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		String[] vals = null;
		try {
			// 配置文件首选项
			TCPreferenceService preferService = session.getPreferenceService();
			vals = preferService.getStringValues(preferenceName);
			if ((vals == null) || (vals.length <= 0)) {
				System.out.println("没有找到首选项配置:" + preferenceName);
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
