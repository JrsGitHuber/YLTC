package com.uds.yl.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.eclipse.swt.widgets.Tree;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.controler.FormulatorLegalCheckControler_T.FormulaCheckJframe;
import com.uds.yl.herb.ProgressDlg;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.IFormulatorService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.service.impl.FormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.ui.FindIndexLawFrame;
import com.uds.yl.ui.ProgressBarDialog;
import com.uds.yl.utils.DoubleUtil;
import com.uds.yl.utils.StringsUtil;

//�䷽���
public class FormulatorControler implements BaseControler {

	private static FormulaBomCreate frame;
	
	private Double SUM;// Ͷ�����ܺ�

	private IFormulatorService iFormulatorService = new FormulatorServiceImpl();
	private List<MaterialBean> materialTableList = new ArrayList<>();// ��ʾ������ԭ�ϱ���Ԫ�صĽṹ
	private List<MaterialBean> formulatorTableList = new ArrayList<>();// ��ʾ���䷽ԭ�ϱ���Ԫ�صĽṹ
	private List<TCComponentItemRevision> materialTableItemRevList = new ArrayList<>();// ����ԭ�ϰ��еĵİ汾����
	private List<TCComponentItemRevision> formulatorTabelItemRevList = new ArrayList<>();// �䷽ԭ�ϱ��еİ汾����
	
	
	//����Ϲ��Լ��
	private TCComponentItemRevision lawRevision;//�������Ϲ��Լ��ķ���
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();

	private List<TCComponentItemRevision> checkLawRevList = new ArrayList<TCComponentItemRevision>();//��Ϊ�洢�ȴ����ķ�������
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//�ȴ�������Ӽ�Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//�ȴ�����ָ��Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//�ȴ�������Ӽ�Bom��Ӧ��pojo
	private List<IndexItemBean> waitIndexBeanList = null;//�ȴ�����ָ��Bom��Ӧ��pojo
	
	private List<MaterialBean> checkMaterialBeanList = null;//��Ӽ�����Bom��Ӧ��pojo
	private List<IndexItemBean> checkIndexBeanList = null;//ָ�귨��Bom��Ӧ��pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//������Ҫд��excel������
	
	private ProgressBarDialog progressBarDialog;//������
	
