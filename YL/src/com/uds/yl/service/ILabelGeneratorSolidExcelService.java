package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.IndexItemBean;

public interface ILabelGeneratorSolidExcelService {
	// 获取单层BOM下的所有的指标项目
	List<IndexItemBean> getAllIndexItemBeanList(TCComponentItemRevision itemRev);

	// 新建的空的以标签中排好顺序的指标为顺序创建一个List
	List<IndexItemBean> getSortedIndexItemBeanList();

	// 讲所有的指标项进行合并计算存储在搜人特点IndexItemBeanList中
	List<IndexItemBean> initSortedIndexItemBeanList(List<IndexItemBean> allIndexItemBeanList,
			List<IndexItemBean> sortedIndexItemBeanList);

	// 对soretdIndexItemBean再进行过滤 将不符合0界值的值置位0
	void filterZero(List<IndexItemBean> sortedIndexItemBeanList);

	// 对经过临界值计算的sortedIndexItemBean计算他们的NRV值
	void NRVCompute(List<IndexItemBean> sortedIndexItemBeanList);
	
	//获取配方下的所有指标条目  营养包中营养素作为指标的说
	List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine);
}
