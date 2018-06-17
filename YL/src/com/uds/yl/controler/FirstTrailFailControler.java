package com.uds.yl.controler;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.service.impl.CreativeManagementServiceImpl;
//��������ж�
public class FirstTrailFailControler implements BaseControler{
	private ICreativeManagementService iCreativeManagementService = new CreativeManagementServiceImpl();
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		
	}

	
	public void userTask(InterfaceAIFComponent[] selectForms) throws TCException {
		boolean isSelectedOk = false;
		List<String> releaseFormList = new ArrayList<>();
		for (InterfaceAIFComponent selComp : selectForms) {
			if (selComp != null && selComp instanceof TCComponentForm){
				TCComponentForm proposalForm = (TCComponentForm) selComp;
				boolean proposalFormIsInRule = iCreativeManagementService.proposalFormCanByPass(proposalForm);
				if(proposalFormIsInRule){//���Ϲ涨
					isSelectedOk = true;
					proposalForm.setProperty("u8_status", "��ͨ��");
				}else{
					String name = proposalForm.getProperty("object_name");
					releaseFormList.add(name);
				}
			}
		}
		if ( releaseFormList.size() > 0) {// ��Ȼѡ����form���Ǵ������Ѿ���������ʾ��
			MessageBox.post(releaseFormList.toString() + "�Ѿ�����", "", MessageBox.INFORMATION);
		}
		
	}
}
