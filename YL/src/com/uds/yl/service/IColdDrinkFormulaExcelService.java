package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;

public interface IColdDrinkFormulaExcelService {

	//��ȡ�������ϵ���������
	List<String> getAllWillSelectedMaterialNameList(TCComponentItemRevision itemRev);
	
	//��ȡ�������ϵ�Bom����
	List<TCComponentBOMLine> getAllWillSelectedMaterialBomList(TCComponentItemRevision itemRev);
	
	//��ȡBomLine����ĵĵ���ԭ��
	List<MaterialBean> getSingleMaterialBeanList(TCComponentBOMLine selectBomLine);
	
	//��ȡBOmLine����ĸ����ԭ��  ��ԭ��������ԭ�ϵ�ԭ��
	List<MinMaterialBean> getComplexMaterialBeanList(TCComponentBOMLine selectBomLine);
}
