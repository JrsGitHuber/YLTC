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
import org.jacorb.poa.gui.beans.DoubleListDialog;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCVariantService.DoubleRangeAndDefault;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.ILabelGeneratorExcelService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.StringsUtil;

public class LabelGeneratorExcelServiceImpl implements ILabelGeneratorExcelService{

	/* (non-Javadoc)
	 * 获取版本下的单层BOM结构下的指标集合
	 */
	@Override
	public List<IndexItemBean> getAllIndexItemBeanList(TCComponentItemRevision itemRev) {
		List<IndexItemBean> allIndexItemBeanList = new ArrayList<>();
		List<TCComponentBOMLine> allMaterialBomLineList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.LabelGeneratorExcel.BOMNAME);
		if(topBomLine==null) return null;
		
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context :children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				if("U8_Material".equals(bomLine.getItem().getType())){
					allMaterialBomLineList.add(bomLine);
				}
			}
			
			
			for(TCComponentBOMLine materialBomLine : allMaterialBomLineList){
				children = materialBomLine.getChildren();
				for(AIFComponentContext context :children){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					if("U8_IndexItem".equals(bomLine.getItem().getType())){
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

	
	
	/* (non-Javadoc)
	 * 根据标签中的指标顺序创建一个初始化的集合
	 */
	@Override
	public List<IndexItemBean> getSortedIndexItemBeanList() {
		List<IndexItemBean> sortedIndexItemBeanList = new ArrayList<>();
		for(String name :Const.LabelGeneratorExcel.LABEL_NAME_ARRY){
			IndexItemBean bean = new IndexItemBean();
			bean.objectName = name;
			sortedIndexItemBeanList.add(bean);
		}
		return sortedIndexItemBeanList;
	}



	/* (non-Javadoc)
	 * 讲所有的指标项去重复、计算存储在sortedIndexItmeList中去
	 */
	@Override
	public List<IndexItemBean> initSortedIndexItemBeanList(List<IndexItemBean> allIndexItemBeanList,
			List<IndexItemBean> sortedIndexItemBeanList) {
		
		for(IndexItemBean sortedBean : sortedIndexItemBeanList){
			for(IndexItemBean bean : allIndexItemBeanList){
				if(sortedBean.objectName.equals(bean.objectName)){
					sortedBean.up = ""+Utils.convertStr2Double(sortedBean.up)+Utils.convertStr2Double(bean.up)*Utils.convertStr2Double(bean.bl_quantity);
					sortedBean.down = ""+Utils.convertStr2Double(sortedBean.down)+Utils.convertStr2Double(bean.down)*Utils.convertStr2Double(bean.bl_quantity);
				}else{
					sortedBean.up = "";
					sortedBean.down = "";
				}
			}
			sortedBean.average = (Utils.convertStr2Double(sortedBean.up) +  Utils.convertStr2Double(sortedBean.down))/2+"";
		}
		
		return null;
	}



	/* (non-Javadoc)
	 * 将不符合临界值的条目的值置为0
	 */
	@Override
	public void filterZero(List<IndexItemBean> sortedIndexItemBeanList) {
		//固定的路径
//		Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH
		//固定的sheet页面
//		Const.LabelGeneratorExcel.ZERO_SHEET_NAME
		
		List<IndexItemBean> zeroIndexBeanList = new ArrayList<>();//
		
		File file = new File(Const.LabelGeneratorExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================液态标签Sheet页
		Sheet sheet = wb.getSheet(Const.LabelGeneratorExcel.ZERO_SHEET_NAME);// 0界限的页面
		int start = 0;
		boolean flag = true;
		while(flag){
			IndexItemBean bean = new IndexItemBean();
			Row row = sheet.getRow(start);
			if(row ==null){
				row = sheet.createRow(start);
			}
			Cell cell = row.getCell(0);
			if(cell==null){
				cell = row.createCell(0);
			}
			bean.objectName = cell.getStringCellValue();
			if("".equals(bean.objectName)){
				flag = false;
				continue;
			}
			
			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			bean.average = cell.getNumericCellValue()+"";
			zeroIndexBeanList.add(bean);
			start++;
		}
		
		
		for(IndexItemBean sortedBean : sortedIndexItemBeanList){
			for(IndexItemBean bean : zeroIndexBeanList){
				if(sortedBean.objectName.equals(bean.objectName)){
					Double sortedAverage = StringsUtil.convertStr2Double(sortedBean.average);
					Double zeroAverage = StringsUtil.convertStr2Double(bean.average);
					if(sortedAverage<zeroAverage){//如果计算出来的值是比临界值小的就要置为0了
						sortedBean.average = "";
					}
				}
			}
		}
	}



	/* (non-Javadoc)
	 * 对经过0界值的过滤的
	 */
	@Override
	public void NRVCompute(List<IndexItemBean> sortedIndexItemBeanList) {
		List<IndexItemBean> nrvIndexBeanList = new ArrayList<>();//
		
		File file = new File(Const.LabelGeneratorExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================液态标签Sheet页
		Sheet sheet = wb.getSheet(Const.LabelGeneratorExcel.NRV_SHEET_NAME);// 0界限的页面
		int start = 1;
		boolean flag = true;
		while(flag){
			IndexItemBean bean = new IndexItemBean();
			Row row = sheet.getRow(start);
			if(row==null){
				row = sheet.createRow(start);
			}
			Cell cell = row.getCell(0);
			if(cell==null){
				cell = row.createCell(0);
			}
			bean.objectName = cell.getStringCellValue();
			if("".equals(bean.objectName)){
				flag = false;
				continue;
			}
			
			cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			bean.average = cell.getNumericCellValue()+"";
			nrvIndexBeanList.add(bean);
			start++;
		}
		
		
		for(IndexItemBean sortedBean : sortedIndexItemBeanList){
			for(IndexItemBean bean : nrvIndexBeanList){
				if(sortedBean.objectName.equals(bean.objectName)){
					Double sortedAverage = StringsUtil.convertStr2Double(sortedBean.average);
					Double nrvAverage = StringsUtil.convertStr2Double(bean.average);
					sortedBean.average = (sortedAverage/nrvAverage)*100+"%";
				}
			}
		}
	}
	
}
