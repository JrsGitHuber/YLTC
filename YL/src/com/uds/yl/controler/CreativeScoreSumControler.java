package com.uds.yl.controler;

import java.text.DecimalFormat;
import java.util.List;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.service.impl.CreativeManagementServiceImpl;

public class CreativeScoreSumControler implements BaseControler {
	private ICreativeManagementService iCreativeManagementService = new CreativeManagementServiceImpl();
	private List<TCComponentForm> oneScoreFormList ;//打分表   集合  只有战略合作的分
	private List<TCComponentForm> fourScoreFormList;//打分表   集合 另外四个分
	
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
					if (iCreativeManagementService.proposalFormIsInRule(proposalForm)) {// 没发布 并且 status是通过
						
						boolean canComputeSumScore = iCreativeManagementService.canComputeSumScore(proposalForm);
						if(!canComputeSumScore){//打分已经打满了
							MessageBox.post("两次计算次数用完","",MessageBox.INFORMATION);
							continue;
						}
						oneScoreFormList = iCreativeManagementService.getOneScoreFormList(proposalForm);
						fourScoreFormList = iCreativeManagementService.getFourScoreFormList(proposalForm);
						
						//计算总分  
						iCreativeManagementService.computeSumScore(oneScoreFormList, fourScoreFormList, proposalForm);
					}else{
						MessageBox.post("已发布或者没通过","",MessageBox.INFORMATION);
					}
				}
			}
		}
	}

}
