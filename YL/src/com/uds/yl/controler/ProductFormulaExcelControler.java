package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.encoding.ser.DateSerializer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.regexp.recompile;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.ComplexMaterialBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IProductFormulaExcelService;
import com.uds.yl.service.impl.ProductFormulaExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
//生产配方表
public class ProductFormulaExcelControler implements BaseControler {

	private IProductFormulaExcelService iProductFormulaExcelService = new ProductFormulaExcelServiceImpl();

	private List<MaterialBean> allMaterialBeanList;// 配方的单层的原料
	private List<TCComponentBOMLine> allMaterialBomLineList;// 配方的单层原料对应BOM

	private List<MinMaterialBean> allMinMaterialBeanLis;// 所有的小料
	private List<ComplexMaterialBean> allComplexMaterialBeanList;// 所有的复配原料

	private Workbook wb = null;
	private TCComponentItemRevision mItemRev;

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		this.mItemRev = itemRev;
		// 获取所有的单层的原料实体类
		allMaterialBeanList = iProductFormulaExcelService.getAllMaterialBeanList(itemRev);
		if (allMaterialBeanList == null ) {
			MessageBox.post("请查看配方的BOM结构", "", MessageBox.ERROR);
			return;
		}

		// 获取所有的单层的原料BOMLine
		allMaterialBomLineList = iProductFormulaExcelService.getAllMaterialBomLineList(itemRev);

		// 处理所有可以被替代的原料
		iProductFormulaExcelService.handleCanReplectMaterial(allMaterialBeanList);

		// 获取所有的小料
		allMinMaterialBeanLis = iProductFormulaExcelService.getAllMimMaterialList(allMaterialBeanList);

		// 获取所有的复配类型的原料
		allComplexMaterialBeanList = iProductFormulaExcelService.getAllComplexMaterialBeanList(itemRev);

		// 处理复配中的可以替带的原料
		for (ComplexMaterialBean complexMaterialBean : allComplexMaterialBeanList) {
			iProductFormulaExcelService.handleCanReplectMaterial(complexMaterialBean.allChildsMaterial);
		}

		
		//下载模板
		if(!downTemplate()){
			MessageBox.post("模板下载失败请检查","",MessageBox.INFORMATION);
			return;
		}
		
		// 生成报表
		{
			File inFile = new File(Const.ProductFormulaExcel.IN_EXCEL_PATH);
			File outFile = new File(Const.ProductFormulaExcel.OUT_EXCEL_PATH);
			if (outFile.exists()) {
				outFile.delete();
			}
			try {
				wb = new XSSFWorkbook(new FileInputStream(inFile));
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			//生产配方sheet
			writeProductSheetToExcel(allMaterialBeanList, allMinMaterialBeanLis);
			
			//小料sheet
			writeMinMaterialSheetToExcel(allMinMaterialBeanLis);
			
			
			//复配原料sheet
			writeComplexMaterialSheetToExcel(allComplexMaterialBeanList);
			
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
				wb.write(out);
				out.close();
				
				//上传
				if(!uploadFile(mItemRev)){//上传失败
					MessageBox.post("上传失败请检查","",MessageBox.INFORMATION);
					return ;
				}
			} catch (FileNotFoundException e1) {
			} catch (IOException e) {
			}

//			try {
//				Runtime.getRuntime().exec("cmd /c start " + Const.ProductFormulaExcel.OUT_EXCEL_PATH);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		}
		
	}

