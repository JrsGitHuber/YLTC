package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.ComplexMaterialBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IOrderFormulaExcelService;
import com.uds.yl.tcutils.BomUtil;

public class OrderFormulaExcelServiceImpl implements IOrderFormulaExcelService {

	/*
	 * (non-Javadoc) 获取所有的原料版本
	 */
	@Override
	public List<MaterialBean> getAllMaterialBeanList(TCComponentItemRevision itemRev) {
		List<MaterialBean> allMaterialBeanList = null;
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.OrderFormulaExcel.BOM_NAME);
		if (topBomLine == null)
			return null;

		allMaterialBeanList = new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				allMaterialBeanList.add(bean);
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return allMaterialBeanList;
	}

	/*
	 * (non-Javadoc) 处理可以被替代的原料
	 */
	@Override
	public void handleCanReplectMaterial(List<MaterialBean> materialBeanList) {
		for (MaterialBean originBean : materialBeanList) {
			if (originBean.alternate.equals("互替")) {
				for (MaterialBean bean : materialBeanList) {
					if (originBean.alternateItem.equals(bean.objectName)) {
						originBean.canReplace = true;
						originBean.replaceMaterialBean = bean;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc) 获取所有的小料
	 */
	@Override
	public List<MinMaterialBean> getAllMimMaterialList(List<MaterialBean> allMaterialBeanList) {
		List<MinMaterialBean> allMinMaterialBeanList = new ArrayList<>();

		// 获取所有的小料名字
		Set<String> minMaterialNameSet = new HashSet<>();
		for (MaterialBean bean : allMaterialBeanList) {
			if (!"".equals(bean.minMaterialType)) {
				minMaterialNameSet.add(bean.minMaterialType);
			}
		}

		Iterator<String> iterator = minMaterialNameSet.iterator();
		while (iterator.hasNext()) {
			String minMaterialTypeName = iterator.next();

			MinMaterialBean minMaterialBean = new MinMaterialBean();
			minMaterialBean.name = minMaterialTypeName;
			minMaterialBean.allChildsMaterial = new ArrayList<>();

			for (MaterialBean bean : allMaterialBeanList) {
				if (bean.minMaterialType.equals(minMaterialTypeName)) {
					minMaterialBean.allChildsMaterial.add(bean);
				}
			}
			allMinMaterialBeanList.add(minMaterialBean);
		}
		return allMinMaterialBeanList;
	}

	/*
	 * (non-Javadoc) 获取所有的原料BOMLine集合
	 */
	@Override
	public List<TCComponentBOMLine> getAllMaterialBomLineList(TCComponentItemRevision itemRev) {
		List<TCComponentBOMLine> allMaterialBomLineList = null;
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.OrderFormulaExcel.BOM_NAME);
		if (topBomLine == null)
			return null;

		allMaterialBomLineList = new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				allMaterialBomLineList.add(bomLine);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return allMaterialBomLineList;
	}

	/*
	 * (non-Javadoc) 获得所有的复配对象
	 */
	@Override
	public List<ComplexMaterialBean> getAllComplexMaterialBeanList(TCComponentItemRevision itemRev) {
		List<ComplexMaterialBean> allComplexMaterialBeanList = null;

		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.OrderFormulaExcel.BOM_NAME);
		if (topBomLine == null)
			return null;

		allComplexMaterialBeanList = new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				if (bomLine.hasChildren()) {
					boolean isComplex = false;
					AIFComponentContext[] childContexts = bomLine.getChildren();
					for (AIFComponentContext childContext : childContexts) {
						TCComponentBOMLine childBom = (TCComponentBOMLine) childContext.getComponent();
						if (childBom.getItem().getType().equals("U8_Material")) {
							isComplex = true;
						}
					}
					if (!isComplex) {
						continue;
					}
					ComplexMaterialBean complexMaterialBean = new ComplexMaterialBean();
					MaterialBean rootBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
					complexMaterialBean.rootMaterial = rootBean;
					complexMaterialBean.rootBomLine = bomLine;
					complexMaterialBean.allChildsMaterial = new ArrayList<>();
					complexMaterialBean.allMaterialBomLine = new ArrayList<>();

					for (AIFComponentContext childContext : childContexts) {
						TCComponentBOMLine childBom = (TCComponentBOMLine) childContext.getComponent();
						if (childBom.getItem().getType().equals("U8_Material")) {
							MaterialBean childBean = AnnotationFactory.getInstcnce(MaterialBean.class, childBom);
							complexMaterialBean.allChildsMaterial.add(childBean);
							complexMaterialBean.allMaterialBomLine.add(childBom);
						}
					}
					allComplexMaterialBeanList.add(complexMaterialBean);
				}

			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return allComplexMaterialBeanList;
	}


}
