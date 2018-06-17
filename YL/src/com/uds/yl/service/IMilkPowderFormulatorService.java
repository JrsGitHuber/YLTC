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
	
	//����ԭ��
	public List<TCComponentItemRevision> searchMaterialResult(String name,String type,String supplier);
	
	//����ԭ�϶�Ӧ��ʵ���༯��
	public List<MaterialBean> searchMaterialBeansList(String name);
	
	//��ȡ�䷽�е�ԭ��
	public List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine);
	
	//��ȡ�䷽�е�Ӫ��ָ��
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine);
	
	//������д��excel������Ӫ���ɷֱ�
	public void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList) ;
	
	//��ȡ��Ϊ������䷽�����topbomLine
	public TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);
	
	
	//�����䷽����ͼ�ṹ
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,TCComponentItemRevision lossItemRev);

	//�����ɷ����䷽
	public void createDryFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList);

	//����Ӫ�����еĽṹ
	public void updateNutritionStruct(TCComponentItemRevision nutritionItemRev,List<TCComponentItemRevision> materialRevList,List<MaterialBean> materialBeanList);
	
	//����һ��ʪ���䷽ ����home�ļ�������
	public void createWetFormulatorInHome(List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,String name,TCComponentItemRevision lossItemRev);
	
	//�����䷽��
	public void createFormulatorExcel(TCComponentItemRevision formulatorRev);
	
	
	//����Ӫ������Ϣ��
	public void createNutritionExcel(TCComponentItemRevision formulatorRev);
	
	//��ȡ����е�ָ���IndexBeanList
	public List<IndexItemBean> getLossIndexBeanList(TCComponentBOMLine topBomLine);
	
	//��ȡʪ���䷽�е�ָ����
	public List<IndexItemBean> getIndexBeanListFromBaseBom(List<TCComponentBOMLine> baseBomList);
	
	//��ȡ�ɷ��䷽��ԭ�ϵ�ָ��
	public List<IndexItemBean> getIndexBeanListFromDryBom(TCComponentBOMLine topBomLine);
	
	//����topBomLine��ȡ����Ļ��� 
	public List<TCComponentBOMLine> getBasePowderBomLine(TCComponentBOMLine topBomLine);
	
	//���ݸɷ��䷽����������Ľ��м���������ָ�꼯�Ͻ��
	public List<IndexItemBean> getFinallIndexBeanList(TCComponentBOMLine topBomLine
			,TCComponentItemRevision wetLossItemRevsion,
			TCComponentItemRevision dryLossItemRevsion,
			TCComponentItemRevision dateLossItemRevsion);

	//����Ӫ���ɷֱ� �����̷��䷽������Ե�˵
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
