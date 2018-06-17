package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.commands.voidDS.VoidDigitalSignatureDataBean;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;

public interface IMilkPowderFormulatorService {
	
	//搜索原料
	public List<TCComponentItemRevision> searchMaterialResult(String name,String type,String supplier);
	
	//搜索原料对应的实体类集合
	public List<MaterialBean> searchMaterialBeansList(String name);
	
	//获取配方中的原料
	public List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine);
	
	//获取配方中的营养指标
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine);
	
	//将数据写到excel中生成营养成分表
	public void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList) ;
	
	//获取作为缓存的配方对象的topbomLine
	public TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);
	
	
	//创建配方的视图结构
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,TCComponentItemRevision lossItemRev);

	//创建干法的配方
	public void createDryFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList);

	//更新营养包中的结构
	public void updateNutritionStruct(TCComponentItemRevision nutritionItemRev,List<TCComponentItemRevision> materialRevList,List<MaterialBean> materialBeanList);
	
	//创建一个湿法配方 放在home文件夹下面
	public void createWetFormulatorInHome(List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,String name,TCComponentItemRevision lossItemRev);
	
	//生成配方表
	public void createFormulatorExcel(TCComponentItemRevision formulatorRev);
	
	
	//生成营养包信息表
	public void createNutritionExcel(TCComponentItemRevision formulatorRev);
	
	//获取损耗中的指标的IndexBeanList
	public List<IndexItemBean> getLossIndexBeanList(TCComponentBOMLine topBomLine);
	
	//获取湿法配方中的指标项
	public List<IndexItemBean> getIndexBeanListFromBaseBom(List<TCComponentBOMLine> baseBomList);
	
	//获取干法配方中原料的指标
	public List<IndexItemBean> getIndexBeanListFromDryBom(TCComponentBOMLine topBomLine);
	
	//根据topBomLine获取下面的基粉 
	public List<TCComponentBOMLine> getBasePowderBomLine(TCComponentBOMLine topBomLine);
	
	//根据干法配方来对三个损耗进行计算后的最终指标集合结果
	public List<IndexItemBean> getFinallIndexBeanList(TCComponentBOMLine topBomLine
			,TCComponentItemRevision wetLossItemRevsion,
			TCComponentItemRevision dryLossItemRevsion,
			TCComponentItemRevision dateLossItemRevsion);

	//生成营养成分表 对于奶粉配方搭建器而言的说
	public void createNutritionIndexExcel(List<IndexItemBean> allIndexBeanList,TCComponentItemRevision formulatorRev);
	
	
	public List<IndexItemBean> getAllIndexBeanContainNutrition(List<IndexItemBean> indexBeanList,TCComponentBOMLine topBomLine);
	
	public List<TCComponentBOMLine> getWaitMaterialBomList(TCComponentBOMLine topBomLine);
	
	public List<MaterialBean> getWaitMaterialBeanList(List<TCComponentBOMLine> waitMaterialBomList);
	
	
	public List<TCComponentBOMLine> getWaitIndexBomList(TCComponentBOMLine topBomLine);
	
	public List<IndexItemBean> getWaitIndexBeanList(List<TCComponentBOMLine> waitIndexBomList);
	
	public List<MaterialBean> getCheckMaterialBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	public List<TCComponentItemRevision> getCheckLawRevList(String id);
	
	public List<IndexItemBean> getCheckIndexBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,
			List<MaterialBean> checkMaterialBeanList);
	
	
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,
			List<IndexItemBean> checkIndexBeanList);
	
	public void write2Excel(List<FormulatorCheckedBean> checkedBeanList);
	
	public void getCheckIndexBeanListByIndexStandard(List<IndexItemBean> chechIndexItemBeanList,TCComponentItemRevision indexStandardRev);
}
