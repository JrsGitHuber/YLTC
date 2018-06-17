package com.uds.yl.service;

import java.util.List;
import java.util.logging.Logger;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.LawBean;

public interface ILawImportService {
	
	List<LawBean> getLawBeansFromExcel(String filePath);
	
	String getLawCategoryFromExcel(String fielPath);
	
	void createOrUpdateLawBom(TCComponentBOMLine topBomLine,List<LawBean> lawBeansFromExcel,List<TCComponentBOMLine> lawBomLineChilds ,String type,
			TCComponentFolder folder,TCComponentFolder materialFolder)throws TCException, InstantiationException, IllegalAccessException;
	
	//检查是否有多与64个汉字128个字节的名字
	boolean isCanImportByName(List<LawBean> lawBeansFromExcel);
	
	//根据ID查询法规
	public TCComponentItem searchLawItemByID(String id);
	
	//创建法规
	public TCComponentItemRevision createLawItem(TCComponentFolder folder,String lawID,String lawName,String lawRevNum,Logger logger);
	
	
	//更新法规
	public TCComponentItemRevision updateLawItem(TCComponentItem lawItem,String lawRevNum);
	
	//获取专门导入指标的文件夹
	public TCComponentFolder getIndexFolder();
	
	//获取专门导入原料的文件夹
	public TCComponentFolder getMaterialFolder();
}

