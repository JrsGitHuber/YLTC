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
	private List<TCComponentForm> oneScoreFormList ;//��ֱ�   ����  ֻ��ս�Ժ����ķ�
	private List<TCComponentForm> fourScoreFormList;//��ֱ�   ���� �����ĸ���
	
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}

	/**
	 * ��ڴ�
	 * @param selectForms
	 * @throws TCException
	 */
	public void userTask(InterfaceAIFComponent[] selectForms) throws TCException {
		for (InterfaceAIFComponent selComp : selectForms) {
			if (selComp != null && selComp instanceof TCComponentForm) {
				TCComponentForm proposalForm = (TCComponentForm) selComp;
				String objType = proposalForm.getType();
				if ("U8_InformReport".equals(objType)) {// �����᰸������
					if (iCreativeManagementService.proposalFormIsInRule(proposalForm)) {// û���� ���� status��ͨ��
						
						boolean canComputeSumScore = iCreativeManagementService.canComputeSumScore(proposalForm);
						if(!canComputeSumScore){//����Ѿ�������
							MessageBox.post("���μ����������","",MessageBox.INFORMATION);
							continue;
						}
						oneScoreFormList = iCreativeManagementService.getOneScoreFormList(proposalForm);
						fourScoreFormList = iCreativeManagementService.getFourScoreFormList(proposalForm);
						
						//�����ܷ�  
						iCreativeManagementService.computeSumScore(oneScoreFormList, fourScoreFormList, proposalForm);
					}else{
						MessageBox.post("�ѷ�������ûͨ��","",MessageBox.INFORMATION);
					}
				}
			}
		}
	}

}
