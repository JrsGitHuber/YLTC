package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponenetBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponentBom;

public interface IColdFormulatorService {
	//获取 组分BOm集合
	public List<ComponentBom> getComponentBomLineList(TCComponentItemRevision formulatorRev);
	//获取配方下的组分实体类的集合
	public List<ComponenetBean> getComponentBeanList(TCComponentItemRevision formulatorRev);
	
	//获取到的作为临时的topBomLine
	public TCComponentBOMLine getCacheTopBomLine(List<ComponentBom> formulatorBomList,
			List<ComponenetBean> formulatorBeanList);
}
