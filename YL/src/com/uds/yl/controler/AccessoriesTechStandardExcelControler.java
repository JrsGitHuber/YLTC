package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IMaterialTechStandardExcelService;
import com.uds.yl.service.impl.MaterialTechStandardExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;

//辅料技术标准报表
public class AccessoriesTechStandardExcelControler implements BaseControler {
	private IMaterialTechStandardExcelService imaterialTechStandardService = new MaterialTechStandardExcelServiceImpl();
	
	private TCComponentBOMLine topBomLine = null;//技术标准版本的BOM的topbomline
	private List<TCComponentBOMLine> allIndexBomList = null;//技术标准中的所有指标项BOM集合
	private List<IndexItemBean> allIndexBeanList = null;//技术标准中所有指标项的Bean集合
	
	private int mIncrement = 0;//增量  从第二种类型开始都要累计这个增量
	private int mSensoryStart = 130;//感官要求开始行号 *
	private int mPhysicalStart = 142;//理化指标开始行号 *
	private int mHealthStart = 152;//污染物指标(替换卫生安全)
	private int mMicroorganismStart = 161;//微生物指标开始行号*
	private int mOtherStart = 170;//其他指标开始行号
	
	private List<TCComponentBOMLine> mSensoryBomList = new ArrayList<>();//感官要求的BOm集合
	private List<TCComponentBOMLine> mPhysicalBomList = new ArrayList<>();//理化指标的Bom集合
	private List<TCComponentBOMLine> mHealthBomList = new ArrayList<>();//污染物指标
	private List<TCComponentBOMLine> mMicroorganismBomList = new ArrayList<>();//微生物指标
	private List<TCComponentBOMLine> mOtherBomList = new ArrayList<>();//其他的类型暂时不使用
	
	private List<IndexItemBean> mSensoryBeanList = new ArrayList<>();//感官指标的Bean集合
	private List<IndexItemBean> mPhysicalBeanList = new ArrayList<>();//理化指标的Bean集合
	private List<IndexItemBean> mHealthBeanList = new ArrayList<>();//污染物指标的Bean集合
	private List<IndexItemBean> mMicroorganismBeanList = new ArrayList<>();//微生物指标Bean集合
	private List<IndexItemBean> mOtherBeanList = new ArrayList<>();//其他的类型的Bean集合暂时不在使用
	
	
	
