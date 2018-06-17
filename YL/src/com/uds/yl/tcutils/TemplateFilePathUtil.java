package com.uds.yl.tcutils;

import org.apache.xalan.templates.DecimalFormatProperties;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.Const.CommonCosnt;

public class TemplateFilePathUtil {
	
	
	/**
	 * @param code  ��ѡ��Ĵ��� ��ʾ���ŵı��
	 * @param fileName  ��.���߲���.
	 * @return
	 */
	public static TCComponentDataset getDatasetByNameAndCode(String code,String fileName){
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{Const.QueryKey.TYPE,Const.QueryKey.NAME,Const.QueryKey.OWNER}, 
				new String[]{"Folder","Home","infodba"});
		if(searchResult.length!=1){//ֻ��һ����ʱ���ǶԵ�
			return null;
		}
		TCComponentFolder homeFolder = (TCComponentFolder) searchResult[0];
		try {
			AIFComponentContext[] children = homeFolder.getChildren();
			for(AIFComponentContext context :children){
				TCComponent component = (TCComponent) context.getComponent();
				String name = component.getProperty("object_name");
				if(Const.CommonCosnt.Model_File_Root_Name.endsWith(name)){//���ݼ�ģ��
					TCComponentFolder rootFolder = (TCComponentFolder) component;
					AIFComponentContext[] departmentChildren = rootFolder.getChildren();
					for(AIFComponentContext departmentContext : departmentChildren){
						TCComponent departmentComponent = (TCComponent) departmentContext.getComponent();
						String departmentName = departmentComponent.getProperty("object_name");
						if(code.equals(departmentName)){//�ҵ��ļ��в��Ŷ�Ӧ���ļ���
							TCComponentFolder departmentFolder = (TCComponentFolder) departmentComponent;
							AIFComponentContext[] fileChildren = departmentFolder.getChildren();
							for(AIFComponentContext fileContext : fileChildren){
								TCComponent fileComponent = (TCComponent) fileContext.getComponent();
								String datasetName = fileComponent.getProperty("object_name");
								if(fileName.contains(datasetName)){//�ҵ��ļ��Ļ�
									TCComponentDataset dataset = (TCComponentDataset) fileComponent;
									return dataset;
								}
							}
						}
					}
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
