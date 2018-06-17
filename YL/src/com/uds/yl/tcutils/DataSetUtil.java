package com.uds.yl.tcutils;

import java.io.File;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.util.MessageBox;

public class DataSetUtil {

	public interface DataSetNameRef{
		String Excel = "U8_excel";
		String Word = "U8_word";
		String Pdf = "pdf";
	}
	public interface DataSetType{
		String MSExcelX = "MSExcelX";
		String MSExcel = "MSExcel";
		String MSWord = "MSWord";
		String MSWordX = "MSWordX";
		String PDF = "PDF";
	}
	
	
	/**
	 * �ϴ������ļ���TC TCϵͳ�в��������ļ������ݼ�
	 * 
	 * @param localFile
	 * @param datasetType MSExcelX
	 * @param datasetNamedRef  excel
	 * @param datasetName
	 * @return
	 */
	public static TCComponentDataset setDatasetFileToTC(String localFile, String datasetType, String datasetNamedRef,
			String datasetName) {
		try {
			TCSession tcSession = (TCSession) AIFUtility.getDefaultSession();
			String filePathNames[] = { localFile };
			String namedRefs[] = { datasetNamedRef };
			TCTypeService typeService = tcSession.getTypeService();
			TCComponentDatasetType TCDatasetType = (TCComponentDatasetType) typeService.getTypeComponent(datasetType);
			TCComponentDataset datasetComponent = TCDatasetType.setFiles(datasetName, "Created by program.",
					datasetType, filePathNames, namedRefs);
			return datasetComponent;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	/**
	 * �������ݼ�
	 * 
	 * @param componentDataset
	 *            ���ݼ�����
	 * @param namedRefName
	 *            ���ݼ�����
	 * @param localDir
	 *            ����Ŀ¼
	 * @return
	 */
	public  static String[] downDateSetToLocalDir(TCComponentDataset componentDataset, String namedRefName,
			String localDir) {
		try {
			// ��ȡ����·��
			File dirObject = new File(localDir);
			if (!dirObject.exists()) {
				dirObject.mkdirs();
			}

			componentDataset = componentDataset.latest();

			// ע�⣺��������[������]��ͬ���ļ����ܴ��ڶ��
			String namedRefFileName[] = componentDataset.getFileNames(namedRefName);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				MessageBox.post("���ݼ�<" + componentDataset.toString() + ">û�ж�Ӧ����������!", "����", MessageBox.ERROR);
				return null;
			}

			String fileDirName[] = new String[namedRefFileName.length];
			for (int i = 0; i < namedRefFileName.length; i++) {
				File tempFileObject = new File(localDir, namedRefFileName[i]);
				if (tempFileObject.exists()) {
					tempFileObject.delete();
				}
				File fileObject = componentDataset.getFile(namedRefName, namedRefFileName[i], localDir);
				fileDirName[i] = fileObject.getAbsolutePath();
			}
			return fileDirName;

		} catch (Exception e) {
			MessageBox.post("���ݼ�<" + componentDataset.toString() + ">���ô���!", "����", MessageBox.ERROR);
			return null;
		}

	}
	
	
	
}
