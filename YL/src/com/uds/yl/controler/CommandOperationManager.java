package com.uds.yl.controler;


import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;




import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.Jr.GetDataAndExportExcel;
import com.uds.yl.base.BaseModuleOperation;
import com.uds.yl.bean.NodeBean;
import com.uds.yl.Jr.ExportProductStandardWord;
import com.uds.yl.Jr.ExportMaterialStandardWord;
import com.uds.yl.herb.FormulaCompareManager;
import com.uds.yl.herb.RawQueryManager;
import com.uds.yl.service.INewMilkFormulatorService;
import com.uds.yl.service.impl.NewMilkFormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.ui.DragTableTreeTest;
import com.uds.yl.ui.LabelSolidFrame;
import com.uds.yl.ui.NewMilkFormulatorFrame;
import com.uds.yl.ui.ProgressBar;
import com.uds.yl.ui.YNKPIStatisticsframe;


public class CommandOperationManager extends BaseModuleOperation {
	public String m_commandId = "";
	private Rectangle rectangleObj = null; 
	List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();
	
	
	
	
	
	
	
	public CommandOperationManager(Rectangle rectangleObj){
		this.rectangleObj = rectangleObj;
	}
	
	@Override
	protected void DoUserTask() {
		String dlgTitle = "伊利报表";
		String failure = "出错";
		String currentUserUid = this.GetCurrentUserUid();
		String userName = this.getSession().getUserName();

		try {
			if ("YL.commands.sampleCommand".equals(m_commandId)) {// 测试命令
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				String type2 = selComp.getType();
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) selComp;
				
				INewMilkFormulatorService iNewMilkFormulatorService = new NewMilkFormulatorServiceImpl();
				NodeBean rootNodeBean = iNewMilkFormulatorService.initRootNode(itemRevision);
				
				NewMilkFormulatorFrame newMilkFormulatorFrame = new NewMilkFormulatorFrame(rootNodeBean);
				newMilkFormulatorFrame.setVisible(true);

				String type = selComp.getType();
				System.out.println(type);
				
				
			}	


			if ("YL.commands.formulatorCommand".equals(m_commandId)) {// 配方生成器
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType) || "U8_MaterialRevision".equals(objType)) {
						isSelectedOk = true;
						FormulatorControler formulatorControler = new FormulatorControler();
						formulatorControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "配方生成器",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.techStandardCommand".equals(m_commandId)) {// 技术标准
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						TechStandardControler techStandardControler = new TechStandardControler();
						techStandardControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "技术标准",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.lawImportCommand".equals(m_commandId)) {// 法规导入
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentFolder) {
					TCComponentFolder folder = (TCComponentFolder) selComp;
					String objType = folder.getType();
					if ("Folder".equals(objType)) {
						isSelectedOk = true;
						LawImportControler lawImportControler = new LawImportControler();
						lawImportControler.userTask(folder);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "法规导入",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.projectStatisticsCommand".equals(m_commandId)) {// 项目统计报表
				ProjectStatisticsControler projectStatisticsControler = new ProjectStatisticsControler();
				projectStatisticsControler.userTask(null);// 这个功能不需要点击item
			}

			if ("YL.commands.formulatorLegalCheckCommand".equals(m_commandId)) {// 配方合规检查
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
						FormulatorLegalCheckControler formulatorLegalCheckControler = new FormulatorLegalCheckControler();
						formulatorLegalCheckControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "配方合规检查",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.formulatorModifyCommand".equals(m_commandId)) {// 组合配方搭建器
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					System.out.println(objType);
					if ("U8_FormulaRevision".equals(objType) || "U8_MaterialRevision".equals(objType)) {
						isSelectedOk = true;
						
						FormulatorModifyControler formulatorModifyControler = new FormulatorModifyControler(rectangleObj);
						formulatorModifyControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "配方生成器新增的版本",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.projectMemorabiliaCommand".equals(m_commandId)) {// 液奶大事记
				ProjectMemorabiliaControler projectMemorabiliaControler = new ProjectMemorabiliaControler(rectangleObj);
				projectMemorabiliaControler.userTask();
			}
			if ("YL.commands.techStandardModifyCommand".equals(m_commandId)) {// 技术标准新增
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						TechStandarModifyControler techStandardModifyControler = new TechStandarModifyControler();
						techStandardModifyControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "技术标准新增",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.dispatchDocumentToProjectCommand".equals(m_commandId)) {// 发文系统
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentTask) {
					TCComponentTask task = (TCComponentTask) selComp;
					isSelectedOk = true;
					DispatchDocumentToProjectControler dispatchDocumentToProjectControler = new DispatchDocumentToProjectControler();
					dispatchDocumentToProjectControler.douUserTask(task);
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "发文系统",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}

			}

			if ("YL.commands.pdfConvertCommand".equals(m_commandId)) {// PDF转换
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					isSelectedOk = true;

					PDFConvertControler pdfConvertControler = new PDFConvertControler();
					pdfConvertControler.userTask(itemRev);

				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "PDF合并",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}

			}

