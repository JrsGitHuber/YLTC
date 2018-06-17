package com.uds.yl.base;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.herb.EnvironmentVariableManager;
import com.uds.yl.herb.EnvironmentVariables;
import com.uds.yl.herb.TcPreferenceReader;

public class BaseModuleOperation extends AbstractAIFOperation {
	protected com.teamcenter.rac.kernel.TCSession m_session;
	protected AbstractAIFUIApplication m_app;
	protected void init(){
		m_app = AIFUtility.getCurrentApplication();		
		m_session = (TCSession) m_app.getSession();	
	}

	protected String GetSelectedComponentUid(){
		//用户选择的对象
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		String selUid = selComp.getUid();
		return selUid;
	}
	protected InterfaceAIFComponent GetSelectedComponent(){
		//用户选择的对象
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		return selComp;
	}
	protected String GetCurrentUserUid(){
		//当前用户
		TCComponentUser currentUser = this.m_session.getUser();
		String userUid = currentUser.getUid();
		return userUid;
	}
	protected String GetCurrentUserId(){
		//当前用户
		TCComponentUser currentUser = this.m_session.getUser();
		String userId = null;
		try {
			userId = currentUser.getUserId();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return userId;
	}
	protected void DoUserTask() {}
	
	/**
	 * @return
	 * 获取SOA的地址
	 */
	protected String GetSoaServiceAddress(){
		//SOA服务器地址
		String soaEnv = EnvironmentVariables.ENV_TC_UDSSERVER_WEB_SERVICE;
		String soaAddr = TcPreferenceReader.GetPreference(m_session, soaEnv);
		if(soaAddr == null || "".equals(soaAddr)){
			soaAddr = EnvironmentVariableManager.GetSoaServer();
		}
		
		if(soaAddr != null && !"".equals(soaAddr)){
			soaAddr = "http://" + soaAddr + "/WebCommonService.svc?wsdl";
		}
		
		return soaAddr;
	}
	
	/**
	 * @param session
	 * @param preferenceName
	 * @return
	 * 获取首选项的配置
	 */
	public static String getPreference(TCSession session, String preferenceName) {
		String[] vals = null;
		try {
			// 配置文件首选项
			TCPreferenceService preferService = session.getPreferenceService();
			preferService.getStringValues(preferenceName);
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
	
	
	/**
	 * 获得Ctrl多选的对象
	 * @return
	 */
	protected InterfaceAIFComponent[] GetSelectedComponents(){
		InterfaceAIFComponent[] comps = m_app.getTargetComponents();
		return comps;
	}
	
	@Override
	public void executeOperation() throws Exception {
		init();
		DoUserTask();
	}
}
