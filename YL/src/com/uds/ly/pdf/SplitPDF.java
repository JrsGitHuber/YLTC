package com.uds.ly.pdf;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;
public class SplitPDF {
	//" -password <password> Password to decrypt document\n"
	//" -split <integer> split after this many pages (default 1, if startPage and endPage are unset)\n"(拆分后每个文档几页)
	//" -startPage <integer> start page\n"
	//" -endPage <integer> end page\n"
	//" -nonSeq Enables the new non-sequential parser\n"
	//" -outputPrefix <output prefix> Filename prefix for image files\n"
	//" <PDF file> The PDF document to use\n"
	public String splitPDF(String PDFFileName)
	{
		String[] args = new String[3];
		args[0] = "-startPage";
		args[1] = "2";
		//args[2] = "-endPage";
		//args[3] = "1";
		//args[4] = "C:\\Siemens\\TC11Dev\\Temp\\pdffile.pdf";
		args[2] = PDFFileName;
		try {
			String[] fileNames = split(args);
			if (fileNames == null)
				return null;
			return fileNames[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static final String PASSWORD = "-password";
	private static final String SPLIT = "-split";
	private static final String START_PAGE = "-startPage";
	private static final String END_PAGE = "-endPage";
	private static final String NONSEQ = "-nonSeq";
	private static final String OUTPUT_PREFIX = "-outputPrefix";

	private String[] split(String[] args) throws Exception {
		String password = "";
		String split = null;
		String startPage = null;
		String endPage = null;
		boolean useNonSeqParser = false;
		Splitter splitter = new Splitter();
		String pdfFile = null;
		String outputPrefix = null;
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-password")) {
				++i;
				if (i >= args.length) {
					usage();
				}
				password = args[i];
			} else if (args[i].equals("-split")) {
				++i;
				if (i >= args.length) {
					usage();
				}
				split = args[i];
			} else if (args[i].equals("-startPage")) {
				++i;
				if (i >= args.length) {
					usage();
				}
				startPage = args[i];
			} else if (args[i].equals("-endPage")) {
				++i;
				if (i >= args.length) {
					usage();
				}
				endPage = args[i];
			} else if (args[i].equals("-outputPrefix")) {
				++i;
				outputPrefix = args[i];
			} else if (args[i].equals("-nonSeq")) {
				useNonSeqParser = true;
			} else {
				if (pdfFile != null)
					continue;
				pdfFile = args[i];
			}

		}

		if (pdfFile == null) {
			usage();
		} else {
			if (outputPrefix == null) {
				outputPrefix = pdfFile.substring(0, pdfFile.lastIndexOf(46));
			}
			PDDocument document = null;
			List documents = null;
			try {
				if (useNonSeqParser) {
					document = PDDocument.loadNonSeq(new File(pdfFile), null, password);
				} else {
					document = PDDocument.load(pdfFile);
					if (document.isEncrypted()) {
						document.decrypt(password);
					}
				}

				int numberOfPages = document.getNumberOfPages();
				boolean startEndPageSet = false;
				if (startPage != null) {
					splitter.setStartPage(Integer.parseInt(startPage));
					startEndPageSet = true;
					if (split == null) {
						splitter.setSplitAtPage(numberOfPages);
					}
				}
				if (endPage != null) {
					splitter.setEndPage(Integer.parseInt(endPage));
					startEndPageSet = true;
					if (split == null) {
						splitter.setSplitAtPage(Integer.parseInt(endPage));
					}
				}
				if (split != null) {
					splitter.setSplitAtPage(Integer.parseInt(split));
				} else if (!(startEndPageSet)) {
					splitter.setSplitAtPage(1);
				}

				documents = splitter.split(document);
				String[] documentsNames = new String[documents.size()];
				for (int i = 0; i < documents.size(); ++i) {
					PDDocument doc = (PDDocument) documents.get(i);
					String fileName = outputPrefix + "-" + i + ".pdf";
					java.io.File file = new java.io.File(fileName);
					if (file.exists())
						file.delete();
					writeDocument(doc, fileName);
					doc.close();
					documentsNames[i] = fileName;
				}
				return documentsNames;
			}
			catch(Exception e){
				e.printStackTrace();
				
			}
			finally {
				if (document != null) {
					document.close();
				}
				for (int i = 0; (documents != null) && (i < documents.size()); ++i) {
					PDDocument doc = (PDDocument) documents.get(i);
					doc.close();
				}
			}
		}
		return null;
	}

	private static void writeDocument(PDDocument doc, String fileName) throws IOException, COSVisitorException {
		FileOutputStream output = null;
		COSWriter writer = null;
		try {
			output = new FileOutputStream(fileName);
			writer = new COSWriter(output);
			writer.write(doc);
		} finally {
			if (output != null) {
				output.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static void usage() {
		System.err.println(
				"Usage: java -jar pdfbox-app-x.y.z.jar PDFSplit [OPTIONS] <PDF file>\n  -password  <password>  Password to decrypt document\n  -split     <integer>   split after this many pages (default 1, if startPage and endPage are unset)\n  -startPage <integer>   start page\n  -endPage   <integer>   end page\n  -nonSeq                Enables the new non-sequential parser\n  -outputPrefix <output prefix>  Filename prefix for image files\n  <PDF file>             The PDF document to use\n");

		System.exit(1);
	}}
