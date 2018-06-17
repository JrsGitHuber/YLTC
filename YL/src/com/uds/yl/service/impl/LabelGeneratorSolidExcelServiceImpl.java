package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.ILabelGeneratorSolidExcelService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.StringsUtil;

public class LabelGeneratorSolidExcelServiceImpl implements ILabelGeneratorSolidExcelService{
	/*
	 * (non-Javadoc) 获取版本下的单层BOM结构下的指标集合
	 */
	@Override
	public List<IndexItemBean> getAllIndexItemBeanList(TCComponentItemRevision itemRev) {
		List<IndexItemBean> allIndexItemBeanList = new ArrayList<>();
		List<TCComponentBOMLine> allMaterialBomLineList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.LabelGeneratorExcel.BOMNAME);
		if (topBomLine == null)
			return null;

		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				if ("U8_Material".equals(bomLine.getItem().getType())) {
					allMaterialBomLineList.add(bomLine);
				}
			}

			for (TCComponentBOMLine materialBomLine : allMaterialBomLineList) {
				children = materialBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					if ("U8_IndexItem".equals(bomLine.getItem().getType())) {
						IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
						allIndexItemBeanList.add(bean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return allIndexItemBeanList;
	}

	/*
	 * (non-Javadoc) 根据标签中的指标顺序创建一个初始化的集合
	 */
	@Override
	public List<IndexItemBean> getSortedIndexItemBeanList() {
		List<IndexItemBean> sortedIndexItemBeanList = new ArrayList<>();
		for (String name : Const.LabelGeneratorExcel.LABEL_NAME_ARRY) {
			IndexItemBean bean = new IndexItemBean();
			bean.objectName = name;
			sortedIndexItemBeanList.add(bean);
		}
		return sortedIndexItemBeanList;
	}

	/*
	 * (non-Javadoc) 讲所有的指标项去重复、计算存储在sortedIndexItmeList中去
	 */
	@Override
	public List<IndexItemBean> initSortedIndexItemBeanList(List<IndexItemBean> allIndexItemBeanList,
			List<IndexItemBean> sortedIndexItemBeanList) {

		List<IndexItemBean> resutlBeanList = new ArrayList<IndexItemBean>();
		for (IndexItemBean sortedBean : sortedIndexItemBeanList) {
			IndexItemBean itemBean = new IndexItemBean();
			itemBean.objectName = sortedBean.objectName;
			for (IndexItemBean bean : allIndexItemBeanList) {
				if (sortedBean.objectName.equals(bean.objectName)) {
					itemBean.bl_quantity = bean.bl_quantity;
					itemBean.U8_inventory = bean.U8_inventory;
				}
			}
			resutlBeanList.add(itemBean);
		}
		return resutlBeanList;
	}
	
	
	/**
	 * @param topBomLine
	 * @return 获取配方中的指标的Bom对象  使用的是这个  计算了所有的指标并合并  计算出比例  100g
	 */
	@Override
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine)  {
		// ===================获取所有的可以使用的indexItem类型的BOM
		Double sumInventory = 0d;//用来记录这个配方中的总量
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();//存储的是所有的原料的说
		List<MaterialBean> materialBeanList = new ArrayList<>();//存储的是所有的原料对应的实体Bean
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();//第一层的原料
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				}else if(bomLineTemp.getItem().getType().equals("U8_Formula")){//是基粉的话  bomLineTemp是基粉
					Double outPut = StringsUtil.convertStr2Double(bomLineTemp.getItemRevision().getProperty("u8_OutPut"));//出粉量
					outPut =1000d;
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,bomLineTemp);//基粉的实体列
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
					AIFComponentContext[] baseChilds = bomLineTemp.getChildren();
					for(AIFComponentContext baseChildContext : baseChilds){//基粉下面的第一层不管是不是营养包
						TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
						bomList.add(materialBomLine);
						MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
						materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//转换一下量的说
						materialBeanList.add(materialBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// 遍历 获取所有的指标bom
		for(int i=0;i<bomList.size();i++){
			TCComponentBOMLine bomLine = bomList.get(i);// 不论这个元素是营养包还是普通的原料 里面的内容都作为指标来看待
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for(AIFComponentContext indexContext : indexChilds){
 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
 					
 					if(isNutritionBom(bomLine)){//如果是营养包的话 下面的指标是上下线的均值
 						indexItemBean.bl_quantity = (StringsUtil.convertStr2Double(indexItemBean.up)+StringsUtil.convertStr2Double(indexItemBean.down))/2+"";
 					}
 					indexItemBean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)*StringsUtil.convertStr2Double(indexItemBean.bl_quantity)/100+"";
 					
					allIndexBomList.add(indexBomLine);
					allIndexBeanList.add(indexItemBean);
				}
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		
		// 初始化allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (IndexItemBean bean : allIndexBeanList) {
			// 去重复
			if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);
				
				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// 标记过之后就直接相加
					exitBean.U8_inventory = inventory + "";
				} else {
					exitBean.down = inventory+exitInventory+ "";
					exitBean.isFirst = false;
				}

			} else {
				indexBeanList.add(bean);
				nameList.add(bean.objectName);
			}
		}
		
		//计算每个指标的数量   
		for(IndexItemBean bean : indexBeanList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/sumInventory*100+"";//根据总的投料量来计算的说
		}
		return indexBeanList;
	}

	/*
	 * (non-Javadoc) 将不符合临界值的条目的值置为0
	 */
	@Override
	public void filterZero(List<IndexItemBean> sortedIndexItemBeanList) {
		// 固定的路径
		// Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH
		// 固定的sheet页面
		// Const.LabelGeneratorExcel.ZERO_SHEET_NAME

		List<IndexItemBean> zeroIndexBeanList = new ArrayList<>();//

		File file = new File(Const.LabelGeneratorSolidExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================液态标签Sheet页
		Sheet sheet = wb.getSheet(Const.LabelGeneratorSolidExcel.ZERO_SHEET_NAME);// 0界限的页面
		int start = 0;
		boolean flag = true;
		while (flag) {
			IndexItemBean bean = new IndexItemBean();
			Row row = sheet.getRow(start);
			if (row == null) {
				row = sheet.createRow(start);
			}
			Cell cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			bean.objectName = cell.getStringCellValue();
			if ("".equals(bean.objectName)) {
				flag = false;
				continue;
			}

			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			bean.average = cell.getNumericCellValue() + "";
			zeroIndexBeanList.add(bean);
			start++;
		}

		for (IndexItemBean sortedBean : sortedIndexItemBeanList) {
			for (IndexItemBean bean : zeroIndexBeanList) {
				if (sortedBean.objectName.equals(bean.objectName)) {
					Double sortedAverage = StringsUtil.convertStr2Double(sortedBean.bl_quantity);//这里是用计算出来的每100克的说
					Double zeroAverage = StringsUtil.convertStr2Double(bean.average);
					if (sortedAverage < zeroAverage) {// 如果计算出来的值是比临界值小的就要置为0了
						sortedBean.bl_quantity = "";
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc) 对经过0界值的过滤的
	 */
	@Override
	public void NRVCompute(List<IndexItemBean> sortedIndexItemBeanList) {

		List<IndexItemBean> nrvIndexBeanList = new ArrayList<>();//

		File file = new File(Const.LabelGeneratorSolidExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================液态标签Sheet页
		Sheet sheet = wb.getSheet(Const.LabelGeneratorSolidExcel.NRV_SHEET_NAME);// 0界限的页面
		int start = 1;
		boolean flag = true;
		while (flag) {
			IndexItemBean bean = new IndexItemBean();
			Row row = sheet.getRow(start);
			if (row == null) {
				row = sheet.createRow(start);
			}
			Cell cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			bean.objectName = cell.getStringCellValue();
			if ("".equals(bean.objectName)) {
				flag = false;
				continue;
			}

			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			bean.average = cell.getNumericCellValue() + "";
			nrvIndexBeanList.add(bean);
			start++;
		}

		for (IndexItemBean sortedBean : sortedIndexItemBeanList) {
			for (IndexItemBean bean : nrvIndexBeanList) {
				if (sortedBean.objectName.equals(bean.objectName)) {
					Double sortedAverage = StringsUtil.convertStr2Double(sortedBean.bl_quantity);
					Double nrvAverage = StringsUtil.convertStr2Double(bean.average);
					sortedBean.bl_quantity = (sortedAverage / nrvAverage) * 100 + "%";
				}
			}
		}
	}
	
	
	/**
	 * 判断一个原料是否是营养包的说
	 * @param bomLine
	 * @return 
	 */
	public boolean isNutritionBom(TCComponentBOMLine bomLine){
		boolean isNutritionFlag = false;
		try {
			//判断是否是营养包
			AIFComponentContext[] childrens2 = bomLine.getChildren();
			for(AIFComponentContext context2 : childrens2){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context2.getComponent();
				String type = tempBomLine.getItemRevision().getType();
				if("U8_MaterialRevision".equals(type)){//如果第一层下面包含的是原料 就说明这个是一个营养包
					isNutritionFlag = true;
					break;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isNutritionFlag;
	}
}
