package com.uds.yl.controler;

import java.awt.geom.RectangularShape;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.ui.YNKPIFrame;

public class YNKPIControler implements BaseControler {

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}

	//��������dateset�ķ���
	public void userTask(TCComponentDataset dataset){
		try {
			//�����жϵ�ǰ�û��Ƿ��Ǵ������û�
			TCComponentGroup[] groups = UserInfoSingleFactory.getInstance().getUser().getGroups();
			boolean flag = false;
//			for(TCComponentGroup group : groups){
//				String groupName = group.toString();
//				if(groupName.contains("ScoreGroup")){
//					flag = true;
//				}
//			}
			flag = true;//�����޸�Ϊ���д��Ȩ��û������������
			if(!flag){//���flagΪfalse�ͱ��ǵ�ǰ�û�û��Ȩ��ȥ����������־Ͳ���
				MessageBox.post("��Ǹ,��û�д��Ȩ��","",MessageBox.INFORMATION);
				return ;
			}
			TCComponent[] referenceListProperty = dataset.getReferenceListProperty("process_stage_list");
			if(referenceListProperty.length<=0){
				MessageBox.post("ѡ�е��ĵ�û����������,��Է������̵��ĵ����","",MessageBox.INFORMATION);
				return ;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		YNKPIFrame ynkpiFrame = new YNKPIFrame(dataset);
		ynkpiFrame.setVisible(true);
	}
}