	@Override
	public void userTask(final TCComponentItemRevision itemRev) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if(frame==null || !frame.isVisible()){
						frame = new FormulaBomCreate(itemRev);
					}
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	class FormulaBomCreate extends JFrame {

		private JPanel contentPane;
		private JTextField formulaEdtName;
		private JTextField formulaEdtRevision;
		private JTextField searchEdt_1;
		private JTextField searchEdt_2;
		private JTextField searchEdt_3;
		private JTable materialTable;
		private JTable formulaTable;
		private JTextField sumEdt1;//Ͷ�����ϼ�
		private JTextField sumEdt2;//��Ⱥϼ� �ܹ���100
		private JTextField sumEdt3;//���������۳ɱ�
		private JTextField supplementEdt;
		private JButton cancleBtn;
		private JButton nutritionBtn;//Ӫ���ɷֵ���Ϣ
		private JButton createBtn;
		private JButton supplementBtn;
		private JButton deleteBtn;
		private JButton addBtn;
		private JButton searchBtn;
		private JButton clearBtn;
		private JButton lawCheckBtn;//����Ա�
		private JTextField lawTextField;//��ʾ���������
		private JButton searchLawBtn;//��������
		
		private TCComponentItemRevision mItemRevision;

		/**
		 * Create the frame.
		 */
		public FormulaBomCreate(final TCComponentItemRevision itemRev) {
		
			{
				this.mItemRevision = itemRev;
				setBounds(100, 100, 915, 636);
				contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				setContentPane(contentPane);
				contentPane.setLayout(null);

				JLabel label = new JLabel("�䷽������");
				label.setBounds(10, 0, 85, 25);
				contentPane.add(label);

				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel.setBounds(10, 25, 879, 563);
				contentPane.add(panel);
				panel.setLayout(null);

				JLabel lblNewLabel = new JLabel("�䷽��");
				lblNewLabel.setBounds(19, 10, 48, 23);
				panel.add(lblNewLabel);

				formulaEdtName = new JTextField();
				formulaEdtName.setBounds(87, 10, 88, 23);
				panel.add(formulaEdtName);
				formulaEdtName.setColumns(10);
				formulaEdtName.setEditable(false);

				JLabel lblNewLabel_1 = new JLabel("�汾��");
				lblNewLabel_1.setBounds(212, 10, 48, 23);
				panel.add(lblNewLabel_1);

				formulaEdtRevision = new JTextField();
				formulaEdtRevision.setBounds(284, 10, 88, 23);
				panel.add(formulaEdtRevision);
				formulaEdtRevision.setColumns(10);
				formulaEdtRevision.setEditable(false);

				JLabel label_1 = new JLabel("ԭ������");
				label_1.setBounds(19, 53, 58, 23);
				panel.add(label_1);

				searchEdt_1 = new JTextField();
				searchEdt_1.setColumns(10);
				searchEdt_1.setBounds(87, 53, 88, 23);
				panel.add(searchEdt_1);

				JLabel label_2 = new JLabel("���Ӵ��룺");
				label_2.setBounds(212, 53, 72, 23);
				panel.add(label_2);

				searchEdt_2 = new JTextField();
				searchEdt_2.setColumns(10);
				searchEdt_2.setBounds(284, 53, 88, 23);
				panel.add(searchEdt_2);

				JLabel label_3 = new JLabel("��Ӧ�̣�");
				label_3.setBounds(409, 53, 66, 23);
				panel.add(label_3);

				searchEdt_3 = new JTextField();
				searchEdt_3.setColumns(10);
				searchEdt_3.setBounds(495, 53, 88, 23);
				panel.add(searchEdt_3);
				searchEdt_3.setEditable(false);

				searchBtn = new JButton("����");
				searchBtn.setBounds(600, 53, 88, 23);
				panel.add(searchBtn);

				JLabel lblNewLabel_2 = new JLabel("��ѡԭ�ϱ�");
				lblNewLabel_2.setBounds(19, 101, 97, 23);
				panel.add(lblNewLabel_2);

				addBtn = new JButton("���");
				addBtn.setBounds(694, 234, 88, 23);
				panel.add(addBtn);

				JLabel lblNewLabel_3 = new JLabel("�䷽��");
				lblNewLabel_3.setBounds(13, 267, 54, 15);
				panel.add(lblNewLabel_3);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(42, 132, 586, 125);
				panel.add(scrollPane);

				materialTable = new JTable() {
					public boolean isCellEditable(int rowIndex, int ColIndex) {
						return false;
					}
				};
				materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				materialTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "ԭ����", "���Ӵ���", "��Ӧ��", "��λ�ɱ�(Ԫ/ǧ��)", "��׼��λ" }));
				scrollPane.setViewportView(materialTable);
				materialTable.setBorder(new LineBorder(new Color(0, 0, 0)));

				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel_1.setBounds(23, 292, 846, 211);
				panel.add(panel_1);
				panel_1.setLayout(null);

				JScrollPane scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(10, 10, 621, 149);
				panel_1.add(scrollPane_1);

