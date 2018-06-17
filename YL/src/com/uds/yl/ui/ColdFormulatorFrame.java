package com.uds.yl.ui;

//�����䷽���

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;


import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;










import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IColdFormulatorService;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.impl.ColdFormulatorServiceImpl;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.utils.DoubleUtil;
import com.uds.yl.utils.StringsUtil;



























import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;







import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author infodba
 * 
 * bl_quantity ����  ָ����ڵ�ֵ��˵ ���絰���ʵ�����10mg/100g
 *
 */
public class ColdFormulatorFrame extends JFrame {

	
	public static boolean isShow = false;
	
	private IColdFormulatorService iColdFormulatorService = new ColdFormulatorServiceImpl();
	
	private JPanel contentPane;
	private JTextField formulatorNameText;
	private JTextField formulatorRevText;
	
	private JTable componentTable;
	private JTextField componentNameText;//��ֵ�����
	private JTextField materialSum0;//Ͷ�����ϼ�
	private JTextField materialSum1;//��Ⱥϼ�
	private JTextField materialSum2;//��λ�ɱ��ϼ�
	
	private JTable formulatorTable;
	private JTextField componentSumText0;//Ͷ�����ϼ�
	private JTextField componentSumText1;//��Ⱥϼ�
	private JTextField componentSumText2;//��λ�ɱ��ϼ�
	private JTextField componentSumText3;//�����ϼ�
	private JTextField componentSumText4;//�����ʺϼ�
	private JTextField componentSumText5;//֬���ϼ�
	private JTextField componentSumText6;//̼ˮ������ϼ�
	private JTextField componentSumText7;//�ƺϼ�
	private JTextField componentSumText8;//��ʽ֬����ϼ�
	private JTextField componentSumText9;
	private JTextField componentSumText10;
	private JTextField componentSumText11;
	private JTextField componentSumText12;
	
	private JTextField supplementEdt;//������ʾ��ǰ��ֵĲ����ֵ�Ƕ���
	
	private TCComponentItemRevision formulatorRev;//ѡ�е��䷽�汾
	
	private List<ComponenetBean> mComponenetBeansList;//�䷽�е����ʵ���м���
	private List<ComponentBom> mComponentBomsList;//�䷽�е����BOM����
	
	private ComponenetBean currentComponentBean;//��ǰѡ�е����Bean����
	private ComponentBom currentComponentBom;//��ǰѡ�е���ֵ�BOMʵ����

	private JButton showBtn;//�鿴һ�����
	private JButton newComponentBtn;//����һ����� ����ֵ�table��
	private JButton deleteComponentBtn;//ɾ��һ�����

	private JButton addMaterialBtn;//����������һ��ԭ��
	private JButton deleteMaterialBtn;//�������ɾ��һ��ԭ��
	private JButton addComponentBtn;//���������������ֶ�����ӵ��䷽��  Ҫ������Щ�Ƿ����½��Ļ������Ѿ����ڵ�
	private JButton supplementBtn;//�����ڴ��ֵ�ʱ����в�ˮ�Ĺ���  ˮ�е�Na����Ϊ20mg/100g
	
	private JButton createBtn;//����
	private JButton labelComputeBtn;//��ǩ����
	private JButton physicalAndChemicalBtn;//����ǩ
	private JButton clearBtn;//��ʱ��Ϊ����䷽�Ľṹ  ����list������գ����������
	private JButton cancleBtn;//�䷽���ȡ��
	
	private JButton searchLawBtn;//��������
	private JTextField lawRevIDEdt;//���������
	private TCComponentItemRevision lawRevsion = null;//���Ϲ��Լ��ķ���
	
	private JButton lawCheckBtn;//����Ա�
	
	private List<TCComponentItemRevision> checkLawRevList;//��Ϊ�洢�ȴ����ķ�������
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//�ȴ�������Ӽ�Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//�ȴ�����ָ��Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//�ȴ�������Ӽ�Bom��Ӧ��pojo
	private List<IndexItemBean> waitIndexBeanList = null;//�ȴ�����ָ��Bom��Ӧ��pojo
	
	private List<MaterialBean> checkMaterialBeanList = null;//��Ӽ�����Bom��Ӧ��pojo
	private List<IndexItemBean> checkIndexBeanList = null;//ָ�귨��Bom��Ӧ��pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//������Ҫд��excel������
	
	private ProgressBarDialog progressBarDialog;//������
	
	private JLabel componentSumLabel0;
	private JLabel componentSumLabel1;
	private JLabel componentSumLabel2;
	private JLabel componentSumLabel3;
	private JLabel componentSumLabel4;
	private JLabel componentSumLabel5;
	private JLabel componentSumLabel6;
	private JLabel componentSumLabel7;
	private JLabel componentSumLabel8;
	private JLabel componentSumLabel9;
	private JLabel componentSumLabel10;
	private JLabel componentSumLabel11;
	private JLabel componentSumLabel12;
	
	private DefaultTableModel formulatorTableModel;//�䷽��
	private DefaultTableModel componentTableModel;//��ֱ�
	
	private String[] indexNameArray = new String[]{"����","֬��","̼ˮ������","��","��ʽ֬����","������"};

	private boolean isNutritionFlag = true;//Ĭ���Ǽ����Ӫ����ǩָ��



