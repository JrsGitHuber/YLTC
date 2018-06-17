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
	//���ݰ汾��ȡtopLine
	public TCComponentBOMLine getTopBomLine(TCComponentItemRevision itemRev);
	
	//�����䷽��ȡ�ȴ�������Ӽ�Bom
	public List<TCComponentBOMLine> getWaitMaterialBomList(TCComponentBOMLine topBomLine); 
	
	//���ݴ������Ӽ���Bom��ȡ��Ӧ��Bean  ȥ�ظ���BomLine
	public List<MaterialBean> getWaitMaterialBeanList(List<TCComponentBOMLine> waitMaterialBomList);
	
	//�����䷽��ȡ������ָ���Ӧ�ĸ��ڵ� Bom
	public List<TCComponentBOMLine> getWaitIndexBomList(TCComponentBOMLine topBomLine);
	
	//���ݴ�����ָ���Ӧ�ĸ��ڵ��Bom��ȡָ���Bean  ȥ�ظ���ָ��Bean
	public List<IndexItemBean> getWaitIndexBeanList(TCComponentBOMLine topBomLine);
	
	//����id��ѯ��Ӧ�ķ���
	public List<TCComponentItemRevision> getCheckLawRevList(String id);
	
	//����id��ѯ���ķ���Ķ�Ӧ����������
	public List<String> getCheckLawNameList(List<TCComponentItemRevision> lawRevList);
	
	//��ȡ�����е���Ӽ������Bean
	public List<MaterialBean> getCheckMaterialBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	//��ȡ�����е�ָ�������Bean
	public List<IndexItemBean> getCheckIndexBeanList(List<TCComponentItemRevision> checkLawRevList);
	
	//�����Ӽ�
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,List<MaterialBean> checkMaterialBeanList);
	
	//���ָ��
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,List<IndexItemBean> checkIndexBeanList);
	
	//д��excel
	public void write2Excel(List<FormulatorCheckedBean> checkedBeanList);
	
	//��ȡ���ԭ�ϵķ���
	public List<TCComponentItemRevision> getCheckMaterialLawRev();
	
	
	//����һ����ʱ�䷽
	public TCComponentBOMLine getCacheTopBomLine(
			List<TCComponentItemRevision> formulatorItemRevList,List<MaterialBean> formulatorBeanList);
	
	
	//��ȡ���������еķ���
	public List<TCComponentItemRevision> getRelatedIDLaws(TCComponentItemRevision lawRev);
}
