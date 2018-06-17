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
		String dlgTitle = "��������";
		String failure = "����";
		String currentUserUid = this.GetCurrentUserUid();
		String userName = this.getSession().getUserName();

		try {
			if ("YL.commands.sampleCommand".equals(m_commandId)) {// ��������
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


			if ("YL.commands.formulatorCommand".equals(m_commandId)) {// �䷽������
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�䷽������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.techStandardCommand".equals(m_commandId)) {// ������׼
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "������׼",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.lawImportCommand".equals(m_commandId)) {// ���浼��
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "���浼��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.projectStatisticsCommand".equals(m_commandId)) {// ��Ŀͳ�Ʊ���
				ProjectStatisticsControler projectStatisticsControler = new ProjectStatisticsControler();
				projectStatisticsControler.userTask(null);// ������ܲ���Ҫ���item
			}

			if ("YL.commands.formulatorLegalCheckCommand".equals(m_commandId)) {// �䷽�Ϲ���
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�䷽�Ϲ���",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.formulatorModifyCommand".equals(m_commandId)) {// ����䷽���
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�䷽�����������İ汾",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.projectMemorabiliaCommand".equals(m_commandId)) {// Һ�̴��¼�
				ProjectMemorabiliaControler projectMemorabiliaControler = new ProjectMemorabiliaControler(rectangleObj);
				projectMemorabiliaControler.userTask();
			}
			if ("YL.commands.techStandardModifyCommand".equals(m_commandId)) {// ������׼����
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "������׼����",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.dispatchDocumentToProjectCommand".equals(m_commandId)) {// ����ϵͳ
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "����ϵͳ",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}

			}

			if ("YL.commands.pdfConvertCommand".equals(m_commandId)) {// PDFת��
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "PDF�ϲ�",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}

			}

			if ("YL.commands.materialUseQueryCommand".equals(m_commandId)) {// ԭ��ʹ�������ѯ����
				String soaAddr = this.GetSoaServiceAddress();
				if (soaAddr == null || "".equals(soaAddr)) {
					String msg = "û��ָ��UDS��������ַ";
					com.teamcenter.rac.util.MessageBox.post(msg, "����", com.teamcenter.rac.util.MessageBox.ERROR);
					return;
				}

				RawQueryManager rawQuery = new RawQueryManager();
				rawQuery.m_wsdlAddr = soaAddr;
				rawQuery.m_session = m_session;
				String result = rawQuery.DoCreate(currentUserUid);
				if (result != null && !"".equals(result)) {
					com.teamcenter.rac.util.MessageBox.post(result, "ԭ��ʹ�ò�ѯ",
							com.teamcenter.rac.util.MessageBox.WARNING);
				}
			}

			if ("YL.commands.formulaCompareCommand".equals(m_commandId)) {// �䷽�Աȿ�������
				String soaAddr = this.GetSoaServiceAddress();
				if (soaAddr == null || "".equals(soaAddr)) {
					String msg = "û��ָ��UDS��������ַ";
					com.teamcenter.rac.util.MessageBox.post(msg, "����", com.teamcenter.rac.util.MessageBox.ERROR);
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", dlgTitle,
							com.teamcenter.rac.util.MessageBox.ERROR);
				} else if (uids.size() < 2) {
					com.teamcenter.rac.util.MessageBox.post("����Ҫѡ�������䷽����ԭ�϶���", dlgTitle,
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

			if ("YL.commands.labelGeneratorCommand".equals(m_commandId)) {// Һ̬��ǩ������
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��ǩ������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.labelGeneratorSolidCommand".equals(m_commandId)) {// ��̬̬��ǩ������
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��ǩ������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.produceFormulaExcelCommand".equals(m_commandId)) {// �����䷽��
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
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "�����䷽");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("����\n\n��ֵ����\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("��ʾ")){
										MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
									} else {
										MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
						};
						//TipsUI.ShowUI("���ڵ��������䷽��", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("���ڵ��������䷽��", thread, rectangleObj);
						progressBar.Start();
						
						
//						try {
//							GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, this.m_session.toString(), "�����䷽");
//							obj.AllExcelOperation();
//						} catch (Exception e) {
//							if(e.getMessage().startsWith("��ʾ")){
//								MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
//							}else{
//								MessageBox.post("����\n\n" + e.getMessage(), "��ʾ", MessageBox.INFORMATION);
//							}
//						}
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("��ѡ���䷽�汾���в���", "��ʾ", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.financeFormulaExcelCommand".equals(m_commandId)) {// �����䷽��
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
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "�����䷽");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("����\n\n��ֵ����\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("��ʾ")){
										MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
									} else {
										MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
							
						};
//						TipsUI.ShowUI("���ڵ��������䷽��", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("���ڵ��������䷽��", thread, rectangleObj);
						progressBar.Start();
						
						
//						try {
//							GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, this.m_session.toString(), "�����䷽");
//							obj.AllExcelOperation();
//						} catch (Exception e) {
//							if(e.getMessage().startsWith("��ʾ")){
//								MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
//							}else{
//								MessageBox.post("����\n\n" + e.getMessage(), "��ʾ", MessageBox.INFORMATION);
//							}
//						}
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("��ѡ���䷽�汾���в���", "��ʾ", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.orderFormulaExcelCommand".equals(m_commandId)) {// �����䷽��
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
									GetDataAndExportExcel obj = new GetDataAndExportExcel(itemRev, sessionStr, "��Ʒ����");
									obj.AllExcelOperation();
								} catch (Exception e) {
									if (e.getMessage() == null) {
										MessageBox.post("����\n\n��ֵ����\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									} else if (e.getMessage().startsWith("��ʾ")){
										MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
									} else {
										MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
									}
									
									e.printStackTrace();
								}
							}
							
						};
						//TipsUI.ShowUI("���ڵ�����Ʒ������", thread, rectangleObj);
						ProgressBar progressBar = new ProgressBar("���ڵ�����Ʒ������", thread, rectangleObj);
						progressBar.Start();
					}
				}
				if (!isSelectedOk) {
					MessageBox.post("��ѡ���䷽�汾���в���", "��ʾ", MessageBox.INFORMATION);
				}
			}
			if ("YL.commands.materialtechStandardCommand".equals(m_commandId)) {// ԭ�ϼ�����׼
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					final TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						
						if ("ԭ�ϼ�����׼".equals(indexType)) {
							final String sessionStr = this.m_session.toString();
							Thread thread = new Thread()
							{
								@Override
								public void run() {
									try {
										ExportMaterialStandardWord obj = new ExportMaterialStandardWord(itemRev, sessionStr, "ԭ�ϼ�����׼");
										obj.initGetDataAndExportWord();
										obj.AllWordOperation();
									} catch (Exception e) {
										if (e.getMessage() == null) {
											MessageBox.post("����\n\n��ֵ����\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
										} else if (e.getMessage().startsWith("��ʾ")) {
											MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
										} else {
											MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
										}										
										e.printStackTrace();
									}
								}								
							};
							ProgressBar progressBar = new ProgressBar("���ڵ���ԭ�ϼ�����׼��", thread, rectangleObj);
							progressBar.Start();
						}else{
							MessageBox.post("��ѡ��ԭ�ϼ�����׼�汾���в���", "��ʾ", MessageBox.INFORMATION);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ��ԭ�ϼ�����׼�汾���в���", "��ʾ", MessageBox.INFORMATION);
				}
			}

			if ("YL.commands.accessoriestechStandardCommand".equals(m_commandId)) {// ���ϼ�����׼
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
						if ("���ϼ�����׼".equals(indexType)) {
							AccessoriesTechStandardExcelControler accessoriestechStandardControler = new AccessoriesTechStandardExcelControler();
							accessoriestechStandardControler.userTask(itemRev);
						} else {
							com.teamcenter.rac.util.MessageBox.post("��ѡ�������Ͳ��Ǹ��ϼ�����׼", "���ϼ�����׼",
									com.teamcenter.rac.util.MessageBox.ERROR);
						}

					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "���ϼ�����׼",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			// Jr ��Ʒ������׼����
			if ("YL.commands.endProtechStandardCommand".equals(m_commandId)) {// ��Ʒ������׼����
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
						
						if ("��Ʒ������׼".equals(indexType)) {
							final String sessionStr = this.m_session.toString();
							Thread thread = new Thread()
							{
								@Override
								public void run() {
									try {
										ExportProductStandardWord obj = new ExportProductStandardWord(itemRev, sessionStr, "��Ʒ������׼");
										obj.initGetDataAndExportWord();
										obj.AllWordOperation();
									} catch (Exception e) {
										if (e.getMessage() == null) {
											MessageBox.post("����\n\n��ֵ����\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
										} else if (e.getMessage().startsWith("��ʾ")) {
											MessageBox.post(e.getMessage(), "��ʾ", MessageBox.INFORMATION);
										} else {
											MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
										}										
										e.printStackTrace();
									}
								}								
							};
							ProgressBar progressBar = new ProgressBar("���ڵ���ԭ�ϼ�����׼��", thread, rectangleObj);
							progressBar.Start();
						}else{
							MessageBox.post("��ѡ���Ʒ������׼�汾���в���", "��ʾ", MessageBox.INFORMATION);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ���Ʒ������׼�汾���в���", "��ʾ", MessageBox.INFORMATION);
				}
			}
			
			// ԭ���Ʒ������׼����
//			if ("YL.commands.endProtechStandardCommand".equals(m_commandId)) {// ��Ʒ������׼����
//				System.out.println(this.m_session);
//				InterfaceAIFComponent selComp = this.GetSelectedComponent();
//				boolean isSelectedOk = false;
//				if (selComp != null && selComp instanceof TCComponentItemRevision) {
//					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
//					String objType = itemRev.getType();
//					String indexType = itemRev.getProperty("u8_techstandardtype");
//					if ("U8_IndexRevision".equals(objType)) {
//						isSelectedOk = true;
//						if ("��Ʒ������׼".equals(indexType)) {
//							ProductTechStandardExcelControler endProtechStandardControler = new ProductTechStandardExcelControler();
//							endProtechStandardControler.userTask(itemRev);
//						} else {
//							com.teamcenter.rac.util.MessageBox.post("��ѡ�������Ͳ��ǲ�Ʒ������׼����", "��Ʒ������׼����",
//									com.teamcenter.rac.util.MessageBox.ERROR);
//						}
//					}
//				}
//				if (!isSelectedOk) {
//					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��Ʒ������׼����",
//							com.teamcenter.rac.util.MessageBox.ERROR);
//				}
//			}
			
			

			if ("YL.commands.halfProtechStandardCommand".equals(m_commandId)) {// ���Ʒ������׼
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRev = (TCComponentItemRevision) selComp;
					String objType = itemRev.getType();
					String indexType = itemRev.getProperty("u8_techstandardtype");
					if ("U8_IndexRevision".equals(objType)) {
						isSelectedOk = true;
						if ("���Ʒ������׼".equals(indexType)) {
							SimiFinishProTechStandardExcelControler halfProtechStandardControler = new SimiFinishProTechStandardExcelControler();
							halfProtechStandardControler.userTask(itemRev);
						} else {
							com.teamcenter.rac.util.MessageBox.post("��ѡ�������Ͳ��ǰ��Ʒ������׼", "���Ʒ������׼",
									com.teamcenter.rac.util.MessageBox.ERROR);
						}
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "���Ʒ������׼",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkFinManageExcelCommand".equals(m_commandId)) {// Һ�̼�Ч����ͳ�Ʊ���
//				MilkFinManageExcelControler milkFinManageExcelControler = new MilkFinManageExcelControler();
//				milkFinManageExcelControler.userTask(null);// ������ܲ���Ҫ���item
			}


			if ("YL.commands.coldDrinkFormulaExcelCommand".equals(m_commandId)) {// ���������䷽��
																					// word��ʽ
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
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�����䷽��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.firstTrailJugeCommand".equals(m_commandId)) {// ��������ж�
				// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailSuccessOrNotControler firstTrailSuccessOrNotControler = new FirstTrailSuccessOrNotControler();
				firstTrailSuccessOrNotControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�������ͨ��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.secondTrailJugeCommand".equals(m_commandId)) {// ��������ж�
				// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				SecondTrailSuccessOrNotControler secondTrailSuccessOrNotControler = new SecondTrailSuccessOrNotControler();
				secondTrailSuccessOrNotControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�������ͨ��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.firstTrailSuccessCommand".equals(m_commandId)) {// �������ͨ��
																				// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailSuccessControler firstTrailSuccessControler = new FirstTrailSuccessControler();
				firstTrailSuccessControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�������ͨ��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.firstTrailFailCommand".equals(m_commandId)) {// �������ͨ��
																			// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent[] getSelectedComponents = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				FirstTrailFailControler firstTrailFailControler = new FirstTrailFailControler();
				firstTrailFailControler.userTask(getSelectedComponents);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�������ͨ��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.creativeScoreOneCommand".equals(m_commandId)) {// ��������һ����
																			// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentForm) {
					TCComponentForm form = (TCComponentForm) selComp;
					String objType = form.getType();
					if ("U8_InformReport".equals(objType)) {// �����᰸������
						isSelectedOk = true;
						OneScoreFormControler oneScoreFormControler = new OneScoreFormControler();
						oneScoreFormControler.userTask(form, m_session);
					}
				}

				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}

			if ("YL.commands.creativeScoreFourCommand".equals(m_commandId)) {// ���������ĸ�����
																				// ��ѡ
				System.out.println(this.m_session);
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentForm) {
					TCComponentForm form = (TCComponentForm) selComp;
					String objType = form.getType();
					if ("U8_InformReport".equals(objType)) {// �����᰸������
						isSelectedOk = true;
						FourScoreFormConstroler fourScoreFormConstroler = new FourScoreFormConstroler();
						fourScoreFormConstroler.userTask(form, m_session);
					}
				}

				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeSumCommand".equals(m_commandId)) {// ��������
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeScoreSumControler creativeScoreSumControler = new CreativeScoreSumControler();
				creativeScoreSumControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeJugeCommand".equals(m_commandId)) {// �����ж�
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeJugeControler creativeJugeControler = new CreativeJugeControler();
				creativeJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeNoJugeCommand".equals(m_commandId)) {// �ǵ����ж�
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeNoJugeControler creativeNoJugeControler = new CreativeNoJugeControler();
				creativeNoJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.creativeSecondJugeCommand".equals(m_commandId)) {// ���ε����ж�
				System.out.println(this.m_session);
				InterfaceAIFComponent[] selComps = this.GetSelectedComponents();
				boolean isSelectedOk = true;
				CreativeSecondJugeControler creativeSecondJugeControler = new CreativeSecondJugeControler();
				creativeSecondJugeControler.userTask(selComps);
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "��������",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			
			if ("YL.commands.coldFormulatorCommand".equals(m_commandId)) {// �����䷽���
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision rev = (TCComponentItemRevision) selComp;
					String objType = rev.getType();
					if ("U8_FormulaRevision".equals(objType)) {// ѡ���䷽����
						isSelectedOk = true;
						ColdFormulatorControler coldFormulatorControler = new ColdFormulatorControler();
						coldFormulatorControler.userTask(rev);
						
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�����䷽���",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			
			if ("YL.commands.milkPowderFormulatorCommand".equals(m_commandId)) {// �̷��䷽���
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentItemRevision) {
					TCComponentItemRevision rev = (TCComponentItemRevision) selComp;
					String objType = rev.getType();
					if ("U8_FormulaRevision".equals(objType)) {// ѡ���䷽����
						isSelectedOk = true;
						MilkPowderFormulatorControler milkPowderFormulatorControler = new MilkPowderFormulatorControler();
						milkPowderFormulatorControler.userTask(rev);
					}
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "�̷��䷽���",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkKPIScoreCommand".equals(m_commandId)) {//Һ�̼�Ч���
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = false;
				if (selComp != null && selComp instanceof TCComponentDataset) {
					TCComponentDataset dateset = (TCComponentDataset) selComp;
					isSelectedOk = true;
					YNKPIControler ynkpiControler = new YNKPIControler();
					ynkpiControler.userTask(dateset);
					
				}
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "Һ�̴��",
							com.teamcenter.rac.util.MessageBox.ERROR);
				}
			}
			if ("YL.commands.milkKPIScoreSumCommand".equals(m_commandId)) {//Һ�̼�Ч����
				InterfaceAIFComponent selComp = this.GetSelectedComponent();
				boolean isSelectedOk = true;
				//���õ������
				YNKPIStatisticsframe frame = new YNKPIStatisticsframe();
				frame.setVisible(true);
				
				if (!isSelectedOk) {
					com.teamcenter.rac.util.MessageBox.post("��ѡ�������ʹ���", "Һ�̼�Ч����",
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
