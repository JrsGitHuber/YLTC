package com.uds.yl.service;

import java.util.List;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.IndexItemBean;

public interface IMaterialTechStandardExcelService {
	//根据版本获取TopLine
	public TCComponentBOMLine getTopBOMLine(TCComponentItemRevision itemRevision);
	//根据topBomLine获取原料标准指标的BOM
	public List<TCComponentBOMLine> getAllIndexBomList(TCComponentBOMLine topBomLine);
	//获取质量技术标准下的bom对应的bean实体类的集合
	public List<IndexItemBean> getAllIndexBeanList(List<TCComponentBOMLine> allIndexBomList);
	
	public List<TCComponentBOMLine> getSensoryBomList(List<TCComponentBOMLine> allIndexBomList);//感官要求的BOm集合
	public List<TCComponentBOMLine> getPhysicalBomList(List<TCComponentBOMLine> allIndexBomList);//理化指标的Bom集合
	public List<TCComponentBOMLine> getHealthBomList(List<TCComponentBOMLine> allIndexBomList);//污染物指标
	public List<TCComponentBOMLine> getMicroorganismBomList(List<TCComponentBOMLine> allIndexBomList);//微生物指标
	public List<TCComponentBOMLine> getOtherBomList(List<TCComponentBOMLine> allIndexBomList);//其他的类型暂时不使用
	
	
	public List<IndexItemBean> getSensoryBeanList(List<TCComponentBOMLine> mSensoryBomList);//感官指标的Bean集合
	public List<IndexItemBean> getPhysicalBeanList(List<TCComponentBOMLine> mPhysicalBomList);//理化指标的Bean集合
	public List<IndexItemBean> getHealthBeanList(List<TCComponentBOMLine> mHealthBomList);//污染物指标的Bean集合
	public List<IndexItemBean> getMicroorganismBeanList(List<TCComponentBOMLine> mMicroorganismBomList);//微生物指标Bean集合
	public List<IndexItemBean> getOtherBeanList(List<TCComponentBOMLine> mOtherBomList);//其他的类型的Bean集合暂时不在使用
	
}
