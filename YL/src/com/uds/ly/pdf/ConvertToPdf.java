 /**************************************************************************************************                                      
 *                                               版权归UDS所有，2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Nov-2016    ChenChun               更正保密代码清单关系为U8_SecretBOM_Relation
 **************************************************************************************************/

package com.uds.ly.pdf;

import java.util.HashMap;
import java.util.Map;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.tcutils.TcDatasetUtils;

public class ConvertToPdf {
	public TCComponentItemRevision m_techDocItemRevision = null;
	public com.teamcenter.rac.kernel.TCSession m_session = null;
	public String m_dlgTitle = "UDS";

	static String m_coverRelation = "IMAN_specification";
	static String m_formulaRelation = "U8_Formu_Relation";
	static String m_dosageRelation = "U8_Dosage_Relation";
	static String m_bomRelation = "U8_BOM_Relation";
	static String m_secretRelation = "U8_SecretBOM_Relation";
	
	//static String m_pdfDataSetType = com.uds.tc.common.util.TcCommonDefines.DATASET_pdf_type;
	public static String m_pdfDataSetType = "PDF";
	//static String m_pdfDataSetRef = com.uds.tc.common.util.TcCommonDefines.DATASET_REF_pdfRef;
	public static String m_pdfDataSetRef = "PDF_Reference";
	
	public static String m_techDocRevType = "U8_TechDocRevision";
	public static String m_techDocYSRevType = "U8_TechDoc_YSRevision";
	public static String m_techDocHYRevType = "U8_TechDoc_HYRevision";
	
