package com.uds.yl.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.apache.soap.util.Bean;
import org.apache.xalan.xsltc.dom.SAXImpl.NamespaceWildcardIterator;
import org.jacorb.trading.constraint.DoubleValue;
import org.jacorb.transaction.Sleeper;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.actions.NewECMAction;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.IMilkPowderFormulatorService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.service.impl.MilkPowderFormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.LOVUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.LogFactory;
import com.uds.yl.utils.StringsUtil;

public class MilkPowderFormulatorFrame extends JFrame {

	private Logger logger = LogFactory.initLog("MilkPowderFormulatorFrame", LogLevel.INFO.getValue());
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private JPanel contentPane;
	private JTextField codeEdt;
	private JTextField revisionEdt;
	private JTextField nutritionEdt;//��ʵ�����䷽�����Ʊ༭��
	
	private JTable materialTable;
	private JTable formulatorTable;
	
	private JTextField inventorySumEdt;
	private JTextField ratioSumEdt;
	private JTextField unitCostSumEdt;

	private JComboBox wetLossCombox;//������ʾʪ����ĵ�combox
	private JComboBox dryLossCombox;//������ʾ�ɷ���ĵ�combox
	private JComboBox dateLossCombox;//������ʾ��������ĵ�combox
	private JComboBox typeCombox;//������ʾ�䷽����combox
	
	private JButton showWetLossBtn;//ʪ�����ģʽ�Ĳ鿴
	private JButton showDryLossBtn;//�ɷ����ģʽ�Ĳ鿴
	private JButton showDateLossBtn;//���������ģʽ�Ĳ鿴
	private JButton showMaterialBtn;//ԭ�ϵĲ鿴
	private JButton wetAddMaterialBtn;//ʪ�����
	private JButton dryAddMaterialBtn;//�ɷ����
	private JButton nutritionBtn;
	private JButton cancleBtn;
	private JButton createBtn;
	private JButton searchIndexBtn;
	private JButton searchLawBtn;
	private JButton searchMaterialBtn;
	private JButton lawCompareBtn;
	private JButton indexCompareBtn;
	private JButton deleteBtn;
	private JButton creatDryFormulatorBtn;//�����ɷ��䷽
	private JButton addWetFormulatorBtn;//��ӻ���
	private JButton productExcelBtn;//�����䷽��
	private JButton nutritionExcelBtn;//Ӫ��������
	
	private JTextField materialNameEdt;//��ѯԭ������
	private JTextField supplierEdt;//��ѯԭ�Ϲ�Ӧ��
	private JTextField materialTypeEdt;//��ѯԭ�����
	
	private JTextField formulatorIndexEdt;//����
	private JTextField formulatorLawEdt;//ִ�б�׼
	
	private DefaultTableModel materialTableModel;//ԭ�ϱ���model
	private DefaultTableModel formulatorTableModel;//�䷽���е�model
	
	private IMilkPowderFormulatorService milkPowderFormulatorService = new MilkPowderFormulatorServiceImpl();
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();
	private TCComponentItemRevision itemRevision;//�䷽�汾����
	
	private List<TCComponentItemRevision> materialList = new ArrayList<>();//����ԭ���б�
	private List<String> materialNameList = new ArrayList<>();//ԭ�ϱ��е�����
	private List<MaterialBean> materialBeansList = new ArrayList<>();//ԭ�ϱ��е�ʵ���༯��
	
	private List<TCComponentItemRevision> formulatorList = new ArrayList<>();//�䷽�е�ԭ���б�
	private List<String> formulatorNameList = new ArrayList<>();//�䷽���е�����
	private List<MaterialBean> formulatorMaterialBeanList = new ArrayList<>();//�䷽��ԭ�϶�Ӧ��ʵ����
	
	
	private List<String> lossNameList;//���ģʽ����ʾ��combox�е�ֵ
	private List<TCComponentItemRevision> lossItemRevList;//ģʽ��ĵ����еļ���
	private TCComponentItemRevision wetLossItemRevsion;//ѡ�е�ʪ�����ģʽ
	private TCComponentItemRevision dryLossItemRevsion;//ѡ�еĸɷ����ģʽ
	private TCComponentItemRevision dateLossItemRevsion;//ѡ�еı��������ģʽ
	
	private List<String> formulatorTypeNameList;//�䷽����������ʾ��combonx�е�ֵ  ��Ϊ����ֻ��һ������ string���Բ�������ת��
	private String formulatorType;//�䷽��� ѡ�е�
	