	/**
	 * Create the frame.
	 */
	public ColdFormulatorFrame(TCComponentItemRevision rev) {
		
		super("�����䷽������");
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				isShow = false;
				if(progressBarDialog.isLive()){
					progressBarDialog.stop();
				}
			}
		});
		
		
		{
			isShow = true;
			setResizable(false);
			this.formulatorRev = rev;
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 1405, 738);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblNewLabel = new JLabel("�䷽��");
			lblNewLabel.setBounds(47, 10, 54, 15);
			contentPane.add(lblNewLabel);

			formulatorNameText = new JTextField();
			formulatorNameText.setBounds(117, 7, 109, 21);
			contentPane.add(formulatorNameText);
			formulatorNameText.setColumns(10);

			JLabel label = new JLabel("�汾��");
			label.setBounds(312, 13, 54, 15);
			contentPane.add(label);

			formulatorRevText = new JTextField();
			formulatorRevText.setColumns(10);
			formulatorRevText.setBounds(376, 7, 109, 21);
			contentPane.add(formulatorRevText);

			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(36, 56, 1238, 212);
			contentPane.add(panel);
			panel.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(26, 38, 754, 128);
			panel.add(scrollPane);

			componentTable = new JTable();
			componentTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "ԭ����", "Ͷ����(kg)", "���%", "��λ�ɱ�(Ԫ/kg)","С������" }));
			componentTable.getColumnModel().getColumn(1).setPreferredWidth(93);
			componentTable.getColumnModel().getColumn(2).setPreferredWidth(56);
			componentTable.getColumnModel().getColumn(3).setPreferredWidth(116);
			scrollPane.setViewportView(componentTable);

			JLabel label_1 = new JLabel("��֣�");
			label_1.setBounds(133, 13, 54, 15);
			panel.add(label_1);

			componentNameText = new JTextField();
			componentNameText.setColumns(10);
			componentNameText.setBounds(203, 10, 109, 21);
			panel.add(componentNameText);

			addMaterialBtn = new JButton("���ԭ��");
			addMaterialBtn.setBounds(929, 38, 132, 23);
			panel.add(addMaterialBtn);

			deleteMaterialBtn = new JButton("ɾ��ԭ��");
			deleteMaterialBtn.setBounds(929, 96, 132, 23);
			panel.add(deleteMaterialBtn);

			addComponentBtn = new JButton("ȷ��");
			addComponentBtn.setBounds(929, 140, 132, 23);
			panel.add(addComponentBtn);

			supplementBtn = new JButton("����");
			supplementBtn.setBounds(929, 179, 100, 23);
			panel.add(supplementBtn);

			supplementEdt = new JTextField();
			supplementEdt.setColumns(10);
			supplementEdt.setBounds(1062, 181, 100, 21);
			panel.add(supplementEdt);
			supplementEdt.setEditable(false);

			JLabel label_2 = new JLabel("�ϼƣ�");
			label_2.setBounds(87, 176, 54, 15);
			panel.add(label_2);

			materialSum0 = new JTextField();
			materialSum0.setColumns(10);
			materialSum0.setBounds(180, 176, 132, 21);
			panel.add(materialSum0);

			materialSum1 = new JTextField();
			materialSum1.setColumns(10);
			materialSum1.setBounds(416, 176, 115, 21);
			panel.add(materialSum1);

			materialSum2 = new JTextField();
			materialSum2.setColumns(10);
			materialSum2.setBounds(585, 180, 122, 21);
			panel.add(materialSum2);

			JPanel componentSumLabelTwo = new JPanel();
			componentSumLabelTwo.setBorder(new LineBorder(new Color(0, 0, 0)));
			componentSumLabelTwo.setBounds(47, 316, 1328, 329);
			contentPane.add(componentSumLabelTwo);
			componentSumLabelTwo.setLayout(null);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(10, 29, 1205, 204);
			componentSumLabelTwo.add(scrollPane_1);

			formulatorTable = new JTable() {
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column == 1) {// ֻ�е�һ�п��Ա༭
						return true;
					}
					return false;
				}
			};
			formulatorTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "�����", "Ͷ����(��)", "���%", "��λ�ɱ�(Ԫ/ǧ��)"
					// "����/KJ", "������%", "֬��%", "̼ˮ������%", "��/mg",
					// "��ʽ֬����/g","������/g"
			}));
			formulatorTable.getColumnModel().getColumn(3).setPreferredWidth(118);
			// formulatorTable.getColumnModel().getColumn(7).setPreferredWidth(93);
			// formulatorTable.getColumnModel().getColumn(8).setPreferredWidth(63);
			// formulatorTable.getColumnModel().getColumn(9).setPreferredWidth(92);
			scrollPane_1.setViewportView(formulatorTable);

			JLabel label_4 = new JLabel("�ϼƣ�");
			label_4.setBounds(20, 301, 54, 15);
			componentSumLabelTwo.add(label_4);

			componentSumText0 = new JTextField();
			componentSumText0.setColumns(10);
			componentSumText0.setBounds(85, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText0);

			componentSumText1 = new JTextField();
			componentSumText1.setColumns(10);
			componentSumText1.setBounds(160, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText1);

			componentSumText2 = new JTextField();
			componentSumText2.setColumns(10);
			componentSumText2.setBounds(248, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText2);

			componentSumText3 = new JTextField();
			componentSumText3.setColumns(10);
			componentSumText3.setBounds(347, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText3);

			componentSumText4 = new JTextField();
			componentSumText4.setColumns(10);
			componentSumText4.setBounds(436, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText4);

			componentSumText5 = new JTextField();
			componentSumText5.setColumns(10);
			componentSumText5.setBounds(535, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText5);

			componentSumText6 = new JTextField();
			componentSumText6.setColumns(10);
			componentSumText6.setBounds(628, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText6);

			componentSumText7 = new JTextField();
			componentSumText7.setColumns(10);
			componentSumText7.setBounds(722, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText7);

			componentSumText8 = new JTextField();
			componentSumText8.setColumns(10);
			componentSumText8.setBounds(814, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText8);

			showBtn = new JButton("�鿴");
			showBtn.setBounds(1225, 73, 93, 23);
			componentSumLabelTwo.add(showBtn);

			newComponentBtn = new JButton("����");
			newComponentBtn.setBounds(1225, 115, 93, 23);
			componentSumLabelTwo.add(newComponentBtn);

			deleteComponentBtn = new JButton("ɾ��");
			deleteComponentBtn.setBounds(1225, 148, 93, 23);
			componentSumLabelTwo.add(deleteComponentBtn);

			componentSumText9 = new JTextField();
			componentSumText9.setColumns(10);
			componentSumText9.setBounds(896, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText9);

			componentSumText10 = new JTextField();
			componentSumText10.setColumns(10);
			componentSumText10.setBounds(984, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText10);

			componentSumText11 = new JTextField();
			componentSumText11.setColumns(10);
			componentSumText11.setBounds(1066, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText11);

			componentSumText12 = new JTextField();
			componentSumText12.setColumns(10);
			componentSumText12.setBounds(1151, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText12);

			componentSumLabel0 = new JLabel("Ͷ����");
			componentSumLabel0.setBounds(85, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel0);

			componentSumLabel1 = new JLabel("���");
			componentSumLabel1.setBounds(160, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel1);

			componentSumLabel2 = new JLabel("��λ�ɱ�");
			componentSumLabel2.setBounds(248, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel2);

			componentSumLabel3 = new JLabel("");
			componentSumLabel3.setBounds(347, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel3);

			componentSumLabel4 = new JLabel("");
			componentSumLabel4.setBounds(436, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel4);

			componentSumLabel5 = new JLabel("");
			componentSumLabel5.setBounds(535, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel5);

			componentSumLabel6 = new JLabel("");
			componentSumLabel6.setBounds(628, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel6);

			componentSumLabel7 = new JLabel("");
			componentSumLabel7.setBounds(722, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel7);

			componentSumLabel8 = new JLabel("");
			componentSumLabel8.setBounds(814, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel8);

			componentSumLabel9 = new JLabel("");
			componentSumLabel9.setBounds(896, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel9);

			componentSumLabel10 = new JLabel("");
			componentSumLabel10.setBounds(984, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel10);

			componentSumLabel11 = new JLabel("");
			componentSumLabel11.setBounds(1066, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel11);

			componentSumLabel12 = new JLabel("");
			componentSumLabel12.setBounds(1151, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel12);

			JLabel label_3 = new JLabel("�����䷽");
			label_3.setBounds(60, 291, 83, 15);
			contentPane.add(label_3);

			createBtn = new JButton("����");
			createBtn.setBounds(171, 667, 93, 23);
			contentPane.add(createBtn);

			labelComputeBtn = new JButton("��ǩ����");
			labelComputeBtn.setBounds(311, 667, 93, 23);
			contentPane.add(labelComputeBtn);

			physicalAndChemicalBtn = new JButton("��ָ��");
			physicalAndChemicalBtn.setBounds(445, 667, 93, 23);
			contentPane.add(physicalAndChemicalBtn);

			clearBtn = new JButton("���");
			clearBtn.setBounds(741, 667, 93, 23);
			contentPane.add(clearBtn);
			
			cancleBtn = new JButton("ȡ��");
			cancleBtn.setBounds(880, 667, 93, 23);
			contentPane.add(cancleBtn);
			
			JLabel label_5 = new JLabel("����");
			label_5.setBounds(629, 10, 54, 15);
			contentPane.add(label_5);
			
			lawRevIDEdt = new JTextField();
			lawRevIDEdt.setColumns(10);
			lawRevIDEdt.setBounds(674, 7, 160, 21);
			contentPane.add(lawRevIDEdt);
			lawRevIDEdt.setEditable(false);
			
			searchLawBtn = new JButton("��ӷ���");
			searchLawBtn.setBounds(892, 6, 132, 23);
			contentPane.add(searchLawBtn);
			
			lawCheckBtn = new JButton("����Ա�");
			lawCheckBtn.setBounds(577, 667, 93, 23);
			contentPane.add(lawCheckBtn);
			
		}
		
		
		
		
		{//��ʼ��Name�Ͱ汾
			try {
				
				formulatorTableModel = (DefaultTableModel) formulatorTable.getModel();
				componentTableModel = (DefaultTableModel) componentTable.getModel();
				
				String name = formulatorRev.getProperty("object_name");
				String revId = formulatorRev.getProperty("item_revision_id");
				formulatorNameText.setText(name);formulatorNameText.setEditable(false);
				formulatorRevText.setText(revId);formulatorRevText.setEditable(false);
				
				//��������Լ�һЩ�����ĺϼƱ༭������Ϊ���ɱ༭
				componentNameText.setEditable(false);
				componentSumText0.setEditable(false);componentSumText1.setEditable(false);
				componentSumText2.setEditable(false);componentSumText3.setEditable(false);
				componentSumText4.setEditable(false);componentSumText5.setEditable(false);
				componentSumText6.setEditable(false);componentSumText7.setEditable(false);
				componentSumText8.setEditable(false);componentSumText9.setEditable(false);
				componentSumText10.setEditable(false);componentSumText11.setEditable(false);
				componentSumText12.setEditable(false);
				materialSum0.setEditable(false);materialSum1.setEditable(false);materialSum2.setEditable(false);
				
				
				//�䷽��ļ����¼�  �����䷽�е������˵����
				formulatorTableModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						int row = e.getFirstRow();
						int column = e.getColumn();
						if(column !=1){
							return ;
						}
						if(column==1){//Ͷ�����Ļ�
							refreshFormualtorTable();
						}
					}
				});
				
				//����ֱ��е��������и���
				componentTableModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						//���˵�һ���е�ֵ������лָ�
						int row = e.getFirstRow();
						int column = e.getColumn();
						if(column==1 || column==4){
							refreshCurrentComponentTable();
						}
					}
				});
				
				
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			
			
			progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
				@Override
				public void run() {
					
					//��ȡ���BOM����Ϣ
					mComponentBomsList = iColdFormulatorService.getComponentBomLineList(formulatorRev);
					mComponenetBeansList = iColdFormulatorService.getComponentBeanList(formulatorRev);
					
					//��ʼ���䷽�����Ϣ   --�ȳ�ʼ���䷽��Ȼ���ȡ��ǰѡ�е���ֶ��� Ĭ���ǵ�һ����
					initFormulaTable();
					
					//��ʼ��ֻ��Ĭ��ѡ�е��ǵ�һ�����
					currentComponentBean = mComponenetBeansList.size()==0 ? null : mComponenetBeansList.get(0);
					currentComponentBom = mComponentBomsList.size()==0 ? null : mComponentBomsList.get(0);
					//��ֱ�Ĭ���ǵ�һ����ֵ���Ϣ
					initComponentTable();
					
					//��Ӫ��ָ����Ŀ��label��text��������
					setComponentSumTextAndLabelInvisiable();
					
					//�����ԭ�ϵ�Ͷ�������ۺϼ���
					Double currentComponentSum = computeSumCurrentComponent();
					
					//ˢ�������ԭ�ϵı���
					int rowCount = componentTable.getRowCount();
					DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
					for(int i=0;i<rowCount;i++){
						String materialName = model.getValueAt(i,0).toString();
						for(MaterialBean materialBean : currentComponentBean.childBeanList){
							if(materialName.equals(materialBean.objectName)){
								Double blQuantity = StringsUtil.convertStr2Double(materialBean.U8_inventory) / currentComponentSum * 100;
								materialBean.bl_quantity = DoubleUtil.formatNumber(blQuantity);
								model.setValueAt(materialBean.bl_quantity,i, 2);
							}
						}
					}
					progressBarDialog.stop();
				}
			}));
			progressBarDialog.start();
			
			
			
			
		}
		
		//����������ԭ��
		addMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//��ѯ��ѡ��ԭ�Ϻ�Ļص����� 
				AbstractCallBack callBack = new AbstractCallBack() {
					@Override
					public void setMaterialItem(TCComponentItemRevision materialItemRev) {
						super.setMaterialItem(materialItemRev);
						
						//TODO:ȷ�����֮ǰ��Ҫ�ж��Ƿ��Ѿ����ڸø�ԭ��
						
						try {
							//��Bean��ӵ����ʵ������
							MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, materialItemRev);
							currentComponentBean.childBeanList.add(materialBean);
							
							DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
							model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});
							
							//��Bom��ӵ����Bomʵ������
							TCComponentBOMLine bomLine  = null;
							if(isNutritionFlag){
								 bomLine = BomUtil.getTopBomLine(materialItemRev, "����Ӫ����ǩ");
							}else {
								 bomLine = BomUtil.getTopBomLine(materialItemRev, "��ͼ");
							}
							
							if(bomLine==null){
								bomLine = BomUtil.setBOMViewForItemRev(materialItemRev);
							}
							currentComponentBom.childBomList.add(bomLine);
							
							
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
						
						
					}
				};
				
				SearchMaterialFrame searchMaterialFrame = new SearchMaterialFrame(callBack);
				searchMaterialFrame.setVisible(true);
			}
		});
	
	
		//��ӷ���
		searchLawBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchLawFrame searchLawFrame = new SearchLawFrame(new AbstractCallBack() {
					@Override
					public void setLawRev(TCComponentItemRevision lawItemRev) {
						super.setLawRev(lawItemRev);
						lawRevsion = lawItemRev;
						try {
							String lawName = lawItemRev.getProperty("object_name");
							lawRevIDEdt.setText(lawName);
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				});
				searchLawFrame.setVisible(true);
			}
		});
		
		//����Ա�
		lawCheckBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						if(lawRevsion==null){//�������ûѡ��Ļ�����ʾ
							MessageBox.post("��ѡ���䷽��Ӧ�Ĳ�Ʒ����","",MessageBox.INFORMATION);
							progressBarDialog.stop();
							return;
						}
						
						//���滻Ϊ��ָ��
						resetPhysicalAndChemicalIndexBom();
						
						
						//��������Ϊ��ʱ���䷽�����˵  ��Ҫ���ݲ�ͬ�Ľṹ ������ԭ��Ͷ����д��ȥ
						TCComponentBOMLine cacheTopBomLine = iColdFormulatorService.getCacheTopBomLine(mComponentBomsList, mComponenetBeansList);
						
						checkLawRevList = iFormulatorLegalCheckService.getRelatedIDLaws(lawRevsion);
						
						
						//�����䷽��ȡԭ�ϵ�BOM
						waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
						//����ԭ�ϵ�BOM��ȡ��Ӧ��Bean
						waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
						
						//�����䷽��ȡָ���BOM
