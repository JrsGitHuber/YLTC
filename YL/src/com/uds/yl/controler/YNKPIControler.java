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

	//用来处理dateset的方法
	public void userTask(TCComponentDataset dataset){
		try {
			//首先判断当前用户是否是打分组的用户
			TCComponentGroup[] groups = UserInfoSingleFactory.getInstance().getUser().getGroups();
			boolean flag = false;
//			for(TCComponentGroup group : groups){
//				String groupName = group.toString();
//				if(groupName.contains("ScoreGroup")){
//					flag = true;
//				}
//			}
			flag = true;//后来修改为都有打分权限没有了这个打分组
			if(!flag){//如果flag为false就表是当前用户没有权限去个这个人评分就不让
				MessageBox.post("抱歉,你没有打分权限","",MessageBox.INFORMATION);
				return ;
			}
			TCComponent[] referenceListProperty = dataset.getReferenceListProperty("process_stage_list");
			if(referenceListProperty.length<=0){
				MessageBox.post("选中的文档没有在流程中,请对发布流程的文档打分","",MessageBox.INFORMATION);
				return ;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		YNKPIFrame ynkpiFrame = new YNKPIFrame(dataset);
		ynkpiFrame.setVisible(true);
	}
}
