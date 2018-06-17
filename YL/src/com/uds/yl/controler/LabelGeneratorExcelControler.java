package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.ILabelGeneratorExcelService;
import com.uds.yl.service.impl.LabelGeneratorExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
//液态标签生成器
public class LabelGeneratorExcelControler implements BaseControler {

	private ILabelGeneratorExcelService iLabelGeneratorExcelService = new LabelGeneratorExcelServiceImpl();

	private List<IndexItemBean> sortedIndexItemBeanList;// 按照标签中的字段的顺序初始化的一个集合
	private List<IndexItemBean> allIndexItemBeanList;// 单层BOM下的所有得指标

	private List<ResultExcelBean> resultExcelBeansList;

	@Override
	public void userTask(TCComponentItemRevision itemRev) {

		// 获取单层原料下面的所有IndexItem
		allIndexItemBeanList = iLabelGeneratorExcelService.getAllIndexItemBeanList(itemRev);

		if (allIndexItemBeanList == null) {
			MessageBox.post("请检查BOM结构", "", MessageBox.ERROR);
			return;
		}

		// 根据标签顺序创建List<IndexItem>
		sortedIndexItemBeanList = iLabelGeneratorExcelService.getSortedIndexItemBeanList();

		// 遍历所有的IndexItem，累加对应的值到排序的集合里的Bean中去
		iLabelGeneratorExcelService.initSortedIndexItemBeanList(allIndexItemBeanList, sortedIndexItemBeanList);

		//进行0界值的过滤
		iLabelGeneratorExcelService.filterZero(sortedIndexItemBeanList);
		
		
		resultExcelBeansList = new ArrayList<>();//resultbean作为存储 100g的单位值的说
		for(IndexItemBean bean : sortedIndexItemBeanList){
			ResultExcelBean resultExcelBean = new ResultExcelBean();
			resultExcelBean.name = bean.objectName;
			resultExcelBean.value = bean.average;
			resultExcelBeansList.add(resultExcelBean);
		}
		
		//进行NRV的计算
		iLabelGeneratorExcelService.NRVCompute(sortedIndexItemBeanList);
		for(int i=0;i<sortedIndexItemBeanList.size();i++){
			IndexItemBean bean = sortedIndexItemBeanList.get(i);
			ResultExcelBean resultExcelBean = resultExcelBeansList.get(i);
			resultExcelBean.nrv = bean.average;
		}
		
		
		//下载  标签的模板和0界限NRV
		File file = new File(Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH);
		if (file.exists()) {// 存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.LabelGeneratorExcel.LABEL_EXCEL_NAME);
		if (dataset == null) {
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.LabelGeneratorSolidExcel.Template_Dir);
		if (resultStrs.length == 0) {// 下载失败
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}

		// 下载0界值和NRV值只用来使用不会上传
		file = new File(Const.LabelGeneratorExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		if (file.exists()) {// 存在就删除
			file.delete();
		}
		code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.LabelGeneratorExcel.NRV_ZERO_VALUE__EXCEL_NAME);
		if (dataset == null) {
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}
		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.LabelGeneratorExcel.Template_Dir);
		if (resultStrs.length == 0) {// 下载失败
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}
		
		// 写入到Excel
		writeToExcel();
		
		//上传 标签模板
		file = new File(Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH);
		if (file.exists()) {// 存在就上传
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH, DataSetUtil.DataSetType.MSExcelX,
						"excel", Const.LabelGeneratorExcel.Label_Excel_Upload_Name);
				itemRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
				return;
			}
		}
	}


	/**
	 * 将拍好顺序的Bena对象写入道Excel中去
	 */
	private void writeToExcel() {
		// ======写数据
		File outFile = new File(Const.LabelGeneratorExcel.LABEL_EXCEL_INPUT_PATH);
		// 写入Excel
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(outFile));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================液态标签Sheet页
		Sheet sheet = wb.getSheet(Const.LabelGeneratorExcel.SHEET_NAME);// 液态标签

		// 是从row(18)开始的
		int start = 18;
		for (int i = 0; i < sortedIndexItemBeanList.size(); i++) {
			IndexItemBean sortBean = sortedIndexItemBeanList.get(i);
			ResultExcelBean resultBean = resultExcelBeansList.get(i);
//			if ("".equals(sortBean.up) && "".equals(sortBean.down)) {
//				continue;
//			}

			Row row = sheet.getRow(start);
			if (row == null) {
				row = sheet.createRow(start);
			}

			//合并单元格
			
			CellRangeAddress cra=new CellRangeAddress(start, start, 1, 2);  
			sheet.addMergedRegion(cra); 
			cra=new CellRangeAddress(start, start, 3, 4);  
			sheet.addMergedRegion(cra); 
			cra=new CellRangeAddress(start, start, 5, 6);  
			sheet.addMergedRegion(cra); 
			
			Cell cell1 = row.getCell(1);
			Cell cell2 = row.getCell(3);
			Cell cell3 = row.getCell(5);
			if (cell1 == null) {
				cell1 = row.createCell(1);
			}
			if (cell2 == null) {
				cell2 = row.createCell(3);
			}
			if (cell3 == null) {
				cell3 = row.createCell(5);
			}

			cell1.setCellValue(resultBean.name);
			cell2.setCellValue(resultBean.value);
			cell3.setCellValue(resultBean.nrv);
			
			sheet.shiftRows(start, sheet.getLastRowNum(), 1);//插入一行
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(new File("C:\\temp\\液态标签表temp.xlsx"));
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

		try {
			Runtime.getRuntime().exec("cmd /c start " + "C:\\temp\\液态标签表temp.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	class ResultExcelBean{
		String name ;
		String value;
		String nrv;
	}

}
