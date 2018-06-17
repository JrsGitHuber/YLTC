package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponenetBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponentBom;

public interface IColdFormulatorService {
	//��ȡ ���BOm����
	public List<ComponentBom> getComponentBomLineList(TCComponentItemRevision formulatorRev);
	//��ȡ�䷽�µ����ʵ����ļ���
	public List<ComponenetBean> getComponentBeanList(TCComponentItemRevision formulatorRev);
	
	//��ȡ������Ϊ��ʱ��topBomLine
	public TCComponentBOMLine getCacheTopBomLine(List<ComponentBom> formulatorBomList,
			List<ComponenetBean> formulatorBeanList);
}
