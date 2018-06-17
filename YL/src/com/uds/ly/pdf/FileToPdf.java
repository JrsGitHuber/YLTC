package com.uds.ly.pdf;

import java.util.Map;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

public class FileToPdf {

	private static final int wdFormatPDF = 17;
	private static final int xlTypePDF = 0;
	private static final int ppSaveAsPDF = 32;
	
	public static boolean WordToPDF(String inputFile,String pdfFile){
		try{
			ActiveXComponent app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs,"Open",inputFile,false,true).toDispatch();
			Dispatch.call(doc,
					"ExportAsFixedFormat",
					pdfFile,
					wdFormatPDF
					);
			Dispatch.call(doc, "Close",false);
			app.invoke("Quit", 0);
			
			System.out.println("word success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean WordToPDFs(Map<String,String> inputs){
		
		try{
			com.jacob.activeX.ActiveXComponent app = new com.jacob.activeX.ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);
			Dispatch docs = app.getProperty("Documents").toDispatch();
			for(Map.Entry<String, String> item:inputs.entrySet()){
				String inputFile = item.getKey();
				String pdfFile = item.getValue();
				if(inputFile != null && !inputFile.isEmpty() && pdfFile != null){
					Dispatch doc = Dispatch.call(docs,"Open",inputFile,false,true).toDispatch();
					Dispatch.call(doc,
							"ExportAsFixedFormat",
							pdfFile,
							wdFormatPDF
							);
					Dispatch.call(doc, "Close",false);
					
				}
			}
			app.invoke("Quit", 0);
			
			System.out.println("word success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean ExcelToPDF(String inputFile,String pdfFile){
		try{
			ActiveXComponent app = new ActiveXComponent("Excel.Application");
			app.setProperty("Visible", false);
			Dispatch excels = app.getProperty("Workbooks").toDispatch();
			Dispatch excel = Dispatch.call(excels,"Open",inputFile,false,true).toDispatch();
			Dispatch.call(excel, "ExportAsFixedFormat", xlTypePDF, pdfFile);
			Dispatch.call(excel, "Close",false);
			app.invoke("Quit");
			
			System.out.println("excel success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean ExcelToPDFs(Map<String,String> inputs){
		
		try{
			ActiveXComponent app = new ActiveXComponent("Excel.Application");
			app.setProperty("Visible", false);
			Dispatch excels = app.getProperty("Workbooks").toDispatch();
			for(Map.Entry<String, String> item:inputs.entrySet()){
				String inputFile = item.getKey();
				String pdfFile = item.getValue();
				if(inputFile != null && !inputFile.isEmpty() && pdfFile != null){
					Dispatch excel = Dispatch.call(excels,"Open",inputFile,false,true).toDispatch();
					Dispatch.call(excel, "ExportAsFixedFormat", xlTypePDF, pdfFile);
					Dispatch.call(excel, "Close",false);
				}
			}
			app.invoke("Quit", 0);
			
			System.out.println("excel success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean PPTToPDFs(Map<String,String> inputs){
		System.out.println("启动PPT");
		try {
			ActiveXComponent app = new ActiveXComponent("Powerpoint.Application");
			Dispatch presentations = app.getProperty("Presentations").toDispatch();
			for(Map.Entry<String, String> item:inputs.entrySet()){
				String inputFile = item.getKey();
				String pdfFile = item.getValue();
				if(inputFile != null && !inputFile.isEmpty() && pdfFile != null){
					Dispatch presentation = Dispatch.call(presentations,"Open",inputFile,false,false,false).toDispatch();
					Dispatch.call(presentation, "SaveAs", pdfFile, ppSaveAsPDF);
					Dispatch.call(presentation, "Close");
					System.out.println("转换文档到PDF " + pdfFile);
				}
			}
			app.invoke("Quit");
			System.out.println("ppt success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean PPTToPDF(String inputFile,String pdfFile){
		try{
			ActiveXComponent app = new ActiveXComponent("Powerpoint.Application");
			Dispatch presentations = app.getProperty("Presentations").toDispatch();
			Dispatch presentation = Dispatch.call(presentations,"Open",inputFile, false, false, false).toDispatch();
			Dispatch.call(presentation, "SaveAs", pdfFile, ppSaveAsPDF);
			Dispatch.call(presentation, "Close");
			app.invoke("Quit");
			
			System.out.println("ppt success!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean DeleteDirectory(String dir){
		java.io.File temp = new java.io.File(dir);
		return DeleteDirectory(temp);
	}
	public static boolean DeleteDirectory(java.io.File temp){
		try{
    		
    		if(temp.exists()){
    			if(temp.isDirectory()){
    				boolean hasFile = false;
    				java.io.File[] allFiles = temp.listFiles();
    				if(allFiles != null && allFiles.length>0){
    					for(int jj=0;jj<allFiles.length;jj++){
    						boolean isDeleted = DeleteDirectory(allFiles[jj]);
    						if(!isDeleted){
    							hasFile = true;
    						}
    					}
    				}
    				if(!hasFile){
    					return temp.delete();
    				}
    			}else if(temp.isFile()){
    				return temp.delete();
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
		return false;
	}
}