				formulaTable = new JTable(){
					public boolean isCellEditable(int rowIndex, int ColIndex) {
						
						if(ColIndex==2){
							return true;
						}
						return false;
					}
				};
				formulaTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				formulaTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "ԭ����", "���Ӵ���", "Ͷ����(ǧ��)", "���(%)", "������/���۳ɱ�" }));
				formulaTable.getColumnModel().getColumn(2).setPreferredWidth(103);
				formulaTable.getColumnModel().getColumn(4).setPreferredWidth(136);
				scrollPane_1.setViewportView(formulaTable);

				deleteBtn = new JButton("ɾ��");
				deleteBtn.setBounds(730, 163, 88, 23);
				panel_1.add(deleteBtn);

				JLabel lblNewLabel_4 = new JLabel("�ϼƣ�");
				lblNewLabel_4.setBounds(172, 178, 54, 23);
				panel_1.add(lblNewLabel_4);

				sumEdt1 = new JTextField();
				sumEdt1.setBounds(217, 179, 98, 21);
				panel_1.add(sumEdt1);
				sumEdt1.setColumns(10);

				sumEdt2 = new JTextField();
				sumEdt2.setBounds(314, 179, 106, 21);
				panel_1.add(sumEdt2);
				sumEdt2.setColumns(10);

				sumEdt3 = new JTextField();
				sumEdt3.setBounds(417, 179, 88, 21);
				panel_1.add(sumEdt3);
				sumEdt3.setColumns(10);

				JLabel lblNewLabel_5 = new JLabel("Ԫ/��");
				lblNewLabel_5.setBounds(527, 179, 54, 21);
				panel_1.add(lblNewLabel_5);

				JLabel lblNewLabel_6 = new JLabel("����:");
				lblNewLabel_6.setBounds(641, 72, 54, 23);
				panel_1.add(lblNewLabel_6);

				supplementEdt = new JTextField();
				supplementEdt.setBounds(728, 72, 90, 23);
				panel_1.add(supplementEdt);
				supplementEdt.setColumns(10);

				supplementBtn = new JButton("����");
				supplementBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				supplementBtn.setBounds(730, 120, 88, 23);
				panel_1.add(supplementBtn);

				createBtn = new JButton("����");
				createBtn.setBounds(151, 530, 88, 23);
				panel.add(createBtn);

				nutritionBtn = new JButton("Ӫ���ɷ�");
				nutritionBtn.setBounds(528, 530, 100, 23);
				panel.add(nutritionBtn);

				cancleBtn = new JButton("ȡ��");
				cancleBtn.setBounds(703, 530, 79, 23);
				panel.add(cancleBtn);
				
				clearBtn = new JButton("���");
				clearBtn.setBounds(694, 201, 88, 23);
				panel.add(clearBtn);
				
				lawCheckBtn = new JButton("����Ա�");
				lawCheckBtn.setBounds(340, 530, 100, 23);
				panel.add(lawCheckBtn);
				
				JLabel label_4 = new JLabel("����:");
				label_4.setBounds(409, 86, 66, 23);
				panel.add(label_4);
				label_4.setVisible(false);
				
				lawTextField = new JTextField();
				lawTextField.setColumns(10);
				lawTextField.setBounds(495, 87, 88, 23);
				lawTextField.setEditable(false);
				panel.add(lawTextField);
				lawTextField.setVisible(false);
				
				searchLawBtn = new JButton("��������");
				searchLawBtn.setBounds(600, 86, 140, 23);
				panel.add(searchLawBtn);
				searchLawBtn.setVisible(false);
			}
			

			// ���������س�������ʧ
			setEditTextFocuse();
			 
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();

					// �䷽���ֺͰ汾��EditText��ʼ��
					try {
						String formulatorName = itemRev.getProperty("object_name");
						String formulatorRevision = itemRev.getProperty("item_revision_id");
						formulaEdtName.setText(formulatorName);
						formulaEdtRevision.setText(formulatorRevision);
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					
				
					
					

					// �����BOM��ͼ�Ļ��䷽�������Table���г�ʼֵ��
					List<MaterialBean> initBean = iFormulatorService.getInitBean(itemRev);
					List<TCComponentItemRevision> initItemRevLst = iFormulatorService.getInitMaterialItemRevList(itemRev);
					formulatorTabelItemRevList.addAll(initItemRevLst);

					DefaultTableModel formulaModel = (DefaultTableModel) formulaTable.getModel();
					for (MaterialBean materialBean : initBean) {
						formulatorTableList.add(materialBean);// ���ܵ��䷽�����
						formulaModel.addRow(new String[] { materialBean.objectName, materialBean.code,
								materialBean.U8_inventory, materialBean.bl_quantity, materialBean.price });
					}
					if (initBean.size() > 0) {
						formulaTable.setRowSelectionInterval(0, 0);// Ĭ��ѡ�е�һ��
					}

					initQuality();// ����Ͷ�����仯�ı����
					
					progressBarDialog.stop();
					
				}
			}).start();
			

			
			
			// ����ԭ��
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							// ������������ :ԭ�����ơ����Ӵ��롢��Ӧ��
							String materialName = "*"+searchEdt_1.getText().toString()+"*";
							String materialCode = searchEdt_2.getText().toString();
							String materialSupplier = searchEdt_3.getText().toString();

							
							// ����ԭ����
							List<TCComponentItemRevision> searchItemRevisionList = iFormulatorService.getSearchItemRev(materialName,
									materialCode, materialSupplier);
							
							List<MaterialBean> searchBeans = iFormulatorService.getSearchBean(searchItemRevisionList);
							if (searchBeans == null) {
								return;
							}
							// ����������ŵ�Table�У�����Ĭ��ѡ�����������һ��
							DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
							for (int i=0;i<searchBeans.size();i++) {
								MaterialBean materialBean = searchBeans.get(i);
								TCComponentItemRevision itemRevision = searchItemRevisionList.get(i);
								if (!isInMaterialTable(materialBean)) {
									model.addRow(new String[] { materialBean.objectName, materialBean.code,
											materialBean.suppplier, materialBean.price, materialBean.u8Uom });
									materialTableList.add(materialBean);// ���ܵ�ԭ�ϱ����
									materialTableItemRevList.add(itemRevision);
								}
							}
							if (searchBeans.size() > 0) {// ��ֵ����Ĭ��ѡ�е�һ��
								materialTable.setRowSelectionInterval(0, 0);
							}
							
							progressBarDialog.stop();
							
						}
					}).start();
					

				}
			});

			// ���ԭ��
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ��ȡԭ�ϱ��е�ѡ����
					int selectRowIndex = materialTable.getSelectedRow();
					if (selectRowIndex == -1) {
						MessageBox.post("��ѡ��", "", MessageBox.ERROR);
						return;
					}
					// �ж�ѡ�����Ƿ��Ѿ����䷽����
					MaterialBean selectMaterialBean = materialTableList.get(selectRowIndex);
					TCComponentItemRevision selectItemRev = materialTableItemRevList.get(selectRowIndex);

					boolean inFormulatorTable = isInFormulatorTable(selectMaterialBean);
					if (inFormulatorTable) {
						MessageBox.post("ԭ�����Ѿ������䷽��", "", MessageBox.INFORMATION);
						return;
					}
					// ��ԭ�ϱ���ѡ�е�����Ϣ��ӵ��䷽����
					DefaultTableModel defaultTableModel = (DefaultTableModel) formulaTable.getModel();
					defaultTableModel.addRow(new String[] { selectMaterialBean.objectName, selectMaterialBean.code,
							selectMaterialBean.U8_inventory, selectMaterialBean.bl_quantity,
							selectMaterialBean.price });
					formulatorTableList.add(selectMaterialBean);
					formulatorTabelItemRevList.add(selectItemRev);
					// ��̬�����䷽���е����
					initQuality();// ����Ͷ�����仯�ı����
				}
			});

			// ɾ��
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					// ɾ���䷽����ѡ�е���
					int selectedRowIndex = formulaTable.getSelectedRow();
					if (selectedRowIndex == -1) {
						MessageBox.post("��ѡ��", "", MessageBox.ERROR);
						return;
					}
					MaterialBean materialBean = formulatorTableList.get(selectedRowIndex);
					TCComponentItemRevision selectItemRev = formulatorTabelItemRevList.get(selectedRowIndex);
					// Ҫɾ���Ļ���Ҫɾ����ԭ�ϱ���е�
					DefaultTableModel defaultTableModel = (DefaultTableModel) formulaTable.getModel();
					defaultTableModel.removeRow(selectedRowIndex);// ɾ���䷽���е�
					formulatorTableList.remove(selectedRowIndex);// ɾ���䷽��List�е�����
					formulatorTabelItemRevList.remove(selectedRowIndex);
					// ��̬�����䷽���е�����Լ��ϼ�
					initQuality();// ����Ͷ�����仯�ı����
				}
			});

			// �䷽���޸ĵļ����¼�
			formulaTable.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent propertychangeevent) {

					// ��̬�����䷽���е�����Լ��ϼ�
					initQuality();// ����Ͷ�����仯�ı����
					//
					for (int i = 0; i < formulatorTableList.size(); i++) {
						MaterialBean materialBean = formulatorTableList.get(i);
						formulaTable.setValueAt(materialBean.objectName, i, 0);
						formulaTable.setValueAt(materialBean.code, i, 1);
						formulaTable.setValueAt(materialBean.U8_inventory, i, 2);
						formulaTable.setValueAt(materialBean.bl_quantity, i, 3);
						formulaTable.setValueAt(materialBean.price, i, 4);
					}

				}
			});

			// ����BOM��ͼ
			createBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							iFormulatorService.createFormulatorBOM(itemRev, formulatorTabelItemRevList, formulatorTableList);
							
							//Ͷ���������������۳ɱ���д���䷽�汾����u8_dosagebase2��u8_price д�ص��汾
							try {
								String sumInventoryStr = sumEdt1.getText().toString();
								itemRev.setProperty("u8_dosagebase2", sumInventoryStr);
								
								String sumContentStr = sumEdt3.getText().toString();
								itemRev.setProperty("u8_price", sumContentStr);
							} catch (TCException e) {
								e.printStackTrace();
							}
							
							progressBarDialog.stop();
							MessageBox.post("OK", "", MessageBox.INFORMATION);
							
						}
					}).start();
				}
			});

			// ����Ӫ���ɷֱ���
			nutritionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							// ���ɱ� 
							TCComponentBOMLine cacheTopBomLine = iFormulatorService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
							List<TCComponentBOMLine> materialBomList = iFormulatorService.getMaterialBomList(cacheTopBomLine);
							List<IndexItemBean> indexBeanList = iFormulatorService.getIndexBeanList(cacheTopBomLine);

							iFormulatorService.write2Excel(materialBomList, indexBeanList);
							progressBarDialog.stop();
						}
					}).start();
				}
			});

			// ȡ��
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
					if(progressBarDialog.isLive()){
						progressBarDialog.stop();
					}
				}
			});

			// ����
			supplementBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					Double supplement = Utils.convertStr2Double(supplementEdt.getText());
					int index = formulaTable.getSelectedRow();
					Double inventory = Utils.convertStr2Double(formulaTable.getValueAt(index, 2).toString());
					if (supplement == 0)
						supplement = 1000d;
					if (supplement < 0||(supplement - (SUM - inventory))<0){
						MessageBox.post("��������", "", MessageBox.ERROR);
						return;
					}
					// ������Ҫ������
					inventory = supplement - (SUM - inventory);
					formulaTable.setValueAt(inventory + "", index, 2);
					sumEdt1.setText(supplement + "");
					MaterialBean materialBean = formulatorTableList.get(index);
					materialBean.U8_inventory = Const.CommonCosnt.doubleFormat.format(inventory) + "";
					initQuality();
				}
			});

			//���
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
					for(int i=0;i<materialTableList.size();i++){
						model.removeRow(0);
					}
					materialTableList.clear();
					materialTableItemRevList.clear();
				}
			});
			
			//����Ա�
			lawCheckBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
