package com.uds.yl.service;

import java.util.List;
import java.util.Set;

import javax.swing.JTable;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.TechStandarTableBean;


public interface ITechStandarModifyService {

	//根据itemId获取对应的法规的版本
//	public List<TCComponentItemRevision> getSearchLawItemRevisionList(String itemId,String name);
	
	//根据指标的类别 和名称 查找指标的版本
	public List<TCComponentItemRevision> getSearchIndexItemRevsionList(String indexType,String indexName);
	
	
	//获取选中的质量技术标准和法规中的所有的技术指标的名称集合
	public Set<String> getAllIndexItemNames(TCComponentItemRevision revision,List<TCComponentItemRevision> selectLawList);
	
	//根据版本号获取上一个版本号的质量技术标准的版本
	public TCComponentItemRevision getOriginRev(String revNum,String revItemID);
	
	//根据名称来便利所有的版本进行过滤，得到最后的TechTabelBean集合
	public List<TechStandarTableBean> getAllTableBeans(List<TCComponentItemRevision> allRevs,boolean hasPreRev,List<TechStandarTableBean> indexBeanList);
	
	//验证逻辑
	public boolean vertifyStandardIsOk(List<TechStandarTableBean> allTechTableBean,JTable table);
	
	//根据是是否有上一个版本进行处理
	public void writeBack2Tc(TCComponentItemRevision techItemRev,List<TechStandarTableBean> allTechBeans,List<TCComponentItemRevision> allItemRevision,boolean hasPreRev);
	
	//将选中的技术标准的版本中的BOM信息写到tableBeans中去，即newStandard属性
	public void getNewStatdard(TCComponentItemRevision itemRev,List<TechStandarTableBean> techTableBeanList);
	
	//获取选中技术标准中的指标的Bean  名称和单位
	public List<TechStandarTableBean> getIndexFormSelectedIndexRev(TCComponentItemRevision itemRevision,int lawsNum);
}
