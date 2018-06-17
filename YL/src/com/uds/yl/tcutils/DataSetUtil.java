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
	 * 上传本地文件至TC TC系统中不含本地文件的数据集
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
	 * 下载数据集
	 * 
	 * @param componentDataset
	 *            数据集对象
	 * @param namedRefName
	 *            数据集引用
	 * @param localDir
	 *            缓存目录
	 * @return
	 */
	public  static String[] downDateSetToLocalDir(TCComponentDataset componentDataset, String namedRefName,
			String localDir) {
		try {
			// 获取缓存路径
			File dirObject = new File(localDir);
			if (!dirObject.exists()) {
				dirObject.mkdirs();
			}

			componentDataset = componentDataset.latest();

			// 注意：命名引用[引用名]相同的文件可能存在多个
			String namedRefFileName[] = componentDataset.getFileNames(namedRefName);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				MessageBox.post("数据集<" + componentDataset.toString() + ">没有对应的命名引用!", "错误", MessageBox.ERROR);
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
			MessageBox.post("数据集<" + componentDataset.toString() + ">配置错误!", "错误", MessageBox.ERROR);
			return null;
		}

	}
	
	
	
}
