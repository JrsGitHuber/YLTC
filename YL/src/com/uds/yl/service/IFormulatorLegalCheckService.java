package com.uds.yl.service;

import java.util.List;



import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponenetBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponentBom;

public interface IFormulatorLegalCheckService {
	//根据版本获取topLine
	public TCComponentBOMLine getTopBomLine(TCComponentItemRevision itemRev);
	
	//根据配方获取等待检查的添加剂Bom
	public List<TCComponentBOMLine> getWaitMaterialBomList(TCComponentBOMLine topBomLine); 
	
	//根据待检的添加剂的Bom获取对应的Bean  去重复的BomLine
	public List<MaterialBean> getWaitMaterialBeanList(List<TCComponentBOMLine> waitMaterialBomList);
	
	//根据配方获取待检查的指标对应的父节点 Bom
	public List<TCComponentBOMLine> getWaitIndexBomList(TCComponentBOMLine topBomLine);
	
	//根据待检查的指标对应的父节点的Bom获取指标的Bean  去重复的指标Bean
	public List<IndexItemBean> getWaitIndexBeanList(TCComponentBOMLine topBomLine);
	
	//根据id查询对应的法规
	public List<TCComponentItemRevision> getCheckLawRevList(String id);
	
	//根据id查询到的法规的对应的名称数组
	public List<String> getCheckLawNameList(List<TCComponentItemRevision> lawRevList);
	
	//获取法规中的添加剂检测项Bean
	public List<MaterialBean> getCheckMaterialBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	//获取法规中的指标检测项的Bean
	public List<IndexItemBean> getCheckIndexBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	//检查添加剂
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,List<MaterialBean> checkMaterialBeanList);
	
	//检查指标
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,List<IndexItemBean> checkIndexBeanList);
	
	//写到excel
	public void write2Excel(List<FormulatorCheckedBean> checkedBeanList);
	
	//获取检查原料的法规
	public List<TCComponentItemRevision> getCheckMaterialLawRev();
	
	
	//产生一个临时配方
	public TCComponentBOMLine getCacheTopBomLine(
			List<TCComponentItemRevision> formulatorItemRevList,List<MaterialBean> formulatorBeanList);
	
	
	//获取关联的所有的法规
	public List<TCComponentItemRevision> getRelatedIDLaws(TCComponentItemRevision lawRev);
}