	private TCComponentItemRevision formulatorLawIRev;//�䷽����Ӧ�ķ���汾
	private TCComponentItemRevision formulatorIndexRev;//�䷽����Ӧ�ļ�����׼�汾

	
	//�̷��䷽��������ִ����
	private String Wet_Method = "ʪ��";
	private String Dry_Method = "�ɷ�";
	
	//��Ϊ����Ա���ʹ�õĲ���
	private List<TCComponentItemRevision> checkLawRevList = null;//��������ƥ������з���
	private List<TCComponentBOMLine> waitMaterialBomList = null;//�ȴ�������Ӽ�Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//�ȴ�����ָ��Bom
	private List<MaterialBean> waitMaterialBeanList = null;//�ȴ�������Ӽ�Bom��Ӧ��pojo
	private List<IndexItemBean> waitIndexBeanList = null;//�ȴ�����ָ��Bom��Ӧ��pojo
	private List<MaterialBean> checkMaterialBeanList = null;//��Ӽ�����Bom��Ӧ��pojo
	private List<IndexItemBean> checkIndexBeanList = null;//ָ�귨��Bom��Ӧ��pojo
	private List<FormulatorCheckedBean> allCheckedBeanList = null;//������Ҫд��excel������

	
	/**
	 * Create the frame.
	 */
	public MilkPowderFormulatorFrame(final TCComponentItemRevision itemRevision) {
		super("�̷��䷽���");
		{
			setResizable(false);
			this.itemRevision = itemRevision;
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(70, 70, 1071, 775);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("���룺");
			lblNewLabel.setBounds(34, 13, 79, 15);
			contentPane.add(lblNewLabel);
			
			codeEdt = new JTextField();
			codeEdt.setBounds(167, 10, 100, 21);
			contentPane.add(codeEdt);
			codeEdt.setColumns(10);
			
			JLabel lblNewLabel_1 = new JLabel("�汾��");
			lblNewLabel_1.setBounds(409, 13, 54, 15);
			contentPane.add(lblNewLabel_1);
			
			revisionEdt = new JTextField();
			revisionEdt.setColumns(10);
			revisionEdt.setBounds(548, 10, 100, 21);
			contentPane.add(revisionEdt);
			
			JLabel label = new JLabel("�䷽���ƣ�");
			label.setBounds(34, 41, 111, 15);
			contentPane.add(label);
			
			nutritionEdt = new JTextField();
			nutritionEdt.setColumns(10);
			nutritionEdt.setBounds(167, 38, 100, 21);
			contentPane.add(nutritionEdt);
			
			JLabel label_1 = new JLabel("�䷽���");
			label_1.setBounds(409, 41, 79, 15);
			contentPane.add(label_1);
			
			JLabel label_2 = new JLabel("ʪ�����:");
			label_2.setBounds(34, 70, 111, 15);
			contentPane.add(label_2);
			
			
			wetLossCombox = new JComboBox<String>();
			wetLossCombox.setBounds(167, 67, 111, 21);
			contentPane.add(wetLossCombox);
			
			showWetLossBtn = new JButton("�鿴");
			showWetLossBtn.setBounds(298, 66, 77, 23);
			contentPane.add(showWetLossBtn);
			
			
			JLabel label_8 = new JLabel("�ɷ���ģ�");
			label_8.setBounds(34, 103, 111, 15);
			contentPane.add(label_8);
			
			JLabel label_9 = new JLabel("��������ģ�");
			label_9.setBounds(34, 133, 111, 15);
			contentPane.add(label_9);
			
			dryLossCombox = new JComboBox();
			dryLossCombox.setBounds(167, 100, 111, 21);
			contentPane.add(dryLossCombox);
			
			dateLossCombox = new JComboBox();
			dateLossCombox.setBounds(167, 130, 111, 21);
			contentPane.add(dateLossCombox);
			
			showDryLossBtn = new JButton("�鿴");
			showDryLossBtn.setBounds(298, 99, 77, 23);
			contentPane.add(showDryLossBtn);
			
			showDateLossBtn = new JButton("�鿴");
			showDateLossBtn.setBounds(298, 129, 77, 23);
			contentPane.add(showDateLossBtn);
			
			JLabel lblNewLabel_2 = new JLabel("ִ�б�׼��");
			lblNewLabel_2.setBounds(409, 98, 85, 18);
			contentPane.add(lblNewLabel_2);
			
			typeCombox = new JComboBox();
			typeCombox.setBounds(548, 38, 111, 21);
			contentPane.add(typeCombox);
			
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(10, 172, 987, 286);
			contentPane.add(panel);
			panel.setLayout(null);
			
			JLabel label_3 = new JLabel("ԭ�����");
			label_3.setBounds(10, 14, 111, 15);
			panel.add(label_3);
			
			JLabel label_4 = new JLabel("ԭ������");
			label_4.setBounds(321, 14, 85, 15);
			panel.add(label_4);
			
			materialNameEdt = new JTextField();
			materialNameEdt.setColumns(10);
			materialNameEdt.setBounds(414, 11, 100, 21);
			panel.add(materialNameEdt);
			
			searchMaterialBtn = new JButton("����");
			searchMaterialBtn.setBounds(598, 10, 77, 23);
			panel.add(searchMaterialBtn);
			
			JLabel label_5 = new JLabel("��Ӧ�̣�");
			label_5.setBounds(10, 50, 111, 15);
			panel.add(label_5);
			
			supplierEdt = new JTextField();
			supplierEdt.setColumns(10);
			supplierEdt.setBounds(111, 47, 100, 21);
			panel.add(supplierEdt);
			
			JLabel lblNewLabel_3 = new JLabel("��ѡԭ������");
			lblNewLabel_3.setBounds(21, 88, 201, 15);
			panel.add(lblNewLabel_3);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(31, 114, 725, 140);
			panel.add(scrollPane);
			
			materialTable = new JTable(){
				public boolean isCellEditable(int row, int column) {
					return false;
				};
			};
			materialTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ԭ����", "��Ӧ��", "��λ�ɱ�(Ԫ/ǧ��)", "��׼��λ", "״̬"
				}
			));
			scrollPane.setViewportView(materialTable);
			
			showMaterialBtn = new JButton("�鿴");
			showMaterialBtn.setBounds(826, 162, 115, 23);
			panel.add(showMaterialBtn);
			
			wetAddMaterialBtn = new JButton("ʪ�����");
			wetAddMaterialBtn.setBounds(826, 195, 115, 23);
			panel.add(wetAddMaterialBtn);
			
			materialTypeEdt = new JTextField();
			materialTypeEdt.setColumns(10);
			materialTypeEdt.setBounds(111, 11, 100, 21);
			panel.add(materialTypeEdt);
			
			dryAddMaterialBtn = new JButton("�ɷ����");
			dryAddMaterialBtn.setBounds(826, 228, 115, 23);
			panel.add(dryAddMaterialBtn);
			
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel_1.setBounds(34, 493, 992, 185);
			contentPane.add(panel_1);
			panel_1.setLayout(null);
			
			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(26, 10, 738, 121);
			panel_1.add(scrollPane_1);
			
			formulatorTable = new JTable(){
				public boolean isCellEditable(int row, int column) {
					if(column==1){
						return true;
					}
					return false;
				};
			};
			formulatorTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ԭ����", "Ͷ����(ǧ��)", "���%", "��λ�ɱ�(Ԫ/ǧ��)","��ʽ"
				}
			));
			scrollPane_1.setViewportView(formulatorTable);
			
			JLabel label_6 = new JLabel("�ϼƣ�");
			label_6.setBounds(126, 151, 91, 15);
			panel_1.add(label_6);
			
			inventorySumEdt = new JTextField();
			inventorySumEdt.setBounds(207, 148, 180, 21);
			panel_1.add(inventorySumEdt);
			inventorySumEdt.setColumns(10);
			
			ratioSumEdt = new JTextField();
			ratioSumEdt.setColumns(10);
			ratioSumEdt.setBounds(387, 148, 180, 21);
			panel_1.add(ratioSumEdt);
			
			unitCostSumEdt = new JTextField();
			unitCostSumEdt.setColumns(10);
			unitCostSumEdt.setBounds(566, 148, 180, 21);
			panel_1.add(unitCostSumEdt);
			
			deleteBtn = new JButton("ɾ��");
			deleteBtn.setBounds(808, 92, 123, 23);
			panel_1.add(deleteBtn);
			
			addWetFormulatorBtn = new JButton("��ӻ���");
			addWetFormulatorBtn.setBounds(808, 56, 123, 23);
			panel_1.add(addWetFormulatorBtn);
			
			JLabel lblNewLabel_4 = new JLabel("�䷽��");
			lblNewLabel_4.setBounds(34, 468, 258, 15);
			contentPane.add(lblNewLabel_4);
			
			createBtn = new JButton("��������");
			createBtn.setBounds(34, 704, 111, 23);
			contentPane.add(createBtn);
			
			nutritionBtn = new JButton("Ӫ���ɷ�");
			nutritionBtn.setBounds(576, 704, 93, 23);
			contentPane.add(nutritionBtn);
			
			cancleBtn = new JButton("ȡ��");
			cancleBtn.setBounds(952, 704, 93, 23);
			contentPane.add(cancleBtn);
			
			formulatorIndexEdt = new JTextField();
			formulatorIndexEdt.setColumns(10);
			formulatorIndexEdt.setBounds(548, 97, 100, 21);
			contentPane.add(formulatorIndexEdt);
			
			JLabel label_7 = new JLabel("ִ�з��棺");
			label_7.setBounds(409, 68, 85, 18);
			contentPane.add(label_7);
			
			formulatorLawEdt = new JTextField();
			formulatorLawEdt.setColumns(10);
			formulatorLawEdt.setBounds(548, 67, 100, 21);
			contentPane.add(formulatorLawEdt);
			
			searchIndexBtn = new JButton("��ȡ��׼");
			searchIndexBtn.setBounds(681, 96, 100, 23);
			contentPane.add(searchIndexBtn);
			
			searchLawBtn = new JButton("��ȡ����");
			searchLawBtn.setBounds(681, 66, 100, 23);
			contentPane.add(searchLawBtn);
			
			lawCompareBtn = new JButton("����Ա�");
			lawCompareBtn.setBounds(321, 704, 93, 23);
			contentPane.add(lawCompareBtn);
			
			indexCompareBtn = new JButton("��׼�Ա�");
			indexCompareBtn.setBounds(447, 704, 93, 23);
			contentPane.add(indexCompareBtn);
			
			creatDryFormulatorBtn = new JButton("�����ɷ�");
			creatDryFormulatorBtn.setBounds(186, 704, 100, 23);
			contentPane.add(creatDryFormulatorBtn);
			
			productExcelBtn = new JButton("�����䷽");
			productExcelBtn.setBounds(706, 704, 93, 23);
			contentPane.add(productExcelBtn);
			
			nutritionExcelBtn = new JButton("Ӫ����");
			nutritionExcelBtn.setBounds(824, 704, 93, 23);
			contentPane.add(nutritionExcelBtn);
			
			materialTableModel = (DefaultTableModel) materialTable.getModel();
			formulatorTableModel = (DefaultTableModel) formulatorTable.getModel();
			
		}
	
		
		{
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					ProgressBarDialog progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();
					//��ʼ��
					try {
						String formulatorName = itemRevision.getProperty("object_name");
						String revisionId = itemRevision.getProperty("item_revision_id");
						
						nutritionEdt.setText(formulatorName);
						revisionEdt.setText(revisionId);
						
						
						nutritionEdt.setEditable(false);
						revisionEdt.setEditable(false);
						codeEdt.setEditable(false);
						
						inventorySumEdt.setEditable(false);
						ratioSumEdt.setEditable(false);
						unitCostSumEdt.setEditable(false);
						
						
						
						formulatorIndexEdt.setEditable(false);
						formulatorLawEdt.setEditable(false);
						
						initFrame();//��ʼ������ķ���
						
						progressBarDialog.stop();
					} catch (TCException e) {
						e.printStackTrace();
						progressBarDialog.stop();
					}
				}
			}).start();
			
			
			//����ʪ�����ģʽ��combox�ļ����¼�
			wetLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = wetLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
						wetLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//���øɷ����ģʽ��combox�ļ����¼�
			dryLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = dryLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
						dryLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//���ñ��������ģʽ��combox�ļ����¼�
			dateLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = dateLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//����-1�ͱ�ʾѡ����һ��
						dateLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//�����䷽����combox�ļ����¼�
			typeCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = typeCombox.getSelectedIndex();
					if(selectIndex!=-1){
//						formulatorType = formulatorTypeNameList.get(selectIndex);
					}
				}
			});
			
			
			//�����䷽����Ͷ�����ı任
			formulatorTableModel.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					int column = e.getColumn();
					int row = e.getFirstRow();
					if(column==1){//��Ͷ������˵
						MaterialBean materialBean = formulatorMaterialBeanList.get(row);
						materialBean.U8_inventory = formulatorTableModel.getValueAt(row, column).toString();//����bean��ֵ
					}
					
					//�ϼ�ֵ��Ҫ���¼��� ���Է�װһ������
					computeSumInventory();
				}
			});
			
			//����ԭ��
			searchMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							String name = materialNameEdt.getText().toString();
							String type = materialTypeEdt.getText().toString();
							String supplier = supplierEdt.getText().toString();
							
							if(StringsUtil.isEmpty(name)&&StringsUtil.isEmpty(type)&&StringsUtil.isEmpty(supplier)){
								MessageBox.post("����д��ѯ��Ϣ��","��ʾ",MessageBox.INFORMATION);
								return;
							}
							
							List<TCComponentItemRevision> searchMaterialResult = milkPowderFormulatorService.searchMaterialResult(name,type,supplier);
							//���ԭ��table�е�����
							int size = materialList.size();
							for(int i=0;i<size;i++){
								materialList.remove(0);
								materialNameList.remove(0);
								materialTableModel.removeRow(0);
								materialBeansList.remove(0);
							}
							//���¸�ֵ
							for(TCComponentItemRevision rev : searchMaterialResult){
								try {
									String materiaName = rev.getProperty("object_name");
									materialList.add(rev);
									materialNameList.add(materiaName);
									materialTableModel.addRow(new String[]{materiaName});
									
									MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, rev);
									materialBeansList.add(materialBean);
								} catch (TCException | InstantiationException | IllegalAccessException e1) {
									e1.printStackTrace();
								}
							}
							progressBarDialog.stop();
						}
					}).start();
					
					
				}
			});
			
			//ʪ�����һ��ԭ��
			wetAddMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = materialTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
						return;
					}
					TCComponentItemRevision selectRev = materialList.get(selectedIndex);
					String revName = materialNameList.get(selectedIndex);
					MaterialBean materialBean = materialBeansList.get(selectedIndex);
					materialBean.productMethod = Wet_Method;//���Ϊʪ�����ԭ�ϵ�˵
					
					formulatorList.add(selectRev);
					formulatorNameList.add(revName);
					formulatorMaterialBeanList.add(materialBean);
					
					formulatorTableModel.addRow(new String[]{revName,"","","",Wet_Method});//��ӵ��䷽����
					
					computeSumInventory();
					
				}
			});
			
			
			//�ɷ����һ��ԭ��
			dryAddMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = materialTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
						return;
					}
					TCComponentItemRevision selectRev = materialList.get(selectedIndex);
					String revName = materialNameList.get(selectedIndex);
					MaterialBean materialBean = materialBeansList.get(selectedIndex);
					materialBean.productMethod = Dry_Method;//���Ϊ�ɷ����ԭ��
					
					formulatorList.add(selectRev);
					formulatorNameList.add(revName);
					formulatorMaterialBeanList.add(materialBean);
					
					formulatorTableModel.addRow(new String[]{revName,"","","",Dry_Method});//��ӵ��䷽����
					
					computeSumInventory();
					
				}
			});
		
			
			//ɾ��һ��ԭ�ϴ��䷽��table��
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = formulatorTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("��ѡ��һ��","",MessageBox.INFORMATION);
						return;
					}
					formulatorList.remove(selectedIndex);
					formulatorNameList.remove(selectedIndex);
					formulatorMaterialBeanList.remove(selectedIndex);
					formulatorTableModel.removeRow(selectedIndex);
					
					computeSumInventory();
				}
			});
		
		
			
			//��������
			createBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
