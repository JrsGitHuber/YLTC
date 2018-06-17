package com.uds.yl.controler;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.eclipse.ui.testing.TestableObject;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.TechStandarTableBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.interfaces.CallBack;
import com.uds.yl.service.ITechStandarModifyService;
import com.uds.yl.service.impl.TechStandarModifyServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.LOVUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.ui.AddNewIndexFrame;
import com.uds.yl.ui.ModifyIndexBeanFrame;
import com.uds.yl.ui.ProgressBarDialog;
import com.uds.yl.ui.StandardAndWaringSelectedFrame;
import com.uds.yl.ui.StandardSelectedFrame;
import com.uds.yl.utils.StringsUtil;

public class TechStandarModifyControler implements BaseControler {
	
	private static TechStandarModify frame;

	private List<TechStandarTableBean> allTableBeans = null;//���е�ָ��
	private List<TCComponentItemRevision> allItemRevision;// �������ϸ��汾+���еķ���İ汾
															// ����û���ϸ��汾
	private TCComponentItemRevision itemRev;//����������׼
	private ITechStandarModifyService iTechStandarModifyServcie = new TechStandarModifyServiceImpl();

	private List<TCComponentItemRevision> indexList = new ArrayList<>();// ��Ų�ѯ����ָ��汾
	private List<TCComponentItemRevision> relatedLawList = new ArrayList<>();// ������׼�����������з���

	private TCComponentItemRevision originRev;// ѡ������������׼����һ���汾
	private Set<String> namesSet = null;
	
	private List<TechStandarTableBean> indexBeans  = null;//�洢ѡ�еļ�����׼�е�����ָ����Ŀ
	
	
	public int unitIndex = 1;//��λ�е��±�
	public int typeIndex = 2 ;//�����±�
	public int newStandardIndex = 3;//���ڿر�׼���±�
	public int oldStandartIndex = 4;//ԭ�ڿر�׼�±�
	public int newWaringIndex = 5;//��Ԥ�����±�
	public int oldWaringIndex = 6;//ԭԤ��ֵ�±�
	public int indexIntroduceIndex = 7;//ָ�����Ʊ�ע�±�
	public int methodIndex = 8;//��ⷽ�����±�
	public int methodBasicIndex = 9;//��ⷽ�������±�
	public int remarkIndex = 10;//��ע
	public int lawStartIndex = 11;//�����еĿ�ʼ�±�
	
//	 "ָ������","��λ", "����", "���ڿر�׼","ԭ�ڿر�׼","��Ԥ��ֵ","ԭԤ��ֵ","ָ��˵��","��ⷽ��","��ⷽ������"
	
	
	private List<JComboBox<String>> comBoxList = new ArrayList<JComboBox<String>>();//�洢������ؼ�
	
