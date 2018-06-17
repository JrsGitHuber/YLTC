package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public interface ITechStandardService {
	//根据查询条件获取itemRevision结果	
	List<TCComponentItemRevision> getSearchItemRevison(String name,String revision,String type);
	
	//将TComponentItemRevison转换为Name的数组
	List<String> getSearchItemRevisonName(List<TCComponentItemRevision> itemRevisonList);

	//将选中的版本的BOM结构清除掉，用table中选中的结构去替换
	void copyBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,TCComponentItemRevision tableItemRevision);
	
	//将选中的版本的BOM中追加table中选中的结构去替换
	void appendBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,TCComponentItemRevision tableItemRevision);
}
