package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;

public interface IColdDrinkFormulaExcelService {

	//获取所有组料的名称数组
	List<String> getAllWillSelectedMaterialNameList(TCComponentItemRevision itemRev);
	
	//获取所有组料的Bom对象
	List<TCComponentBOMLine> getAllWillSelectedMaterialBomList(TCComponentItemRevision itemRev);
	
	//获取BomLine下面的的单层原料
	List<MaterialBean> getSingleMaterialBeanList(TCComponentBOMLine selectBomLine);
	
	//获取BOmLine下面的复配的原料  既原料下面有原料的原料
	List<MinMaterialBean> getComplexMaterialBeanList(TCComponentBOMLine selectBomLine);
}
