package com.uds.yl.controler;

import java.util.Date;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessOperation;
import com.uds.yl.base.BaseControler;
import com.uds.yl.common.Const;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.service.impl.CreativeManagementServiceImpl;

public class CreativeSecondJugeControler implements BaseControler{
	private ICreativeManagementService iCreativeManagementService = new CreativeManagementServiceImpl();
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		
	}
	
	
	/**
	 * 入口处
	 * @param selectForms
	 * @throws TCException
	 */
	public void userTask(InterfaceAIFComponent[] selectForms) throws TCException {
		for (InterfaceAIFComponent selComp : selectForms) {
			if (selComp != null && selComp instanceof TCComponentForm) {
				TCComponentForm proposalForm = (TCComponentForm) selComp;
				String objType = proposalForm.getType();
				if ("U8_InformReport".equals(objType)) {// 创意提案表类型
					String status = proposalForm.getProperty("u8_status");
					if ("pass".equals(status)) {//status是通过就可以发送流程
						//设置为发布状态
						setReleaseForm(proposalForm);
					}else{
						MessageBox.post("没通过","",MessageBox.INFORMATION);
					}
				}
			}
		}
	}
	
	
	/**
	 * @param proposalForm
	 * @throws TCException
	 * 发布流程
	 */
	public void setReleaseForm(TCComponentForm proposalForm) throws TCException {
		AbstractAIFUIApplication mApp =  AIFUtility.getCurrentApplication();
		TCSession mSession = (TCSession) mApp.getSession();
		
		TCComponentTaskTemplateType taskTemplateType = (TCComponentTaskTemplateType) mSession.getTypeComponent("EPMTaskTemplate");
		taskTemplateType.extentTemplates(TCComponentTaskTemplate.PROCESS_TEMPLATE_TYPE);
		String processName=Const.MilkScore.Second_Idea_Judg_Process;
		TCComponentTaskTemplate releaseProcess = taskTemplateType.find(processName, TCComponentTaskTemplate.PROCESS_TEMPLATE_TYPE);
		
		int types[] = { 1 };
		TCComponent components[] = {proposalForm};
		NewProcessOperation newProcessOpt = new NewProcessOperation(mSession, AIFDesktop.getActiveDesktop(), new Date() + "借用", "Realeased by programm", releaseProcess, components, types);
		if(newProcessOpt!=null){
			try {
				newProcessOpt.executeOperation();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
