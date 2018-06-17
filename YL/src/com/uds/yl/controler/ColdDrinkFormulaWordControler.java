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

	private List<TCComponentBOMLine> allWillSelectedMaterialBomList = null;// ���ϵ�Bom����
	private List<String> allWillSelectedNameList = null;// ���ϵ�����
	
	private String formulaName ="" ;
	private TCComponentItemRevision mItemRev;
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		
		this.mItemRev = itemRev;
		// ��ȡ���е����ϼ��ϴ��ݸ�Frame�����
		allWillSelectedNameList = iColdDrinkFormulaExcelService.getAllWillSelectedMaterialNameList(itemRev);
		allWillSelectedMaterialBomList = iColdDrinkFormulaExcelService.getAllWillSelectedMaterialBomList(itemRev);
		if (allWillSelectedNameList == null || allWillSelectedNameList.size() == 0) {// û��topBOMline
			MessageBox.post("", "����BOM�ṹ", MessageBox.ERROR);
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
	 * (non-Javadoc) ColdDrinkSelectFrame�����ȷ�ϰ�ť��Ļص�����
	 */
	@Override
	public void setResult(String result) {
		// ������е�ѡ�е����������ַ�������#���� ������
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
		// ��ȡ֮����ҵ���߼�����
		createDoc(selectNameList, selsectBomList);
		createMinMaterialWord(selectNameList, selsectBomList);
		MessageBox.post("","ok",MessageBox.INFORMATION);
	}

	/**
	 * ����һ��word�ĵ�
	 * 
	 * @param selectNameList
	 * @param selsectBomList
	 */
	private void createDoc(List<String> selectNameList, List<TCComponentBOMLine> selsectBomList) {

		// Write the Document in file system
		try {
			
			String templatePath = Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path;
			String outPath = Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path;
			if(!downTemplate()){//����ʧ��
				MessageBox.post("ģ������ʧ�����飡","",MessageBox.INFORMATION);
				return;
			}
			
			// Blank Document
			XWPFDocument document = new XWPFDocument(new FileInputStream(new File(templatePath)));
			XWPFParagraphPackage titleParagraph = new XWPFParagraphPackage(document.createParagraph());
			titleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage titleRun = new XWPFRunPackage(titleParagraph.createRun());
			titleRun.setFontSize(18).setBold(true).setText("��"+formulaName+"��"+"�������ϵ�");

			XWPFParagraphPackage subTitleParagraph = new XWPFParagraphPackage(document.createParagraph());
			subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage subTitleRun = new XWPFRunPackage(subTitleParagraph.createRun());
			subTitleRun.setText("��ţ�YLLY/JB/JS/339/01[N/0]");

			XWPFParagraphPackage firstParagraph = new XWPFParagraphPackage(document.createParagraph());
			firstParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage firstRun = new XWPFRunPackage(firstParagraph.createRun());
			firstRun.setText("һ����Ʒ���ƣ�"+formulaName+";");
			

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
			secondRun.setText("�������ϣ�"+formulaName+"="+secondTitle+";").addBreak();
			
			//�ڶ����֣����ϵ���ϸ��Ϣ
			List<MinMaterialBean> allComplexMaterialBeanList = new ArrayList<>();
			for(int i=0;i<selsectBomList.size();i++){
				TCComponentBOMLine selectBomLine = selsectBomList.get(i);
				String bomName = selectBomLine.getItem().getProperty("object_name");
				List<MaterialBean> singleMaterialBeanList = iColdDrinkFormulaExcelService.getSingleMaterialBeanList(selectBomLine);
				List<MinMaterialBean> complexMaterialBeanList = iColdDrinkFormulaExcelService.getComplexMaterialBeanList(selectBomLine);
				allComplexMaterialBeanList.addAll(complexMaterialBeanList);
				
				XWPFRunPackage run = new XWPFRunPackage(secondParagraph.createRun());
				run.setUnderline(UnderlinePatterns.SINGLE).setText(bomName+"/�֣�");
				for(int j = 0;j<singleMaterialBeanList.size();j++){//��ϵ�ԭ��
					MaterialBean bean = singleMaterialBeanList.get(j);
					if(j==0){
						run.setText(bean.objectName+":"+bean.U8_inventory+"����");
					}else{
						run.setText("+"+bean.objectName+":"+bean.U8_inventory+"����");
					}
					
				}
				for(int j=0;j<complexMaterialBeanList.size();j++){
					MinMaterialBean bean = complexMaterialBeanList.get(j);
					run.setText("+"+bean.name+":"+bean.inventory+"����");
				}
				run.addBreak();
				
			}
			
			//�������֣�С�ϵ�����
			XWPFParagraphPackage thirdParagraph = new XWPFParagraphPackage(document.createParagraph());
			thirdParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage thirdRun = new XWPFRunPackage(secondParagraph.createRun());
			thirdRun.setText("����С��������").addBreak();
			for(int i=0;i<allComplexMaterialBeanList.size();i++){
				MinMaterialBean bean = allComplexMaterialBeanList.get(i);
				XWPFRunPackage run = new XWPFRunPackage(secondParagraph.createRun());
				if(i%2==0){//������
					run.setUnderline(UnderlinePatterns.SINGLE).setText(bean.name+" = "+bean.inventory);
					run.setText("      ");
				}else{//������
					run.setUnderline(UnderlinePatterns.SINGLE).setText(bean.name+" = "+bean.inventory);
					run.addBreak();
				}
			}
			secondParagraph.createRun().addBreak();
			
			//���Ĳ��֣�������������˵��
			XWPFParagraphPackage fourParagraph = new XWPFParagraphPackage(document.createParagraph());
			fourParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage fourRun = new XWPFRunPackage(secondParagraph.createRun());
			fourRun.setText("�ġ�������������˵����").addBreak();
			File file = new File(outPath);
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			out.close();
			
			if(!uploadFile(mItemRev)){//�ϴ�ʧ��
				MessageBox.post("�ϴ�ʧ������","",MessageBox.INFORMATION);
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
	 * С���䷽֪ͨ�������ɣ�ѡ�е��Ǽ���
	 * @param selectNameList
	 * @param selsectBomList
	 */
	public void createMinMaterialWord(List<String> selectNameList, List<TCComponentBOMLine> selsectBomList){
		// Write the Document in file system
		try {
			
			
			String templatePath = Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path;
			String outPath = Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path;
			if(!downTemplateMinMaterial()){//����ʧ��
				MessageBox.post("ģ������ʧ�����飡","",MessageBox.INFORMATION);
				return;
			}
			
			// Blank Document
			XWPFDocument document = new XWPFDocument(new FileInputStream(new File(templatePath)));
			XWPFParagraphPackage titleParagraph = new XWPFParagraphPackage(document.createParagraph());
			titleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage titleRun = new XWPFRunPackage(titleParagraph.createRun());
			titleRun.setFontSize(18).setBold(true).setText("С������֪ͨ��");

			XWPFParagraphPackage subTitleParagraph = new XWPFParagraphPackage(document.createParagraph());
			subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRunPackage subTitleRun = new XWPFRunPackage(subTitleParagraph.createRun());
			subTitleRun.setText("��ţ�YLLY/JB/JS/963/03[A/0]").addBreak();
			
			

			XWPFParagraphPackage firstParagraph = new XWPFParagraphPackage(document.createParagraph());
			firstParagraph.setAlignment(ParagraphAlignment.LEFT);
			
			XWPFRunPackage descRun = new XWPFRunPackage(firstParagraph.createRun());
			descRun.setText("С�Ϸ����ֽ�����С����������֪ͨ����").addBreak();
			
			
			XWPFRunPackage firstRun = new XWPFRunPackage(firstParagraph.createRun());
			firstRun.setText("һ��С�ϴ��ţ�����1.0��/����").addBreak();
			
			//�ҵ�ѡ�е�bom�е�����
			for(int i=0;i<selectNameList.size();i++){
				String name = selectNameList.get(i);
				List<MinMaterialBean> complexMaterialBeanList = iColdDrinkFormulaExcelService.getComplexMaterialBeanList(selsectBomList.get(i));
				XWPFRunPackage run1 = new XWPFRunPackage(firstParagraph.createRun());
				run1.setText(name+"/��=");
				
				String run2String ="";
				for(int j=0;j<complexMaterialBeanList.size();j++){
					MinMaterialBean materialBean = complexMaterialBeanList.get(j);
					if(j==0){
						run2String = run2String+materialBean.name+":"+materialBean.inventory+"����";
					}else{
						run2String = run2String+"+"+materialBean.name+":"+materialBean.inventory+"����";
					}
				}
				
				XWPFRunPackage run2 = new XWPFRunPackage(firstParagraph.createRun());
				run2.setUnderline(UnderlinePatterns.SINGLE).setText(run2String).addBreak();
			}
			

			XWPFParagraphPackage secondParagraph = new XWPFParagraphPackage(document.createParagraph());
			secondParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage secondRun = new XWPFRunPackage(secondParagraph.createRun());
			secondRun.setText("����ע�����").addBreak();
			
			
			XWPFParagraphPackage thirdParagraph = new XWPFParagraphPackage(document.createParagraph());
			thirdParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRunPackage thirdRun = new XWPFRunPackage(secondParagraph.createRun());
			secondRun.setText("��ע").addBreak();
			secondRun.setText("   1���ļ����Ʊ���").addBreak();
			secondRun.setText("   2��ע�����ִ��ţ��Ͻ�����").addBreak();
			
			File file = new File(outPath);
			if(file.exists()){
				file.delete();
			}
			
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			out.close();
			
			if(!uploadMinMaterilFile(mItemRev)){//�ϴ�ʧ��
				MessageBox.post("�ϴ�ʧ������","",MessageBox.INFORMATION);
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("createdocument.docx written successully");
	}

	/*
	 * (non-Javadoc) �˻ص���������ʹ��
	 */
	@Override
	public void setNewIndexNameResult(String result) {
	}

	
	
	
	/**
	 * ����ģ���ļ�
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path);
		if(file.exists()){//���ھ�ɾ��
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

		if (resultStrs.length == 0) {// ����ʧ��
			return false;
		}

		return true;
	}
	
	
	/**
	 * ����ģ���ļ�
	 * @return
	 */
	public boolean downTemplateMinMaterial(){

		File file = new File(
				Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path);
		if (file.exists()) {// ���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil
				.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Name);
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,
				DataSetUtil.DataSetNameRef.Word,
				Const.ColdDrinkFormulaExcel.Template_Dir);

		if (resultStrs.length == 0) {// ����ʧ��
			return false;
		}

		return true;
	}
	
	
	
	/**
	 * �ϴ������䷽��
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_Formula_Excel_Input_Path);
		 if(file.exists()){//���ھ��ϴ�
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
	 * �ϴ�С���䷽��
	 * @param component
	 * @return
	 */
	public boolean uploadMinMaterilFile(TCComponent component){
		
		 File file = new File(Const.ColdDrinkFormulaExcel.ColdDrink_MinMaterial_Excel_Input_Path);
		 if(file.exists()){//���ھ��ϴ�
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

