package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.service.ITechStandardService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.QueryUtil;

public class TechStandardServiceImpl implements ITechStandardService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ITechStandardService#getSearchItemRevison(java.lang.
	 * String, java.lang.String, java.lang.String) type������ʱ�ȴ�����
	 */
	@Override
	public List<TCComponentItemRevision> getSearchItemRevison(String name, String revision, String type) {
		List<TCComponentItemRevision> resultList = new ArrayList<>();
		// ��ȡ����
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEX.getValue());
		if (query == null) {
			MessageBox.post("��ȡ��ѯ��ʧ��", "", MessageBox.ERROR);
			return resultList;
		}
		if("".equals(revision)) revision ="*";
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "����","�汾"}, new String[] { name,revision });
		if (searchResult == null) {
			return resultList;
		}
		for (TCComponent tcComponent : searchResult) {
			if (tcComponent instanceof TCComponentItemRevision) {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) tcComponent;
				resultList.add(itemRevision);
			}
		}
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ITechStandardService#getSearchItemRevisonName(java.
	 * util.List) ����ȡ�õ��İ汾���listת��Ϊname��List
	 */
	@Override
	public List<String> getSearchItemRevisonName(List<TCComponentItemRevision> itemRevisonList) {
		List<String> nameList = new ArrayList<>();
		for (TCComponentItemRevision itemRevision : itemRevisonList) {
			try {
				String name = itemRevision.getProperty("object_name");
				nameList.add(name);
			} catch (TCException e) {
			}
		}
		return nameList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ITechStandardService#copyBomToSelectedItemRevision(com
	 * .teamcenter.rac.kernel.TCComponentItemRevision,
	 * com.teamcenter.rac.kernel.TCComponentItemRevision)
	 * ��ѡ�еİ汾��BOM�ṹ���������table��ѡ�еĽṹȥ�滻
	 */
	@Override
	public void copyBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,
			TCComponentItemRevision tableItemRevision) {

		// ���ѡ�а汾��BONM
		TCComponentBOMLine selectTopBomLine = BomUtil.getTopBomLine(selectItemRevison, Const.TechStandard.BOMNAME);
		if (selectTopBomLine == null) {// ���Ϊ��˵��û����ͼ�ʹ���һ����ͼ
			selectTopBomLine = BomUtil.setBOMViewForItemRev(selectItemRevison);
		} else {// ����null�Ļ������
			try {
				AIFComponentContext[] children = selectTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();// �����߲�ճ����
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}

		// ����table��ѡ�ж���İ汾
		TCComponentBOMLine tableSelectTopBomLine = BomUtil.getTopBomLine(tableItemRevision, Const.TechStandard.BOMNAME);
		if (tableSelectTopBomLine != null) {
			try {
				AIFComponentContext[] children = tableSelectTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					selectTopBomLine.add("",bomLine);
				}
			} catch (TCException e) {
				e.printStackTrace();
			}

		}
		
		
		try {
			TCComponentBOMWindow bomWindow = selectTopBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ITechStandardService#appendBomToSelectedItemRevision(
	 * com.teamcenter.rac.kernel.TCComponentItemRevision,
	 * com.teamcenter.rac.kernel.TCComponentItemRevision)
	 * //��ѡ�еİ汾��BOM��׷��table��ѡ�еĽṹȥ�滻
	 */
	@Override
	public void appendBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,
			TCComponentItemRevision tableItemRevision) {

		// ���ѡ�а汾��BONM
		TCComponentBOMLine selectTopBomLine = BomUtil.getTopBomLine(selectItemRevison, Const.TechStandard.BOMNAME);
		if (selectTopBomLine == null) {// ���Ϊ��˵��û����ͼ�ʹ���һ����ͼ
			selectTopBomLine = BomUtil.setBOMViewForItemRev(selectItemRevison);
		} //���ǲ��������

		// ����table��ѡ�ж���İ汾
		TCComponentBOMLine tableSelectTopBomLine = BomUtil.getTopBomLine(tableItemRevision, Const.TechStandard.BOMNAME);
		if (tableSelectTopBomLine != null) {
			try {
				AIFComponentContext[] children = tableSelectTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					TCComponent revView = bomLine.getBOMView();
					selectTopBomLine.add("",bomLine);
				}
			} catch (TCException e) {
				e.printStackTrace();
			}

		}
		
		try {
			TCComponentBOMWindow bomWindow = selectTopBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

}