	/**
	 * @param materialBeanList  配方下的所有的原料
	 * @param minMaterialBeanList  所有的小料
	 * 生产配方sheet页
	 */
	private void writeProductSheetToExcel(List<MaterialBean> materialBeanList,
			List<MinMaterialBean> minMaterialBeanList) {

		// 设置边框
		 CellStyle cellBorderStyle = wb.createCellStyle();
		 cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		Sheet sheet = wb.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		if(sheet==null){
			sheet = wb.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// 生产配方
		}
		
		// 初始化标题：

		Row row = sheet.createRow(102);
		{
			//将合并的单元格加上边框
			Cell cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
		}

		Cell cell1 = row.createCell(1);
		Cell cell2 = row.createCell(4);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

//		cell1.setCellStyle(cellColorStyle);
		cell1.setCellStyle(cellBorderStyle);
//		cell2.setCellStyle(cellColorStyle);
		cell2.setCellStyle(cellBorderStyle);
		cell1.setCellValue("名称");
		cell2.setCellValue("投料量");

		// 写原料
		int rowIndex = 103;
		for (MaterialBean bean : materialBeanList){
			if("".equals(bean.minMaterialType)){//不是小料的
				row = sheet.createRow(rowIndex);
				Cell cell = row.createCell(1);
				cell.setCellValue(bean.objectName);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(bean.U8_inventory+"");
				rowIndex++;
				
				{
					//将合并的单元格加上边框
					cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
				}
			}
		}

		// 写小料
		for (MinMaterialBean bean : minMaterialBeanList) {
			row = sheet.createRow(rowIndex);
			Cell cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
			cell.setCellValue(bean.name);
			cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
			cell.setCellValue("一份");
			rowIndex++;
			
			
			{
				//将合并的单元格加上边框
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
				
				cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
			}
		}
		
	}
	
	
	/**
	 * @param minMaterialBeanList
	 * 写小料sheet页
	 */
	private void writeMinMaterialSheetToExcel(List<MinMaterialBean> minMaterialBeanList) {

		// 设置边框
		 CellStyle cellBorderStyle = wb.createCellStyle();
		 cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		Sheet sheet = wb.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// 小料sheet页
		if(sheet==null){
			wb.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		}
				
		// 初始化标题：

		CellRangeAddress mergeArea=new CellRangeAddress(157, 157, 1, 2);
		sheet.addMergedRegion(mergeArea);
		Row row = sheet.createRow(157);
		

		{
			//将合并的单元格加上边框
			Cell cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
		}

		Cell cell1 = row.createCell(1);cell1.setCellStyle(cellBorderStyle);
		Cell cell2 = row.createCell(3);cell2.setCellStyle(cellBorderStyle);
		Cell cell3 = row.createCell(5);cell3.setCellStyle(cellBorderStyle);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

//		cell1.setCellStyle(cellColorStyle);
//		cell2.setCellStyle(cellColorStyle);
//		cell3.setCellStyle(cellColorStyle);
		cell1.setCellValue("名称");
		cell2.setCellValue("组合");
		cell3.setCellValue("投料量");
		

		// 写原料
		int rowIndex = 158;

		// 写小料
		for (MinMaterialBean minMaterialBean : minMaterialBeanList) {
			List<MaterialBean> replaceOtherBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceBeanList = new ArrayList<>();
			for(MaterialBean materialBean : minMaterialBean.allChildsMaterial){
				if(materialBean.canReplace){
					replaceOtherBeanList.add(materialBean);
				}else {
					canBeReplaceBeanList.add(materialBean);
				}
			}
			mergeArea=new CellRangeAddress(rowIndex, rowIndex+canBeReplaceBeanList.size()-1, 1, 2);
			sheet.addMergedRegion(mergeArea);
			for(MaterialBean materialBean : canBeReplaceBeanList){
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(minMaterialBean.name);
				
				cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
				String materialName = getReplaceMaterialStr(materialBean,replaceOtherBeanList);
				cell.setCellValue(materialName);
				
				cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.U8_inventory);
				{
					//将合并的单元格加上边框
					cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
				}
			}
		}
	}

	//复配原料表
	private void writeComplexMaterialSheetToExcel(List<ComplexMaterialBean> complexMaterialBeanList){
		File inFile = new File(Const.ProductFormulaExcel.COMPLEX_IN_EXCEL_PATH);
		File outFile = new File(Const.ProductFormulaExcel.COMPLEX_OUT_EXCEL_PATH);
		
		Workbook wbTemp = null;
		try {
			wbTemp = new XSSFWorkbook(new FileInputStream(inFile));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Sheet sheet = wbTemp.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// 复配表的sheet
		if(sheet==null){
			sheet = wbTemp.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		}
		// 设置边框
		CellStyle cellBorderStyle = wbTemp.createCellStyle();
		cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);
		// 初始化标题：

		Row row = sheet.createRow(102);

		Cell cell1 = row.createCell(0);
		Cell cell2 = row.createCell(1);
		Cell cell3 = row.createCell(2);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

		cell1.setCellStyle(cellBorderStyle);
		cell2.setCellStyle(cellBorderStyle);
		cell3.setCellStyle(cellBorderStyle);
		
		cell1.setCellValue("产品名称");
		cell2.setCellValue("原料代码");
		cell3.setCellValue("替换情况");
		

		// 写复配原料
		int rowIndex = 103;

		// 写复配原料
		for (ComplexMaterialBean complexMaterialBean : complexMaterialBeanList) {
			List<MaterialBean> replaceOtherBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceBeanList = new ArrayList<>();
			List<MaterialBean> singleBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceExceptSingleBeanList = new ArrayList<>();
			for(MaterialBean materialBean : complexMaterialBean.allChildsMaterial){
				if(materialBean.canReplace){
					replaceOtherBeanList.add(materialBean);
				}else {
					canBeReplaceBeanList.add(materialBean);
				}
			}
			for(MaterialBean canBeReplaceBean : canBeReplaceBeanList){
				boolean isSingle = true;
				for(MaterialBean replaceOtherBean : replaceOtherBeanList){
					if(replaceOtherBean.alternateItem.equals(canBeReplaceBean.objectName)){
						//说明是可以被替换的
						isSingle = false;
						canBeReplaceExceptSingleBeanList.add(canBeReplaceBean);
						break;
					}
				}
				if(isSingle) singleBeanList.add(canBeReplaceBean);
			}
			
			//单独的没有替换项的原料
			CellRangeAddress mergeArea=new CellRangeAddress(rowIndex, rowIndex+complexMaterialBean.allChildsMaterial.size()-1, 0, 0);
			sheet.addMergedRegion(mergeArea);
			for(MaterialBean materialBean : singleBeanList){
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(0);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(complexMaterialBean.rootMaterial.objectName);
				
				cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.objectName);
				
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				
			}
			//存在替换项的原料
			for(MaterialBean materialBean : canBeReplaceExceptSingleBeanList){//可被替代的
				List<MaterialBean> allReplaceBeanList = new ArrayList<>();
				for(MaterialBean replaceOtehrBean : replaceOtherBeanList){
					if(materialBean.objectName.equals(replaceOtehrBean.alternateItem)){//可以用来替换该原料
						allReplaceBeanList.add(replaceOtehrBean);
					}
				}
				
				mergeArea=new CellRangeAddress(rowIndex, rowIndex+allReplaceBeanList.size(), 2, 2);
				sheet.addMergedRegion(mergeArea);
				//写allReplaceBeanList
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(0);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(complexMaterialBean.rootMaterial.objectName);
				cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.objectName);
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue("任选其一合并单元格");
				
				//可用来替换的选项
				for(MaterialBean bean :allReplaceBeanList){
					row = sheet.createRow(rowIndex++);
					
					cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
					cell.setCellValue(bean.objectName);
				}
			}
		}
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(outFile);
			wbTemp.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

