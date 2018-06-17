package com.uds.yl.controler;

import java.io.File;
import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.common.Const;
import com.uds.yl.service.IProductTechStandardExcelService;
import com.uds.yl.service.impl.ProductTechStandardExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
//产品技术标准报表
public class ProductTechStandardExcelControler implements BaseControler {
	private IProductTechStandardExcelService iendProTechStandardService = new ProductTechStandardExcelServiceImpl();
	private TCComponentBOMLine topBomLine = null;//技术标准版本的BOM的topbomline
	
	private List<TCComponentBOMLine> endProIndexBomList = null;//等待获取的BOM
	
	public void userTask(TCComponentItemRevision itemRev) {
		// TODO Auto-generated method stub
		//根据配方获取配方的topBOM
		 topBomLine=iendProTechStandardService.getTopBOMLine(itemRev);
		 if(topBomLine==null){
				MessageBox.post("请检查配方的BOM结构","",MessageBox.ERROR);
				return;
		 }
		//根据配方获取原辅料标准的的BOM
		 endProIndexBomList = iendProTechStandardService.getendProIndexBomList(topBomLine);
		 iendProTechStandardService.getIndexBeanList(endProIndexBomList);
		 
		 //上传到版本下
		 File file = new File(Const.MaterialorAccIndexStandard.ENDPRO_INDEXSTANDARD_EXCEL_PATH);
		 if(file.exists()){//存在就上传
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(Const.MaterialorAccIndexStandard.ENDPRO_INDEXSTANDARD_EXCEL_PATH,
					 DataSetUtil.DataSetType.MSExcelX, 
					 DataSetUtil.DataSetNameRef.Excel, 
					 Const.MaterialorAccIndexStandard.Product_Excel_Name);
			 
			 try {
				itemRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
			}
		 }
		
		
	}

	
}
