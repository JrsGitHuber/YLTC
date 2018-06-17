
 /**************************************************************************************************                                      
 *                                               版权归UDS所有，2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Nov-2016    ChenChun               处理pdf合并时需要让用户选择顺序
 **************************************************************************************************/

package com.uds.ly.pdf;

import java.util.List;


import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.tcutils.TcDatasetUtils;

public class MergePdfs {
	public TCComponentItemRevision m_ItemRevision = null;
	public com.teamcenter.rac.kernel.TCSession m_session = null;
	
	public boolean m_clickClose = false;//是否点击了关闭按钮
	public boolean m_isOver = false;//是都点击了确定按钮
	
	private String ReplaceInvalidCharacters(String fileName){
		String inValidChars = "<>: /\\|?*";
		String newName = fileName;
		for(int i=0;i<inValidChars.length();i++){
			char arg0 = inValidChars.charAt(i);
			char arg1 = '_';
			newName = newName.replace(arg0, arg1);
			
		}
		return newName;
	}
	public int DoMergeWithFiles(String tempDir, java.util.List<String> pdfList){
		int count = 0;
		try{
 			//合并之前，需要让用户选择合并顺序
 			java.util.List<String> pdfListSorted = null;
 			if(pdfList.size() > 0){
 				//更改这个功能，调出对话框，让用户选择顺序
 				pdfListSorted = pdfList;
 				
 				SortFilePanel sortFilePanel = new SortFilePanel(pdfListSorted);
 				if(sortFilePanel.showDialog()){
 					m_clickClose = sortFilePanel.isClickClose;
 					m_isOver = sortFilePanel.isOver;
 					//关闭按钮
 					if(m_clickClose == true && m_isOver == false){
 						return 0;
 					}
 					//确定按钮
 				}
 				
 				while(!sortFilePanel.isOver){
 					Thread.sleep(100);
 				}

 			}
 			
 			//合并
 			if(pdfListSorted != null && pdfListSorted.size() > 0){
 				String[] arg0 = {TcCommonDefines.PROPERTY_itemid,
 						TcCommonDefines.PROPERTY_objectname};
				String[] retVals = m_ItemRevision.getProperties(arg0);
 				String revId = retVals[0];
 				String objName = retVals[1];
 				String postName = "";
 				String fileName = ReplaceInvalidCharacters(revId + objName + postName);
 				String outputFile = tempDir + java.io.File.separator + fileName + ".pdf";
 				if(MergePdfToOne.PDFMerge(pdfListSorted.toArray(new String[pdfListSorted.size()]), outputFile)){
 					MergedFileData inputData = new MergedFileData();
 					inputData.m_datasetName = fileName;
 					inputData.m_localPdf = outputFile;
 					inputData.m_datasetType = ConvertToPdf.m_pdfDataSetType;
 					inputData.m_datasetRef = ConvertToPdf.m_pdfDataSetRef;
 					
 					if(this.UploadFile(inputData)){
 						count = pdfListSorted.size();
 					}
 					
 				}
 			}
		}catch(Exception ex){
//			if(m_log != null){
//				m_log.write("Merge pdf has error:" + ex.getMessage());
//			}
		}		
		return count;		
	}
	
