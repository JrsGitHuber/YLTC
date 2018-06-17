package com.uds.yl.controler;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.ICreativeManagementService;
import com.uds.yl.service.impl.CreativeManagementServiceImpl;
import com.uds.yl.ui.PassOrNotFrame;

public class FirstTrailSuccessOrNotControler implements BaseControler{
	private ICreativeManagementService iCreativeManagementService = new CreativeManagementServiceImpl();
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}
	
	public void userTask(final InterfaceAIFComponent[] selectForms) throws TCException {
		
		boolean isSelectedOk = false;
		List<String> releaseFormList = new ArrayList<>();
		for (InterfaceAIFComponent selComp : selectForms) {
			if (selComp != null && selComp instanceof TCComponentForm){
				final TCComponentForm proposalForm = (TCComponentForm) selComp;
				boolean proposalFormIsInRule = iCreativeManagementService.proposalFormCanByPass(proposalForm);
				if(proposalFormIsInRule){//ֻҪ���ǳ�ɸ�Ѿ�ͨ������
					
					AbstractCallBack callBack = new AbstractCallBack() {
						@Override
						public void setComment(String passOrNot, String comment,
								String box1,String box2,String box3,String box4,String box5) throws TCException {
							super.setComment(passOrNot, comment,box1,box2,box3,box4,box5);
							proposalForm.setProperty("u8_status", passOrNot);
							proposalForm.setProperty("u8_comment", comment);
							proposalForm.setProperty("u8_nopass1", box1);
							proposalForm.setProperty("u8_nopass2", box2);
							proposalForm.setProperty("u8_nopass3", box3);
							proposalForm.setProperty("u8_nopass4", box4);
							proposalForm.setProperty("u8_nopass5", box5);
						}
					};
					
					PassOrNotFrame frame = new PassOrNotFrame(callBack);
					frame.setVisible(true);
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
