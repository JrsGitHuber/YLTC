package com.uds.yl.service;

import java.util.List;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.IndexItemBean;

public interface IMaterialTechStandardExcelService {
	//���ݰ汾��ȡTopLine
	public TCComponentBOMLine getTopBOMLine(TCComponentItemRevision itemRevision);
	//����topBomLine��ȡԭ�ϱ�׼ָ���BOM
	public List<TCComponentBOMLine> getAllIndexBomList(TCComponentBOMLine topBomLine);
	//��ȡ����������׼�µ�bom��Ӧ��beanʵ����ļ���
	public List<IndexItemBean> getAllIndexBeanList(List<TCComponentBOMLine> allIndexBomList);
	
	public List<TCComponentBOMLine> getSensoryBomList(List<TCComponentBOMLine> allIndexBomList);//�й�Ҫ���BOm����
	public List<TCComponentBOMLine> getPhysicalBomList(List<TCComponentBOMLine> allIndexBomList);//��ָ���Bom����
	public List<TCComponentBOMLine> getHealthBomList(List<TCComponentBOMLine> allIndexBomList);//��Ⱦ��ָ��
	public List<TCComponentBOMLine> getMicroorganismBomList(List<TCComponentBOMLine> allIndexBomList);//΢����ָ��
	public List<TCComponentBOMLine> getOtherBomList(List<TCComponentBOMLine> allIndexBomList);//������������ʱ��ʹ��
	
	
	public List<IndexItemBean> getSensoryBeanList(List<TCComponentBOMLine> mSensoryBomList);//�й�ָ���Bean����
	public List<IndexItemBean> getPhysicalBeanList(List<TCComponentBOMLine> mPhysicalBomList);//��ָ���Bean����
	public List<IndexItemBean> getHealthBeanList(List<TCComponentBOMLine> mHealthBomList);//��Ⱦ��ָ���Bean����
	public List<IndexItemBean> getMicroorganismBeanList(List<TCComponentBOMLine> mMicroorganismBomList);//΢����ָ��Bean����
	public List<IndexItemBean> getOtherBeanList(List<TCComponentBOMLine> mOtherBomList);//���������͵�Bean������ʱ����ʹ��
	
}
