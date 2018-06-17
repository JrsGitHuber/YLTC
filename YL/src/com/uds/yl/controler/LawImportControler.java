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
	
	Logger logger = LogFactory.initLog("法规导入：", LogLevel.ERROE.getValue());
	
	
	public void userTask(final TCComponentFolder folder) {
		
		AbstractCallBack callBack = new AbstractCallBack() {
			@Override
			public void setFilesAndType(final String type, final File[] files) {
				super.setFilesAndType(type, files);
				
				//遍历files一个一个的导入的说啊
				StringBuilder failBuilder = new StringBuilder();
				StringBuilder successBuilder = new StringBuilder();
				for(File file : files){
				
					boolean flag = handleOneExcelFile(file,folder,type);
					if(!flag){//如果这个文件导入失败就记录下来
						failBuilder.append(file.getName()).append("##");
					}else{
						successBuilder.append(file.getName()).append("##");
					}
				}
				
				
				String failResult = failBuilder.toString();
				String sucResult = successBuilder.toString();
				int failLength = 0;
				int successLength = 0;
				if(failResult.contains("##")){//说明有没有导入的
					failLength = failResult.split("##").length;
				}
				if(sucResult.contains("##")){//成功的提示
					successLength = sucResult.split("##").length;
				}
				MessageBox.post("失败个数:"+failLength+";"+"成功个数： "+successLength+"请到C盘YLLog下查看具体信息。",
						"文件已经存在或者失败",MessageBox.INFORMATION);
				
				
			}
		};
		
		LawImportFrame lawImportFrame = new LawImportFrame(callBack, logger);
		lawImportFrame.setVisible(true);
		
		
	}

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}
	
	
	/**
	 * 一次处理一个excel
	 */
	public boolean handleOneExcelFile(File file,TCComponentFolder folder,String type){
		boolean uploadFlag = true;
		//先获取bean判断Bean合不合规则后再进行导入
		filePath = file.getAbsolutePath();
		List<LawBean> lawBeansFromExcel = iLawImportService.getLawBeansFromExcel(filePath);
		boolean canImportByName = iLawImportService.isCanImportByName(lawBeansFromExcel);
		if(!canImportByName){//不能导入
			logger.fine("[Failure]请检查"+filePath+"excel中的条目名字长度,检查后重新导入");
			MessageBox.post("请检查"+filePath+"excel中的条目名字长度,检查后重新导入","",MessageBox.INFORMATION);
			uploadFlag = false;
			return uploadFlag;
		}
		
		
		//将从excel中封装成Bean的集合中的上限符号和下限符号处理
		for(LawBean lawBean : lawBeansFromExcel){
			if("1.0".equals(lawBean.upOperation)){//1代表包含=号  2代表不包含=号
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
		
		
		
		//导入一个 要先分出来    体系ID  名称   版本   
		String fileName;
		String id;
		String revId;
		String revNum;
		String revName;
		try{
			fileName = file.getName().split("\\.")[0];//去除后缀后的名字
			id = fileName.split(" ")[0];//体系ID
			revId = fileName.split(" ")[1]+" "+fileName.split(" ")[2].split("-")[0];//法规的名称
			revNum = fileName.split(" ")[2].split("-")[1];//法规的版本号
			revName = fileName.split(" ")[3];//法规的名字 
		}catch(ArrayIndexOutOfBoundsException e){
			//数组越界 
			MessageBox.post("请检查"+file.getPath()+"的文件格式","",MessageBox.INFORMATION);
			logger.fine("[Failure]请检查"+file.getPath()+"的文件格式");
			uploadFlag = false;
			return uploadFlag;
		}
		
		//先找对应的法规如果存在的话就要更新啊  以为 id + revId作为法规的ID
		String lawID = id+ " "+revId;
		String lawName = revName;
		String lawRevNum = revNum;
		
		//根据ID查找法规  如果存在就升本版本
		TCComponentItem searchLawItem = iLawImportService.searchLawItemByID(lawID);
		TCComponentItemRevision lawRev = null;
		if(searchLawItem==null){//创建
			lawRev = iLawImportService.createLawItem(folder,lawID, lawName, lawRevNum,logger);
			if(lawRev==null){
				uploadFlag = false;
				return uploadFlag;
			}
		}else{//更新版本号
			lawRev = iLawImportService.updateLawItem(searchLawItem, lawRevNum);
			if(lawRev==null){
				logger.fine("[Failure]请检查"+filePath+"的版本号再系统中已经存在");
				uploadFlag = false;
				return uploadFlag;
			}
		}
		
		String bomType ="";
		if("添加剂".equals(type)){
			bomType="U8_Material";
		}else if("指标".equals(type)){
			bomType="U8_IndexItem";
		}
		
		
		//获取选中法规的topLine,没有的话就新建
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRev, Const.Law.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(lawRev);
		}
		
		//获取topBomLine中的孩子
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
			
			
			//在法规的版本下的BOM版本下的描述属性中写一个属性（法规）
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
