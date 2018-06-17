package com.uds.yl.controler;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.TableColumn;


import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.ui.FindIndexLawFrame;
import com.uds.yl.utils.LogFactory;
//�䷽��������汾
public class FormulatorModifyControler_Copy implements BaseControler {
	
	private Logger logger = LogFactory.initLog("FormulatorModifyControler", LogLevel.INFO.getValue());
	
	private Double SUM = 0d;//Ͷ�������ܺ�
	
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();
	private List<MaterialBean> materialTableList = new ArrayList<>();// ��ʾ������ԭ�ϱ���Ԫ�صĽṹ
	private List<MaterialBean> formulatorTableList = new ArrayList<>();// ��ʾ���䷽ԭ�ϱ���Ԫ�صĽṹ
	private List<TCComponentItemRevision> materialTableItemRevList = new ArrayList<>();// ����ԭ�ϰ��еĵİ汾����
	private List<TCComponentItemRevision> formulatorTabelItemRevList = new ArrayList<>();// �䷽ԭ�ϱ��еİ汾����

	
	//������Ϊ����Ϲ��Լ��
	private TCComponentItemRevision lawRevision;//�������Ϲ��Լ��ķ���
	private List<TCComponentItemRevision> checkLawRevList;//��Ϊ�洢�ȴ����ķ�������
	
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//�ȴ�������Ӽ�Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//�ȴ�����ָ��Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//�ȴ�������Ӽ�Bom��Ӧ��pojo
	private List<IndexItemBean> waitIndexBeanList = null;//�ȴ�����ָ��Bom��Ӧ��pojo
	
	
	private List<MaterialBean> checkMaterialBeanList = null;//��Ӽ�����Bom��Ӧ��pojo
	private List<IndexItemBean> checkIndexBeanList = null;//ָ�귨��Bom��Ӧ��pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//������Ҫд��excel������
	
	
	@Override
	public void userTask(final TCComponentItemRevision itemRev) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormulaBomCreate frame = new FormulaBomCreate(itemRev);
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
		private JTextField sumEdt1;
		private JTextField sumEdt2;
		private JTextField sumEdt3;
		private JTextField supplementEdt;
		private JButton createBtn;
		private JButton containBtn;
		private JButton clearBtn;
		private JButton cancleBtn;
		private JButton supplementBtn;
		private JButton deleteBtn;
		private JButton searchBtn;
		private JButton addBtn;
		private JTextField lawTextField; //��ʾ�����������
		private JButton searchLawBtn;//��ѯ����
		private JButton lawCheckBtn;//����Ϲ��Լ��
		/**
		 * Create the frame.
		 */
		public FormulaBomCreate(final TCComponentItemRevision itemRev) {
			{
				setBounds(100, 100, 878, 636);
				contentPane = new JPanel();
				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				setContentPane(contentPane);
				contentPane.setLayout(null);

				JLabel label = new JLabel("�䷽������");
				label.setBounds(10, 0, 85, 25);
				contentPane.add(label);

				JPanel panel = new JPanel();
				panel.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel.setBounds(10, 25, 842, 563);
				contentPane.add(panel);
				panel.setLayout(null);

				JLabel lblNewLabel = new JLabel("�䷽/С�ϣ�");
				lblNewLabel.setBounds(19, 10, 69, 23);
				panel.add(lblNewLabel);

				formulaEdtName = new JTextField();
				formulaEdtName.setBounds(98, 10, 88, 23);
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
				searchEdt_1.setBounds(98, 53, 88, 23);
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

				searchBtn = new JButton("����");
				searchBtn.setBounds(649, 53, 88, 23);
				panel.add(searchBtn);

				JLabel lblNewLabel_2 = new JLabel("��ѡԭ�ϱ�");
				lblNewLabel_2.setBounds(19, 101, 97, 23);
				panel.add(lblNewLabel_2);

				addBtn = new JButton("���");
				addBtn.setBounds(661, 234, 88, 23);
				panel.add(addBtn);

				JLabel lblNewLabel_3 = new JLabel("�䷽��");
				lblNewLabel_3.setBounds(13, 267, 54, 15);
				panel.add(lblNewLabel_3);

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(42, 132, 571, 125);
				panel.add(scrollPane);

				materialTable = new JTable();
				materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				materialTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "ԭ����", "���Ӵ���", "��Ӧ��", "��λ�ɱ�(Ԫ/ǧ��)", "��׼��λ" }));
				scrollPane.setViewportView(materialTable);
				materialTable.setBorder(new LineBorder(new Color(0, 0, 0)));

				JPanel panel_1 = new JPanel();
				panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
				panel_1.setBounds(23, 292, 814, 211);
				panel.add(panel_1);
				panel_1.setLayout(null);

				JScrollPane scrollPane_1 = new JScrollPane();
				scrollPane_1.setBounds(10, 10, 621, 149);
				panel_1.add(scrollPane_1);

				formulaTable = new JTable();
				formulaTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				formulaTable.setModel(new DefaultTableModel(new Object[][] {},
						new String[] { "ԭ����", "���Ӵ���", "Ͷ����(ǧ��)", "���(%)", "������/���۳ɱ�", "����˵��", "�滻��", "���˵��" }));
				formulaTable.getColumnModel().getColumn(2).setPreferredWidth(103);
				formulaTable.getColumnModel().getColumn(3).setPreferredWidth(95);
				formulaTable.getColumnModel().getColumn(4).setPreferredWidth(95);
				formulaTable.getColumnModel().getColumn(5).setPreferredWidth(91);
				formulaTable.getColumnModel().getColumn(6).setPreferredWidth(87);
				scrollPane_1.setViewportView(formulaTable);

				deleteBtn = new JButton("ɾ��");
				deleteBtn.setBounds(714, 136, 88, 23);
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

				JLabel lblNewLabel_5 = new JLabel("Ԫ");
				lblNewLabel_5.setBounds(527, 179, 54, 21);
				panel_1.add(lblNewLabel_5);

				JLabel lblNewLabel_6 = new JLabel("����:");
				lblNewLabel_6.setBounds(650, 56, 54, 23);
				panel_1.add(lblNewLabel_6);

				supplementBtn = new JButton("����");
				supplementBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				supplementBtn.setBounds(714, 103, 88, 23);
				panel_1.add(supplementBtn);

				supplementEdt = new JTextField();
				supplementEdt.setBounds(714, 56, 90, 23);
				panel_1.add(supplementEdt);
				supplementEdt.setColumns(10);

				createBtn = new JButton("����");
				createBtn.setBounds(129, 530, 81, 23);
				panel.add(createBtn);

				containBtn = new JButton("Ӫ���ɷ�");
				containBtn.setBounds(404, 530, 100, 23);
				panel.add(containBtn);

				cancleBtn = new JButton("ȡ��");
				cancleBtn.setBounds(533, 530, 80, 23);
				panel.add(cancleBtn);

				clearBtn = new JButton("���");
				clearBtn.setBounds(661, 201, 88, 23);
				panel.add(clearBtn);

				JComboBox<String> comboBox = new JComboBox<String>(new String[] { "", "����", "��ϻ���" });
				DefaultCellEditor editor = new DefaultCellEditor(comboBox);
				TableColumn column = formulaTable.getColumnModel().getColumn(5); // nΪ�е���ţ��Լ��޸�
				column.setCellEditor(editor);

				JLabel lblNewLabel_7 = new JLabel("���棺");
				lblNewLabel_7.setBounds(421, 105, 54, 15);
				panel.add(lblNewLabel_7);

				lawTextField = new JTextField();
				lawTextField.setBounds(495, 102, 88, 21);
				panel.add(lawTextField);
				lawTextField.setColumns(10);

				searchLawBtn = new JButton("��������");
				searchLawBtn.setBounds(649, 101, 132, 23);
				panel.add(searchLawBtn);

				lawCheckBtn = new JButton("���");
				lawCheckBtn.setBounds(267, 530, 93, 23);
				panel.add(lawCheckBtn);
			}

			// �䷽���ֺͰ汾��EditText��ʼ��
			try {
				String formulatorName = itemRev.getProperty("object_name");
				String formulatorRevision = itemRev.getProperty("item_revision_id");
				formulaEdtName.setText(formulatorName);
				formulaEdtRevision.setText(formulatorRevision);
				lawTextField.setEditable(false);
				
				
			} catch (TCException e1) {
				e1.printStackTrace();
			}

			// �����BOM��ͼ�Ļ��䷽�������Table���г�ʼֵ��
			List<MaterialBean> initBean = iFormulatorModifyService.getInitBean(itemRev);
			List<TCComponentItemRevision> initItemRevLst = iFormulatorModifyService.getInitMaterialItemRevList(itemRev);
			formulatorTabelItemRevList.addAll(initItemRevLst);

			DefaultTableModel formulaModel = (DefaultTableModel) formulaTable.getModel();
			for (MaterialBean materialBean : initBean) {
				formulatorTableList.add(materialBean);// ���ܵ��䷽�����
				formulaModel.addRow(new String[] { materialBean.objectName, materialBean.code,
						materialBean.U8_inventory, materialBean.bl_quantity, materialBean.price, materialBean.alternate,
						 materialBean.alternateItem,materialBean.groupItem});
			}
			if (initBean.size() > 0) {
				formulaTable.setRowSelectionInterval(0, 0);// Ĭ��ѡ�е�һ��
			}

			initQuality();// ����Ͷ�����仯�ı����

			// ����ԭ��
			searchBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// ������������ :ԭ�����ơ����Ӵ��롢��Ӧ��
					String materialName = searchEdt_1.getText().toString();
					String materialCode = searchEdt_2.getText().toString();
					String materialSupplier = searchEdt_3.getText().toString();

					// ����ԭ����
					List<MaterialBean> searchBeans = iFormulatorModifyService.getSearchBean(materialName, materialCode,
							materialSupplier);
					List<TCComponentItemRevision> searchItemRevisionList = iFormulatorModifyService.getSearchItemRev(materialName,
							materialCode, materialSupplier);
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
							selectMaterialBean.U8_inventory, selectMaterialBean.bl_quantity, selectMaterialBean.price,
							"", "",
							"" });
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
					initQuality();// ����Ͷ�����仯�ı����  ִ�м����  ���Ǳ仯���ֶ�

					//���� ĳЩֵ���ֲ���
					for (int i = 0; i < formulatorTableList.size(); i++) {
						//��Щ���޸ĺ�Ҫ���ֵ
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
					iFormulatorModifyService.createFormulatorBOM(itemRev, formulatorTabelItemRevList,
							formulatorTableList);
					MessageBox.post("OK", "", MessageBox.INFORMATION);
				}
			});

			// ����Ӫ���ɷֱ���
			containBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					// ���ɱ�
					TCComponentBOMLine cacheTopBomLine = iFormulatorModifyService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
					List<TCComponentBOMLine> materialBomList = iFormulatorModifyService.getMaterialBomList(cacheTopBomLine);
					List<IndexItemBean> indexBeanList = iFormulatorModifyService.getIndexBeanList(cacheTopBomLine);
					iFormulatorModifyService.write2Excel(materialBomList, indexBeanList);
					try {
						logger.fine(cacheTopBomLine.getItem().getProperty("object_name"));
					} catch (TCException e) {
						e.printStackTrace();
					}
					logger.fine(formulatorTabelItemRevList.toString());
				}
			});

			// ȡ��
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
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
					if (supplement < 0 || (supplement - (SUM - inventory)) < 0) {
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

			// ���
			clearBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) materialTable.getModel();
					for (int i = 0; i < materialTableList.size(); i++) {
						model.removeRow(0);
					}
					materialTableList.clear();
					materialTableItemRevList.clear();
				}
			});

			//��ѯ����
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FindIndexLawFrame findIndexLawFrame = new FindIndexLawFrame(new AbstractCallBack() {
						@Override
						public void setLawAndName(String lawName, TCComponentItemRevision lawRev) {
							super.setLawAndName(lawName, lawRev);
							lawRevision = lawRev;
							lawTextField.setText(lawName);
						}
					});
					findIndexLawFrame.setVisible(true);
				}
			});
			
			
			//���䷽�ĺϹ��Լ��
			lawCheckBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(lawRevision==null){//�������ûѡ��Ļ�����ʾ
						MessageBox.post("��ѡ���䷽��Ӧ�Ĳ�Ʒ����","",MessageBox.INFORMATION);
						return;
					}
					//��������Ϊ��ʱ���䷽�����˵
					TCComponentBOMLine cacheTopBomLine = iFormulatorModifyService.getCacheTopBomLine(formulatorTabelItemRevList, formulatorTableList);
					
					checkLawRevList = new ArrayList<>();
					checkLawRevList.add(lawRevision);
					
					//�����䷽��ȡԭ�ϵ�BOM
					waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
					//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
					waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
					
					//�����䷽��ȡָ���BOM
