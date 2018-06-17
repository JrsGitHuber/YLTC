package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.ComplexMaterialBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;

public interface IFinanceFormulaExcelService {
	// ��ȡ���е�ԭ�ϰ汾
	public List<MaterialBean> getAllMaterialBeanList(TCComponentItemRevision itemRev);

	// ��ȡ���е�ԭ�϶�Ӧ��BomLine
	public List<TCComponentBOMLine> getAllMaterialBomLineList(TCComponentItemRevision itemRev);

	// �������п��Ա������ԭ��
	public void handleCanReplectMaterial(List<MaterialBean> materialBeanList);

	// ��ȡ���е�С������
	public List<MinMaterialBean> getAllMimMaterialList(List<MaterialBean> allMaterialBeanList);

	// ��ȡ���еĸ������͵�ԭ��
	public List<ComplexMaterialBean> getAllComplexMaterialBeanList(TCComponentItemRevision itemRev);

}
