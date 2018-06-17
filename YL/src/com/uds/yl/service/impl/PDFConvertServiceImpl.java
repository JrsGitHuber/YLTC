package com.uds.yl.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.ly.pdf.ConvertToPdf;
import com.uds.ly.pdf.TcCommonDefines;
import com.uds.yl.service.IPDFConvertService;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.TcDatasetUtils;

public class PDFConvertServiceImpl implements IPDFConvertService {

	public TCComponentItemRevision itemRev = null;

	public void convertToPdf() {
		try {
			String rootDir = "c:" + java.io.File.separator + "temp" + java.io.File.separator + "UDS";
			
			File rootFile = new File(rootDir);
			if(!rootFile.exists()){
				rootFile.mkdirs();
			}
			try {
				java.util.Map<String, String> filesToBeConvert = new java.util.HashMap<String, String>();
				AIFComponentContext[] children = itemRev.getRelated();
				if (children != null && children.length > 0) {
					for (Integer i = 0; i < children.length; i++) {
						AIFComponentContext child = children[i];
						if (child != null) {
							InterfaceAIFComponent comp = child.getComponent();
							if (comp instanceof TCComponentDataset) {
								TCComponentDataset ds = (TCComponentDataset) comp;
								// 获取PDF
								List<String> namedRefs = new java.util.ArrayList<String>();
								namedRefs.add(TcCommonDefines.DATASET_REF_pdfRef);
								namedRefs.add(TcCommonDefines.DATASET_REF_excelRef);
								namedRefs.add(TcCommonDefines.DATASET_REF_wordRef);
								namedRefs.add(TcCommonDefines.DATASET_REF_pptRef);
								java.util.Map<String, String> files = TcDatasetUtils.FileToLocalDirWithRefNames(ds,
										rootDir, namedRefs);

								if (files != null && files.size() > 0)
									filesToBeConvert.putAll(files);
							}
						}
					}
				}

				ConvertToPdf con = new ConvertToPdf();
				Map<String, String> pdfFiles = con.ConvertFileToPDF(filesToBeConvert);
				
				//上传转换成的pdf到TC
				Map<String, String> pdfNameAndPathMap = new HashMap<>();
				Iterator<String> iterator = pdfFiles.values().iterator();
				while(iterator.hasNext()){
					String pdfPath = iterator.next();
					if(!"".equals(pdfPath)){
						String pdfName = pdfPath.substring(pdfPath.lastIndexOf("\\")+1);
						pdfNameAndPathMap.put(pdfName, pdfPath);
					}
				}
				System.out.println();
				
				Iterator<Entry<String, String>> pdfNameAndPathIterator = pdfNameAndPathMap.entrySet().iterator();
				while(pdfNameAndPathIterator.hasNext()){
					Entry<String, String> pdfNameAndPath = pdfNameAndPathIterator.next();
					String pdfName = pdfNameAndPath.getKey();
					String pdfPath = pdfNameAndPath.getValue();
					TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(pdfPath, "PDF", "PDF_Reference", pdfName);
					itemRev.add("IMAN_specification", dataSet);
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception ex) {
		}
	}

}