//					waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
					//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
					waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
					
					//��ȡ�����е���Ӽ�Bean   Ҫ������Ӽ���������ѡ�еķ��� 
					checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
					//��ȡ�����е�ָ��Bean�Ǵӷ���������˵  ��������ѡ�еĲ�Ʒ��׼
					checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
					
					
					//�����Ӽ�  	��Ӽ��ķ���Ƚ����� ר������һ������  ����
					List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
					//���ָ��
					List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
					
					//д��excel��
					allCheckedBeanList.addAll(materialCheckedBean);
					allCheckedBeanList.addAll(indexCheckedBean);
					
					iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
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
				if (materialBean.objectName.equals(formulatorTableList.get(i).objectName)) {
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
				String strInventory = (String) model.getValueAt(i, 2);
				if ("".equals(strInventory))
					strInventory = "0";
				Double inventory = 0d;
				try {
					inventory = Double.valueOf(strInventory);
				} catch (Exception e) {
					inventory = 0d;
				}
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
				} else {
					materialBean.bl_quantity = inventory / SUM * 100 + "";
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

				// ����Ǿ���Ļ�����Ҫ��������
				if (materialBean.objectName.contains("��")) {
					// TODO:������
					continue;
				}

				// ----�ܼ۸�----
				sumMoney += price * inventory;
				
				//����˵��   �滻��  ���˵�� 
				materialBean.alternate = (String) model.getValueAt(i, 5)+"";//����˵��
				materialBean.alternateItem = (String) model.getValueAt(i, 6)+"";//�滻��
				materialBean.groupItem = (String) model.getValueAt(i, 7)+"";//���˵��
				
			}

			// ���ºϼ��������ӵ�ֵ
			// sumEdt1 Ͷ�����ϼ�
			// sumEdt2 ��Ⱥϼ�
			// sumEdt3 ��Ǯ�ϼ�
			sumEdt1.setText(SUM + "");
			sumEdt2.setText("100");
			sumEdt3.setText(sumMoney + "");

		}
	}

}
