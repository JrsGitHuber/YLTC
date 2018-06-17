package com.uds.yl.controler;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.base.BaseControler;
import com.uds.yl.service.impl.PDFConvertServiceImpl;

public class PDFConvertControler implements BaseControler{

	
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		PDFConvertServiceImpl mgr = new PDFConvertServiceImpl();
		mgr.itemRev = itemRev;
		mgr.convertToPdf();
	}

}
