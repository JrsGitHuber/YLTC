package com.uds.yl.controler;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.base.BaseControler;
import com.uds.yl.ui.ColdFormulatorFrame;

//�����䷽���
public class ColdFormulatorControler implements BaseControler {

	
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		//���䷽���в���
		if(ColdFormulatorFrame.isShow){//�����������չʾ�Ͳ��ڴ���
			return ;
		}
		ColdFormulatorFrame coldFrame = new ColdFormulatorFrame(itemRev);
		coldFrame.setVisible(true);
		
	}


}