//		try {
//			Runtime.getRuntime().exec("cmd /c start " + Const.ProductFormulaExcel.COMPLEX_OUT_EXCEL_PATH);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	/**
	 * @param canBeReplaceMaterialBean  要被替代的
	 * @param materialBeanList  所有的用来替代其他原料的集合
	 * @return
	 * 获取可以被替代的原料的所有可能性
	 */
	private String getReplaceMaterialStr(MaterialBean canBeReplaceMaterialBean,List<MaterialBean> materialBeanList){
		StringBuilder result = new StringBuilder();
		result.append(canBeReplaceMaterialBean.objectName);
		for(MaterialBean bean : materialBeanList){
			if(canBeReplaceMaterialBean.objectName.equals(bean.alternateItem)){
				result.append(" 或 ");
				result.append(bean.objectName);
			}
		}
		return result.toString();
	}



	/**
	 * 下载模板文件
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path);
		if(file.exists()){//存在就删除
			file.delete();
		}
		file = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
		if(file.exists()){//存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Formula_Excel_Name);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
		if(resultStrs.length==0){//下载失败
			return false;
		}
		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Complex_Excel_Name);
		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
		if(resultStrs.length==0){//下载失败
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * 将生成的文件
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path);
		 File fileComplex = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
		 if(file.exists()&&fileComplex.exists()){//存在就上传
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path,
					 DataSetUtil.DataSetType.MSExcelX, 
					 "excel", 
					 Const.ProductFormulaExcel.Product_Formula_Excel_Upload_Name);
			 
			 try {
				 component.add("IMAN_specification", dataSet);
				 
				 dataSet = DataSetUtil.setDatasetFileToTC(
						 Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path,
						 DataSetUtil.DataSetType.MSExcelX, 
						 "excel", 
						 Const.ProductFormulaExcel.Product_Complex_Excel_Upload_Name);
				 component.add("IMAN_specification", dataSet);
				 
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
	}


}