	static String m_tempDir = "c:"+java.io.File.separator + "temp"+java.io.File.separator + "UDS";
	public java.util.Map<String, String> ConvertFileToPDF(java.util.Map<String, String> files) {
		java.util.Map<String, String> pdfFiles = new java.util.HashMap<String, String>();
		pdfFiles = this.DoPdfConvert(files);
		return pdfFiles;
	}
	public java.util.List<String> DoConvertNotUpload(){
		java.util.List<String> pdfFiles = new java.util.ArrayList<String>();
		try{
			String tempDir = m_tempDir;
			//封面
			java.util.ArrayList<PdfConvertData> files = this.GetConvertedFiles(m_coverRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//检查是否有转失败的文件
 					PdfConvertData myData = files.get(i);
 					if(myData.m_failedSrcFiles != null && myData.m_failedSrcFiles.size()>0){
 						String msg = "以下文件转换pdf失败：";
 						for(Integer j=0;j<myData.m_failedSrcFiles.size();j++){
 							msg += "\n";
 							msg += myData.m_failedSrcFiles.get(j);
 						}
 						
 						com.teamcenter.rac.util.MessageBox.post(msg, m_dlgTitle,
 								com.teamcenter.rac.util.MessageBox.ERROR);	
 						return null;
 					}else{
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//生产配方
 			files = this.GetConvertedFiles(m_formulaRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//检查是否有转失败的文件
 					PdfConvertData myData = files.get(i);
 					if(myData.m_failedSrcFiles != null && myData.m_failedSrcFiles.size()>0){
 						String msg = "以下文件转换pdf失败：";
 						for(Integer j=0;j<myData.m_failedSrcFiles.size();j++){
 							msg += "\n";
 							msg += myData.m_failedSrcFiles.get(j);
 						}
 						
 						com.teamcenter.rac.util.MessageBox.post(msg, m_dlgTitle,
 								com.teamcenter.rac.util.MessageBox.ERROR);	
 						return null;
 					}else{
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//投料表
 			files = this.GetConvertedFiles(m_dosageRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//检查是否有转失败的文件
 					PdfConvertData myData = files.get(i);
 					if(myData.m_failedSrcFiles != null && myData.m_failedSrcFiles.size()>0){
 						String msg = "以下文件转换pdf失败：";
 						for(Integer j=0;j<myData.m_failedSrcFiles.size();j++){
 							msg += "\n";
 							msg += myData.m_failedSrcFiles.get(j);
 						}
 						
 						com.teamcenter.rac.util.MessageBox.post(msg, m_dlgTitle,
 								com.teamcenter.rac.util.MessageBox.ERROR);	
 						return null;
 					}else{
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//原料清单
 			files = this.GetConvertedFiles(m_bomRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//检查是否有转失败的文件
 					PdfConvertData myData = files.get(i);
 					if(myData.m_failedSrcFiles != null && myData.m_failedSrcFiles.size()>0){
 						String msg = "以下文件转换pdf失败：";
 						for(Integer j=0;j<myData.m_failedSrcFiles.size();j++){
 							msg += "\n";
 							msg += myData.m_failedSrcFiles.get(j);
 						}
 						
 						com.teamcenter.rac.util.MessageBox.post(msg, m_dlgTitle,
 								com.teamcenter.rac.util.MessageBox.ERROR);	
 						return null;
 					}else{
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//保密代码
 			files = this.GetConvertedFiles(m_secretRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//检查是否有转失败的文件
 					PdfConvertData myData = files.get(i);
 					if(myData.m_failedSrcFiles != null && myData.m_failedSrcFiles.size()>0){
 						String msg = "以下文件转换pdf失败：";
 						for(Integer j=0;j<myData.m_failedSrcFiles.size();j++){
 							msg += "\n";
 							msg += myData.m_failedSrcFiles.get(j);
 						}
 						
 						com.teamcenter.rac.util.MessageBox.post(msg, m_dlgTitle,
 								com.teamcenter.rac.util.MessageBox.ERROR);	
 						return null;
 					}else{
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
		}catch(Exception ex){
//			if(m_log != null){
//				m_log.write("Convert pdf has error:" + ex.getMessage());
//			}
		}
		return pdfFiles;
	}
	public int DoConvert(){
		java.util.List<String> pdfFiles = new java.util.ArrayList<String>();
		try{
			String tempDir = m_tempDir;
			//封面
			java.util.ArrayList<PdfConvertData> files = this.GetConvertedFiles(m_coverRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//上传到TC
 					PdfConvertData myData = files.get(i);
 					if(this.UploadFile(myData)){
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//生产配方
 			files = this.GetConvertedFiles(m_formulaRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//上传到TC
 					PdfConvertData myData = files.get(i);
 					if(this.UploadFile(myData)){
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//用量
 			files = this.GetConvertedFiles(m_dosageRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//上传到TC
 					PdfConvertData myData = files.get(i);
 					if(this.UploadFile(myData)){
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//结构
 			files = this.GetConvertedFiles(m_bomRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//上传到TC
 					PdfConvertData myData = files.get(i);
 					if(this.UploadFile(myData)){
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
 			//保密
 			files = this.GetConvertedFiles(m_secretRelation,tempDir);
 			if(files != null && files.size()>0){
 				for(Integer i=0;i<files.size();i++){
 					//上传到TC
 					PdfConvertData myData = files.get(i);
 					if(this.UploadFile(myData)){
 						pdfFiles.addAll(myData.m_pdfFiles);
 					}
 				}
 			}
		}catch(Exception ex){
//			if(m_log != null){
//				m_log.write("Convert pdf has error:" + ex.getMessage());
//			}
		}
		return pdfFiles.size();
	}
	private boolean UploadFile(PdfConvertData pdfFile){
		Map<String,String> localFiles = new HashMap<String,String>();
		
		try{
			for(int i=0; i<pdfFile.m_pdfFiles.size();i++){
				String file = pdfFile.m_pdfFiles.get(i);
				java.io.File myFile = new java.io.File(file);
				if(myFile.exists()){
					localFiles.put(file,TcCommonDefines.DATASET_REF_pdfRef);
				}			
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try{
			
			if(localFiles.size() > 0){
				String[] arg0 = localFiles.keySet().toArray(new String[localFiles.size()]);
				String[] arg1 = localFiles.values().toArray(new String[localFiles.size()]);
				
				TCComponentDatasetType datasetType = (TCComponentDatasetType)this.m_session.getTypeComponent(TcCommonDefines.DATASET_type);
				TCComponentDataset dataset = datasetType.create(pdfFile.m_datasetName, pdfFile.m_description, "PDF");
				dataset.setFiles(arg0, arg1);
				dataset.save();
				//add to item
				this.m_techDocItemRevision.lock();
				this.m_techDocItemRevision.add(pdfFile.m_relation, dataset);
				this.m_techDocItemRevision.unlock();
				
				return true;				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	private java.util.ArrayList<PdfConvertData> GetConvertedFiles(String refType, String rootDir){
		try{
			java.util.ArrayList<PdfConvertData> myList = new java.util.ArrayList<PdfConvertData>();
			AIFComponentContext[] children = m_techDocItemRevision.getRelated(refType);
			if(children != null && children.length > 0){
				for(Integer i=0; i<children.length; i++){
					AIFComponentContext child = children[i];
					if(child != null){
						InterfaceAIFComponent comp = child.getComponent();
						if(comp instanceof TCComponentDataset){
							TCComponentDataset ds = (TCComponentDataset)comp;
							java.util.Map<String,String> files = TcDatasetUtils.FileToLocalDir(ds, rootDir);
							//转PDF
							if(files != null && files.size() > 0){
								java.util.Map<String,String> pdfFiles = this.DoPdfConvert(files);
								if(pdfFiles != null && pdfFiles.size()>0){
									
									PdfConvertData pdfData = new PdfConvertData();
									pdfData.m_datasetName = ds.getProperty(TcCommonDefines.PROPERTY_objectname);
									pdfData.m_description = ds.getProperty(TcCommonDefines.PROPERTY_description);
									pdfData.m_relation = refType;
									pdfData.m_pdfFiles = new java.util.ArrayList<String>();
									pdfData.m_failedSrcFiles = new java.util.ArrayList<String>();
									for(Map.Entry<String, String> item:pdfFiles.entrySet()){
										String srcFile = item.getKey();
										String pdfSuccess = item.getValue();
										if(pdfSuccess == null || pdfSuccess.length() == 0){
											pdfData.m_failedSrcFiles.add(srcFile);
										}else{
											pdfData.m_pdfFiles.add(pdfSuccess);
										}
									}
									myList.add(pdfData);
								}
							}
						}
					}
					
				}
			}
			
			return myList;
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	private boolean IsPdfFile(String source) {
		java.io.File file = new java.io.File(source);
		if(file.isFile()){
			String fileName = file.getAbsolutePath();
			int dotIndex = fileName.lastIndexOf(".");
			String fileExt = fileName.substring(dotIndex + 1);
			if (fileExt.toLowerCase().equals("pdf")){
				return true;
			}
		}
		return false;
	}
	private String GetPdfName(String source){
		try{
			java.io.File file = new java.io.File(source);
			if(file.isFile()){
				String fileName = file.getAbsolutePath();
				int dotIndex = fileName.lastIndexOf(".");
				String fileNoExt = fileName.substring(0,dotIndex);
				return fileNoExt + ".pdf";
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return source + ".pdf";
	}
	/*
	 * 参数source:key=原始文件,val=文件类型
	 * 返回:key=原始文件,val=转换后的pdf文件,如果不成功则为""
	 */
	private java.util.Map<String,String> DoPdfConvert(java.util.Map<String,String> source){
		
		try{
			java.util.Map<String,String> myList = new java.util.HashMap<String,String>();
			if(source != null && source.size()>0){
				for(Map.Entry<String, String> item:source.entrySet()){
					String fileName = item.getKey();
					String fileType = item.getValue();

					boolean isSuccess = false;
					//获取pdf文件名
					if (this.IsPdfFile(fileName)) {
						myList.put(fileName,fileName);
					}
					else {
						String pdfFile = this.GetPdfName(fileName);
						try{
							java.io.File myFile = new java.io.File(pdfFile);
							if (myFile.exists()){
								myFile.delete();
							}
							
							if(fileType.indexOf(TcCommonDefines.DATASET_REF_wordRef)>=0){
								try{
									isSuccess = FileToPdf.WordToPDF(fileName, pdfFile);							
								}catch(Exception ex){
									ex.printStackTrace();
								}
	
							}else if(fileType.indexOf(TcCommonDefines.DATASET_REF_excelRef)>=0){
								try{
									isSuccess = FileToPdf.ExcelToPDF(fileName, pdfFile);
								}catch(Exception ex){
									ex.printStackTrace();
								}
							}else if (fileType.indexOf(TcCommonDefines.DATASET_REF_pptRef) >= 0) {
								try{
									isSuccess = FileToPdf.PPTToPDF(fileName, pdfFile);
								}catch(Exception ex){
									ex.printStackTrace();
								}
							}
							
						}catch(Exception ex){
							ex.printStackTrace();
						}
		
						if(isSuccess){
							myList.put(fileName,pdfFile);
						}else{
							myList.put(fileName, "");	
						}
					}
				}
			}
			return myList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
class PdfConvertData{
	public java.util.List<String> m_pdfFiles = null;
	public String m_datasetName = null;
	public String m_description = "";
	public String m_relation = null;
	
	public java.util.List<String> m_failedSrcFiles = null;
}
