package com.uds.yl.controler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;







import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.ecmanagement.dialogs.ECMSelectSnapshotDialog;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.LawBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.ILawImportService;
import com.uds.yl.service.impl.LawImportServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.ui.LawImportFrame;
import com.uds.yl.ui.ProgressBarDialog;
import com.uds.yl.utils.LogFactory;
import com.uds.yl.utils.StringsUtil;

public class LawImportControler implements BaseControler {

	private ILawImportService iLawImportService = new LawImportServiceImpl();
	private String filePath = "";
	private File file = null;
	
	private TCComponentFolder indexFolder;
	private TCComponentFolder materialFolder;
	
	Logger logger = LogFactory.initLog("���浼�룺", LogLevel.ERROE.getValue());
	
	
	public void userTask(final TCComponentFolder folder) {
		
		AbstractCallBack callBack = new AbstractCallBack() {
			@Override
			public void setFilesAndType(final String type, final File[] files) {
				super.setFilesAndType(type, files);
				
				//����filesһ��һ���ĵ����˵��
				StringBuilder failBuilder = new StringBuilder();
				StringBuilder successBuilder = new StringBuilder();
				for(File file : files){
				
					boolean flag = handleOneExcelFile(file,folder,type);
					if(!flag){//�������ļ�����ʧ�ܾͼ�¼����
						failBuilder.append(file.getName()).append("##");
					}else{
						successBuilder.append(file.getName()).append("##");
					}
				}
				
				
				String failResult = failBuilder.toString();
				String sucResult = successBuilder.toString();
				int failLength = 0;
				int successLength = 0;
				if(failResult.contains("##")){//˵����û�е����
					failLength = failResult.split("##").length;
				}
				if(sucResult.contains("##")){//�ɹ�����ʾ
					successLength = sucResult.split("##").length;
				}
				MessageBox.post("ʧ�ܸ���:"+failLength+";"+"�ɹ������� "+successLength+"�뵽C��YLLog�²鿴������Ϣ��",
						"�ļ��Ѿ����ڻ���ʧ��",MessageBox.INFORMATION);
				
				
			}
		};
		
		LawImportFrame lawImportFrame = new LawImportFrame(callBack, logger);
		lawImportFrame.setVisible(true);
		
		
	}

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}
	
	
	/**
	 * һ�δ���һ��excel
	 */
	public boolean handleOneExcelFile(File file,TCComponentFolder folder,String type){
		boolean uploadFlag = true;
		//�Ȼ�ȡbean�ж�Bean�ϲ��Ϲ�����ٽ��е���
		filePath = file.getAbsolutePath();
		List<LawBean> lawBeansFromExcel = iLawImportService.getLawBeansFromExcel(filePath);
		boolean canImportByName = iLawImportService.isCanImportByName(lawBeansFromExcel);
		if(!canImportByName){//���ܵ���
			logger.fine("[Failure]����"+filePath+"excel�е���Ŀ���ֳ���,�������µ���");
			MessageBox.post("����"+filePath+"excel�е���Ŀ���ֳ���,�������µ���","",MessageBox.INFORMATION);
			uploadFlag = false;
			return uploadFlag;
		}
		
		
		//����excel�з�װ��Bean�ļ����е����޷��ź����޷��Ŵ���
		for(LawBean lawBean : lawBeansFromExcel){
			if("1.0".equals(lawBean.upOperation)){//1�������=��  2��������=��
				lawBean.upOperation="<=";
			}else if(StringsUtil.isEmpty(lawBean.upOperation)){
				lawBean.upOperation="";
			}else{
				lawBean.upOperation="<";
			}
			
			if("1.0".equals(lawBean.downOperation)){
				lawBean.downOperation=">=";
			}else if(StringsUtil.isEmpty(lawBean.downOperation)){
				lawBean.downOperation="";
			}else {
				lawBean.downOperation=">";
			}
			
			
		}
		
		
		
		//����һ�� Ҫ�ȷֳ���    ��ϵID  ����   �汾   
		String fileName;
		String id;
		String revId;
		String revNum;
		String revName;
		try{
			fileName = file.getName().split("\\.")[0];//ȥ����׺�������
			id = fileName.split(" ")[0];//��ϵID
			revId = fileName.split(" ")[1]+" "+fileName.split(" ")[2].split("-")[0];//���������
			revNum = fileName.split(" ")[2].split("-")[1];//����İ汾��
			revName = fileName.split(" ")[3];//��������� 
		}catch(ArrayIndexOutOfBoundsException e){
			//����Խ�� 
			MessageBox.post("����"+file.getPath()+"���ļ���ʽ","",MessageBox.INFORMATION);
			logger.fine("[Failure]����"+file.getPath()+"���ļ���ʽ");
			uploadFlag = false;
			return uploadFlag;
		}
		
		//���Ҷ�Ӧ�ķ���������ڵĻ���Ҫ���°�  ��Ϊ id + revId��Ϊ�����ID
		String lawID = id+ " "+revId;
		String lawName = revName;
		String lawRevNum = revNum;
		
		//����ID���ҷ���  ������ھ������汾
		TCComponentItem searchLawItem = iLawImportService.searchLawItemByID(lawID);
		TCComponentItemRevision lawRev = null;
		if(searchLawItem==null){//����
			lawRev = iLawImportService.createLawItem(folder,lawID, lawName, lawRevNum,logger);
			if(lawRev==null){
				uploadFlag = false;
				return uploadFlag;
			}
		}else{//���°汾��
			lawRev = iLawImportService.updateLawItem(searchLawItem, lawRevNum);
			if(lawRev==null){
				logger.fine("[Failure]����"+filePath+"�İ汾����ϵͳ���Ѿ�����");
				uploadFlag = false;
				return uploadFlag;
			}
		}
		
		String bomType ="";
		if("��Ӽ�".equals(type)){
			bomType="U8_Material";
		}else if("ָ��".equals(type)){
			bomType="U8_IndexItem";
		}
		
		
		//��ȡѡ�з����topLine,û�еĻ����½�
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRev, Const.Law.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(lawRev);
		}
		
		//��ȡtopBomLine�еĺ���
		List<TCComponentBOMLine> lawBomLineChilds = new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				lawBomLineChilds.add(bomLine);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		try {
			indexFolder = iLawImportService.getIndexFolder();
			materialFolder = iLawImportService.getMaterialFolder();
			
			iLawImportService.createOrUpdateLawBom(topBomLine,lawBeansFromExcel, lawBomLineChilds,bomType,indexFolder,materialFolder);
			
			
			//�ڷ���İ汾�µ�BOM�汾�µ�����������дһ�����ԣ����棩
			TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(lawRev);
			if(bomRevByItemRev != null){
				bomRevByItemRev.setProperty("object_desc", Const.BomViewType.LAW);
			}
			
		} catch (InstantiationException | IllegalAccessException | TCException e1) {
			e1.printStackTrace();
		}
		
		
		return uploadFlag;
	}

}
