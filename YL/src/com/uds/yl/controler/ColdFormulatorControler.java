package com.uds.yl.controler;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.base.BaseControler;
import com.uds.yl.ui.ColdFormulatorFrame;

//冷饮配方搭建器
public class ColdFormulatorControler implements BaseControler {

	
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		//对配方进行操作
		if(ColdFormulatorFrame.isShow){//如果界面正在展示就不在处理
			return ;
		}
		ColdFormulatorFrame coldFrame = new ColdFormulatorFrame(itemRev);
		coldFrame.setVisible(true);
		
	}


}
