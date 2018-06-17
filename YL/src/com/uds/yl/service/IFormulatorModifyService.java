package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;

public interface IFormulatorModifyService {
	// ��ȡ�汾���Ѿ��е�BOM��ͼ�е�ԭ����
	List<MaterialBean> getInitBean(TCComponentItemRevision itemRevision);

	// ��ȡ�汾�����е�BOM��ͼ�е�ԭ���ϵİ汾����
	List<TCComponentItemRevision> getInitMaterialItemRevList(TCComponentItemRevision itemRevision);

	// ��ȡ��ѯ����ԭ����
	List<MaterialBean> getSearchBean(String materialName, String materialCode, String materialSupplier);

	// ��ȡ��ѯ����ԭ���ϰ汾����
	List<TCComponentItemRevision> getSearchItemRev(String materialName, String materialCode, String materialSupplier);

	// �����䷽BOM
	void createFormulatorBOM(TCComponentItemRevision itemRevision, List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);

	// ��ȡԭ���ϵ�Bean����
	List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine);

	// ��ȡָ���Bean����
	List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine);

	// ��Ϊ����ʹ�õ�һ��topBomLine
	TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList);

	// ����Ӫ���ɷֱ���
	void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList);
	
	//ɾ����ʱ�䷽��
	void deleteTempFormula();

}
