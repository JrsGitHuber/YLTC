package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.interfaces.CallBack;
import com.uds.yl.poi.word.XWPFParagraphPackage;
import com.uds.yl.poi.word.XWPFRunPackage;
import com.uds.yl.service.IColdDrinkFormulaExcelService;
import com.uds.yl.service.impl.ColdDrinkFormulaExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
import com.uds.yl.ui.ColdDrinkSelectFrame;

public class ColdDrinkFormulaWordControler implements BaseControler, CallBack {

	private IColdDrinkFormulaExcelService iColdDrinkFormulaExcelService = new ColdDrinkFormulaExcelServiceImpl();

	private List<TCComponentBOMLine> allWillSelectedMaterialBomList = null;// 组料的Bom对象
	private List<String> allWillSelectedNameList = null;// 组料的名字
	
	private String formulaName ="" ;
	private TCComponentItemRevision mItemRev;
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		
		this.mItemRev = itemRev;
		// 获取所有的组料集合传递给Frame界面的
		allWillSelectedNameList = iColdDrinkFormulaExcelService.getAllWillSelectedMaterialNameList(itemRev);
		allWillSelectedMaterialBomList = iColdDrinkFormulaExcelService.getAllWillSelectedMaterialBomList(itemRev);
		if (allWillSelectedNameList == null || allWillSelectedNameList.size() == 0) {// 没有topBOMline
			MessageBox.post("", "请检查BOM结构", MessageBox.ERROR);
			return;
		}
		ColdDrinkSelectFrame frame = new ColdDrinkSelectFrame(allWillSelectedNameList);
		frame.setVisible(true);
		frame.setCallBack(this);
		
		try {
			formulaName = itemRev.getProperty("object_name");
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc) ColdDrinkSelectFrame点击完确认按钮后的回调方法
	 */
	@Override
	public void setResult(String result) {
		// 获得所有的选中的组料名称字符串，以#隔开 明湖曾
		List<String> selectNameList = new ArrayList<>();
		List<TCComponentBOMLine> selsectBomList = new ArrayList<>();
		String[] splitName = result.split("#");
		for (int i = 0; i < splitName.length; i++) {
			String name = splitName[i];
			TCComponentBOMLine bomLine;
			if (allWillSelectedNameList.contains(name)) {
				int index = allWillSelectedNameList.indexOf(name);
				bomLine = allWillSelectedMaterialBomList.get(index);
				selectNameList.add(name);
				selsectBomList.add(bomLine);
			}
		}
		// 获取之后做业务逻辑处理
		createDoc(selectNameList, selsectBomList);
		createMinMaterialWord(selectNameList, selsectBomList);
		MessageBox.post("","ok",MessageBox.INFORMATION);
	}

