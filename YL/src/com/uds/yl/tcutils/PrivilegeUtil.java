package com.uds.yl.tcutils;

import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.common.UserInfoSingleFactory;

/**
 * @author uds
 *	权限工具类
 */
public class PrivilegeUtil {

	
	/**
	 * 赋予当前登录用户对于目标对象的权限
	 * @param targetObj
	 * @param privilegeKey
	 */
	public static void grantUserPrivilege(TCComponent targetObj,String privilegeKey){
		try {
			TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
			TCComponentUser currentUser =  UserInfoSingleFactory.getInstance().getUser();
			TCAccessControlService accessControlService = session.getTCAccessControlService();
			accessControlService.grantPrivilege(targetObj, currentUser, new String[]{privilegeKey});
		} catch (TCException e) {
			e.printStackTrace();
		}
	}
	
	
	public interface PRIVILEGE_KEY{
		String WRITE = "WRITE";
		String READ = "READ";
	}

}