//							if(lawRevision==null){//�������ûѡ��Ļ�����ʾ
//								MessageBox.post("��ѡ���䷽��Ӧ�Ĳ�Ʒ����","",MessageBox.INFORMATION);
//								progressBarDialog.stop();
//								return;
//							}
							
							//��������Ϊ��ʱ���䷽�����˵  ��Ҫ���ݲ�ͬ�Ľṹ ������ԭ��Ͷ����д��ȥ
							TCComponentBOMLine cacheTopBomLine = iFormulatorService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
							
							checkLawRevList = getRelatedLawItemList();
							
							
							//�����䷽��ȡԭ�ϵ�BOM
							waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
							//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
							waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//�����䷽��ȡָ���BOM
//							waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
							//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
							waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
							
							//���������յ�ָ���������Ƶ�ֵ
							Double sum =getSumInventory();
							for(int i=0;i<waitIndexBeanList.size();i++){
								IndexItemBean indexBean = waitIndexBeanList.get(i);
								Double up  =  StringsUtil.convertStr2Double(indexBean.up) /sum;
								Double down  =  StringsUtil.convertStr2Double(indexBean.down) /sum;
								
								indexBean.up = up+"";
								indexBean.down = down+"";
							}
							
							
							
							//��ȡ�����е���Ӽ�Bean   Ҫ������Ӽ���������ѡ�еķ��� 
							checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
							//��ȡ�����е�ָ��Bean�Ǵӷ���������˵  ��������ѡ�еĲ�Ʒ��׼
							checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
							
							
							//�����Ӽ�  	��Ӽ��ķ���Ƚ����� ר������һ������  ����
							List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
							//���ָ��
							List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
							
							//д��excel��
							allCheckedBeanList.clear();//�����
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
							
							progressBarDialog.stop();
							
							//�ر�bom
							TCComponentBOMWindow bomWindow = cacheTopBomLine.getCachedWindow();
							try {
								bomWindow.refresh();
								bomWindow.save();
								bomWindow.close();
								cacheTopBomLine.refresh();
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					
					
				}
			});
			
			
			//��������
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					FindIndexLawFrame findIndexLawFrame = new FindIndexLawFrame(new AbstractCallBack() {
						@Override
						public void setLawAndName(String lawName, TCComponentItemRevision lawRev) {
							super.setLawAndName(lawName, lawRev);
							lawRevision = lawRev;
							lawTextField.setText(lawName);
							
//							checkLawRevList.add(lawRevision);
						}
					});
					findIndexLawFrame.setVisible(true);
				}
			});
		}

		
		/**
		 * �������ݻس�ʧȥ����
		 */
		private void setEditTextFocuse() {

			searchEdt_1.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // ���س���ִ����Ӧ����;
					{
						searchEdt_1.setFocusable(false);
						searchEdt_1.setFocusable(true);
					}
				}
			});
			searchEdt_2.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // ���س���ִ����Ӧ����;
					{
						searchEdt_2.setFocusable(false);
						searchEdt_2.setFocusable(true);
					}
				}
			});
			searchEdt_3.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) // ���س���ִ����Ӧ����;
					{
						searchEdt_3.setFocusable(false);
						searchEdt_3.setFocusable(true);
					}
				}
			});

		}

		/**
		 * ��������ԭ����ʱ�������ԭ���ϱ����
		 * 
		 * @return
		 */
		public boolean isInMaterialTable(MaterialBean materialBean) {
			if (materialTableList.size() == 0)
				return false;

			for (int i = 0; i < materialTableList.size(); i++) {
				if (materialBean.objectName.equals(materialTableList.get(i).objectName)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * ѡ��Ҫ��ӵ�ԭ�����Ƿ����䷽�����
		 * 
		 * @param materialBean
		 * @return
		 */
		public boolean isInFormulatorTable(MaterialBean materialBean) {
			if (formulatorTableList.size() == 0)
				return false;

			for (int i = 0; i < formulatorTableList.size(); i++) {
				if (materialBean.itemID.equals(formulatorTableList.get(i).itemID)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * ��̬�����䷽���е�����Լ��ϼ�
		 */
		public void initQuality() {
			DefaultTableModel model = (DefaultTableModel) formulaTable.getModel();
			SUM = 0d;// Ͷ�����ܺ�
			Double sumMoney = 0d;// ��Ǯ�ܺ�
			for (int i = 0; i < formulatorTableList.size(); i++) {
				MaterialBean materialBean = formulatorTableList.get(i);
				
				// �ж��Ƿ�ʹ��
				String isbacteria = materialBean.getIsbacteria();
				if(isbacteria != null && "��".equals(isbacteria)){
					continue;
				}
				
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}

				// ����Ǿ���Ļ��ܵ�Ͷ��������Ͳ���������
				// if(!materialBean.price.contains("kg")&&!materialBean.price.contains("g")&&!materialBean.price.contains("ml")){
				// //��ʾ��Ͷ����Ϊ0
				// inventory = 0d;
				// }

				SUM += inventory;// �ۼ����
			}

			for (int i = 0; i < formulatorTableList.size(); i++) {
				MaterialBean materialBean = formulatorTableList.get(i);
				
				
				// =======Ͷ�����ͰٷֱȵĶ�̬�仯
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}

				materialBean.U8_inventory = inventory + "";// �ö�̬�ı��Ͷ������ʱ�������䷽�������ݽṹlist��ȥ

				if (SUM == 0) {
					materialBean.bl_quantity = "0";
					materialBean.bl_quantity = DoubleUtil.formatNumber(materialBean.bl_quantity);
				} else {
					materialBean.bl_quantity = inventory / SUM * 100 + "";
					materialBean.bl_quantity = DoubleUtil.formatNumber(materialBean.bl_quantity);
				}

				formulaTable.setValueAt(materialBean.bl_quantity, i, 3);
				// ==============��λ�۸�̬�仯
				String strPrice = (String) model.getValueAt(i, 4);
				Double price = 0d;
				try {
					price = Double.valueOf(strPrice);
				} catch (Exception e) {
					price = 0d;
				}
				materialBean.price = price + "";

				// ----�ܼ۸�----
				sumMoney += price * inventory;
			}
			//�ܼ۸�Ҫ����ÿ�ּ���
			sumMoney = sumMoney/1000;

			// ���ºϼ��������ӵ�ֵ
			// sumEdt1 Ͷ�����ϼ�
			// sumEdt2 ��Ⱥϼ�
			// sumEdt3 ��Ǯ�ϼ�
			sumEdt1.setText(DoubleUtil.formatNumber(SUM) + "");
			sumEdt2.setText("100");
			sumEdt3.setText(DoubleUtil.formatNumber(sumMoney) + "");

		}
	
		
		/**
		 * ����ѡ�е��䷽�汾��ȡ����������ļ��У���ط��棩
		 * �������Ƿ���İ汾
		 * Ҫ���ص��Ƿ���İ汾
		 * 
		 */
		private List<TCComponentItemRevision> getRelatedLawItemList() {
			List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();

			Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
			try {
				// ��ʼ������
				TCComponent[] relatedComponents = mItemRevision
						.getRelatedComponents("U8_LawRel");
				for (TCComponent component : relatedComponents) {
					if (component instanceof TCComponentItemRevision) {
						TCComponentItemRevision lawRev = (TCComponentItemRevision) component;
						queue.offer(lawRev);// ��ֱ�ӹ����ķ�����ӵ�������

					} else {
						continue;
					}
				}

				// �ݹ��������
				while (!queue.isEmpty()) {
					TCComponentItemRevision lawRevsion = queue.poll();
					revList.add(lawRevsion);// ���еķ��涼��������list��

					TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(
							lawRevsion, "��ͼ");
					if (topBomLine == null) {
						topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
					}
					AIFComponentContext[] bomChilds = topBomLine.getChildren();
					for (int i = 0; i < bomChilds.length; i++) {
						TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i]
								.getComponent();

						String indicatorRequire = bomChild
								.getProperty("U8_indexrequirment");
						String relatedSystemId = bomChild
								.getProperty("U8_AssociationID");
						if (StringsUtil.isEmpty(relatedSystemId)) {// ����
							continue;
						}

						TCComponentItemRevision linkedLawRevision = getLinkedLaw(
								indicatorRequire, relatedSystemId);

						if (linkedLawRevision == null) {
							continue;
						}
						queue.offer(linkedLawRevision);
					}

				}

				// ȥ�ظ�
				HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
				for (TCComponentItemRevision lawRevision : revList) {
					String id = lawRevision.getProperty("current_id");
					if (revisionMap.containsKey(id)) {// id��Ψһ��ʶ������ֶ�
						continue;
					}
					revisionMap.put(id, lawRevision);
				}

				// ����װ������
				revList.clear();
				for (TCComponentItemRevision lawRevsion : revisionMap.values()) {
					revList.add(lawRevsion);
				}

			} catch (TCException e) {
				e.printStackTrace();
			}
			return revList;
		}

		
		
		/**
		 * ���ݹ�����ϵid
		 * 
		 * ��ȡ������GB 2199 ����
		 * 
		 * ��Ϊ���� id ���в�ѯ
		 * @param indicatorRequire
		 * @param relatedSystemId
		 * @return
		 */
		private TCComponentItemRevision getLinkedLaw(String indicatorRequire,String relatedSystemId) {
			//�������ӵķ����ID�ҵ�����
			String[] splitsLawIds = indicatorRequire.split("#");
			String relatedIds = relatedSystemId;
			
			for(String lawId : splitsLawIds){//������׼�Ƚ����� ֻ��Ҫָ��ķ���
				if(lawId.startsWith("GB")&&(lawId.contains("2760")||lawId.contains("14880"))){//˵������  �ǲ�Ʒ��׼  ����2760����14880
					//�����ҵ�����
					TCComponentItemRevision itemRevision = null;
					String lawID = relatedIds+" "+lawId;
					TCComponentItemRevision lawRevision = getLawRevisionById(lawID);
					
					if(lawRevision == null){
						return null;
					}
					return lawRevision;
				}
			}
			
			return null;
		}
		
		
		/**
		 * @param area
		 * @return ���ݷ���ID��ѯ����
		 */
		public TCComponentItemRevision getLawRevisionById(String lawId) {
			TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
			TCComponent[] result = QueryUtil.getSearchResult(query, new String[] { Const.FormulatorCheck.QUERY_ITME_ID },
					new String[] { lawId });

			if (result == null || result.length == 0 ) {
				return null;
			}
			
			TCComponentItemRevision itemRevision = null;
			try {
				itemRevision = ((TCComponentItem) result[0]).getLatestItemRevision();
			} catch (TCException e) {
				e.printStackTrace();
			}
			return itemRevision;
		}
		
		
		
		/**
		 * �����䷽�еĺ�
		 * @return
		 */
		public Double getSumInventory(){
			Double sum = 0d;
			for(int i=0;i<formulatorTableList.size();i++){
				MaterialBean bean = formulatorTableList.get(i);
				sum += StringsUtil.convertStr2Double(bean.U8_inventory);
			}
			return sum;
		}
		
	}

}
