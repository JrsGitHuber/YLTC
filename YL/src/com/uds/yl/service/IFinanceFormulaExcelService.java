package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.ComplexMaterialBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;

public interface IFinanceFormulaExcelService {
	// 获取所有的原料版本
	public List<MaterialBean> getAllMaterialBeanList(TCComponentItemRevision itemRev);

	// 获取所有的原料对应的BomLine
	public List<TCComponentBOMLine> getAllMaterialBomLineList(TCComponentItemRevision itemRev);

	// 处理所有可以被替代的原料
	public void handleCanReplectMaterial(List<MaterialBean> materialBeanList);

	// 获取所有的小料类型
	public List<MinMaterialBean> getAllMimMaterialList(List<MaterialBean> allMaterialBeanList);

	// 获取所有的复配类型的原料
	public List<ComplexMaterialBean> getAllComplexMaterialBeanList(TCComponentItemRevision itemRev);

}
