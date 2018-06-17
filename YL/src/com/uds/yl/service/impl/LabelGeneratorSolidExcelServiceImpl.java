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
	 * (non-Javadoc) ��ȡ�汾�µĵ���BOM�ṹ�µ�ָ�꼯��
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
	 * (non-Javadoc) ���ݱ�ǩ�е�ָ��˳�򴴽�һ����ʼ���ļ���
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
	 * (non-Javadoc) �����е�ָ����ȥ�ظ�������洢��sortedIndexItmeList��ȥ
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
	 * @return ��ȡ�䷽�е�ָ���Bom����  ʹ�õ������  ���������е�ָ�겢�ϲ�  ���������  100g
	 */
	@Override
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine)  {
		// ===================��ȡ���еĿ���ʹ�õ�indexItem���͵�BOM
		Double sumInventory = 0d;//������¼����䷽�е�����
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();//�洢�������е�ԭ�ϵ�˵
		List<MaterialBean> materialBeanList = new ArrayList<>();//�洢�������е�ԭ�϶�Ӧ��ʵ��Bean
		// �Ƚ���һ�������
		try {
			AIFComponentContext[] children = topBomLine.getChildren();//��һ���ԭ��
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				}else if(bomLineTemp.getItem().getType().equals("U8_Formula")){//�ǻ��۵Ļ�  bomLineTemp�ǻ���
					Double outPut = StringsUtil.convertStr2Double(bomLineTemp.getItemRevision().getProperty("u8_OutPut"));//������
					outPut =1000d;
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,bomLineTemp);//���۵�ʵ����
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
					AIFComponentContext[] baseChilds = bomLineTemp.getChildren();
					for(AIFComponentContext baseChildContext : baseChilds){//��������ĵ�һ�㲻���ǲ���Ӫ����
						TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
						bomList.add(materialBomLine);
						MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
						materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//ת��һ������˵
						materialBeanList.add(materialBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// ���� ��ȡ���е�ָ��bom
		for(int i=0;i<bomList.size();i++){
			TCComponentBOMLine bomLine = bomList.get(i);// �������Ԫ����Ӫ����������ͨ��ԭ�� ��������ݶ���Ϊָ��������
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for(AIFComponentContext indexContext : indexChilds){
 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
 					
 					if(isNutritionBom(bomLine)){//�����Ӫ�����Ļ� �����ָ���������ߵľ�ֵ
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
		
		
		// ��ʼ��allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (IndexItemBean bean : allIndexBeanList) {
			// ȥ�ظ�
			if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);
				
				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
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
		
		//����ÿ��ָ�������   
		for(IndexItemBean bean : indexBeanList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/sumInventory*100+"";//�����ܵ�Ͷ�����������˵
		}
		return indexBeanList;
	}

	/*
	 * (non-Javadoc) ���������ٽ�ֵ����Ŀ��ֵ��Ϊ0
	 */
	@Override
	public void filterZero(List<IndexItemBean> sortedIndexItemBeanList) {
		// �̶���·��
		// Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH
		// �̶���sheetҳ��
		// Const.LabelGeneratorExcel.ZERO_SHEET_NAME

		List<IndexItemBean> zeroIndexBeanList = new ArrayList<>();//

		File file = new File(Const.LabelGeneratorSolidExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================Һ̬��ǩSheetҳ
		Sheet sheet = wb.getSheet(Const.LabelGeneratorSolidExcel.ZERO_SHEET_NAME);// 0���޵�ҳ��
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
					Double sortedAverage = StringsUtil.convertStr2Double(sortedBean.bl_quantity);//�������ü��������ÿ100�˵�˵
					Double zeroAverage = StringsUtil.convertStr2Double(bean.average);
					if (sortedAverage < zeroAverage) {// ������������ֵ�Ǳ��ٽ�ֵС�ľ�Ҫ��Ϊ0��
						sortedBean.bl_quantity = "";
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc) �Ծ���0��ֵ�Ĺ��˵�
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

		// =======================Һ̬��ǩSheetҳ
		Sheet sheet = wb.getSheet(Const.LabelGeneratorSolidExcel.NRV_SHEET_NAME);// 0���޵�ҳ��
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
	 * �ж�һ��ԭ���Ƿ���Ӫ������˵
	 * @param bomLine
	 * @return 
	 */
	public boolean isNutritionBom(TCComponentBOMLine bomLine){
		boolean isNutritionFlag = false;
		try {
			//�ж��Ƿ���Ӫ����
			AIFComponentContext[] childrens2 = bomLine.getChildren();
			for(AIFComponentContext context2 : childrens2){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context2.getComponent();
				String type = tempBomLine.getItemRevision().getType();
				if("U8_MaterialRevision".equals(type)){//�����һ�������������ԭ�� ��˵�������һ��Ӫ����
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