	private ProgressBarDialog progressBarDialog=null;
	
	
	

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(frame==null || !frame.isVisible()){
						frame = new TechStandarModify("����������׼������");
					}
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class TechStandarModify extends JFrame  {

		
		private List<TCComponentItemRevision> searchedRevList;
		StandardSelectedFrame frame;
		StandardAndWaringSelectedFrame standardAndWaringSelectedFrame;
		AddNewIndexFrame addIndexFrame;
		ModifyIndexBeanFrame modifyIndexBeanFrame;
		
		// ========�ؼ�	
		private JPanel contentPane;
		private JTextField nameEdit;
		private JTextField revisionEdt;
		private JTextField searchIndexNameEdt;
		private JTable insertTable;//�����ǵȴ�����ָ���table
		private JTable indexTable;//������׼�ͷ����е�ָ��ĺϼ�
		private JButton searchIndexBtn;
		private JButton insertIndexBtn;
		private JButton verificatBtn;
		private JButton outputBtn;
		private JButton obtainBtn;
		private JButton cancleBtn;
		private JButton addBtn;
		private JComboBox indexTypeComBox;//ָ������combox
		private JButton deleteIndexBtn;//ɾ��һ��Bean
		private JButton modifyBtn;//�޸�һ��Bean
		
		
		private int rowAtPoint;
		private int columnAtPoint;

		
		//��ѯ���ķ������������Ͱ汾����
		private List<String> searchNamesList = null;
		private List<TCComponentItemRevision> searchRevsList = null;

		private String selectedRevName;	// ѡ�м���ָ��İ汾����
		private String selectedIndexRevNum;// ѡ�м���ָ��İ汾��
		private String selectedIndexRevItemId;//ѡ�м�����׼��itemId
		
		/**
		 * Create the frame.
		 */
		public TechStandarModify(String name) {
			super(name);
			setBounds(100, 100, 1270, 635);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			nameEdit = new JTextField();
			nameEdit.setBounds(156, 28, 208, 25);
			contentPane.add(nameEdit);
			nameEdit.setColumns(10);
			nameEdit.setEditable(false);

			JLabel lblNewLabel = new JLabel("\u540D\u79F0\uFF1A");
			lblNewLabel.setFont(new Font("����", Font.PLAIN, 14));
			lblNewLabel.setBounds(80, 28, 54, 25);
			contentPane.add(lblNewLabel);

			JLabel lblNewLabel_1 = new JLabel("\u7248\u672C\uFF1A");
			lblNewLabel_1.setFont(new Font("����", Font.PLAIN, 14));
			lblNewLabel_1.setBounds(478, 28, 54, 25);
			contentPane.add(lblNewLabel_1);

			revisionEdt = new JTextField();
			revisionEdt.setColumns(10);
			revisionEdt.setBounds(539, 28, 106, 25);
			contentPane.add(revisionEdt);
			revisionEdt.setEditable(false);

			JLabel lblNewLabel_2 = new JLabel("ָ�����:");
			lblNewLabel_2.setFont(new Font("����", Font.PLAIN, 14));
			lblNewLabel_2.setBounds(80, 90, 75, 25);
			contentPane.add(lblNewLabel_2);

			searchIndexNameEdt = new JTextField();
			searchIndexNameEdt.setColumns(10);
			searchIndexNameEdt.setBounds(449, 90, 106, 25);
			contentPane.add(searchIndexNameEdt);
			
			JLabel label = new JLabel("ָ������:");
			label.setFont(new Font("����", Font.PLAIN, 14));
			label.setBounds(341, 90, 98, 25);
			contentPane.add(label);

			searchIndexBtn = new JButton("����");
			searchIndexBtn.setFont(new Font("����", Font.PLAIN, 14));
			searchIndexBtn.setBounds(736, 90, 93, 25);
			contentPane.add(searchIndexBtn);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(132, 173, 2, 2);
			contentPane.add(scrollPane);

			insertIndexBtn = new JButton("����");
			insertIndexBtn.setFont(new Font("����", Font.PLAIN, 14));
			insertIndexBtn.setBounds(740, 248, 75, 25);
			contentPane.add(insertIndexBtn);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(196, 144, 469, 129);
			contentPane.add(scrollPane_1);

			insertTable = new JTable() {
				public boolean isCellEditable(int rowIndex, int ColIndex) {
					return false;
				}
			};

			insertTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "ָ������","ָ�굥λ"}));
			insertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane_1.setViewportView(insertTable);

			JScrollPane scrollPane_2 = new JScrollPane();
			scrollPane_2.setBounds(49, 350, 1090, 146);
			contentPane.add(scrollPane_2);

			indexTable = new JTable() {
				public boolean isCellEditable(int rowIndex, int ColIndex) {
					
					if(ColIndex==methodIndex){
						return true;
					}
					return false;
				}
				
				@Override
				public TableCellEditor getCellEditor(int row, int column) {
					if(column==methodIndex){
						return new DefaultCellEditor(comBoxList.get(row));
					}
					return super.getCellEditor(row, column);
				}
			};
			indexTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "ָ������","��λ", "����", "���ڿر�׼","ԭ�ڿر�׼","��Ԥ��ֵ","ԭԤ��ֵ","ָ�����Ʊ�ע","��ⷽ��","��ⷽ������","��ע"}));
			scrollPane_2.setViewportView(indexTable);

			verificatBtn = new JButton("��֤");
			verificatBtn.setFont(new Font("����", Font.PLAIN, 14));
			verificatBtn.setBounds(439, 547, 93, 25);
			contentPane.add(verificatBtn);

			outputBtn = new JButton("���");
			outputBtn.setFont(new Font("����", Font.PLAIN, 14));
			outputBtn.setBounds(682, 547, 93, 25);
			contentPane.add(outputBtn);
//			outputBtn.setEnabled(false);

			obtainBtn = new JButton("��ȡ");
			obtainBtn.setFont(new Font("����", Font.PLAIN, 14));
			obtainBtn.setBounds(184, 547, 93, 25);
			contentPane.add(obtainBtn);
			obtainBtn.setVisible(false);

			cancleBtn = new JButton("ȡ��");
			cancleBtn.setFont(new Font("����", Font.PLAIN, 14));
			cancleBtn.setBounds(891, 547, 93, 25);
			contentPane.add(cancleBtn);

			addBtn = new JButton("+");
			addBtn.setBounds(21, 506, 55, 40);
			contentPane.add(addBtn);
			addBtn.setEnabled(true);
			addBtn.setVisible(false);
			
			indexTypeComBox = new JComboBox();
			indexTypeComBox.setBounds(165, 92, 112, 21);
			contentPane.add(indexTypeComBox);
			
			deleteIndexBtn = new JButton("ɾ��");
			deleteIndexBtn.setFont(new Font("����", Font.PLAIN, 14));
			deleteIndexBtn.setBounds(1149, 443, 75, 25);
			contentPane.add(deleteIndexBtn);
			
			
			modifyBtn = new JButton("�޸�");
			modifyBtn.setFont(new Font("����", Font.PLAIN, 14));
			modifyBtn.setBounds(1149, 402, 75, 25);
			contentPane.add(modifyBtn);
			modifyBtn.setVisible(false);
			
			