	//指标类型来自于属性u8_category
	//
	public void userTask(TCComponentItemRevision itemRev) {
		//下载模板
		if(!downTemplate()){
			MessageBox.post("模板下载失败","",MessageBox.INFORMATION);
			return;
		}
		
		
		
		
		//根据配方获取配方的topBOM
		 topBomLine=imaterialTechStandardService.getTopBOMLine(itemRev);
		 if(topBomLine==null){
				MessageBox.post("请检查技术标准的BOM结构","",MessageBox.ERROR);
				return;
		 }
		//根据配方获取原辅料标准的的BOM
		 allIndexBomList = imaterialTechStandardService.getAllIndexBomList(topBomLine);
		 //获取技术标准下的所有指标的Bean集合
		 allIndexBeanList = imaterialTechStandardService.getAllIndexBeanList(allIndexBomList);
		 
		//初始化 获取质量技术标准下的不同分类的bom集合和bom对应的实体类的集合
		mSensoryBomList = imaterialTechStandardService.getSensoryBomList(allIndexBomList);
		mPhysicalBomList = imaterialTechStandardService.getPhysicalBomList(allIndexBomList);
		mHealthBomList = imaterialTechStandardService.getHealthBomList(allIndexBomList);
		mMicroorganismBomList = imaterialTechStandardService.getMicroorganismBomList(allIndexBomList);

		mSensoryBeanList = imaterialTechStandardService.getSensoryBeanList(mSensoryBomList);
		mPhysicalBeanList = imaterialTechStandardService.getPhysicalBeanList(mPhysicalBomList);
		mHealthBeanList = imaterialTechStandardService.getHealthBeanList(mHealthBomList);
		mMicroorganismBeanList = imaterialTechStandardService.getMicroorganismBeanList(mMicroorganismBomList);
		
		
		// 判断BOM的不同的类型填写在excel中的固定位置
		File inFile = new File(Const.MaterialorAccIndexStandard.MATERIAL_INDEXSTANDARD_EXCEL_INPUT_PATH);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet("Sheet1");
			
			//每一块的开始都要累加一个值
			
			
			/*
			 * 1.感官指标
			 * 2.理化指标
			 * 3.污染物指标
			 * 4.微生物指标
			 * 每次写一行就插入一行 增量++，如果是分块的最后一个的话就直接写一行但是不插入一行了
			 * */
			
			//1.感官指标  mSensoryBomList  mSensoryBeanList
			for(int i=0;i<mSensoryBeanList.size();i++){
				
				//先合并单元格
				CellRangeAddress cra=new CellRangeAddress(mSensoryStart, mSensoryStart, 1, 3);        
			    sheet.addMergedRegion(cra);  
			    //合并单元格第2个字段
			    cra=new CellRangeAddress(mSensoryStart,mSensoryStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //合并单元格第3个字段
			    cra=new CellRangeAddress(mSensoryStart,mSensoryStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
			       
				IndexItemBean bean = mSensoryBeanList.get(i);
				Cell cell = getCell(sheet, mSensoryStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mSensoryStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mSensoryStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				if(i<mSensoryBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(mSensoryStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//最后一个增量不增加1
				}
			}
			
			//2.理化指标 mPhysicalBomList mPhysicalBeanList
			mPhysicalStart = mPhysicalStart+mIncrement;//根据之前的增量的值
			for(int i=0;i<mPhysicalBeanList.size();i++){
				
				//先合并单元格
				CellRangeAddress cra=new CellRangeAddress(mPhysicalStart, mPhysicalStart, 1, 3);        
			    sheet.addMergedRegion(cra);  
			    //合并单元格第2个字段
			    cra=new CellRangeAddress(mPhysicalStart,mPhysicalStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //合并单元格第3个字段
			    cra=new CellRangeAddress(mPhysicalStart,mPhysicalStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
				
				IndexItemBean bean = mPhysicalBeanList.get(i);
				Cell cell = getCell(sheet, mPhysicalStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mPhysicalStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mPhysicalStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				
				if(i<mPhysicalBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(mPhysicalStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//最后一个增量不增加1
				}
			}
			
			//3.污染物指标  mHealthBomList   mHealthBeanList
			mHealthStart = mHealthStart +mIncrement;
			for(int i=0;i<mHealthBeanList.size();i++){
				
				//先合并单元格
				CellRangeAddress cra=new CellRangeAddress(mHealthStart, mHealthStart, 1, 3);        
			    sheet.addMergedRegion(cra);
			    //合并单元格第2个字段
			    cra=new CellRangeAddress(mHealthStart,mHealthStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //合并单元格第3个字段
			    cra=new CellRangeAddress(mHealthStart,mHealthStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
				
				IndexItemBean bean = mHealthBeanList.get(i);
				Cell cell = getCell(sheet, mHealthStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mHealthStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mHealthStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				
				if(i<mHealthBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(mHealthStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//最后一个增量不增加1
				}
			}
			
			//4.微生物指标  mMicroorganismBomList mMicroorganismBeanList
			mMicroorganismStart = mMicroorganismStart + mIncrement;
			for(int i=0;i<mMicroorganismBeanList.size();i++){
				
				//合并单元格第1个字段
				CellRangeAddress cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 1, 3);        
			    sheet.addMergedRegion(cra); 
			    //合并单元格第2个字段
			    cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //合并单元格第3个字段
			    cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
			    
				IndexItemBean bean = mHealthBeanList.get(i);
				Cell cell = getCell(sheet, mMicroorganismStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mMicroorganismStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mMicroorganismStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				if(i<mMicroorganismBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(mMicroorganismStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//最后一个增量不增加1
				}
			}
	        
			//保存
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 
		if(!uploadFile(itemRev)){
			MessageBox.post("文件上传失败","",MessageBox.INFORMATION);
			return;
		}
		MessageBox.post("OK","",MessageBox.INFORMATION);
	}
	
	
	/**
	 * @param cell
	 * @param wb
	 * 为单元格设置边框
	 */
	private void setCellBorder(Cell cell,Workbook wb){
		 CellStyle cellStyle = wb.createCellStyle();
	     cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	     
	     cell.setCellStyle(cellStyle);
	}
	
	
	/**
	 * @param sheet 那个sheet页面
	 * @param rowIndex 多少行
	 * @param cellIndex 这一行的哪一个单元格  如果是合并单元格的话就是第一个
	 * @return
	 */
	private Cell getCell(Sheet sheet,int rowIndex,int cellIndex){
		Row row = sheet.getRow(rowIndex);
		if(row==null){
			row = sheet.createRow(rowIndex);
		}
		Cell cell = row.getCell(cellIndex);
		if(cell==null){
			cell = row.createCell(cellIndex);
		}
		return cell;
	}



	/**
	 * 下载模板文件
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		if(file.exists()){//存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_NAME);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.MaterialorAccIndexStandard.TEMPLATE_DIR);
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
		 File file = new File(Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		 if(file.exists()){//存在就上传
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH,
					 DataSetUtil.DataSetType.MSExcelX, 
					 "excel", 
					 Const.MaterialorAccIndexStandard.Accessories_Excel_Name);
			 
			 try {
				 component.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
	}

	
}
