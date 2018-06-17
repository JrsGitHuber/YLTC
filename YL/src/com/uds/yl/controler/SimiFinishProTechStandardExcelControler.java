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
import com.uds.yl.service.ISimiFinishProTechStandardExcelService;
import com.uds.yl.service.impl.SimiFinishProTechStandardServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
//���Ʒ������׼����
public class SimiFinishProTechStandardExcelControler implements BaseControler {
	private ISimiFinishProTechStandardExcelService ihalfProTechStandardService = new SimiFinishProTechStandardServiceImpl();
	private TCComponentBOMLine topBomLine = null;//������׼�汾��BOM��topbomline
	
	private List<TCComponentBOMLine> halfProIndexBomList = null;//�ȴ���ȡ��BOM
	
	public void userTask(TCComponentItemRevision itemRev) {
		// TODO Auto-generated method stub
		//�����䷽��ȡ�䷽��topBOM
		 topBomLine=ihalfProTechStandardService.getTopBOMLine(itemRev);
		 if(topBomLine==null){
				MessageBox.post("�����䷽��BOM�ṹ","",MessageBox.ERROR);
				return;
		 }
		//�����䷽��ȡԭ���ϱ�׼�ĵ�BOM
		 halfProIndexBomList = ihalfProTechStandardService.gethalfProIndexBomList(topBomLine);
		 ihalfProTechStandardService.getIndexBeanList(halfProIndexBomList);
		 
		 //�ϴ����汾��
		 File file = new File(Const.MaterialorAccIndexStandard.HALFPRO_INDEXSTANDARD_EXCEL_PATH);
		 if(file.exists()){//���ھ��ϴ�
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(Const.MaterialorAccIndexStandard.HALFPRO_INDEXSTANDARD_EXCEL_PATH,
					 DataSetUtil.DataSetType.MSExcelX, 
					 DataSetUtil.DataSetNameRef.Excel, 
					 Const.MaterialorAccIndexStandard.Simi_Finish_Product_Excel_Name);
			 
			 try {
				itemRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
			}
		 }
		
	}



	
}