//						waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
						//���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
						waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
						
						//���е�ָ���е���Ҫ�����䷽���ܵĺ��� 
						Double sumInventory = 0d;
						for(int i=0;i<mComponenetBeansList.size();i++){
							Double inventory = StringsUtil.convertStr2Double(mComponenetBeansList.get(i).component.U8_inventory);
							sumInventory += inventory;
						}
						for(int i=0;i<waitIndexBeanList.size();i++){
							Double indexInventory = StringsUtil.convertStr2Double(waitIndexBeanList.get(i).U8_inventory);
							indexInventory = indexInventory/sumInventory;
							waitIndexBeanList.get(i).U8_inventory = indexInventory+"";
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
						allCheckedBeanList.addAll(materialCheckedBean);
						allCheckedBeanList.addAll(indexCheckedBean);
						iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
						
						//������е����ݻ���ȥ
						if(isNutritionFlag){
							resetNutririonIndexBom();
						}else{
							resetPhysicalAndChemicalIndexBom();
						}
						
						progressBarDialog.stop();
						
					}
				}));
				progressBarDialog.start();
				
				
				
			}
		});
		
		//ɾ��ԭ��
		deleteMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel modle = (DefaultTableModel) componentTable.getModel();
				int selectIndex = componentTable.getSelectedRow();
				if(selectIndex==-1){//��ѡ��ԭ��
					MessageBox.post("��ѡ��Ҫɾ����ԭ��","",MessageBox.INFORMATION);
					return;
				}
				
				//����ֽ�������ɾ������Ҫɾ����ԭ��
				modle.removeRow(selectIndex);
			
				//ɾ��ѡ�е�ԭ�϶���
				currentComponentBean.childBeanList.remove(selectIndex);
				currentComponentBom.childBomList.remove(selectIndex);
				
			}
		});
		
		
		
		//����������ֱ��е���ֶ�����ӵ��䷽��ȥ �� �䷽�����ݶ��󼯺���ȥ
		addComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				//�����ǰ���Ϊ��   Ҳ����ͨ��componentNameText�ж�
				if("".equals(componentNameText.getText())){
					MessageBox.post("���������Ϣ","",MessageBox.INFORMATION);
					return;
				}
				boolean exitComponent = false;//Ĭ�ϲ�����
				for(ComponenetBean componenetBean : mComponenetBeansList){
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if(componentName.equals(currentComponentName)){//��ǰ��������䷽������
						exitComponent = true;
						break;
					}
				}
				if(exitComponent){//��ִ���
					//TODO:����������䷽���е���Ϣ    ����ֶ�����Ϣ����list�����еĲ����޸�
				}else{
					//���������ӵ��䷽���е�һ�� Ȼ�󽫸���ֶ����ŵ���Ӧ��list������ȥ
					mComponenetBeansList.add(currentComponentBean);
					mComponentBomsList.add(currentComponentBom);
					
					DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
					//TODO:�����䷽���еĴ��������Ϣ��һ��
					model.addRow(new String[]{currentComponentBean.component.objectName});
					
				}
			}
		});
	
		
		//չʾѡ�е�������
		showBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				
				boolean componentExit = false;
				// �����жϵ���ǰ����Ƿ��Ѿ������˵
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if (currentComponentName.equals(componentName)) {// �ҵ����������
						componentExit = true;
						break;
					}
				}
				
				
				//Ҫ�����ж��Ƿ���ɾ���ĺ�ǰ���Ϊ����  ͨ���ж�componentNameText���������Ƿ�Ϊ��
				if("".equals(componentNameText.getText())){//Ϊ�վͲ��ô����
					componentExit=true;//�Ȱ��մ��ڴ���
				}
				
				if (!componentExit) {// ��������ھ���ʾҪ����
					MessageBox.post("�뱣�浱ǰ�����Ϣ", "", MessageBox.INFORMATION);
					return;
				}
				//��ǰ�����Ϣ�Ѿ��ڼ�����Ҳ���䷽������� �������û�ѡ��չʾ��
				int selectComponentIndex = formulatorTable.getSelectedRow();//�䷽����ѡ�е���ֵ��±�
				if(selectComponentIndex==-1){//ûѡ
					MessageBox.post("��ѡ��չʾ�����","",MessageBox.INFORMATION);
					return;
				}
				
				currentComponentBean = mComponenetBeansList.get(selectComponentIndex);
				currentComponentBom = mComponentBomsList.get(selectComponentIndex);
				
				componentNameText.setText(currentComponentBean.component.objectName);//��ʼ����ֱ� componentTable
				supplementEdt.setText(currentComponentBean.complementContent);//������ֲ���ı༭��
				
				DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
				int rowCount = model.getRowCount();
				for(int i=0;i<rowCount;i++){
					model.removeRow(0);//�����ֱ�
				}
				for(int i=0;i<currentComponentBean.childBeanList.size();i++){
					MaterialBean materialBean = currentComponentBean.childBeanList.get(i);
					model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});//��ʼ��ѡ�е����
				}
				
				refreshCurrentComponentTable();
			}
		});
		
		
		//�½�һ����ֶ���
		newComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean componentExit = false;
				// �����жϵ���ǰ����Ƿ��Ѿ������˵
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if (currentComponentName.equals(componentName)) {// �ҵ����������
						componentExit = true;
						break;
					}
				}
				
				//Ҫ�����ж��Ƿ���ɾ���ĺ�ǰ���Ϊ����  ͨ���ж�componentNameText���������Ƿ�Ϊ��
				if("".equals(componentNameText.getText())){//Ϊ�վͲ��ô����
					componentExit=true;//�Ȱ��մ��ڴ���
				}
				
				if (!componentExit) {// ��������ھ���ʾҪ����
					MessageBox.post("�뱣�浱ǰ�����Ϣ", "", MessageBox.INFORMATION);
					return;
				}

				String result = JOptionPane.showInputDialog("������������ƣ�");
				if (result == null) {// ˵����ȡ��
					return;
				}
				if ("".equals(result)) {// Ϊ��˵����û�������ַ����ȷ��
					MessageBox.post("��������ֵ�����", "", MessageBox.INFORMATION);
					return;
				}
				// ����Ǵ����� �����ж��������ֵ������ǻ���Ѿ�����
				componentExit = false;
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					if (result.equals(componentName)) {// �ҵ����������
						componentExit = true;
						break;
					}
				}
				if (componentExit) {// ����Ѿ�����
					MessageBox.post("����Ѿ��������������룡", "", MessageBox.INFORMATION);
					return;
				} else {

					// ��ʼ��һ���µ����
					currentComponentBean = new ComponenetBean();
					currentComponentBean.component = new MaterialBean();
					currentComponentBean.component.objectName = result;
					currentComponentBean.childBeanList = new ArrayList<>();

					// ����һ��ԭ�϶�����Ϊ��� ������BOM
					TCComponentItem materialItem = ItemUtil.createtItem("U8_Material", result, "");
					TCComponentItemRevision materialRevision = null;
					try {
						AnnotationFactory.setObjectInTC(new MaterialBean(), materialItem);//���һ������
						materialRevision = materialItem.getLatestItemRevision();
					} catch (TCException | InstantiationException | IllegalAccessException e1) {
						e1.printStackTrace();
					}
					TCComponentBOMLine topBomLine = BomUtil.setBOMViewForItemRev(materialRevision);// �´����Ķ���϶��ǿյ�

					currentComponentBom = new ComponentBom();
					currentComponentBom.componentBOMLine = topBomLine;
					currentComponentBom.childBomList = new ArrayList<>();

					componentNameText.setText(result);// ������������ֵ���ʾ
					supplementEdt.setText("");//����༭����Ϊ��
					// ��յ�ǰ��ֱ��е�����
					DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
					int rowCount = model.getRowCount();
					for (int i = 0; i < rowCount; i++) {
						model.removeRow(0);//
					}

				}
			}
		});
				
		
		//���䷽��ɾ��һ����ֶ���
		deleteComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				//ѡ�е�����Ƿ��Ѿ���չʾ��
				DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
				int selectedRow = formulatorTable.getSelectedRow();
				if(selectedRow==-1){//ûѡ
					MessageBox.post("��ѡ��Ҫɾ�������","",MessageBox.INFORMATION);
					return;
				}
				
				//�������չʾ�� ֱ�ӽ���ֱ����Ϊ�� ��Ҫ���䷽����list��ɾ��
				ComponenetBean selectComponent = mComponenetBeansList.get(selectedRow);//ѡ�е����
				if(selectComponent.component.objectName.equals(componentNameText.getText())){
					//����չʾ
					componentNameText.setText("");
					model = (DefaultTableModel) componentTable.getModel();
					int rowCount = model.getRowCount();
					for(int i=0;i<rowCount;i++){
						model.removeRow(0);//�����ֱ����Ϣ
					}
					
				}
				//���䷽����list��ɾ��
				model = (DefaultTableModel) formulatorTable.getModel();
				model.removeRow(selectedRow);
				mComponenetBeansList.remove(selectedRow);
				mComponentBomsList.remove(selectedRow);
				
			}
		});
	
		//���㰴ť
		supplementBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type = currentComponentBean.complementType;
				String content = currentComponentBean.complementContent;
				ComplementFrame complementFrame = new ComplementFrame(type, content, new AbstractCallBack() {
					@Override//���ò������Ϣ��
					public void setCompelemnet(String complementType, String complementContent) {
						super.setCompelemnet(complementType, complementContent);
						if(StringsUtil.isEmpty(complementType)&&StringsUtil.isEmpty(complementContent)){//���Ͳ���Ϊ��
							MessageBox.post("��������������������������д","",MessageBox.INFORMATION);
							return;
						}
						currentComponentBean.complementContent = complementContent;
						currentComponentBean.complementType = complementType;
						supplementEdt.setText(complementContent);
						currentComponentBean.component.componentType = complementType;
						currentComponentBean.component.componentValue = complementContent;
						//���ݲ�����Ϣ�����µ�����
						refreshCurrentComponentTable();
					
					}
				});
				
				complementFrame.setVisible(true);
			}
		});
	
		
		//ȡ����ť
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if(progressBarDialog.isLive()){
					progressBarDialog.stop();
				}
				isShow = false;
				dispose();
				return;
			}
			
		});
	
	
		//��ǩ����  ����ǰ���ж�currentComponent�Ƿ��Ѿ������ڼ�������
		labelComputeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						if(!currentComponentExitInTable()){//��������ڵĻ���Ҫ���е�����ʾ
							MessageBox.post("�뱣�浱ǰչʾ�������Ϣ","",MessageBox.INFORMATION);
							return;
						}
						
						
						//�滻BOM
						resetNutririonIndexBom();
						
						isNutritionFlag = true;
						clearComponentColunms();
						//�����䷽�е�������ֵ�Ӫ��ָ��
						for(int i=0;i<mComponenetBeansList.size();i++){
							ComponenetBean componenetBean = mComponenetBeansList.get(i);
							ComponentBom componentBom = mComponentBomsList.get(i);
							try {
								lableCompute(componenetBean, componentBom);//����һ����ֵ���Ϣ
								
								updateFormulatorTable(componenetBean,componentBom);//����һ����ֵ���Ϣ 
								
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
						
						
						//��Ͷ�������ܺͽ��м���
						Double sumInventory = 0d;
						DefaultTableModel formulatorModel = (DefaultTableModel) formulatorTable.getModel();
						int rowCount = formulatorTable.getRowCount();
						for(int i=0;i<rowCount;i++){
							String componentName = formulatorModel.getValueAt(i, 0).toString();
							for(ComponenetBean componenetBean : mComponenetBeansList){
								if(componentName.equals(componenetBean.component.objectName)){
									//�ҵ���Ӧ�����
									String inventoty = formulatorModel.getValueAt(i, 1).toString()==null ? "":formulatorModel.getValueAt(i, 1).toString();
									componenetBean.component.U8_inventory = inventoty;
									sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
								}
							}
						}
						//�����µı���е�ֵ���м���  ---����е�ֵ�����Ѿ����¹���
						componentSumText0.setText(sumInventory+"");
						
						HashMap<String, String> indexSumMap = computeSumFormulatorTable();//���䷽��ָ����Ŀ�ͼ������
						
						setComponentSumText(indexSumMap);//�����������ָ�����ʾ�ڽ�����
						progressBarDialog.stop();
					}
				}));
				progressBarDialog.start();
				
				
			}
		});
	
		//�����䷽�����¼�
		createBtn.addActionListener(new ActionListener() {
//			topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						//�жϵ�ǰ������Ƿ��Ѿ�����
						if(!currentComponentExitInTable()){
							MessageBox.post("�뱣�����ڱ༭�����","",MessageBox.INFORMATION);
							return;
						}
						
						//���䷽�汾�����bom���
						TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "��ͼ");
						if(topBomLine==null){
							topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
						}
						try {
							AIFComponentContext[] children = topBomLine.getChildren();
							for(AIFComponentContext context : children){
								TCComponentBOMLine childBom = (TCComponentBOMLine) context.getComponent();
								childBom.cut();
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
						
						//�ȴ�����ֵ�bom��ͼ
						for(int i=0;i<mComponenetBeansList.size();i++){
							try {
								ComponenetBean componenetBean = mComponenetBeansList.get(i);
								ComponentBom componentBom = mComponentBomsList.get(i);
								TCComponentItemRevision componentRev = componentBom.componentBOMLine.getItemRevision();
								
								//��ӳɹ��� ������ͼչʾ��BOMLine��ֵ����ֵĶ����˵
								TCComponentBOMLine componentBOMLine = topBomLine.add(componentRev.getItem(), componentRev, null, false);
								AnnotationFactory.setObjectInTC(componenetBean.component, componentBOMLine);//�����BOM���и�ֵ
								componentBom.componentBOMLine = componentBOMLine;
								
								//���������BOMLine�µ�����ԭ��Ȼ�����ٽ����ʵ�����к���ԭ�϶�����ֵ��ȥ
								AIFComponentContext[] children = componentBOMLine.getChildren();
								for(AIFComponentContext context : children){
									TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
									bomLine.cut();
								}
								int size = componentBom.childBomList.size();
								for(int j=0;j<size;j++){
									//�����ӱ���
									TCComponentItemRevision materialRev = componentBom.childBomList.get(j).getItemRevision();
									TCComponentBOMLine materialBom = componentBOMLine.add(materialRev.getItem(), materialRev, null, false);
									AnnotationFactory.setObjectInTC(componenetBean.childBeanList.get(j), materialBom);//��ԭ��BOM��ֵ
									componentBom.childBomList.add(materialBom);
								}
								size = componentBom.childBomList.size();
								for(int j=0;j<size/2;j++){//���淭������������Ҫɾ������Ĳ���
									componentBom.childBomList.remove(0);//˳���ɾ��һ��ĸ���
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						//����bom
						try {
							topBomLine.refresh();
							TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
							bomWindow.refresh();
							bomWindow.save();
							bomWindow.close();
							
							formulatorRev.setProperty("object_desc", "PF");
							
							//���䷽�İ汾�µ�BOM�汾�µ�����������дһ�����ԣ����棩
							TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(formulatorRev);
							bomRevByItemRev.setProperty("object_desc", Const.BomViewType.FORMULATOR);
						} catch (TCException e) {
							e.printStackTrace();
						}
						MessageBox.post("OK","",MessageBox.INFORMATION);
						
						progressBarDialog.stop();
					}
				}));
				progressBarDialog.start();
				
			}
			
		});
	
		//��ָ��  ����е�ԭ��BOM��Ҫ��Ϊ
		//���� ��Ҫ����б��е�ԭ��BOM ��Bean����Ҫ�ı�
		physicalAndChemicalBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						if(currentComponentBom==null){
							return ;
						}
						
						//�滻BOM
						resetPhysicalAndChemicalIndexBom();
					
						
						isNutritionFlag = false;//ת��Ϊ��ָ��
						
						clearComponentColunms();//���ָ����
						
						//����
						try {
							for(int i=0;i<mComponenetBeansList.size();i++){
								ComponenetBean componenetBean = mComponenetBeansList.get(i);
								ComponentBom componentBom = mComponentBomsList.get(i);
								lableCompute(componenetBean, componentBom);
								updateFormulatorTable(componenetBean,componentBom);
							}
						} catch (TCException e1) {
							e1.printStackTrace();
						}
						
						setComponentSumTextAndLabelInvisiable();//��ָ�겻��Ҫ�͵Ľ��
						progressBarDialog.stop();
						
					}
				}));
				progressBarDialog.start();
				
				
			}
			
		});
		
		
	}
	
	
		
		
	
		/**
		 * ��ʼ�� ��ֵı����Ϣ
		 */
		private void initComponentTable() {
			if(currentComponentBean==null){
				return;//û�нṹ ��������
			}
			
			//��ֵ�����
			String componentName = currentComponentBean.component.objectName;
			componentNameText.setText(componentName);
			
			//��ֵ���ϸ��Ϣ
			DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
			for(MaterialBean materialBean : currentComponentBean.childBeanList){
				model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});
			}
			 
			//���ò��������
			supplementEdt.setText(currentComponentBean.complementContent==null?"":currentComponentBean.complementContent);
			
		}
		
		
		/**
		 * ��ʼ��  �䷽�����Ϣ
		 */
		private void initFormulaTable(){
			if(mComponenetBeansList==null||mComponenetBeansList.size()==0){
				return;//û�нṹ ��������
			}
			
			
			for(ComponenetBean bean:mComponenetBeansList){
				formulatorTableModel.addRow(new String[]{bean.component.objectName,bean.component.U8_inventory,bean.component.bl_quantity});
				bean.complementType = bean.component.componentType;
				bean.complementContent = bean.component.componentValue;
			}
			
			
			//�����ܵ�Ͷ���� Ȼ��������
			Double sumInventory = 0d;
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//�ҵ���Ӧ�����
						String inventoty = formulatorTableModel.getValueAt(i, 1).toString()==null ? "":formulatorTableModel.getValueAt(i, 1).toString();
						componenetBeanTemp.component.U8_inventory = inventoty;
						sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
					}
				}
			}
			
			//����������
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//�ҵ���Ӧ�����
						if(sumInventory==0){
							componenetBeanTemp.component.bl_quantity = "";
						}else {
							componenetBeanTemp.component.bl_quantity =  StringsUtil.convertStr2Double(componenetBeanTemp.component.U8_inventory)/sumInventory*100 +"";
						}
						
						formulatorTableModel.setValueAt(DoubleUtil.formatNumber(componenetBeanTemp.component.bl_quantity),i , 2);
						
					}
				}
			}
			
		}

		//�惦�䷽�ṹ��ʵ�������Ϣ ----һ����ֵ� ʵ��Bean��Ϣ 
		public static class ComponenetBean {
			public MaterialBean component;//����Լ�
			public List<MaterialBean> childBeanList;//���е�ԭ�� ������С�Ϻ͵���ԭ�ϵ������˵
 			public List<MinMaterialBean> childMinBeanList;//С������  ��ʱû��ʹ�õ� ��Ϊ����
 			public String complementType;//���������
 			public String complementContent;//�����Ƕ��� 
 			public HashMap<String, String> indexValueMap = new HashMap<String, String>();//��ֵ�ԭ���е�ָ��Ĳ���  /100gΪ��λ��
			
		}

		// ����䷽�е�BOM�ṹ=====һ����ֵ�BOm�Ľṹ��Ϣ  ֱ�Ӱ�С�Ϻ�ԭ�ϵ���ͬ�ֽṹ������
		public static class ComponentBom {
			public TCComponentBOMLine componentBOMLine;
			public List<TCComponentBOMLine> childBomList;
		}
	
		
		
		
		/**
		 * ������һ������
		 * @param componenetBean  һ�����
		 * @param componentBom	һ�����
		 * @return  ֱ�ӽ�ָ���map�ŵ���ֵ�ʵ��������ȥ
		 * @throws TCException
		 */
		public void lableCompute(ComponenetBean componenetBean, ComponentBom componentBom) throws TCException{
			//���㵱ǰ����������͵Ĳ���֮�µĸ���Ӫ���صĺ���/100g
			
			//��ȡ��ǰ����е�����ԭ���е�ָ����ĺϼ�
			HashSet<String> indexNameSet = new HashSet<>(); 
			for(TCComponentBOMLine bomLine : componentBom.childBomList){
				if(bomLine==null) continue;
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
					String indexName = indexBomLine.getItem().getProperty("object_name");//ָ�������
					indexNameSet.add(indexName);
				} 
			}
			
			
			//����һ��ָ����Ŀ   Ҫ�������е�ԭ���е���ͬ���ֵ�ָ����Ŀ����Ͷ�������ۼ�
			if(StringsUtil.isEmpty(componenetBean.complementType)
					||StringsUtil.isEmpty(componenetBean.complementContent)){//�����ֵ����ͻ��߲����ֶ�Ϊ����������ʾ
				MessageBox.post("������-"+componenetBean.component.objectName+"-�Ĳ������ͺ���������Ϣ","",MessageBox.INFORMATION);
				return ;
			}
			
			
			HashMap<String,String> indexMap = new HashMap<>();
			//�ȼ�������������е�Ͷ��ԭ�ϵ��ܵ�ֵ
			Double materialSum = 0d;
			for(int i=0;i<componentBom.childBomList.size();i++){
				TCComponentBOMLine materialBomLine = componentBom.childBomList.get(i);
				MaterialBean materialBean = componenetBean.childBeanList.get(i);
				
				//����ǵ���֮���Ҫ�����ϸ����ʵ����ĵ�˵
				Double inventoryDb = StringsUtil.convertStr2Double(materialBean.U8_inventory);
				Double percentDb = 0d;//�ҵ������ʵĺ���
				if(componenetBean.complementType.equals("��Ͷ����")
						||componenetBean.complementType.equals("��Ͷ����")){//��Ҫ��������ʵ�˵
					AIFComponentContext[] children = materialBomLine.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
						String name = indexBomLine.getProperty("object_name");
						if("������".equals(name)){
							percentDb =  StringsUtil.convertStr2Double(indexBomLine.getProperty("bl_quantity"))/100;
							break;
						}
					}
					
					if(percentDb==0){//���û�и����ʵı����Ļ��Ͱ�1������
						percentDb = 1d;
					}
					materialSum = materialSum +inventoryDb*percentDb;
					
				}else{
					materialSum = materialSum + inventoryDb;
				}
				
			}
			//�������ָ���ֵ
			Iterator<String> indexNameIterator = indexNameSet.iterator();
			while(indexNameIterator.hasNext()){
				String indexName = indexNameIterator.next();
				Double indexSum = 0d;
				for(int i=0;i<componentBom.childBomList.size();i++){
					TCComponentBOMLine materialBom = componentBom.childBomList.get(i);
					MaterialBean materialBean = componenetBean.childBeanList.get(i);
					
					String materialInventory = materialBean.U8_inventory;//����µ�ԭ�ϵ�Ͷ����
					if(materialBom==null) continue;
					AIFComponentContext[] children = materialBom.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBom = (TCComponentBOMLine) context.getComponent();
						String name = indexBom.getItem().getProperty("object_name");
						String value = indexBom.getProperty("bl_quantity");//��ǰָ���ÿ�ٿ˵�ֵ
						if(name.equals(indexName)){//�ҵ��˶�Ӧ��Ӫ��ָ���Bom
							//���ݵ�ǰ��ֵĲ��������Լ�����������������ָ���ֵ
							Double valueDb = StringsUtil.convertStr2Double(value);
							Double inventoryDb = StringsUtil.convertStr2Double(materialInventory);
							if("*".equals(componenetBean.complementContent)){//��*��ʾͶ�����Ƕ���
								indexSum = indexSum + (valueDb*inventoryDb/100)/materialSum;
							}else{
								Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//�������
								indexSum = indexSum + (valueDb*inventoryDb/100)/contentDb;
							}
						}
					}
				}
			
				
				//�������Ƿ��ǲ�ˮ�����Ǹ�����    
				boolean flag1 = false;
				boolean flag2 = false;
				boolean flag3 = false;
				if(componenetBean.complementType.equals("��Һ")|componenetBean.complementType.equals("����"))  flag1 = true;
				if(!componenetBean.complementType.equals("*")) flag2 = true;
				if(indexName.equals("��")) flag3 = true;
				
				if(flag1&&flag2&&flag3){//����������Һ���͵���֣����Բ�ˮ�Ļ�Ҫ����
					Double valueDb = 20d;
					Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//�������
					Double inventoryDb = contentDb - materialSum;//�ܵ���-ԭ�ϵ������=��ˮ����
					Double sumInventory = getComponentInventory(componenetBean, componentBom);//����ֵĲ��������Ͷ��ԭ�ϵ���ֵ
					indexSum = indexSum + (valueDb*inventoryDb/100)/sumInventory;
				}//��������Ͷ���پ��Ƕ��ٵ���ֶ��ԵĻ�����Ҫ����������
				
				//������������ �ۼƼ��������indexName��ָ��ĺ����ŵ�map����
				indexMap.put(indexName, indexSum+"");//Ӫ��ָ�������     Ӫ��ָ�����������еı���
			}
			
			componenetBean.indexValueMap = indexMap;
		}
		
		
		/**
		 * ���㲻ͬ�Ĳ������͵������ԭ��
		 * @param componenetBean  һ�����
		 * @param componentBom	һ�����
		 * @return  ֱ�ӽ�ָ���map�ŵ���ֵ�ʵ��������ȥ
		 * @throws TCException
		 */
		public Double getComponentInventory(ComponenetBean componenetBean, ComponentBom componentBom) throws TCException{
			//���������������ԭ�ϵ�Ͷ����
			
			//�ȼ�������������е�Ͷ��ԭ�ϵ��ܵ�ֵ
			Double materialSum = 0d;
			for(int i=0;i<componentBom.childBomList.size();i++){
				TCComponentBOMLine materialBomLine = componentBom.childBomList.get(i);
				MaterialBean materialBean = componenetBean.childBeanList.get(i);
				
				//����ǵ���֮���Ҫ�����ϸ����ʵ����ĵ�˵
				Double inventoryDb = StringsUtil.convertStr2Double(materialBean.U8_inventory);
				Double percentDb = 0d;//�ҵ������ʵĺ���
				if(componenetBean.complementType.equals("��Ͷ����")
						||componenetBean.complementType.equals("��Ͷ����")){//��Ҫ��������ʵ�˵
					AIFComponentContext[] children = materialBomLine.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
						String name = indexBomLine.getProperty("object_name");
						if("������".equals(name)){
							percentDb =  StringsUtil.convertStr2Double(indexBomLine.getProperty("bl_quantity"))/100;
							break;
						}
					}
					
					if(percentDb==0){//���û�и����ʵı����Ļ��Ͱ�1������
						percentDb = 1d;
					}
					materialSum = materialSum +inventoryDb*percentDb;
					
				}else{
					materialSum = materialSum + inventoryDb;
				}
				
			}
			
			if("*".equals(componenetBean.complementContent)){//��*��ʾͶ�����Ƕ���
				return materialSum;
			}else{
				Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//�������
				return contentDb;
			}
		}
		
		/**
		 * 
		 * ���㵱ǰ�䷽�е�������ֵ��ܵ�Ͷ����
		 * @return
		 */
		public Double getComponentSumInventory(){
			Double sumInventory = 0d;
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
				sumInventory += inventory;
			}
			
			return sumInventory;
			
		}
		
		
		/**
		 * �жϵ�ǰ����չʾ������Ƿ��Ѿ��������˼�������
		 * @return
		 */
		public boolean currentComponentExitInTable(){
			boolean flag = false;
			if(StringsUtil.isEmpty(componentNameText.getText().toString())){//��ֵ�����Ϊ�յĻ�ֱ����ΪΪ��Ȼ������
				return true;
			}
			for(ComponenetBean componenetBean: mComponenetBeansList){
				if(componenetBean.component.objectName.equals(currentComponentBean.component.objectName)){
					flag = true;
				}
				
			}
			return flag;
			
		}


		/**
		 * �������ֻ�Ǹ���Ӫ��ָ�����Ŀ��˵��
		 * @param componenetBean
		 * ����һ�����  ��tabel�д������һ����Ϣ���и���
		 */
		public void updateFormulatorTable(ComponenetBean componenetBean,ComponentBom componentBom){
			//����Ҫ���µ�������Ѿ�������� ָ���map ÿһ��ָ���ֵ�� ��ָ���ڸ������ ��ֵ  ÿg
			
			if(StringsUtil.isEmpty(componenetBean.component.U8_inventory)){//��������ֵ�Ͷ����Ϊ0�Ͳ�����table��
				return;
			}
			
			if(isNutritionFlag){//���Ҫ�������Ӫ���ɷ�  ��ǩ�е�ֵ�ڸ�����еĺ��� �� ÿ100g 
				//����Ŀǰ��ֵ�ָ��Map�д洢���Ǹ�ָ��������еĺ��� ��Ҫת��Ϊ����
				HashMap<String, String> indexValueMap = componenetBean.indexValueMap;
				Set<Entry<String, String>> indexEntrySet = indexValueMap.entrySet();
				for(Entry<String, String> entry : indexEntrySet){
					String indexName = entry.getKey();
					String indexValue = entry.getValue();//�������Valueֻ��ָ��������еı��� ����Ͷ�������Ǻ���
					indexValue = StringsUtil.convertStr2Double(indexValue)*100+"";
					
					indexValueMap.put(indexName, DoubleUtil.formatNumber(indexValue));//����
				} 
			}else{//���������ָ��  ������ֵ�Ͷ�����������ָ��������е��ܵ�ֵ
				Double componentInventory = 0d;
				try {
					componentInventory= getComponentInventory(componenetBean,componentBom);
				} catch (TCException e) {
					e.printStackTrace();
				}
				
				HashMap<String, String> indexValueMap = componenetBean.indexValueMap;
				Set<Entry<String, String>> indexEntrySet = indexValueMap.entrySet();
				for(Entry<String, String> entry : indexEntrySet){
					String indexName = entry.getKey();
					String indexValue = entry.getValue();//�������Valueֻ��ָ��������еı��� ����Ͷ�������Ǻ���
					indexValue = StringsUtil.convertStr2Double(indexValue)*StringsUtil.convertStr2Double(componenetBean.component.U8_inventory)+"";
					
					indexValueMap.put(indexName, DoubleUtil.formatNumber(indexValue));//����
				} 
			}
			
			
			
			DefaultTableModel modle = (DefaultTableModel) formulatorTable.getModel();
			int rowCount = modle.getRowCount();
			int indexRow = -1;//�������Ҫ���µ�������䷽���е����±�
			for(int i=0;i<rowCount;i++){
				String componentName  = modle.getValueAt(i, 0).toString();//
				if(componentName.equals(componenetBean.component.objectName)){//�ҵ���Ӧ�����
					indexRow = i;
					break;
				}
			}
			if(indexRow==-1){
				MessageBox.post("","",MessageBox.INFORMATION);//�������������������˵
			}
			if(componenetBean.indexValueMap==null){
				return ;
			}
			HashMap<String, String> hashMap = componenetBean.indexValueMap;
			Set<Entry<String, String>> entrySet = hashMap.entrySet();
			for(Entry<String, String> entry : entrySet){
				String indexName = entry.getKey();
				String indexValue = entry.getValue();//���Value���������ת���Ѿ��任Ϊ������
				//���������Ϊ���������Ƿ���table��
				int indexColumn = getnIdexNameExitInTableColumns(indexName);
				
				modle.setValueAt(indexValue, indexRow, indexColumn);//����ֵ
			}
			
			
			
		}
		
		/**
		 * @param indexName ָ�������
		 * @return  ���ָ������ƴ��ڵĻ��� �ͷ�������е��±��,��������ڵĻ��ͷ���-1
		 */
		public int getnIdexNameExitInTableColumns(String indexName){
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			int columnCount = model.getColumnCount();
			int k = -1;
			for(int i=0;i<columnCount;i++){
				String columnName = model.getColumnName(i);
				if(columnName.equals(indexName)){
					k = i;
					return k;
				}
			}
			if(k==-1){
				model.addColumn(indexName);
				k = columnCount;//�����һ��֮��պ�������ĩβ
			}
			return k;
		}
		
		
		/**
		 * �Ե�ǰ��ʾ����ֽ��м������
		 */
		public Double computeSumCurrentComponent(){
			Double sumInventory = 0d;
			if(currentComponentBean==null){
				return sumInventory;
			}
			for(MaterialBean materialBean : currentComponentBean.childBeanList){
				sumInventory = sumInventory + StringsUtil.convertStr2Double(materialBean.U8_inventory);
			}
			if("1000".equals(currentComponentBean.complementContent)){
				materialSum0.setText("1000");
			}else if("100".equals(currentComponentBean.complementContent)){
				materialSum0.setText("100");
			}else if("*".equals(currentComponentBean.complementContent)){
				materialSum0.setText(""+sumInventory);
			}
			
			return sumInventory;
			
		}
		
		
		/**
		 * ���䷽���еĸ�����ֽ�����ͼ���
		 * ����ֵ��������д��excel��������ʹ�õ�˵
		 */
		public HashMap<String, String> computeSumFormulatorTable(){
			HashMap<String, String> sumIndexValueMap = new HashMap<>();
			HashSet<String> indexNameSet = new HashSet<>();//�洢���е�Ӫ��ָ����Ŀ�����ƵĲ���
			for(ComponenetBean bean : mComponenetBeansList){
				if(bean.indexValueMap==null){
					continue;
				}
				Iterator<String> iterator = bean.indexValueMap.keySet().iterator();
				while(iterator.hasNext()){
					String name = iterator.next();
					indexNameSet.add(name);
				}
				break;
			}
			
			if(isNutritionFlag){//Ҫ�������Ӫ���ɷ�
				Double sumInventory = getComponentSumInventory();//�����Ͷ�������ܵĺ�
				Iterator<String> iterator = indexNameSet.iterator();
				while(iterator.hasNext()){
					String indexName = iterator.next();
					Double sumIndexValue = 0d;
					for(int i=0;i<mComponenetBeansList.size();i++){
						ComponenetBean componenetBean = mComponenetBeansList.get(i);
						Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
						
						HashMap<String, String> indexMap = componenetBean.indexValueMap;
						Set<Entry<String, String>> entrySet = indexMap.entrySet();
						for(Entry<String,String> entry : entrySet){
							String name = entry.getKey();
							String value = entry.getValue();
							if(indexName.equals(name)){//�ҵ��˶�Ӧ��ָ����Ŀ
								sumIndexValue = sumIndexValue + StringsUtil.convertStr2Double(value)*inventory/100;
							}
						}
					}
					
					sumIndexValue = sumIndexValue/sumInventory*100; //sumIndex����֮����ۼ�Ϊ ������ֵ������¸�ָ��ĺ���   ���Ҫת��Ϊÿ100g
					sumIndexValueMap.put(indexName, DoubleUtil.formatNumber(sumIndexValue));//һ��ָ��������
				}
				
			}else{//Ҫ���������ָ��   ͬһ�ֵ�ָ�����ֵ����ֱ���ۼ�
				Double sumInventory = 0d;//�����Ͷ�������ܵĺ�
				Iterator<String> iterator = indexNameSet.iterator();
				while(iterator.hasNext()){
					String indexName = iterator.next();
					Double sumIndexValue = 0d;
					for(int i=0;i<mComponenetBeansList.size();i++){
						ComponenetBean componenetBean = mComponenetBeansList.get(i);
						Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
						
						HashMap<String, String> indexMap = componenetBean.indexValueMap;
						Set<Entry<String, String>> entrySet = indexMap.entrySet();
						for(Entry<String,String> entry : entrySet){
							String name = entry.getKey();
							String value = entry.getValue();
							if(indexName.equals(name)){//�ҵ��˶�Ӧ��ָ����Ŀ
								sumIndexValue = sumIndexValue + StringsUtil.convertStr2Double(value);
							}
						}
					}
					sumIndexValueMap.put(indexName, DoubleUtil.formatNumber(sumIndexValue));//һ��ָ��������
				}
			}
			
			
			return sumIndexValueMap;
		}
		
		/**
		 * ���䷽table�е�Ӫ��ָ��ĺ���д��text��ȥ
		 * @param indexMap	���䷽���еõ���Ӫ��ָ���ܺ͵�map
		 */
		public void setComponentSumText(HashMap<String, String> sumIndexMap){
			setComponentSumTextAndLabelInvisiable();//���ʼ��ʱ��Ҫ�����еı�ǩ����������� ��Ϊ��֪����ǩ��˳����߸�����ô�����ӵ�˵
			//�Ƚ��䷽table�е������ֻ�ȡ�� �� 3��ʼ����Ӫ��ָ��
			List<String> indexNameList = new ArrayList<>();
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			int columnCount = model.getColumnCount();
			for(int i=4;i<columnCount;i++){
				String indexName = model.getColumnName(i);
				indexNameList.add(indexName);
			}
			
			
			
			//���������˳��ļ���ȥ��map��ƥ���ֵ
			for(int i=0;i<indexNameList.size();i++){
				String indexName = indexNameList.get(i);
				Set<Entry<String, String>> entrySet = sumIndexMap.entrySet();
				for(Entry<String, String> entry : entrySet){
					String key = entry.getKey();
					String value = entry.getValue();
					if(indexName.equals(key)){//�ҵ���ָ���Ӧ������
						setComponentSumKeyAndValue(key,value,i+3);//�����ֵд����Ӧ��text�� ����text��label��ʾ
					}
				}
			}
		}
		
		
		/**
		 * ���䷽�е�Ӫ����ǩ��Ӧ��text��label���������� 
		 * �� 3��ʼ-12
		 */
		public void setComponentSumTextAndLabelInvisiable(){
			componentSumLabel3.setVisible(false);
			componentSumLabel4.setVisible(false);
			componentSumLabel5.setVisible(false);
			componentSumLabel6.setVisible(false);
			componentSumLabel7.setVisible(false);
			componentSumLabel8.setVisible(false);
			componentSumLabel9.setVisible(false);
			componentSumLabel10.setVisible(false);
			componentSumLabel11.setVisible(false);
			componentSumLabel12.setVisible(false);
			
			componentSumText3.setVisible(false);
			componentSumText4.setVisible(false);
			componentSumText5.setVisible(false);
			componentSumText6.setVisible(false);
			componentSumText7.setVisible(false);
			componentSumText8.setVisible(false);
			componentSumText9.setVisible(false);
			componentSumText10.setVisible(false);
			componentSumText11.setVisible(false);
			componentSumText12.setVisible(false);
		}


		/**
		 * @param indexName Ӫ����ǩ������
		 * @param indexValue	Ӫ����ǩ��ֵ
		 * @param number	����Ӧ��label��edittext�ı�Ǻ���
		 */
		public void setComponentSumKeyAndValue(String indexName,String indexValue,int number){
			switch (number) {
			case 3:
				componentSumLabel3.setVisible(true);
				componentSumText3.setVisible(true);
				componentSumLabel3.setText(indexName);
				componentSumText3.setText(indexValue);
				break;
			case 4:
				componentSumLabel4.setVisible(true);
				componentSumText4.setVisible(true);
				componentSumLabel4.setText(indexName);
				componentSumText4.setText(indexValue);
				break;
			case 5:
				componentSumLabel5.setVisible(true);
				componentSumText5.setVisible(true);
				componentSumLabel5.setText(indexName);
				componentSumText5.setText(indexValue);
				break;
			case 6:
				componentSumLabel6.setVisible(true);
				componentSumText6.setVisible(true);
				componentSumLabel6.setText(indexName);
				componentSumText6.setText(indexValue);
				break;
			case 7:
				componentSumLabel7.setVisible(true);
				componentSumText7.setVisible(true);
				componentSumLabel7.setText(indexName);
				componentSumText7.setText(indexValue);
				break;
			case 8:
				componentSumLabel8.setVisible(true);
				componentSumText8.setVisible(true);
				componentSumLabel8.setText(indexName);
				componentSumText8.setText(indexValue);
				break;
			case 9:
				componentSumLabel9.setVisible(true);
				componentSumText9.setVisible(true);
				componentSumLabel9.setText(indexName);
				componentSumText9.setText(indexValue);
				break;
			case 10:
				componentSumLabel10.setVisible(true);
				componentSumText10.setVisible(true);
				componentSumLabel10.setText(indexName);
				componentSumText10.setText(indexValue);
				break;
			case 11:
				componentSumLabel11.setVisible(true);
				componentSumText11.setVisible(true);
				componentSumLabel11.setText(indexName);
				componentSumText11.setText(indexValue);
				break;
			case 12:
				componentSumLabel12.setVisible(true);
				componentSumText12.setVisible(true);
				componentSumLabel12.setText(indexName);
				componentSumText12.setText(indexValue);
				break;
			default:
				break;
			}
		}
		
		
		/**
		 * ������䷽����ֵ�ָ����
		 */
		public void clearComponentColunms(){
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			model.setColumnCount(4);
		
		}
		
		
		/**
		 * ���µ�ǰ��ֵĴ���
		 */
		public void refreshCurrentComponentTable(){
			
			int rowCount = componentTable.getRowCount();
			DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
			
			//����һ�е�ֵ�͵����е�ֵ��д��ԭ�ϵ�bean�� 
			for(int i=0;i<rowCount;i++){
				MaterialBean materialBean  = currentComponentBean.childBeanList.get(i);
				String inventory =model.getValueAt(i, 1)==null ? "" :model.getValueAt(i, 1).toString();
				String minMaterialType = model.getValueAt(i, 4) == null ? "":model.getValueAt(i, 4).toString();
				materialBean.U8_inventory = inventory;
				materialBean.minMaterialType = minMaterialType;
			}
			//�����ԭ�ϵ�Ͷ�������ۺϼ���
			computeSumCurrentComponent();
			Double currentComponentSum = Double.valueOf(StringsUtil.convertStr2Double(materialSum0.getText().toString()));
			
			//ˢ�������ԭ�ϵı���
			for(int i=0;i<rowCount;i++){
				MaterialBean materialBean  = currentComponentBean.childBeanList.get(i);
				Double blQuantity = StringsUtil.convertStr2Double(materialBean.U8_inventory) / currentComponentSum * 100;
				materialBean.bl_quantity = DoubleUtil.formatNumber(blQuantity);
				model.setValueAt(materialBean.objectName, i, 0);//����
				model.setValueAt(materialBean.bl_quantity,i, 2);//����
				model.setValueAt(materialBean.u8Uom,i, 3);//����
			}
			
			
			//ˢ��һ���䷽���е���Ϣ
			//�����䷽�е�������ֵ�Ӫ��ָ��
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				ComponentBom componentBom = mComponentBomsList.get(i);
				try {
					lableCompute(componenetBean, componentBom);//����һ����ֵ���Ϣ
					updateFormulatorTable(componenetBean,componentBom);//����һ����ֵ���Ϣ 
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
			
			//��Ͷ�������ܺͽ��м���
			Double sumInventory = 0d;
			DefaultTableModel formulatorModel = (DefaultTableModel) formulatorTable.getModel();
			rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				//�ҵ���Ӧ�����
				String inventoty = formulatorModel.getValueAt(i, 1).toString()==null ? "":formulatorModel.getValueAt(i, 1).toString();
				componenetBean.component.U8_inventory = inventoty;
				sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
			}
			//�����µı���е�ֵ���м���  ---����е�ֵ�����Ѿ����¹���
			componentSumText0.setText(sumInventory+"");
			
			//��ֱ仯 �����䷽��
			HashMap<String, String> indexSumMap = computeSumFormulatorTable();//���䷽��ָ����Ŀ�ͼ������
			setComponentSumText(indexSumMap);//�����������ָ�����ʾ�ڽ�����
			
		}
		
		
		/**
		 * ˢ�������䷽��
		 */
		public void refreshFormualtorTable(){
			//����һ�е�ֵ���½�bean��
			Double sumInventory = 0d;
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//�ҵ���Ӧ�����
						String inventoty = formulatorTableModel.getValueAt(i, 1).toString()==null ? "":formulatorTableModel.getValueAt(i, 1).toString();
						componenetBeanTemp.component.U8_inventory = inventoty;
						sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
					}
				}
			}
			
			
			
			////������ֵı���
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//�ҵ���Ӧ�����
						if(sumInventory==0){
							componenetBeanTemp.component.bl_quantity = "";
						}else {
							componenetBeanTemp.component.bl_quantity =  StringsUtil.convertStr2Double(componenetBeanTemp.component.U8_inventory)/sumInventory*100 +"";
						}
						
						formulatorTableModel.setValueAt(DoubleUtil.formatNumber(componenetBeanTemp.component.bl_quantity),i , 2);
						
					}
				}
			}
		
			
			
			//Ȼ�����ָ���ֵ
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				ComponentBom componentBom = mComponentBomsList.get(i);
				try {
					lableCompute(componenetBean, componentBom);
					updateFormulatorTable(componenetBean,componentBom);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
			
			
			
			//�����µı���е�ֵ���м���  ---����е�ֵ�����Ѿ����¹���
			componentSumText0.setText(sumInventory+"");
			
			HashMap<String, String> indexSumMap = computeSumFormulatorTable();//���䷽��ָ����Ŀ�ͼ������
			setComponentSumText(indexSumMap);//�����������ָ�����ʾ�ڽ�����
			
		}
		
	
		/**
		 * ��BOM����Ϊ��ָ���е�BOM
		 */
		public void resetPhysicalAndChemicalIndexBom(){
			for(int i=0;i<mComponentBomsList.size();i++){//��ָ��
				ComponentBom componentBom = mComponentBomsList.get(i);
				
				List<TCComponentBOMLine> childBomList = new ArrayList<>();
				
				for(TCComponentBOMLine materialBomLine : componentBom.childBomList){
					try {
						TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
						TCComponentBOMLine physicalBom = BomUtil.getTopBomLine(materialRev, "��ͼ");
						childBomList.add(physicalBom);
						
						
					} catch (TCException e1) {
						if(progressBarDialog.isLive()){
							progressBarDialog.stop();
						}
						e1.printStackTrace();
					}
				}
				componentBom.childBomList = childBomList;
			}
		}
		
		
		/**
		 * ��BOM����ΪӪ���ɷֵ�BOM
		 */
		public void resetNutririonIndexBom(){
			for(int i=0;i<mComponentBomsList.size();i++){//��ָ��
				ComponentBom componentBom = mComponentBomsList.get(i);
				
				List<TCComponentBOMLine> childBomList = new ArrayList<>();
				
				for(TCComponentBOMLine materialBomLine : componentBom.childBomList){
					try {
						TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
						TCComponentBOMLine physicalBom = BomUtil.getTopBomLine(materialRev, "����Ӫ����ǩ");
						childBomList.add(physicalBom);
						
					} catch (TCException e1) {
						if(progressBarDialog.isLive()){
							progressBarDialog.stop();
						}
						e1.printStackTrace();
					}
				}
				componentBom.childBomList = childBomList;
			}
		}
		
		
		
		
		
}