	public int DoMerge(){
		int count = 0;
		try{
			String tempDir = "c:"+java.io.File.separator + "temp"+java.io.File.separator + "UDS";
			
			java.util.List<String> pdfList = new java.util.ArrayList<String>();
			
			PdfFileData[] files = this.GetPdfFiles(tempDir);
 			if(files != null && files.length>0){
 				for(Integer i=0;i<files.length;i++){
 					pdfList.addAll(files[i].m_pdfFiles);
 				}
 			}
 			
 			return DoMergeWithFiles(tempDir, pdfList);
 			
		}catch(Exception ex){
//			if(m_log != null){
//				m_log.write("Merge pdf has error:" + ex.getMessage());
//			}
		}		
		return count;
	}
	private boolean UploadFile(MergedFileData pdfFile){
		
		try{
			java.io.File myFile = new java.io.File(pdfFile.m_localPdf);
			if(!myFile.exists()){
				return false;
			}else{
				String[] arg0 = {pdfFile.m_localPdf};
				String[] arg1 = {pdfFile.m_datasetRef};
				
				TCComponentDatasetType datasetType = 
						(TCComponentDatasetType)this.m_session.getTypeComponent(TcCommonDefines.DATASET_type);
				if(datasetType == null){
//					m_log.write("MergedPDF dataset type not get type component:" + pdfFile.m_datasetType);
					return false;
				}
				TCComponentDataset dataset = null;
				try{
					dataset = datasetType.create(pdfFile.m_datasetName, pdfFile.m_description, pdfFile.m_datasetType);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				if(dataset == null){
//					m_log.write("MergedPDF dataset type not found:" + pdfFile.m_datasetType);
					return false;
				}
				
				dataset.setFiles(arg0, arg1);
				dataset.save();
				
				TCComponent parent = null;
				String childRelation = "";
				//20161129chenchun add to revision
				parent = this.m_ItemRevision;
				childRelation = TcCommonDefines.TCCOMPONENT_RELATION_iman_specification;
				
				//add to newstuff
				//TCComponentFolder newStuff = com.uds.tc.general.util.TcUtils.GetNewStuff(m_session);
				//newStuff.lock();
				//newStuff.add("contents", dataset);
				//newStuff.unlock();
				
				if(parent != null){
					parent.lock();
					parent.add(childRelation, dataset);
					parent.unlock();
				}
				
				return true;				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}	
	/**
	 * @param rootDir
	 * @return
	 * 将选中版本下的dataSet下载到本地固定目录下
	 */
	private PdfFileData[] GetPdfFiles(String rootDir) {
		try{
			java.util.ArrayList<PdfFileData> myList = new java.util.ArrayList<PdfFileData>();
			java.util.Map<String, String> filesToBeConvert = new java.util.HashMap<String, String>();
			AIFComponentContext[] children = m_ItemRevision.getRelated();
			if(children != null && children.length > 0){
				for(Integer i=0; i<children.length; i++){
					AIFComponentContext child = children[i];
					if(child != null){
						InterfaceAIFComponent comp = child.getComponent();
						if(comp instanceof TCComponentDataset){
							TCComponentDataset ds = (TCComponentDataset)comp;
							//获取PDF
							List<String> namedRefs = new java.util.ArrayList<String>();
							namedRefs.add(TcCommonDefines.DATASET_REF_pdfRef);
							namedRefs.add(TcCommonDefines.DATASET_REF_excelRef);
							namedRefs.add(TcCommonDefines.DATASET_REF_wordRef);
							namedRefs.add(TcCommonDefines.DATASET_REF_pptRef);
							java.util.Map<String,String> files = TcDatasetUtils.FileToLocalDirWithRefNames(ds, rootDir,namedRefs);
							
							if (files != null && files.size() > 0)
								
								filesToBeConvert.putAll(files);
								
								
						}
					}
				}
			}
			ConvertToPdf con = new ConvertToPdf();
			java.util.Map<String,String> pdfFiles = con.ConvertFileToPDF(filesToBeConvert);
			if(pdfFiles != null && pdfFiles.size() > 0){									
				PdfFileData pdfData = new PdfFileData();								
				pdfData.m_pdfFiles = new java.util.ArrayList<String>(pdfFiles.values());								
				myList.add(pdfData);
			}
			return myList.toArray(new PdfFileData[myList.size()]);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	private PdfFileData[] GetPdfFiles(String refType, String rootDir){
		try{
			java.util.ArrayList<PdfFileData> myList = new java.util.ArrayList<PdfFileData>();
			AIFComponentContext[] children = m_ItemRevision.getRelated(refType);
			if(children != null && children.length > 0){
				for(Integer i=0; i<children.length; i++){
					AIFComponentContext child = children[i];
					if(child != null){
						InterfaceAIFComponent comp = child.getComponent();
						if(comp instanceof TCComponentDataset){
							TCComponentDataset ds = (TCComponentDataset)comp;
							//获取PDF
							List<String> namedRefs = new java.util.ArrayList<String>();
							namedRefs.add(TcCommonDefines.DATASET_REF_pdfRef);

							java.util.Map<String,String> files =TcDatasetUtils.FileToLocalDirWithRefNames(ds, rootDir,namedRefs);
							
							if(files != null && files.size() > 0){									
								PdfFileData pdfData = new PdfFileData();								
								pdfData.m_pdfFiles = new java.util.ArrayList<String>(files.keySet());								
								myList.add(pdfData);
							}
						}
					}
					
				}
			}
			
			return myList.toArray(new PdfFileData[myList.size()]);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	class PdfFileData{
		public java.util.List<String> m_pdfFiles = null;
		
	}
	class MergedFileData{
		public String m_localPdf = null;
		public String m_datasetName = null;
		
		public String m_description = "";
		
		public String m_datasetType = TcCommonDefines.DATASET_pdf_type;
		public String m_datasetRef = TcCommonDefines.DATASET_REF_pdfRef;
	}
}
