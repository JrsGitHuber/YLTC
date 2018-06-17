package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public interface ITechStandardService {
	//���ݲ�ѯ������ȡitemRevision���	
	List<TCComponentItemRevision> getSearchItemRevison(String name,String revision,String type);
	
	//��TComponentItemRevisonת��ΪName������
	List<String> getSearchItemRevisonName(List<TCComponentItemRevision> itemRevisonList);

	//��ѡ�еİ汾��BOM�ṹ���������table��ѡ�еĽṹȥ�滻
	void copyBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,TCComponentItemRevision tableItemRevision);
	
	//��ѡ�еİ汾��BOM��׷��table��ѡ�еĽṹȥ�滻
	void appendBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,TCComponentItemRevision tableItemRevision);
}
