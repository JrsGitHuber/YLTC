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
	 * String, java.lang.String, java.lang.String) type类型暂时等待需求
	 */
	@Override
	public List<TCComponentItemRevision> getSearchItemRevison(String name, String revision, String type) {
		List<TCComponentItemRevision> resultList = new ArrayList<>();
		// 获取查结果
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEX.getValue());
		if (query == null) {
			MessageBox.post("获取查询器失败", "", MessageBox.ERROR);
			return resultList;
		}
		if("".equals(revision)) revision ="*";
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "名称","版本"}, new String[] { name,revision });
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
	 * util.List) 将获取得到的版本结果list转换为name的List
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
	 * 将选中的版本的BOM结构清除掉，用table中选中的结构去替换
	 */
	@Override
	public void copyBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,
			TCComponentItemRevision tableItemRevision) {

		// 清除选中版本的BONM
		TCComponentBOMLine selectTopBomLine = BomUtil.getTopBomLine(selectItemRevison, Const.TechStandard.BOMNAME);
		if (selectTopBomLine == null) {// 如果为空说明没有视图就创建一个视图
			selectTopBomLine = BomUtil.setBOMViewForItemRev(selectItemRevison);
		} else {// 不是null的话就清空
			try {
				AIFComponentContext[] children = selectTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();// 剪切走不粘贴？
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}

		// 复制table中选中对象的版本
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
	 * //将选中的版本的BOM中追加table中选中的结构去替换
	 */
	@Override
	public void appendBomToSelectedItemRevision(TCComponentItemRevision selectItemRevison,
			TCComponentItemRevision tableItemRevision) {

		// 清除选中版本的BONM
		TCComponentBOMLine selectTopBomLine = BomUtil.getTopBomLine(selectItemRevison, Const.TechStandard.BOMNAME);
		if (selectTopBomLine == null) {// 如果为空说明没有视图就创建一个视图
			selectTopBomLine = BomUtil.setBOMViewForItemRev(selectItemRevison);
		} //就是不用清除了

		// 复制table中选中对象的版本
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