//			addBtn.setVisible(false);//�ӺŰ�ť���ɼ�\
			
			//�޸�һ��Bena�İ�ť
			
			
			
			//���һ���µ�ָ��
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(addIndexFrame!=null&&addIndexFrame.isVisible()){
						return;
					}
					addIndexFrame = new AddNewIndexFrame();
					addIndexFrame.setVisible(true);
					
					addIndexFrame.setCallBack(new AbstractCallBack() {
						@Override
						public void addNewIndexResult(String indexName,
								String indexUnit) {
							super.addNewIndexResult(indexName, indexUnit);
							//�ظ���ָ�겻Ҫ����
							for(int i=0;i<allTableBeans.size();i++){
								TechStandarTableBean currentBean = allTableBeans.get(i);
								if(currentBean.name.equals(indexName)&&currentBean.unit.equals(indexUnit)){
									MessageBox.post("ָ���Ѿ�����", "", MessageBox.INFORMATION);
									return;
								}
							}
							
							TechStandarTableBean bean = new TechStandarTableBean();
							bean.name = indexName;
							bean.unit = indexUnit;
							for(int i=0;i<relatedLawList.size();i++){
								bean.lawStandards.add("");
							}
							
							allTableBeans.add(bean);
							refreshTable();
							
						}
					});
					
				}
			});

			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					if(progressBarDialog.isLive()){
						progressBarDialog.stop();
					}
					dispose();
				}
			});
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					//�򿪽�����
					progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();
					
					//��ʼ��ָ������combox
					indexTypeComBox.addItem("");//���һ����ΪĬ�ϵ�
					
					TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
					List<String> indexTypeList = LOVUtil.getLovDisplayNameList(session, Const.TechStandarModify.INDEX_TYPE_LOV);
					for(String type : indexTypeList){
						indexTypeComBox.addItem(type);
					}
					indexTypeComBox.setSelectedIndex(0);//Ĭ��ѡ�е��ǵ�һ�����
					
					// ��ʼ���x�еļ�����׼�汾�����ְ汾id
					try {
						selectedRevName = itemRev.getProperty("object_name");
						nameEdit.setText(selectedRevName);
						selectedIndexRevNum = itemRev.getProperty("item_revision_id");
						selectedIndexRevItemId = itemRev.getProperty("item_id");
						revisionEdt.setText(selectedIndexRevNum);
					} catch (TCException e) {
						e.printStackTrace();
					}
					
					//��ʼ����׼����Ŀ�Լ���صķ����ж�Ӧ��ָ��
					{
						
						List<TCComponentItemRevision> allRelatedLawList = getRelatedLawItemList();
						relatedLawList.addAll(allRelatedLawList);//����׼��صķ����ȡ��
						
						
						DefaultTableModel indexModel = (DefaultTableModel) indexTable.getModel();
						
						//����������Ƽ��뵽table��
						for(TCComponentItemRevision lawRev : relatedLawList){
							try {
								String lawName = lawRev.getProperty("object_name");
								String lawId = lawRev.getProperty("item_id");
								indexModel.addColumn(lawId+lawName);
							} catch (TCException e1) {
								e1.printStackTrace();
							}
						}
						
						// ��ȡ֮ǰ����������ݾ����
						int rowCount = indexModel.getRowCount();
						if (rowCount > 0) {// ���ǵ�һ�γ�ʼ��Ҫ����Ѵ�������
							for (int i = 0; i < rowCount; i++) {
								indexModel.removeRow(0);
							}
						}
						// ÿ�λ�ȡ�������µ� ����set ȥ�ظ���ļ���
//						namesSet = iTechStandarModifyServcie.getAllIndexItemNames(itemRev, selectedLawList);
						
						//ѡ�м�����׼�İ汾�е�����ָ����Ŀ
						indexBeans = iTechStandarModifyServcie.getIndexFormSelectedIndexRev(itemRev,relatedLawList.size());
						
						originRev = iTechStandarModifyServcie.getOriginRev(selectedIndexRevNum, selectedIndexRevItemId);
						allItemRevision = new ArrayList<>();

						if (originRev == null) {// ���ǰһ���汾��ȡΪ�վͰ�ֵ��Ϊ��
							allItemRevision.addAll(relatedLawList);
							allTableBeans = iTechStandarModifyServcie.getAllTableBeans(allItemRevision, false,indexBeans);// �ڶ���������ʾ�Ƿ��м���ָ���ǰһ���汾
						} else {
							allItemRevision.add(originRev);
							allItemRevision.addAll(relatedLawList);
							allTableBeans = iTechStandarModifyServcie.getAllTableBeans(allItemRevision, true,indexBeans);// �ڶ���������ʾ�����˼���ָ���ǰһ���汾
						}
						//��ѡ�еļ�����׼�İ汾�е�BOM�ṹ����д��bean�У�allTableBeans��û��ѡ�еļ�����׼����Ϣ��˳�㽫typeд��ȥ�͵�λ
//						iTechStandarModifyServcie.getNewStatdard(itemRev, allTableBeans);
						
						// ��beanд�������
						for (int i = 0; i < allTableBeans.size(); i++) {
							final TechStandarTableBean bean = allTableBeans.get(i);
							indexModel.addRow(new String[] { 
									bean.name, // ָ������
									bean.unit,//��λ
									bean.type,// ����
									bean.newStandard, // ���ڿر�׼
									bean.oldStandard, // ԭ�ڿر�׼
									bean.newWaring,//��Ԥ��ֵ
									bean.oldWaring,//ԭԤ��ֵ
									bean.indexIntroduceString,//ָ��˵��
							});
							
							indexModel.setValueAt(bean.remark, i, remarkIndex);//��ע
							
//							 "ָ������","��λ", "����", "���ڿر�׼","ԭ�ڿر�׼","��Ԥ��ֵ","ԭԤ��ֵ","ָ��˵��","��ⷽ��","��ⷽ������","��ע"
							
							final JComboBox<String> comBox = new JComboBox<String>();
							comBox.addItemListener(new ItemListener() {
								@Override
								public void itemStateChanged(ItemEvent itemevent) {
									if(comBox.getItemCount()==0||comBox.getSelectedItem()==null){
										return ;
									}else{
										String selectItem = comBox.getSelectedItem().toString();
										bean.currentMethod = selectItem;
									}
								}
							});
							
							
							comBoxList.add(comBox);//���һ�������ؼ�

							//��ʼ����ⷽ��ֵ
							DefaultCellEditor editor = (DefaultCellEditor) indexTable.getCellEditor(i, methodIndex);
							JComboBox<String> comboBox = (JComboBox<String>) editor.getComponent();
							
							if(bean.allMethodsList!=null && bean.allMethodsList.size()>0){
								for(String method : bean.allMethodsList){
									comboBox.addItem(method);
								}
							}
							
							if(!StringsUtil.isEmpty(bean.currentMethod)){
								comboBox.setSelectedItem(bean.currentMethod);
							}
							
							//��ⷽ������
							indexTable.setValueAt(bean.testGis, i,methodBasicIndex);
							
							
							//��ʼ������ֵ
							if (bean.lawStandards.size() == 0 || bean.lawStandards == null)
								continue;
							for (int j = 0; j < bean.lawStandards.size(); j++) {
								String value = bean.lawStandards.get(j);
								indexTable.setValueAt(value, i, j + lawStartIndex);
							}
						}
						addBtn.setEnabled(true);
						
						//�رս���
						progressBarDialog.stop();
					}
				}
			}).start();
			
			
			

			
			
			//ɾ��һ��ָ��
			deleteIndexBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					int selectedRow = indexTable.getSelectedRow();
					if(selectedRow==-1){
						MessageBox.post("","��ѡ��Ҫɾ������Ŀ",MessageBox.INFORMATION);
						return;
					}
					
					allTableBeans.remove(selectedRow);
					comBoxList.remove(selectedRow);//ɾ��һ�������ؼ�
					refreshTable();
					
				}
			});
			
			
			// ���
			outputBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							updateTableInfo2TableBean();//�ȸ������ݵ�tableBean��
							if (originRev == null) {// û����һ���汾
								iTechStandarModifyServcie.writeBack2Tc(itemRev, allTableBeans, allItemRevision, false);
							} else {// ���ϸ��汾
								iTechStandarModifyServcie.writeBack2Tc(itemRev, allTableBeans, allItemRevision, true);
							}
							
							progressBarDialog.stop();
						}
					}).start();
					
				}
			});

			

			 //�䷽���޸ĵļ����¼�
			indexTable.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent propertychangeevent) {
//					refreshTable();
				}
			});
		
			//�޸�ԭ�ڿر�׼����
			indexTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if(e.getClickCount()==2){
						Point point = new Point(e.getX(), e.getY());
						rowAtPoint = indexTable.rowAtPoint(point);
						columnAtPoint = indexTable.columnAtPoint(point);
						String rswStr = indexTable.getValueAt(rowAtPoint, columnAtPoint) == null ? "" : indexTable.getValueAt(rowAtPoint, columnAtPoint).toString();
						String rawStandardStr = indexTable.getValueAt(rowAtPoint, newStandardIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, newStandardIndex).toString();
						String rawWaringStr = indexTable.getValueAt(rowAtPoint, newWaringIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, newWaringIndex).toString();
						String testGistStr = indexTable.getValueAt(rowAtPoint,methodBasicIndex ) == null ? "" : indexTable.getValueAt(rowAtPoint, methodBasicIndex).toString();
						String indexIntroduceStr = indexTable.getValueAt(rowAtPoint, indexIntroduceIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, indexIntroduceIndex).toString();
						String remarkStr = indexTable.getValueAt(rowAtPoint, remarkIndex) == null ? "" : indexTable.getValueAt(rowAtPoint, remarkIndex).toString();
						if(columnAtPoint==newStandardIndex||columnAtPoint==newWaringIndex ||
								columnAtPoint==indexIntroduceIndex || columnAtPoint==methodBasicIndex ||
								columnAtPoint == remarkIndex){//����������ڿر�׼������Ԥ��ֵ �����Ǳ�ע
							callModifyStandardAndWaringJfreme(rawStandardStr,rawWaringStr,testGistStr,indexIntroduceStr,remarkStr);
						}
						
						if(columnAtPoint>=lawStartIndex){
							
							int position;
							if (originRev == null) {// ���ǰһ���汾��ȡΪ�� ��0��ʼ
								position = columnAtPoint - lawStartIndex;
							}else{//����һ���汾 ��1��ʼ
								 position = columnAtPoint - lawStartIndex + 1;
							}
							
							TCComponentItemRevision lawRevision = allItemRevision.get(position);
							if(!ItemUtil.isModifiable(lawRevision)){//û��Ȩ��
								MessageBox.post("�Ը÷���û���޸�Ȩ��","��ʾ",MessageBox.INFORMATION);
								return ;
							}else {
								callModifyUpAndDownJfreme(rswStr);
							}
						}
						
//						if(columnAtPoint==indexIntroduceIndex || columnAtPoint==methodBasicIndex){
//							if(rowAtPoint==-1||rowAtPoint<0){
//								MessageBox.post("��ѡ��Ҫ�޸ĵ���Ŀ��","��ʾ",MessageBox.INFORMATION);
//								return;
//							}
//							
//							if(modifyIndexBeanFrame!=null&&modifyIndexBeanFrame.isVisible()){
//								return;
//							}
//							final TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
//							
//							modifyIndexBeanFrame = new ModifyIndexBeanFrame();
//							modifyIndexBeanFrame.setVisible(true);
//							modifyIndexBeanFrame.initFrame(bean.testGis, bean.indexIntroduceString);
//							modifyIndexBeanFrame.setCallBack(new AbstractCallBack() {
//								@Override
//								public void modifyIndexBeanResult(String indexIntroduce,
//										String testGist) {
//									super.modifyIndexBeanResult(indexIntroduce,testGist);
//									bean.indexIntroduceString = indexIntroduce;
//									bean.testGis = testGist;
//									refreshTable();
//								}
//							});
//						}
						
//						String name = (String) JOptionPane.showInputDialog(null, "Test��\n", "��ʾ",
//								JOptionPane.PLAIN_MESSAGE, null, null, rowAtPoint+":--:"+columnAtPoint);
					}
					
					
					
				}
			});
			
			

			// ����
			searchIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							String searchIndexTypeStr = indexTypeComBox.getSelectedItem().toString();
							String searchIndexNameStr = searchIndexNameEdt.getText();
							DefaultTableModel model = (DefaultTableModel) insertTable.getModel();
							searchedRevList= iTechStandarModifyServcie.getSearchIndexItemRevsionList(searchIndexTypeStr,searchIndexNameStr);
							
							//ɾ��model�е�����
							int rowCount = model.getRowCount();
							for(int i=0;i<rowCount;i++){
								model.removeRow(0);
							}
							
							for(int i=0;i<searchedRevList.size();i++){
								TCComponentItemRevision indexRevision = searchedRevList.get(i);
								try {
									model.addRow(new String[]{
											indexRevision.getProperty("object_name"),
											indexRevision.getProperty("u8_uom")
											});
								} catch (TCException e1) {
									progressBarDialog.stop();
									e1.printStackTrace();
								}
							}
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
					
				}
			});

			// ����
			insertIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();// ���뵽indexTable
					int rowIndex = insertTable.getSelectedRow();
					if (rowIndex == -1){
						MessageBox.post("��ѡ��ָ��", "", MessageBox.INFORMATION);
						return;
							
					}
					
					TCComponentItemRevision itemRevision = searchedRevList.get(rowIndex);
					try {
						String unit = itemRevision.getProperty("u8_uom");
						String type  = itemRevision.getProperty("u8_category");
						String indexName = itemRevision.getProperty("object_name");
						String itemID = itemRevision.getProperty("item_id");
						
						//�ظ���ָ�겻Ҫ����
						for(int i=0;i<allTableBeans.size();i++){
							TechStandarTableBean currentBean = allTableBeans.get(i);
							if(currentBean.itemId.equals(itemID)){
								MessageBox.post("ָ���Ѿ�����", "", MessageBox.INFORMATION);
								return;
							}
						}
						
						//��table�����һ��ָ����Ŀ
						
						final TechStandarTableBean bean = new TechStandarTableBean();
						bean.name = indexName;
						bean.oldStandard = "";//���ڿر�׼��ʼ����Ϊ��
						bean.newStandard = "";// ���ڿر�׼��ʼ����Ϊ��
						bean.type = type;
						bean.unit = unit;
						bean.itemId = itemID;
						
						//��ȡ��ⷽ��ֵ
						
						bean.allMethodsList = new ArrayList<String>();
						String[] methodSplit = itemRevision.getProperty("u8_testmethod2").split(",");
						for(String method : methodSplit){
							bean.allMethodsList.add(method);
						}
						
						//�����ֵ
						bean.lawStandards = new ArrayList<>();
						setNewIndexNameResult(bean);
						
						//���һ�� ��ͬʱ ���һ���ؼ�
						final JComboBox<String> comboBox = new JComboBox<String>();
						comboBox.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent itemevent) {
								if(comboBox.getItemCount()==0||comboBox.getSelectedItem()==null){
									return ;
								}else{
									String selectItem = comboBox.getSelectedItem().toString();
									bean.currentMethod = selectItem;	
								}
							}
						});
						for(String method : bean.allMethodsList){
							comboBox.addItem(method);
						}
						comBoxList.add(comboBox);
						allTableBeans.add(bean);
						
						refreshTable();
						
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					
						
					
				}
			});

			

			// ��֤
			verificatBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if (allTableBeans == null)
								return;
							
							updateTableInfo2TableBean();//���ݸ��µ�allTableBeans��
							
							boolean vertifyStandardIsOk = iTechStandarModifyServcie.vertifyStandardIsOk(allTableBeans,
									indexTable);
							//���ܷſ�
