package com.uds.yl.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.MaterialaccBean;

public interface ISimiFinishProTechStandardExcelService {
	//根据版本获取TopLine
	public TCComponentBOMLine getTopBOMLine(TCComponentItemRevision itemRevision);
	//根据topBomLine获取原料标准指标的BOM
	public List<TCComponentBOMLine> gethalfProIndexBomList(TCComponentBOMLine topBomLine);
	//根据获取的BOM获取标准指标Bean
	public void getIndexBeanList(List<TCComponentBOMLine> halfProIndexBomList);
	//导出原料Excel
	public void DoIndexExcel(List<MaterialaccBean> tempList,Sheet sheet,int k,int j);

}
