
/**************************************************************************************************                                      
*                                               ��Ȩ��UDS���У�2016
**************************************************************************************************                             
*  
*        Function Description
*        dataset utility
**************************************************************************************************
* Date           Author                   History  
* 24-Nov-2016    ChenChun                Initial
**************************************************************************************************/

package com.uds.yl.tcutils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;

public class TcDatasetUtils {
	

	/**
	 * �������ݼ�
	 * 
	 * @param componentDataset
	 *            ���ݼ�����
	 * @param localDir
	 *            ����Ŀ¼
	 * @return
	 */
	public static java.util.Map<String, String> FileToLocalDirWithRefNames(TCComponentDataset componentDataset,
			String localDir, List<String> namedRefs) {
		try {
			Map<String, String> localFiles = DownloadFiles(componentDataset, localDir, namedRefs);
			if (localFiles != null && localFiles.size() > 0) {
				return localFiles;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static java.util.Map<String, String> FileToLocalDir(TCComponentDataset componentDataset, String localDir) {

		return FileToLocalDirWithRefNames(componentDataset, localDir, null);

	}

	private static java.util.Map<String, String> DownloadFiles(TCComponentDataset componentDataset, String rootDir,
			List<String> hasNamedRefs) {

		try {
			TCComponentDatasetDefinition dsDef = componentDataset.getDatasetDefinitionComponent();
			String[] namedRefs = dsDef.getNamedReferences();// get all named
															// refs which exist
															// in this dataset
			if (namedRefs != null && namedRefs.length > 0) {
				Map<String, String> localFiles = new HashMap<String, String>();
				// ��ȡÿ�����͵��ļ�
				for (Integer i = 0; i < namedRefs.length; i++) {
					String namedRef = namedRefs[i];
					// �ж��Ƿ�����Ч�б�
					if (hasNamedRefs != null && hasNamedRefs.indexOf(namedRef) < 0) {
						continue;
					}
					String[] refFiles = componentDataset.getFileNames(namedRef);
					if (refFiles != null && refFiles.length > 0) {
						// ���Ŀ¼����,Ϊ�˱�֤·��Ψһ
						String localDir = rootDir + java.io.File.separator + namedRef + i.toString();
						// ����·��
						java.io.File dirObject = new java.io.File(localDir);
						if (!dirObject.exists()) {
							dirObject.mkdirs();
						}
						// ��ȡ�ļ�
						for (Integer index = 0; index < refFiles.length; index++) {
							String myFile = DownloadFile(componentDataset, namedRef, refFiles[index], localDir);
							if (myFile != null) {

								localFiles.put(myFile, namedRef);
							}
						}

					}

				}
				return localFiles;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private static String DownloadFile(TCComponentDataset componentDataset, String namedRef, String refFileName,
			String localDir) {

		// first check if the file is already at local, then delete it
		System.out.println("���뺯������������������������������������������");
		String datasetName = "";
		try {
			String suffix = refFileName.substring(refFileName.lastIndexOf(".") + 1); // ��׺��
			datasetName = componentDataset.getProperty("object_name") + componentDataset.getProperty("object_desc");
			// ���ܴ����е����ݼ����ƴ���չ�����еĲ�����չ���������Ҫ��������жϡ�2017-3-17
			if (datasetName.lastIndexOf(".") == -1) {
				datasetName = datasetName + "." + suffix;
			}
			java.io.File myFileDataset = new java.io.File(localDir, datasetName);
			System.out.println("datasetNameFile:-----------" + myFileDataset.toString());
			if (myFileDataset.exists()) {
				myFileDataset.delete();
			}

			java.io.File myFile = new java.io.File(localDir, refFileName);
			if (myFile.exists()) {
				myFile.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;

		}

		// second get file
		try {

			java.io.File fileObj = componentDataset.getFile(namedRef, refFileName, localDir);

			File renameFile = new File(fileObj.getParentFile() + "\\" + datasetName); // ���ļ���������������Ϊ���ݼ�����
			boolean aa = fileObj.renameTo(renameFile);//////////// ----------Added
														//////////// by
														//////////// zhaoyao////////////
														//////////// -2017-3-23
			System.out.println(aa + "------refFileName:---" +fileObj.toString()+ "----"+"RENAME TO"+ "--datasetnameFile:----" +renameFile.toString());
			return renameFile.getAbsolutePath();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean DeleteDataset(TCComponent parent, TCComponentDataset ds, String refType) {
		try {
			parent.remove(refType, ds);
			ds.delete();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
