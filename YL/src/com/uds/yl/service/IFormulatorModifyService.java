package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;

public interface IFormulatorModifyService {
	// 获取版本下已经有的BOM视图中的原材料
	List<MaterialBean> getInitBean(TCComponentItemRevision itemRevision);

	// 获取版本下已有的BOM视图中的原材料的版本对象
	List<TCComponentItemRevision> getInitMaterialItemRevList(TCComponentItemRevision itemRevision);

	// 获取查询到的原材料
	List<MaterialBean> getSearchBean(String materialName, String materialCode, String materialSupplier);

	// 获取查询到的原材料版本集合
	List<TCComponentItemRevision> getSearchItemRev(String materialName, String materialCode, String materialSupplier);

	// 创建配方BOM
	void createFormulatorBOM(TCComponentItemRevision itemRevision, List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);

	// 获取原材料的Bean对象
	List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine);

	// 获取指标的Bean对象
	List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine);

	// 作为缓存使用的一个topBomLine
	TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);

	// 生成营养成分报表
	void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList);
	
	//删除临时配方表
	void deleteTempFormula();

}
