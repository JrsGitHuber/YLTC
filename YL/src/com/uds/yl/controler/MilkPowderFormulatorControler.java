package com.uds.yl.controler;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.base.BaseControler;
import com.uds.yl.ui.MilkPowderFormulatorFrame;

//ÄÌ·ÛÅä·½´î½¨Æ÷
public class MilkPowderFormulatorControler implements BaseControler{

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		MilkPowderFormulatorFrame milkPowderFormulatorFrame = new MilkPowderFormulatorFrame(itemRev);
		milkPowderFormulatorFrame.setVisible(true);
	}

	
}