//					milkPowderFormulatorService.createFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList);
//					MessageBox.post("OK","",MessageBox.INFORMATION);
					
					
					//�������� ѡ����������ǵ�ǰѡ�е��䷽����������Ҫ�����ŵ�Home֮��
					CreateWetFormulatorFrame createWetFormulatorFrame = new CreateWetFormulatorFrame(new AbstractCallBack() {
						@Override
						public void createWetFormulator(final boolean selected, final String name) {
							super.createWetFormulator(selected, name);
							
							new Thread(new Runnable() {
								@Override
								public void run() {
									ProgressBarDialog progressBarDialog = new ProgressBarDialog();
									progressBarDialog.start();
									//�ж�����ӵ������Ƿ���ʪ����
									boolean canCreate = true;
									for(MaterialBean bean : formulatorMaterialBeanList){
										if("�ɷ�".equals(bean.productMethod)){//����иɷ��Ͳ��ô���
											MessageBox.post("��˶��䷽ԭ�϶���ʪ��","",MessageBox.INFORMATION);
											return;
										}
									}
									if(wetLossItemRevsion==null){
										MessageBox.post("��ѡ����ĵİ汾","",MessageBox.INFORMATION);
										return;
									}
									if(selected){
										//selectΪtrue�ͱ�ʾ�ڵ�ǰ���䷽�д����ṹ
										milkPowderFormulatorService.createFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList,wetLossItemRevsion);
										MessageBox.post("OK","",MessageBox.INFORMATION);
									}else {
										//selectΪfalse�ͱ�ʾ��Ҫ��home����һ�����۵��䷽
										milkPowderFormulatorService.createWetFormulatorInHome(formulatorList, formulatorMaterialBeanList, name,wetLossItemRevsion);
										MessageBox.post("OK","",MessageBox.INFORMATION);
									}
									
									progressBarDialog.stop();
								}
							}).start();
							
						}
					});
					createWetFormulatorFrame.setVisible(true);
					//����б��к��иɷ�����Ŀ�Ļ�����ʾ������                                   
				}
				
			});
			
			
			
			//ȡ��
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
				}
			});
			
			//��������e
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SearchLawFrame searchLawFrame = new SearchLawFrame(new AbstractCallBack() {
						@Override
						public void setLawRev(TCComponentItemRevision lawItemRev) {
							super.setLawRev(lawItemRev);
							formulatorLawIRev = lawItemRev;
							try {
								String lawName = lawItemRev.getProperty("object_name");
								formulatorLawEdt.setText(lawName);
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					});
					searchLawFrame.setVisible(true);
				}
			});
			
			//����ִ�б�׼
			searchIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SearchIndexFrame searchIndexFrame = new SearchIndexFrame(new AbstractCallBack() {
						@Override
						public void setIndexRev(TCComponentItemRevision indexItemRev) {
							super.setIndexRev(indexItemRev);
							formulatorIndexRev = indexItemRev;
							try {
								String indexName = indexItemRev.getProperty("object_name");
								formulatorIndexEdt.setText(indexName);
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					});
					searchIndexFrame.setVisible(true);
				}
			});
			
			
			//�鿴ʪ�����ģʽ��
			showWetLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(wetLossItemRevsion==null){//û����Ķ���
								MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(wetLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
			
			//�鿴�ɷ����ģʽ
			showDryLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(dryLossItemRevsion==null){//û����Ķ���
								MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(dryLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
						}
					}).start();
				}
			});
			
			
			//�鿴�����ڵ����ģʽ
			showDateLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(dateLossItemRevsion==null){//û����Ķ���
								MessageBox.post("��ѡ��Ҫ�鿴����Ķ���","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(dateLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
						}
					}).start();
					
				}
			});
			
			
			//�鿴ԭ��(��ר�������鿴ԭ�ϰ���˵)
			showMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedRow = materialTable.getSelectedRow();
					if(selectedRow<0){//û��ѡ��
						MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
						return;
					}
					
					final TCComponentItemRevision selectMaterialRev = materialList.get(selectedRow);
					ShowNutritionInfoFrame showNutritionInfoFrame = new ShowNutritionInfoFrame(selectMaterialRev, new AbstractCallBack() {
						@Override
						public void modifyNutritionRev(List<TCComponentItemRevision> materialList,
								List<MaterialBean> materialBeanList) {
							super.modifyNutritionRev(materialList, materialBeanList);
							//��ѡ�е�Ӫ�������д���  ����� Ȼ�󽫰汾�Ž�bom�У�Ȼ��beanд��tc
							
							milkPowderFormulatorService.updateNutritionStruct(selectMaterialRev, materialList, materialBeanList);
						}
					});
					showNutritionInfoFrame.setVisible(true);
					
				}
			});
		
		
		
			//����Ա�
			lawCompareBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							
							//�������ͼ�����׼��û�еĻ�������
							if(formulatorLawIRev==null){
								MessageBox.post("��ѡ�񷨹�","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;

							}
							
							//��ȡ����
							checkLawRevList = iFormulatorLegalCheckService.getRelatedIDLaws(formulatorLawIRev);
							
							
							if(checkLawRevList.size()==0||checkLawRevList==null){
								MessageBox.post("û��ƥ��ķ���","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;
							}
							//���ݱ���е���������һ����ʱ���䷽����
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							//�����䷽��ȡԭ�ϵ�BOM
							waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
							//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
							waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//�����䷽��ȡָ���BOM
//							waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
							//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
							waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
							
							//��ȡ�����е���Ӽ�Bean   Ҫ������Ӽ���������ѡ�еķ��� 
							checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
							//��ȡ�����е�ָ��Bean�Ǵӷ���������˵  ��������ѡ�еĲ�Ʒ��׼
							checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
//							milkPowderFormulatorService.getCheckIndexBeanListByIndexStandard(checkIndexBeanList, formulatorIndexRev);
							
							
							
							//�����Ӽ�  	��Ӽ��ķ���Ƚ����� ר������һ������  ����
							List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
							//���ָ��
							List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
							
							//д��excel��
							allCheckedBeanList = new ArrayList<>();
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							
							iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
							
							
							progressBarDialog.stop();
						}
					}).start();
					
					
				}
			});
			
			
			//ִ�б�׼�Ա�
			indexCompareBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//���������׼��û�еĻ�������
							if(formulatorIndexRev==null){
								MessageBox.post("��ѡ��ִ�б�׼","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;

							}
							
							checkLawRevList = new ArrayList<>();
							checkLawRevList.add(formulatorIndexRev);
							
							if(checkLawRevList.size()==0||checkLawRevList==null){
								MessageBox.post("û��ƥ��ķ���","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;
							}
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							
							//�����䷽��ȡԭ�ϵ�BOM
							waitMaterialBomList = milkPowderFormulatorService.getWaitMaterialBomList(cacheTopBomLine);
							//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
							waitMaterialBeanList = milkPowderFormulatorService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//�����䷽��ȡָ���BOM
							waitIndexBomList = milkPowderFormulatorService.getWaitIndexBomList(cacheTopBomLine);
							//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
							waitIndexBeanList = milkPowderFormulatorService.getWaitIndexBeanList(waitIndexBomList);
							
							//��ȡ�����е���Ӽ�Bean   Ҫ������Ӽ���������ѡ�еķ��� 
							checkMaterialBeanList = milkPowderFormulatorService.getCheckMaterialBeanList(checkLawRevList);
							//��ȡ�����е�ָ��Bean�Ǵӷ���������˵  ��������ѡ�еĲ�Ʒ��׼
							checkIndexBeanList = milkPowderFormulatorService.getCheckIndexBeanList(checkLawRevList);
//							milkPowderFormulatorService.getCheckIndexBeanListByIndexStandard(checkIndexBeanList, formulatorIndexRev);
							 
							
							
							//�����Ӽ�  	��Ӽ��ķ���Ƚ����� ר������һ������  ����
							List<FormulatorCheckedBean> materialCheckedBean = milkPowderFormulatorService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
							//���ָ��
							List<FormulatorCheckedBean> indexCheckedBean = milkPowderFormulatorService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
							
							//д��excel��
							allCheckedBeanList = new ArrayList<>();
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							
							milkPowderFormulatorService.write2Excel(allCheckedBeanList);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
		
		
			//��ӻ���
			addWetFormulatorBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AddWetFormulatorFrame addWetFormulatorFrame = new AddWetFormulatorFrame(new AbstractCallBack() {
						@Override
						public void addWetFormulator(TCComponentItemRevision wetFormulator,String name) {
							super.addWetFormulator(wetFormulator,name);
							
							try {
								TCComponentItemRevision selectRev = wetFormulator;
								MaterialBean  materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, selectRev);
								materialBean.productMethod = Wet_Method;//���Ϊʪ�� ��Ϊ�ǻ���
								
								formulatorList.add(selectRev);
								formulatorNameList.add(name);
								formulatorMaterialBeanList.add(materialBean);
								
								formulatorTableModel.addRow(new String[]{name,"","","",Wet_Method});//��ӵ��䷽���� ����Ĭ����ʪ��
								computeSumInventory();
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
					addWetFormulatorFrame.setVisible(true);
				}
			});
		
		
			//�ɷ������䷽��˵
			creatDryFormulatorBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//���ﲻ���ж�ֱ�Ӵ�������  ����ʪ���ĵ����䷽��˵��
							milkPowderFormulatorService.createDryFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList);
							MessageBox.post("OK","",MessageBox.INFORMATION);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
			
			
			//���������䷽��
			productExcelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							milkPowderFormulatorService.createFormulatorExcel(itemRevision);
							
							progressBarDialog.stop();
							
						}
					}).start();
				}
			});
			
			
			//����Ӫ������Ϣ��
			nutritionExcelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							milkPowderFormulatorService.createNutritionExcel(itemRevision);
							progressBarDialog.stop();
							
						}
					}).start();	
				}
			});
			
			
			//����Ӫ���ɷֱ�
			nutritionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progerBarDialog = new ProgressBarDialog();
							progerBarDialog.start();
							
							
							//�䷽�����е�ָ����Ŀ
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							
							//��ȡ����������ļ���õ����յ�ָ��ļ���
							List<IndexItemBean> finallIndexBeanList = milkPowderFormulatorService.getFinallIndexBeanList(cacheTopBomLine,
									wetLossItemRevsion, dryLossItemRevsion, dateLossItemRevsion);
							
							
							
							
							//�����е�ָ��д��Ӫ���ɷֱ��  
							milkPowderFormulatorService.createNutritionIndexExcel(finallIndexBeanList,itemRevision);
							
							
							cacheTopBomLine.clearCache();
							TCComponentBOMWindow bomWindow = cacheTopBomLine.getCachedWindow();
							try {
								bomWindow.save();
								bomWindow.close();
							} catch (TCException e) {
								progerBarDialog.stop();
								e.printStackTrace();
							}
							
							progerBarDialog.stop();
						}
					}).start();
					
					
				}
				
			});
	
		}
		
	}



	/**
	 * ����ϼ�ֵ
	 */
	protected void computeSumInventory() {
		Double sumInventory = 0d;
		int rowCount = formulatorTable.getRowCount();
		for(int i=0;i<rowCount;i++){
			Double inventory = StringsUtil.convertStr2Double(formulatorTableModel.getValueAt(i, 1).toString());
			sumInventory += inventory;
		}
		inventorySumEdt.setText(sumInventory+"");
	}



	/**
	 * @param finalIndexBeanList
	 * �������ֵȥ����ָ����Ŀ�е����ߺ�����
	 */
	private void computeIndexBeanByLoss(List<IndexItemBean> finalIndexBeanList) {
		for(IndexItemBean indexItemBean : finalIndexBeanList){
			Double u8Loss = StringsUtil.convertStr2Double(indexItemBean.u8Loss);
			Double quantity = StringsUtil.convertStr2Double(indexItemBean.bl_quantity);
			Double inventory = StringsUtil.convertStr2Double(indexItemBean.U8_inventory);
			quantity = quantity * (1-u8Loss);
			inventory = inventory * (1-u8Loss);
			indexItemBean.bl_quantity = quantity+"";
			indexItemBean.U8_inventory = inventory+"";
		}
	}

	/**
	 * ��ʼ������
	 * 1�����ģʽ�ĳ�ʼ��
	 * 2���䷽���ĳ�ʼ��
	 */
	private void initFrame() {
		//��ʼ����ĵ�˵��
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LOSSITEM.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����"}, new String[]{"*"});
		lossNameList = new ArrayList<String>();
		lossItemRevList = new ArrayList<TCComponentItemRevision>();
		for(TCComponent component : searchResult){
			if(component instanceof TCComponentItem){
				TCComponentItem lossItem  = (TCComponentItem) component;
				try {
					String lossItemName = lossItem.getProperty("object_name");
					TCComponentItemRevision lossItemRev = lossItem.getLatestItemRevision();
					if(lossItemRev == null){
						continue;
					}
					
					lossNameList.add(lossItemName);
					lossItemRevList.add(lossItemRev);
					wetLossCombox.addItem(lossItemName);
					dryLossCombox.addItem(lossItemName);
					dateLossCombox.addItem(lossItemName);
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		//���һ���յ�ѡ��
		lossNameList.add(" ");
		wetLossCombox.addItem(" ");
		dryLossCombox.addItem(" ");
		dateLossCombox.addItem(" ");
		lossItemRevList.add(null);
		
		if(lossItemRevList.size()>0){//�������Ķ���Ļ� Ĭ��ѡ�е�һ��
			wetLossItemRevsion = lossItemRevList.get(0);
			dryLossItemRevsion = lossItemRevList.get(0);
			dateLossItemRevsion = lossItemRevList.get(0);
		}
		
		
		//�䷽���Combox�ĳ�ʼ��
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		List<String> formulatorTypeList = LOVUtil.getLovValues(session, Const.MilkPowderFormulator.FORMULATOR_TYPE_LOV);
		for(String type : formulatorTypeList){
			typeCombox.addItem(type);
		}
		
		if(formulatorTypeList.size()>0){//��ʼ��Ĭ��ֵ
			formulatorType = formulatorTypeList.get(0);
		}
		
		//��ʼ���䷽table����
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.CommonCosnt.BOM_VIEW_NAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
		}
		
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				TCComponentItemRevision materailItemRev = bomLine.getItemRevision();
				formulatorList.add(materailItemRev); 
				formulatorNameList.add(bean.objectName);
				formulatorMaterialBeanList.add(bean);
			}
			
			
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				formulatorTableModel.removeRow(0);
			}
			
			for(MaterialBean bean : formulatorMaterialBeanList){
				formulatorTableModel.addRow(new String[]{bean.objectName,bean.U8_inventory,"","",bean.productMethod});
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
