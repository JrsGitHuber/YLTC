package com.uds.ly.pdf;

import java.io.IOException;

import org.apache.pdfbox.util.*;
import org.apache.pdfbox.exceptions.*;

public class MergePdfToOne {

	public static boolean PDFMerge(String[] pdfFiles, String outputFile){
		try {
			if(pdfFiles != null && pdfFiles.length>0){
				PDFMergerUtility mergePdf = new PDFMergerUtility();        
				for(int i = 0; i < pdfFiles.length; i++)
					mergePdf.addSource(pdfFiles[i]);
				
				mergePdf.setDestinationFileName(outputFile);
				mergePdf.mergeDocuments();
				
				System.out.println("pdf mergeÖ´ÐÐ³É¹¦£¡");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}
		return false;
	}	
}
