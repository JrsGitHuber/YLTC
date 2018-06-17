package com.uds.yl.service;

import java.util.List;
import java.util.logging.Logger;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.LawBean;

public interface ILawImportService {
	
	List<LawBean> getLawBeansFromExcel(String filePath);
	
	String getLawCategoryFromExcel(String fielPath);
	
	void createOrUpdateLawBom(TCComponentBOMLine topBomLine,List<LawBean> lawBeansFromExcel,List<TCComponentBOMLine> lawBomLineChilds ,String type,
			TCComponentFolder folder,TCComponentFolder materialFolder)throws TCException, InstantiationException, IllegalAccessException;
	
	//����Ƿ��ж���64������128���ֽڵ�����
	boolean isCanImportByName(List<LawBean> lawBeansFromExcel);
	
	//����ID��ѯ����
	public TCComponentItem searchLawItemByID(String id);
	
	//��������
	public TCComponentItemRevision createLawItem(TCComponentFolder folder,String lawID,String lawName,String lawRevNum,Logger logger);
	
	
	//���·���
	public TCComponentItemRevision updateLawItem(TCComponentItem lawItem,String lawRevNum);
	
	//��ȡר�ŵ���ָ����ļ���
	public TCComponentFolder getIndexFolder();
	
	//��ȡר�ŵ���ԭ�ϵ��ļ���
	public TCComponentFolder getMaterialFolder();
}

