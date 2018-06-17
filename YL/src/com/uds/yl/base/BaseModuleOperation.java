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
		//�û�ѡ��Ķ���
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		String selUid = selComp.getUid();
		return selUid;
	}
	protected InterfaceAIFComponent GetSelectedComponent(){
		//�û�ѡ��Ķ���
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		return selComp;
	}
	protected String GetCurrentUserUid(){
		//��ǰ�û�
		TCComponentUser currentUser = this.m_session.getUser();
		String userUid = currentUser.getUid();
		return userUid;
	}
	protected String GetCurrentUserId(){
		//��ǰ�û�
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
	 * ��ȡSOA�ĵ�ַ
	 */
	protected String GetSoaServiceAddress(){
		//SOA��������ַ
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
	 * ��ȡ��ѡ�������
	 */
	public static String getPreference(TCSession session, String preferenceName) {
		String[] vals = null;
		try {
			// �����ļ���ѡ��
			TCPreferenceService preferService = session.getPreferenceService();
			preferService.getStringValues(preferenceName);
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
	
	
	/**
	 * ���Ctrl��ѡ�Ķ���
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