//							if (!vertifyStandardIsOk) {// ��֤ûͨ��      
//								outputBtn.setEnabled(false);
//							} else {
//								outputBtn.setEnabled(true);
//							}
							refreshTable();
							
							
							progressBarDialog.stop();
						}
					}).start();
					
				
				}
			});

		}
		
		
		/**
		 * ��ָ���table���е����� д��bean��ȥ
		 */
		public void updateTableInfo2TableBean(){
			//���»�ȡһ��bean ��table��
			DefaultTableModel indexModel = (DefaultTableModel) indexTable.getModel();
			
			
//			"ָ������","��λ", "����", "���ڿر�׼","ԭ�ڿر�׼","��Ԥ��ֵ","ԭԤ��ֵ","ָ��˵��","��ⷽ��","��ⷽ������"
			
			for(int i=0;i<allTableBeans.size();i++){
				TechStandarTableBean bean = allTableBeans.get(i);
				
				bean.unit = indexTable.getValueAt(i, unitIndex) == null ? "" : indexTable.getValueAt(i, unitIndex).toString();//��λ
				bean.type = indexTable.getValueAt(i, typeIndex) == null ? "" : indexTable.getValueAt(i, typeIndex).toString();//����
				bean.newStandard = indexTable.getValueAt(i, newStandardIndex) == null ? "" : indexTable.getValueAt(i, newStandardIndex).toString();//���ڿر�׼
				bean.oldStandard = indexTable.getValueAt(i, oldStandartIndex) == null ? "" : indexTable.getValueAt(i, oldStandartIndex).toString();//ԭ�ڿر�׼
				bean.newWaring = indexTable.getValueAt(i, newWaringIndex) == null ? "" : indexTable.getValueAt(i, newWaringIndex).toString();//��Ԥ��ֵ
				bean.oldWaring = indexTable.getValueAt(i, oldWaringIndex) == null ? "" : indexTable.getValueAt(i, oldWaringIndex).toString();//ԭԤ��ֵ
				bean.indexIntroduceString = indexTable.getValueAt(i, indexIntroduceIndex) == null ? "" : indexTable.getValueAt(i, indexIntroduceIndex).toString();//ָ��˵��
				bean.currentMethod = indexModel.getValueAt(i, methodIndex)==null ?  "" : indexModel.getValueAt(i, methodIndex).toString();//��ⷽ��
				bean.testGis =indexModel.getValueAt(i, methodBasicIndex)==null ?  "" : indexModel.getValueAt(i, methodBasicIndex).toString();//��ⷽ������
				
				if(bean.lawStandards==null){
					bean.lawStandards = new ArrayList<String>();
				}
				
				bean.lawStandards.clear();
				for(int j=0;j<relatedLawList.size();j++){
					String lawStandard = indexModel.getValueAt(i, j+lawStartIndex)==null ?  "" : indexModel.getValueAt(i, j+lawStartIndex).toString();
					bean.lawStandards.add(lawStandard);
				}
				
			}
		}



		/**
		 * ����ѡ�е�����������׼��ȡ����������ļ��У���ط��棩
		 * �������Ƿ���İ汾  
		 * Ҫ���ص��Ƿ���İ汾
		 * 
		 * �Լ����������е��������ķ���
		 * 
		 */
		private List<TCComponentItemRevision> getRelatedLawItemList() {
			List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();
			
			Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
			try {
				//��ʼ������
				TCComponent[] relatedComponents = itemRev.getRelatedComponents("U8_LawRel");
				for(TCComponent component : relatedComponents){
					if(component instanceof TCComponentItemRevision){
						TCComponentItemRevision lawRev = (TCComponentItemRevision) component;
						queue.offer(lawRev);//��ֱ�ӹ����ķ�����ӵ�������
						
					}else{
						continue;
					}
				}
				
				//�ݹ��������
				while(!queue.isEmpty()){
					TCComponentItemRevision lawRevsion = queue.poll();
					revList.add(lawRevsion);//���еķ��涼��������list��
					
					TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRevsion, "��ͼ");
					if(topBomLine==null){
						topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
					}
					AIFComponentContext[] bomChilds = topBomLine.getChildren();
					for(int i=0;i<bomChilds.length;i++){
						TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i].getComponent();
						
						String indicatorRequire = bomChild.getProperty("U8_indexrequirment");
						String relatedSystemId = bomChild.getProperty("U8_AssociationID");
						if(StringsUtil.isEmpty(relatedSystemId)){//����
							continue;
						}
						
						TCComponentItemRevision linkedLawRevision = getLinkedLaw(indicatorRequire, relatedSystemId);
						
						if(linkedLawRevision == null){
							continue;
						}
						queue.offer(linkedLawRevision);
					}
					
				}
				
				
				//ȥ�ظ�
				HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
				for(TCComponentItemRevision lawRevision : revList){
					String id = lawRevision.getProperty("current_id");
					if(revisionMap.containsKey(id)){//id��Ψһ��ʶ������ֶ�
						continue;
					}
					revisionMap.put(id, lawRevision);
				}
				
				
				//����װ������
				revList.clear();
				for(TCComponentItemRevision lawRevsion : revisionMap.values()){
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
				if(lawId.startsWith("GB")){//˵������  �ǲ�Ʒ��׼ ������2760����14880
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
		 * ���û�ȡ���ڿر�׼�Ľ���
		 */
		public void callModifyUpAndDownJfreme(String rawStr){
			if(frame!=null&&frame.isVisible()){
				return;
			}
			frame = new StandardSelectedFrame(rawStr);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setCallBack(new AbstractCallBack() {
				@Override
				public void setUpAndDownResult(String result) {
					super.setUpAndDownResult(result);
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
					model.setValueAt(result, rowAtPoint, columnAtPoint);
					
					//���µ�ʵ��������
					TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
					if(columnAtPoint==newStandardIndex){
						bean.newStandard = result;
					}
					if(columnAtPoint==newWaringIndex){
						bean.newWaring = result;
					}
					if(columnAtPoint>=lawStartIndex){
						int index = columnAtPoint-lawStartIndex;
						List<String> updateLawStandars = new ArrayList<String>();
						for(int i=0;i<bean.lawStandards.size();i++){
							
							if(index==i){
								updateLawStandars.add(result);
							}else{
								updateLawStandars.add(bean.lawStandards.get(i));
							}
						}
						bean.lawStandards = updateLawStandars;
					}
				}
			});
		}
		
		
		/**
		 * ���û�ȡ���ڿر�׼�Ľ���
		 */
		public void callModifyStandardAndWaringJfreme(String rawStandardStr,String rawWaringStr,String indexTestGist,String indexIntroduce,String indexRemark){
			if(standardAndWaringSelectedFrame!=null&&standardAndWaringSelectedFrame.isVisible()){
				return;
			}
			standardAndWaringSelectedFrame = new StandardAndWaringSelectedFrame(rawStandardStr,rawWaringStr,indexIntroduce,indexTestGist,indexRemark);
			standardAndWaringSelectedFrame.setVisible(true);
			standardAndWaringSelectedFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			standardAndWaringSelectedFrame.setCallBack(new AbstractCallBack() {
				
				@Override
				public void setStandardAndWaringResult(String standardResult,
						String waringResult,String indexTestGist,String indexIntroduce,String indexRemark) {
					
					DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
					
					model.setValueAt(standardResult, rowAtPoint,newStandardIndex );
					model.setValueAt(waringResult, rowAtPoint,newWaringIndex);
					model.setValueAt(indexTestGist, rowAtPoint,methodBasicIndex);
					model.setValueAt(indexIntroduce, rowAtPoint,indexIntroduceIndex);
					model.setValueAt(indexRemark, rowAtPoint, remarkIndex);
					
					//���µ�ʵ��������
					TechStandarTableBean bean = allTableBeans.get(rowAtPoint);
					bean.newStandard = standardResult;
					bean.newWaring = waringResult;
					bean.testGis = indexTestGist;
					bean.indexIntroduceString = indexIntroduce;
					bean.remark = indexRemark;
				}
			});
		}
		

		/**
		 * ˢ��һ���������Table���
		 */
		public void refreshTable() {
			if (allTableBeans == null)
				return;

			DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
			
			//ɾ������
			int rowCount = model.getRowCount();
			for(int i=0;i<rowCount;i++){
				model.removeRow(0);
			}
			
//			"ָ������","��λ", "����", "���ڿر�׼","ԭ�ڿر�׼","��Ԥ��ֵ","ԭԤ��ֵ","ָ��˵��","��ⷽ��","��ⷽ������"
			// ��beanд�������
			for (int i = 0; i < allTableBeans.size(); i++) {
				model.addRow(new String[]{""});//�¼�һ������
				TechStandarTableBean bean = allTableBeans.get(i);
				model.setValueAt(bean.name, i, 0);// ����
				model.setValueAt(bean.unit, i, unitIndex);// ��λ
				model.setValueAt(bean.type, i, typeIndex);// ����
				model.setValueAt(bean.newStandard, i, newStandardIndex);// ���ڿر�׼
				model.setValueAt(bean.oldStandard, i, oldStandartIndex);// ԭ�ڿر�׼
				model.setValueAt(bean.newStandard, i, newWaringIndex);// ��Ԥ��ֵ
				model.setValueAt(bean.oldStandard, i, oldWaringIndex);// ԭԤ��ֵ
				model.setValueAt(bean.indexIntroduceString, i, indexIntroduceIndex);//ָ��˵��
				
				//��ʼ����ⷽ��ֵ
				DefaultCellEditor editor = (DefaultCellEditor) indexTable.getCellEditor(i, methodIndex);
				JComboBox<String> comboBox = (JComboBox<String>) editor.getComponent();
				
				if(!StringsUtil.isEmpty(bean.currentMethod)){
					int index = bean.allMethodsList.indexOf(bean.currentMethod);
					indexTable.setValueAt(bean.currentMethod, i, methodIndex);
				}
				
				indexTable.setValueAt(bean.testGis, i, methodBasicIndex);//��ⷽ������
				
				//��ʼ������ֵ
				if (bean.lawStandards.size() == 0 || bean.lawStandards == null)
					continue;
				for (int j = 0; j < bean.lawStandards.size(); j++) {
					String value = bean.lawStandards.get(j);
					indexTable.setValueAt(value, i, j + lawStartIndex);
				}
				
			}
		}


		/**
		 * �ص���������Table�����һ���µ����ݵĻص�����
		 * @param result
		 */
		public void setNewIndexNameResult(TechStandarTableBean bean) {
			//�൱���ǽ�����Ĺ��ܳ���ȥ����+���Ű�ť
			DefaultTableModel model = (DefaultTableModel) indexTable.getModel();
			// �ܹ��ж��ٸ�����
			int count = 10;
			
			String name = bean.name;
			if (name == null || "".equals(name)) {
				return;
			}

			// ��ʼ��bean������
			for (int i = 0; i < relatedLawList.size(); i++) {
				bean.lawStandards.add("");
			}

			// ��ʼ����
			String[] rowStrs = new String[count];
			rowStrs[0] = name;
			for (int i = 1; i < count; i++) {
				rowStrs[i] = "";
			}
			model.addRow(rowStrs);
			
		}
		
		
		
		
	}

	
	
}