			if ("YL.commands.materialUseQueryCommand".equals(m_commandId)) {// 原料使用情况查询报表
				String soaAddr = this.GetSoaServiceAddress();
				if (soaAddr == null || "".equals(soaAddr)) {
					String msg = "没有指定UDS服务器地址";
					com.teamcenter.rac.util.MessageBox.post(msg, "错误", com.teamcenter.rac.util.MessageBox.ERROR);
					return;
				}

				RawQueryManager rawQuery = new RawQueryManager();
				rawQuery.m_wsdlAddr = soaAddr;
				rawQuery.m_session = m_session;
				String result = rawQuery.DoCreate(currentUserUid);
				if (result != null && !"".equals(result)) {
					com.teamcenter.rac.util.MessageBox.post(result, "原料使用查询",
							com.teamcenter.rac.util.MessageBox.WARNING);
				}
			}

			if ("YL.commands.formulaCompareCommand".equals(m_commandId)) {// 配方对比开发报表
				String soaAddr = this.GetSoaServiceAddress();
				if (soaAddr == null || "".equals(soaAddr)) {
					String msg = "没有指定UDS服务器地址";
					com.teamcenter.rac.util.MessageBox.post(msg, "错误", com.teamcenter.rac.util.MessageBox.ERROR);
					return;
				}
				// get the multi-selectedComponents,use 'CTRL' key.
				InterfaceAIFComponent[] selCompS = this.GetSelectedComponents();
				boolean isSelectedOk = false;
				List<String> uids = new ArrayList<String>();
				List<String> names = new ArrayList<String>();
				try {
					for (int i = 0; i < selCompS.length; i++) {
						if (selCompS[i] instanceof TCComponentItem) {
							TCComponentItem item = (TCComponentItem) selCompS[i];
							String objType = item.getType();
							if ("U8_Formula".equals(objType) || "U8_Material".equals(objType)) {
								TCComponentItemRevision itemRev0 = item.getLatestItemRevision();
								if (itemRev0 != null) {
									isSelectedOk = true;
									String uid = itemRev0.getUid();
									String itemName = itemRev0.getProperty("object_string");
									if (!uids.contains(uid)) {
										names.add(itemName);
										uids.add(uid);
									}
								}
							}
						} else {
							if (selCompS[i] instanceof TCComponentItemRevision) {
								TCComponentItemRevision revision = (TCComponentItemRevision) selCompS[i];
								String objType = revision.getType();
								if ("U8_FormulaRevision".equals(objType) || "U8_MaterialRevision".equals(objType)) {
									isSelectedOk = true;
									String uid = revision.getUid();
									String itemName = revision.getProperty("object_string");
									if (!uids.contains(uid)) {
										names.add(itemName);
										uids.add(uid);
									}
								}
							}
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", dlgTitle,
							com.teamcenter.rac.util.MessageBox.ERROR);
				} else if (uids.size() < 2) {
					com.teamcenter.rac.util.MessageBox.post("至少要选择两个配方或者原料对象", dlgTitle,
							com.teamcenter.rac.util.MessageBox.ERROR);
				} else {
					FormulaCompareManager compare = new FormulaCompareManager();
					compare.m_wsdlAddr = soaAddr;
					String result = compare.DoCreate(uids, names, currentUserUid);
					if (result != null && !"".equals(result)) {
						com.teamcenter.rac.util.MessageBox.post(result, dlgTitle,
								com.teamcenter.rac.util.MessageBox.WARNING);
					}
				}
			}

			if ("YL.commands.labelGeneratorCommand".equals(m_commandId)) {// 液态标签生成器
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
						LabelGeneratorExcelControler labelGeneratorExcelControler = new LabelGeneratorExcelControler();
						labelGeneratorExcelControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "标签生成器",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.labelGeneratorSolidCommand".equals(m_commandId)) {// 固态态标签生成器
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
						
						LabelSolidFrame labelSolidFrame = new LabelSolidFrame(itemRev);
						labelSolidFrame.setVisible(true);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "标签生成器",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.produceFormulaExcelCommand".equals(m_commandId)) {// 生产配方表
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;						
//						ProductFormulaExcelControler productFormulaExcelControler = new ProductFormulaExcelControler();
//						productFormulaExcelControler.userTask(itemRev);
						final String sessionStr = this.m_session.toString();
						Thread thread = new Thread()
						{
							@Override
							public void run() {
								try {
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "生产配方");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("出错：\n\n空值错误\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("提示")){
										MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
									} else {
										MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
						};
						//TipsUI.ShowUI("正在导出生产配方表", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("正在导出生产配方表", thread, rectangleObj);
						progressBar.Start();
						
						
//						try {
//							GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, this.m_session.toString(), "生产配方");
//							obj.AllExcelOperation();
//						} catch (Exception e) {
//							if(e.getMessage().startsWith("提示")){
//								MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
//							}else{
//								MessageBox.post("出错：\n\n" + e.getMessage(), "提示", MessageBox.INFORMATION);
//							}
//						}
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("请选择配方版本进行操作", "提示", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.financeFormulaExcelCommand".equals(m_commandId)) {// 财务配方表
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
//						FinanceFormulaExcelControler financeFormulaExcelControler = new FinanceFormulaExcelControler();
//						financeFormulaExcelControler.userTask(itemRev);
						
						final String sessionStr = this.m_session.toString();
						Thread thread = new Thread()
						{
							@Override
							public void run() {
								try {
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "财务配方");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("出错：\n\n空值错误\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("提示")){
										MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
									} else {
										MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
							
						};
//						TipsUI.ShowUI("正在导出财务配方表", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("正在导出财务配方表", thread, rectangleObj);
						progressBar.Start();
						
						
//						try {
//							GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, this.m_session.toString(), "财务配方");
//							obj.AllExcelOperation();
//						} catch (Exception e) {
//							if(e.getMessage().startsWith("提示")){
//								MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
//							}else{
//								MessageBox.post("出错：\n\n" + e.getMessage(), "提示", MessageBox.INFORMATION);
//							}
//						}
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("请选择配方版本进行操作", "提示", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.orderFormulaExcelCommand".equals(m_commandId)) {// 订单配方表
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
//						OrderFormulaExcelControler orderFormulaExcelControler = new OrderFormulaExcelControler();
//						orderFormulaExcelControler.userTask(itemRev);
						
						final String sessionStr = this.m_session.toString();
						Thread thread = new Thread()
						{
							@Override
							public void run() {
								try {
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "产品订单");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("出错：\n\n空值错误\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("提示")){
										MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
									} else {
										MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
							
						};
						//TipsUI.ShowUI("正在导出产品订单表", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("正在导出产品订单表", thread, rectangleObj);
						progressBar.Start();
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("请选择配方版本进行操作", "提示", MessageBox.INFORMATION);
				}
			}
			if ("YL.commands.materialtechStandardCommand".equals(m_commandId)) {// 原料技术标准
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						
						if ("原料技术标准".equals(indexType)) {
							final String sessionStr = this.m_session.toString();
							Thread thread = new Thread()
							{
								@Override
								public void run() {
									try {
										ExportMaterialStandardWord obj = new ExportMaterialStandardWord(itemRev, sessionStr, "原料技术标准");
										obj.initGetDataAndExportWord();
										obj.AllWordOperation();
									} catch (Exception e) {
										if (e.getMessage() == null) {
											MessageBox.post("出错：\n\n空值错误\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
										} else if (e.getMessage().startsWith("提示")) {
											MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
										} else {
											MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
										}										
										e.printStackTrace();
									}
								}								
							};
							ProgressBar progressBar = new ProgressBar("正在导出原料技术标准表", thread, rectangleObj);
							progressBar.Start();
						}else{
							MessageBox.post("请选择原料技术标准版本进行操作", "提示", MessageBox.INFORMATION);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("请选择原料技术标准版本进行操作", "提示", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.accessoriestechStandardCommand".equals(m_commandId)) {// 辅料技术标准
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						if ("辅料技术标准".equals(indexType)) {
							AccessoriesTechStandardExcelControler accessoriestechStandardControler = new AccessoriesTechStandardExcelControler();
							accessoriestechStandardControler.userTask(itemRev);
						} else {
							com.teamcenter.rac.util.MessageBox.post("所选对象类型不是辅料技术标准", "辅料技术标准",
									com.teamcenter.rac.util.MessageBox.ERROR);
						}

					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "辅料技术标准",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			// Jr 产品技术标准报表
			if ("YL.commands.endProtechStandardCommand".equals(m_commandId)) {// 产品技术标准报表
				System.out.println("2018 04 26 14:42");
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						
						if ("产品技术标准".equals(indexType)) {
							final String sessionStr = this.m_session.toString();
							Thread thread = new Thread()
							{
								@Override
								public void run() {
									try {
										ExportProductStandardWord obj = new ExportProductStandardWord(itemRev, sessionStr, "产品技术标准");
										obj.initGetDataAndExportWord();
										obj.AllWordOperation();
									} catch (Exception e) {
										if (e.getMessage() == null) {
											MessageBox.post("出错：\n\n空值错误\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
										} else if (e.getMessage().startsWith("提示")) {
											MessageBox.post(e.getMessage(), "提示", MessageBox.INFORMATION);
										} else {
											MessageBox.post("出错：\n\n" + e.getMessage() + "\n请查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
										}										
										e.printStackTrace();
									}
								}								
							};
							ProgressBar progressBar = new ProgressBar("正在导出原料技术标准表", thread, rectangleObj);
							progressBar.Start();
						}else{
							MessageBox.post("请选择产品技术标准版本进行操作", "提示", MessageBox.INFORMATION);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("请选择产品技术标准版本进行操作", "提示", MessageBox.INFORMATION);
				}
			}
			
			// 原版产品技术标准报表
//			if ("YL.commands.endProtechStandardCommand".equals(m_commandId)) {// 产品技术标准报表
//				System.out.println(this.m_session);
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentItemRevision) {
//					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
//					String objType = itemRev.getType();
//					String indexType = itemRev.getProperty("u8_techstandardtype");
//					if ("U8_IndexRevision".equals(objType)) {
//						isSelectedOk = true;
//						if ("产品技术标准".equals(indexType)) {
//							ProductTechStandardExcelControler endProtechStandardControler = new ProductTechStandardExcelControler();
//							endProtechStandardControler.userTask(itemRev);
//						} else {
//							com.teamcenter.rac.util.MessageBox.post("所选对象类型不是产品技术标准报表", "产品技术标准报表",
//									com.teamcenter.rac.util.MessageBox.ERROR);
//						}
//					}
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "产品技术标准报表",
//							com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
			
			

			if ("YL.commands.halfProtechStandardCommand".equals(m_commandId)) {// 半成品技术标准
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						if ("半成品技术标准".equals(indexType)) {
							SimiFinishProTechStandardExcelControler halfProtechStandardControler = new SimiFinishProTechStandardExcelControler();
							halfProtechStandardControler.userTask(itemRev);
						} else {
							com.teamcenter.rac.util.MessageBox.post("所选对象类型不是半成品技术标准", "半成品技术标准",
									com.teamcenter.rac.util.MessageBox.ERROR);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "半成品技术标准",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkFinManageExcelCommand".equals(m_commandId)) {// 液奶绩效管理统计报表
//				MilkFinManageExcelControler milkFinManageExcelControler = new MilkFinManageExcelControler();
//				milkFinManageExcelControler.userTask(null);// 这个功能不需要点击item
			}


			if ("YL.commands.coldDrinkFormulaExcelCommand".equals(m_commandId)) {// 冷饮生产配方表
																					// word形式
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				boolean hasBOM = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					if ("U8_FormulaRevision".equals(objType)) {
						isSelectedOk = true;
						ColdDrinkFormulaWordControler coldDrinkFormulaExcelControler = new ColdDrinkFormulaWordControler();
						coldDrinkFormulaExcelControler.userTask(itemRev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "生产配方表",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.firstTrailJugeCommand".equals(m_commandId)) {// 创意初审判定
				// 多选
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailSuccessOrNotControler firstTrailSuccessOrNotControler = new FirstTrailSuccessOrNotControler();
				firstTrailSuccessOrNotControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意初审通过",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.secondTrailJugeCommand".equals(m_commandId)) {// 创意审核判定
				// 多选
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				SecondTrailSuccessOrNotControler secondTrailSuccessOrNotControler = new SecondTrailSuccessOrNotControler();
				secondTrailSuccessOrNotControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意初审通过",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.firstTrailSuccessCommand".equals(m_commandId)) {// 创意初审通过
																				// 多选
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailSuccessControler firstTrailSuccessControler = new FirstTrailSuccessControler();
				firstTrailSuccessControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意初审通过",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.firstTrailFailCommand".equals(m_commandId)) {// 创意初审不通过
																			// 多选
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailFailControler firstTrailFailControler = new FirstTrailFailControler();
				firstTrailFailControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意初审不通过",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.creativeScoreOneCommand".equals(m_commandId)) {// 创意评分一属性
																			// 单选
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentForm) {
					TCComponentForm form = (TCComponentForm) selComp;
					String objType = form.getType();
					if ("U8_InformReport".equals(objType)) {// 创意提案表类型
						isSelectedOk = true;
						OneScoreFormControler oneScoreFormControler = new OneScoreFormControler();
						oneScoreFormControler.userTask(form, m_session);
					}
				}

				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.creativeScoreFourCommand".equals(m_commandId)) {// 创意评分四个属性
																				// 单选
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentForm) {
					TCComponentForm form = (TCComponentForm) selComp;
					String objType = form.getType();
					if ("U8_InformReport".equals(objType)) {// 创意提案表类型
						isSelectedOk = true;
						FourScoreFormConstroler fourScoreFormConstroler = new FourScoreFormConstroler();
						fourScoreFormConstroler.userTask(form, m_session);
					}
				}

				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeSumCommand".equals(m_commandId)) {// 创意总评
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeScoreSumControler creativeScoreSumControler = new CreativeScoreSumControler();
				creativeScoreSumControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeJugeCommand".equals(m_commandId)) {// 点子判定
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeJugeControler creativeJugeControler = new CreativeJugeControler();
				creativeJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeNoJugeCommand".equals(m_commandId)) {// 非点子判定
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeNoJugeControler creativeNoJugeControler = new CreativeNoJugeControler();
				creativeNoJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeSecondJugeCommand".equals(m_commandId)) {// 二次点子判定
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeSecondJugeControler creativeSecondJugeControler = new CreativeSecondJugeControler();
				creativeSecondJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "创意评分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			
			if ("YL.commands.coldFormulatorCommand".equals(m_commandId)) {// 冷饮配方搭建器
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision rev = (TCComponentItemRevision) selComp;
					String objType = rev.getType();
					if ("U8_FormulaRevision".equals(objType)) {// 选中配方对象
						isSelectedOk = true;
						ColdFormulatorControler coldFormulatorControler = new ColdFormulatorControler();
						coldFormulatorControler.userTask(rev);
						
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "冷饮配方搭建器",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			
			if ("YL.commands.milkPowderFormulatorCommand".equals(m_commandId)) {// 奶粉配方搭建器
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision rev = (TCComponentItemRevision) selComp;
					String objType = rev.getType();
					if ("U8_FormulaRevision".equals(objType)) {// 选中配方对象
						isSelectedOk = true;
						MilkPowderFormulatorControler milkPowderFormulatorControler = new MilkPowderFormulatorControler();
						milkPowderFormulatorControler.userTask(rev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "奶粉配方搭建器",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkKPIScoreCommand".equals(m_commandId)) {//液奶绩效打分
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentDataset) {
					TCComponentDataset dateset = (TCComponentDataset) selComp;
					isSelectedOk = true;
					YNKPIControler ynkpiControler = new YNKPIControler();
					ynkpiControler.userTask(dateset);
					
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "液奶打分",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkKPIScoreSumCommand".equals(m_commandId)) {//液奶绩效汇总
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = true;
				//不用点击对象
				YNKPIStatisticsframe frame = new YNKPIStatisticsframe();
				frame.setVisible(true);
				
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("所选对象类型错误", "液奶绩效汇总",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
		} catch (Exception ex) {
			String msg = failure;
			ex.printStackTrace();
			com.teamcenter.rac.util.MessageBox.post(msg, "", com.teamcenter.rac.util.MessageBox.INFORMATION);
		}
		
		
	}

}