	/**
	 * 创建一个word文档
	 * 
	 * @param selectNameList
	 * @param selsectBomList
	 */
	private void createDoc(List<String> selectNameList, List<TCComponentBOMLine> selsectBomList) {

		// Write the Document in file system
		try {
			
			String templatePath = Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path;
			String outPath = Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path;
			if(!downTemplate()){//下载失败
				MessageBox.post("模板下载失败请检查！","",MessageBox.INFORMATION);
				return;
			}
			
			// Blank Document
			XWPFDocument document = new XWPFDocument(new FileInputStream(new File(templatePath)));
			XWPFParagraphPackage titleParagraph = new XWPFParagraphPackage(document.createParagraph());
			titleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage titleRun = new XWPFRunPackage(titleParagraph.createRun());
			titleRun.setFontSize(18).setBold(true).setText("（"+formulaName+"）"+"生产配料单");

			XWPFParagraphPackage subTitleParagraph = new XWPFParagraphPackage(document.createParagraph());
			subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage subTitleRun = new XWPFRunPackage(subTitleParagraph.createRun());
			subTitleRun.setText("编号：YLLY/JB/JS/339/01[N/0]");

			XWPFParagraphPackage firstParagraph = new XWPFParagraphPackage(document.createParagraph());
			firstParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage firstRun = new XWPFRunPackage(firstParagraph.createRun());
			firstRun.setText("一、产品名称："+formulaName+";");
			

			String secondTitle = "";
			for(int i=0;i<allWillSelectedNameList.size();i++){
				if(i==0){
					secondTitle = allWillSelectedNameList.get(i);
					continue;
				}
				secondTitle = secondTitle+"+"+allWillSelectedNameList.get(i);
				
			}
			XWPFParagraphPackage secondParagraph = new XWPFParagraphPackage(document.createParagraph());
			secondParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage secondRun = new XWPFRunPackage(secondParagraph.createRun());
			secondRun.setText("二、配料："+formulaName+"="+secondTitle+";").addBreak();
			
			//第二部分：配料的详细信息
			List<MinMaterialBean> allComplexMaterialBeanList = new ArrayList<>();
			for(int i=0;i<selsectBomList.size();i++){
				TCComponentBOMLine selectBomLine = selsectBomList.get(i);
				String bomName = selectBomLine.getItem().getProperty("object_name");
				List<MaterialBean> singleMaterialBeanList = iColdDrinkFormulaExcelService.getSingleMaterialBeanList(selectBomLine);
				List<MinMaterialBean> complexMaterialBeanList = iColdDrinkFormulaExcelService.getComplexMaterialBeanList(selectBomLine);
				allComplexMaterialBeanList.addAll(complexMaterialBeanList);
				
				XWPFRunPackage run = new XWPFRunPackage(secondParagraph.createRun());
				run.setUnderline(UnderlinePatterns.SINGLE).setText(bomName+"/吨：");
				for(int j = 0;j<singleMaterialBeanList.size();j++){//组合单原料
					MaterialBean bean = singleMaterialBeanList.get(j);
					if(j==0){
						run.setText(bean.objectName+":"+bean.U8_inventory+"公斤");
					}else{
						run.setText("+"+bean.objectName+":"+bean.U8_inventory+"公斤");
					}
					
				}
				for(int j=0;j<complexMaterialBeanList.size();j++){
					MinMaterialBean bean = complexMaterialBeanList.get(j);
					run.setText("+"+bean.name+":"+bean.inventory+"公斤");
				}
				run.addBreak();
				
			}
			
			//第三部分：小料的重量
			XWPFParagraphPackage thirdParagraph = new XWPFParagraphPackage(document.createParagraph());
			thirdParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage thirdRun = new XWPFRunPackage(secondParagraph.createRun());
			thirdRun.setText("三、小料重量：").addBreak();
			for(int i=0;i<allComplexMaterialBeanList.size();i++){
				MinMaterialBean bean = allComplexMaterialBeanList.get(i);
				XWPFRunPackage run = new XWPFRunPackage(secondParagraph.createRun());
				if(i%2==0){//左面列
					run.setUnderline(UnderlinePatterns.SINGLE).setText(bean.name+" = "+bean.inventory);
					run.setText("      ");
				}else{//右面列
					run.setUnderline(UnderlinePatterns.SINGLE).setText(bean.name+" = "+bean.inventory);
					run.addBreak();
				}
			}
			secondParagraph.createRun().addBreak();
			
			//第四部分：配料其他事项说明
			XWPFParagraphPackage fourParagraph = new XWPFParagraphPackage(document.createParagraph());
			fourParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage fourRun = new XWPFRunPackage(secondParagraph.createRun());
			fourRun.setText("四、配料其他事项说明：").addBreak();
			File file = new File(outPath);
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			out.close();
			
			if(!uploadFile(mItemRev)){//上传失败
				MessageBox.post("上传失败请检查","",MessageBox.INFORMATION);
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TCException e) {
			e.printStackTrace();
		}
		System.out.println("createdocument.docx written successully");
	}
	
	/**
	 * 小料配方通知单的生成，选中的那几层
	 * @param selectNameList
	 * @param selsectBomList
	 */
	public void createMinMaterialWord(List<String> selectNameList, List<TCComponentBOMLine> selsectBomList){
		// Write the Document in file system
		try {
			
			
			String templatePath = Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path;
			String outPath = Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path;
			if(!downTemplateMinMaterial()){//下载失败
				MessageBox.post("模板下载失败请检查！","",MessageBox.INFORMATION);
				return;
			}
			
			// Blank Document
			XWPFDocument document = new XWPFDocument(new FileInputStream(new File(templatePath)));
			XWPFParagraphPackage titleParagraph = new XWPFParagraphPackage(document.createParagraph());
			titleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage titleRun = new XWPFRunPackage(titleParagraph.createRun());
			titleRun.setFontSize(18).setBold(true).setText("小料配料通知单");

			XWPFParagraphPackage subTitleParagraph = new XWPFParagraphPackage(document.createParagraph());
			subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage subTitleRun = new XWPFRunPackage(subTitleParagraph.createRun());
			subTitleRun.setText("编号：YLLY/JB/JS/963/03[A/0]").addBreak();
			
			

			XWPFParagraphPackage firstParagraph = new XWPFParagraphPackage(document.createParagraph());
			firstParagraph.setAlignment(ParagraphAlignment.LEFT);
			
			XWPFRunPackage descRun = new XWPFRunPackage(firstParagraph.createRun());
			descRun.setText("小料房：现将如下小料配料事宜通知如下").addBreak();
			
			
			XWPFRunPackage firstRun = new XWPFRunPackage(firstParagraph.createRun());
			firstRun.setText("一、小料代号：（按1.0吨/锅）").addBreak();
			
			//找到选中的bom中的下料
			for(int i=0;i<selectNameList.size();i++){
				String name = selectNameList.get(i);
				List<MinMaterialBean> complexMaterialBeanList = iColdDrinkFormulaExcelService.getComplexMaterialBeanList(selsectBomList.get(i));
				XWPFRunPackage run1 = new XWPFRunPackage(firstParagraph.createRun());
				run1.setText(name+"/锅=");
				
				String run2String ="";
				for(int j=0;j<complexMaterialBeanList.size();j++){
					MinMaterialBean materialBean = complexMaterialBeanList.get(j);
					if(j==0){
						run2String = run2String+materialBean.name+":"+materialBean.inventory+"公斤";
					}else{
						run2String = run2String+"+"+materialBean.name+":"+materialBean.inventory+"公斤";
					}
				}
				
				XWPFRunPackage run2 = new XWPFRunPackage(firstParagraph.createRun());
				run2.setUnderline(UnderlinePatterns.SINGLE).setText(run2String).addBreak();
			}
			

			XWPFParagraphPackage secondParagraph = new XWPFParagraphPackage(document.createParagraph());
			secondParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage secondRun = new XWPFRunPackage(secondParagraph.createRun());
			secondRun.setText("二、注意事项：").addBreak();
			
			
			XWPFParagraphPackage thirdParagraph = new XWPFParagraphPackage(document.createParagraph());
			thirdParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage thirdRun = new XWPFRunPackage(secondParagraph.createRun());
			secondRun.setText("备注").addBreak();
			secondRun.setText("   1、文件妥善保存").addBreak();
			secondRun.setText("   2、注意区分代号，严禁混淆").addBreak();
			
			File file = new File(outPath);
			if(file.exists()){
				file.delete();
			}
			
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			out.close();
			
			if(!uploadMinMaterilFile(mItemRev)){//上传失败
				MessageBox.post("上传失败请检查","",MessageBox.INFORMATION);
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("createdocument.docx written successully");
	}

	/*
	 * (non-Javadoc) 此回调方法不做使用
	 */
	@Override
	public void setNewIndexNameResult(String result) {
	}

	
	
	
	/**
	 * 下载模板文件
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path);
		if(file.exists()){//存在就删除
			file.delete();
		}
		String code = PreferenceUtil
				.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil
				.getDatasetByNameAndCode(
						code,
						Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Name);
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,
				DataSetUtil.DataSetNameRef.Word,
				Const.ColdDrinkFormulaExcel.Template_Dir);

		if (resultStrs.length == 0) {// 下载失败
			return false;
		}

		return true;
	}
	
	
	/**
	 * 下载模板文件
	 * @return
	 */
	public boolean downTemplateMinMaterial(){

		File file = new File(
				Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path);
		if (file.exists()) {// 存在就删除
			file.delete();
		}
		String code = PreferenceUtil
				.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Name);
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,
				DataSetUtil.DataSetNameRef.Word,
				Const.ColdDrinkFormulaExcel.Template_Dir);

		if (resultStrs.length == 0) {// 下载失败
			return false;
		}

		return true;
	}
	
	
	
	/**
	 * 上传生产配方表
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path);
		 if(file.exists()){//存在就上传
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path,
					 DataSetUtil.DataSetType.MSWordX, 
					 "word", 
					 Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Upload_Name);
			 
			 try {
				 component.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
		
	}
	
	
	/**
	 * 上传小料配方表
	 * @param component
	 * @return
	 */
	public boolean uploadMinMaterilFile(TCComponent component){
		
		 File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path);
		 if(file.exists()){//存在就上传
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path,
					 DataSetUtil.DataSetType.MSWordX, 
					 "word", 
					 Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Upload_Name);
			 
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

