package com.uds.yl.controler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.Jr.DragDropRowTableUI;
import com.uds.yl.Jr.FormulaModelChoose;
import com.uds.yl.Jr.WaitingUI;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.ui.ProgressBar;


//�䷽��������汾
public class FormulatorModifyControler {
	
	private Rectangle rectangleObj;
	private static FormulaBomCreate frame;
	private TCComponentItemRevision itemRev;
	private String modelType = "";
	private boolean ifModifiable = false;
	
	private String[] bomProperties = new String[] { "bl_item_item_id", "bl_item_object_name", 
			"U8_inventory", "U8_alternate", "U8_alternateitem", "U8_groupitem", "U8_minmaterial" };
	
	public FormulatorModifyControler(Rectangle rectangleObj){
		this.rectangleObj = rectangleObj;
	}
	
	public void userTask(TCComponentItemRevision itemRev1) throws Exception {
		this.itemRev = itemRev1;
		
		// ���ȼ���Ƿ���Ȩ�ޡ��Ƿ�����ͼ
		CheckBomViewBean bean = CheckBomView();
		this.ifModifiable = bean.ifModifiable;
		if (bean.ifModifiable) {
			if (bean.ifHasBom) {
				String modelType = FormulaModelChoose.GetChooseMessage(rectangleObj);
				if (modelType.equals("")) {
					return;
				}
				this.modelType = modelType;
			} else {
				this.modelType = "����ģʽ";
			}
		} else {
			this.modelType = "�༭ģʽ";
			MessageBox.post("�������䷽�����鿴\n�����޸ģ����Ƚ����޶�����", "��ʾ", MessageBox.INFORMATION);
		}
		
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				try {
					if(frame == null){
						frame = new FormulaBomCreate();
					}
					
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
				} catch (Exception e) {
					frame = null;
					MessageBox.post("������г�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
					e.printStackTrace();
				}
			}
		};
		ProgressBar progressBar = new ProgressBar("���ڻ�ȡ�䷽����", thread, rectangleObj);
		progressBar.Start();
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					if(frame == null){
//						frame = new FormulaBomCreate();
//					}
//					
//					frame.setVisible(true);
//					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//					frame.setResizable(false);
//				} catch (Exception e) {
//					frame = null;
//					e.printStackTrace();
//				}
//			}
//		});
	}
	
	private CheckBomViewBean CheckBomView() throws Exception {
		CheckBomViewBean bean = new CheckBomViewBean();
		TCComponent bomView = BomUtil.getRevView(itemRev, Const.Formulator.MATERIALBOMNAME);
		if(bomView != null){
			bean.ifHasBom = true;
			bomView.refresh();
			String is_modifiable = bomView.getProperty("is_modifiable");
			if(is_modifiable.equals("��")){
				bean.ifModifiable = false;
			} else {
				bean.ifModifiable = true;
			}
		}else{
			// ���û����ͼ������Ҫ���汾�Ƿ���޸ģ���Ϊ������Ҫ�ڰ汾���½���ͼ
			bean.ifHasBom = false;
			String is_modifiable = itemRev.getProperty("is_modifiable");
			if(is_modifiable.equals("��")){
				bean.ifModifiable = false;
			} else {
				bean.ifModifiable = true;
			}
		}
		
		return bean;
	}

	class FormulaBomCreate extends JFrame {
		
		// ������ر���
		private static final long serialVersionUID = 1L;
		
		private JPanel contentPane;
		private JPopupMenu m_popupMenu;
		private JTextField textField;
		private JTextField textField_1;
		private JTextField textField_2;
		private JTextField textField_3;
		private JTextField textField_4;
		
		private JTable table;
		private JTable table_1;
		private Vector<Object> columnNames;
		private Vector<Object> columnNames_1;
		
		// ����ѡ����뵽�䷽����һ�е�bomboBox
		private JComboBox<Integer> comboBox;
		
		// TC��ر���
		private TCComponentQuery query;  // ���ڲ�ѯ�� U8_MaterialRevision
		private TCComponentQuery query1; // ���ڲ�ѯ�� �����...
		private Map<String, TCComponentItemRevision> toCreateBomViewMap; // �����ݴ�BomView��Map
		private ArrayList<String> deleteDuplicateList; // ���ڹ�ϵ���䷽BomLineȥ�ز���
		
		public FormulaBomCreate() throws Exception {
			
			// �����Ҽ��˵���
			CreatePopupMenu();
			
			// ��ʼ����ѯ��
			if(query == null){
				String groupName = UserInfoSingleFactory.getInstance().getTCSession().getGroup().toString();
				
				if(groupName.contains("����")){
					query = InitializeQuery(QueryClassConst.U8_MATERIAL_REV_YG.getValue());
				}else if(groupName.contains("Һ��")){
					query = InitializeQuery(QueryClassConst.U8_MATERIAL_REV_LM.getValue());
				}else{
					MessageBox.post("������/Һ���û����룬�޷���ȡ��ѯ��", "��ʾ", MessageBox.INFORMATION);
					throw new Exception("������/Һ���û����룬�޷���ȡ��ѯ��");
				}
				
				// ����TC����ʱʹ��
//				query = InitializeQuery("U8_MaterialRevision");
				
				if(query == null){
					return;
				}
			}
			if(query1 == null){
				query1 = InitializeQuery("�����...");
				if(query1 == null){
					return;
				}
			}
			
			// ��ʼ���ݴ�BomView��Map
			toCreateBomViewMap = new HashMap<String, TCComponentItemRevision>();
			deleteDuplicateList = new ArrayList<String>();
			
			// ��ʼ�����Model����
			InitializeTableModels();			
			
			setAlwaysOnTop(true);
			setResizable(false);
			setType(Type.UTILITY);
			setTitle(" ����䷽������");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// ���ݴ�����Rectangle���ô���λ��
			int centerX = rectangleObj.x + rectangleObj.width / 2;
			int centerY = rectangleObj.y + rectangleObj.height / 2;
			setBounds(centerX - 450, centerY - 325, 900, 650);
			
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel label = new JLabel("�䷽/С�ϣ�");
			label.setBounds(25, 16, 70, 15);
			contentPane.add(label);
			
			textField = new JTextField();
			textField.setColumns(10);
			textField.setEditable(false);
			textField.setBounds(101, 13, 80, 21);
			contentPane.add(textField);		
			
			JLabel label_1 = new JLabel("�汾��");
			label_1.setBounds(236, 16, 70, 15);
			contentPane.add(label_1);
			
			textField_1 = new JTextField();
			textField_1.setColumns(10);
			textField_1.setEditable(false);
			textField_1.setBounds(321, 13, 80, 21);
			contentPane.add(textField_1);
			
			// ��ʼ�� �䷽/С�� �� �汾
			InitializeTextField();
			
			JLabel label_2 = new JLabel("ԭ������");
			label_2.setBounds(25, 58, 70, 15);
			contentPane.add(label_2);
			
			textField_2 = new JTextField();
			textField_2.setColumns(10);
			textField_2.setBounds(101, 55, 80, 21);
			contentPane.add(textField_2);
			
			JLabel label_3 = new JLabel("���Ӵ��룺");
			label_3.setBounds(236, 58, 70, 15);
			contentPane.add(label_3);
			
			textField_3 = new JTextField();
			textField_3.setColumns(10);
			textField_3.setBounds(321, 55, 80, 21);
			contentPane.add(textField_3);
			
			JLabel label_4 = new JLabel("��Ӧ�̣�");
			label_4.setBounds(454, 58, 70, 15);
			contentPane.add(label_4);
			
			textField_4 = new JTextField();
			textField_4.setColumns(10);
			textField_4.setEditable(false);
			textField_4.setBounds(531, 55, 80, 21);
			contentPane.add(textField_4);
			
			JButton btnNewButton = new JButton("����");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = textField_2.getText();
					String code = textField_3.getText();
					//String provider = textField_4.getText();

					if (name.equals("") && code.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "����������һ���������в�ѯ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					while(newTableModel.getRowCount() != 0){
						newTableModel.removeRow(newTableModel.getRowCount() - 1);
					}
					
					String keys = "";
					String valuse = "";
					// ��ѯ������
					if (!"".equals(name)) {
						if ("".equals(keys)) {
							keys += Const.Material_Query_Condition.NAME;
							valuse += "*"+name + "*";
						} else {
							keys += "," + Const.Material_Query_Condition.NAME;
							valuse += "," +"*"+ name + "*";
						}
					}
					if (!"".equals(code)) {
						if ("".equals(keys)) {
							keys += Const.Material_Query_Condition.CODE;
							valuse += code + "*";
						} else {
							keys += "," + Const.Material_Query_Condition.CODE;
							valuse += "," + code + "*";
						}
					}
					TCComponent[] components;
					try{
						components = GetSearchResult(query, keys.split(","), valuse.split(","));
					}catch(Exception e1){
						JOptionPane.showMessageDialog(contentPane, "��ѯ���̳�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}finally{
						WaitingUI.CloseProcessBar();
					}
					if(components == null || components.length == 0){
						JOptionPane.showMessageDialog(contentPane, "û�в�ѯ���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if(components.length > 2000){
						JOptionPane.showMessageDialog(contentPane, "��ѯ������࣬������������Ӿ����������ѯ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
//					ArrayList<MaterialBeanJr> list = new ArrayList<MaterialBeanJr>();					
					try {
						for (TCComponent component : components) {
							TCComponentItemRevision itemRevision = (TCComponentItemRevision) component;
							newTableModel.addRow(new String[] { 
									itemRevision.getProperty("item_id"), 
									itemRevision.getProperty("object_name"),
									itemRevision.getProperty("u8_code"),
									itemRevision.getProperty("u8_supplierinfo"),
									itemRevision.getProperty("u8_price"),
									itemRevision.getProperty("u8_uom")});
							
//							MaterialBeanJr beanJr = new MaterialBeanJr(
//									itemRevision.getProperty("object_name"),
//									itemRevision.getProperty("u8_code"),
//									itemRevision.getProperty("u8_supplierinfo"),
//									itemRevision.getProperty("u8_price"),
//									itemRevision.getProperty("u8_uom"));
//							list.add(beanJr);
						}
						
						// Ĭ��ѡ�е�һ������
						table.setRowSelectionInterval(0, 0);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "��ȡ�汾���Գ�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}
				}
			});
			btnNewButton.setBounds(661, 54, 93, 23);
			contentPane.add(btnNewButton);
			
			JLabel label_5 = new JLabel("��ѡԭ�ϱ�");
			label_5.setBounds(25, 98, 100, 15);
			contentPane.add(label_5);
			
			// ��ѡԭ�ϱ���������
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBounds(25, 120, 845, 152);
			contentPane.add(scrollPane);
			
			JButton btnNewButton_1 = new JButton("����б�");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "�б��Ѿ�Ϊ��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					while(newTableModel.getRowCount() != 0){
						newTableModel.removeRow(newTableModel.getRowCount() - 1);
					}
				}
			});
			btnNewButton_1.setBounds(236, 279, 93, 23);
			contentPane.add(btnNewButton_1);
			
			JLabel label_6 = new JLabel("���뵽�䷽���");
			label_6.setBounds(365, 283, 100, 15);
			contentPane.add(label_6);
			label_6.setVisible(false);
			
			Integer[] comboBoxItems = GetComboBoxItems();
			DefaultComboBoxModel<Integer> defaultComboBoxModel = new DefaultComboBoxModel<Integer>(comboBoxItems);
			
			comboBox = new JComboBox<Integer>();
			comboBox.setBounds(460, 279, 45, 23);
			comboBox.setModel(defaultComboBoxModel);
			// Ĭ��ѡ��comboBox�����һ��ѡ��
			comboBox.setSelectedItem(comboBoxItems.length);
			contentPane.add(comboBox);
			comboBox.setVisible(false);
			
			JLabel label_7 = new JLabel("��");
			label_7.setBounds(515, 283, 54, 15);
			contentPane.add(label_7);
			 label_7.setVisible(false);
			
			JButton btnNewButton_2 = new JButton("����");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ�п�ѡԭ�ϱ��е���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					// �ж��Ƿ��Ѿ����ڸ�ԭ��
					String item_id = table.getModel().getValueAt(row, 0).toString();
					// 20180330jiangren����䷽�������һ��ԭ��
//					javax.swing.table.TableModel tableModel = table_1.getModel();
//					int rowCount = tableModel.getRowCount();
//					for(int i = 0; i < rowCount; i++){
//						String id = tableModel.getValueAt(i, 1).toString();
//						if(id.equals(item_id)){
//							JOptionPane.showMessageDialog(contentPane, "�䷽�����Ѿ����ڸ�ԭ��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
//							return;
//						}
//					}
					
					// 20180412jiangren����䷽��������������� 
//					int comboBoxItem = (int) comboBox.getSelectedItem();
					int comboBoxItem = table_1.getSelectedRow();
					if (comboBoxItem == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ���䷽���е���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					comboBoxItem = comboBoxItem + 2;
					
					try {
						boolean ifSuccess = InsertDataIntoTable(comboBoxItem, item_id);
						// �ɹ�֮���ȥ����comboBox
						if(ifSuccess){
							UpdateComboBoxItems(true);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "���뵽�䷽���������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}
				}
			});
			btnNewButton_2.setBounds(551, 279, 93, 23);
			contentPane.add(btnNewButton_2);
			
			JLabel label_8 = new JLabel("�䷽��");
			label_8.setBounds(25, 321, 100, 15);
			contentPane.add(label_8);
			
			// �䷽�� ��������
			table_1.setUI(new DragDropRowTableUI());
			table_1.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// ������Ҽ���BUTTON3��
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
						int focusedRowIndex = table_1.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						// �������ѡ����Ϊ��ǰ�Ҽ��������
						table_1.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
						// �����˵�
						m_popupMenu.show(table_1, e.getX(), e.getY());
						
						SetComboBoxSelectedItem(focusedRowIndex+1);
					}
					// ����������BUTTON1��
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
						int focusedRowIndex = table_1.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						SetComboBoxSelectedItem(focusedRowIndex+1);
					}
				}

				@Override
				public void mousePressed(MouseEvent e) { }

				@Override
				public void mouseReleased(MouseEvent e) {
//					int focusedRowIndex = table_1.rowAtPoint(e.getPoint());
//					if (focusedRowIndex == -1) {
//						return;
//					}
//					
//					SetComboBoxSelectedItem(focusedRowIndex+1);
				}

				@Override
				public void mouseEntered(MouseEvent e) { }

				@Override
				public void mouseExited(MouseEvent e) {
//					int focusedRowIndex = table_1.rowAtPoint(e.getPoint());
//					if (focusedRowIndex == -1) {
//						DefaultComboBoxModel<Integer> defaultComboBoxModel = (DefaultComboBoxModel<Integer>)comboBox.getModel();
//						comboBox.setSelectedItem(defaultComboBoxModel.getSize());
//					}
//					
//					SetComboBoxSelectedItem(focusedRowIndex+1);
				}
			});
			
			JScrollPane scrollPane_1 = new JScrollPane(table_1);
			scrollPane_1.setBounds(25, 345, 845, 226);
			contentPane.add(scrollPane_1);
			
			JButton btnNewButton_3 = new JButton("����б�");
			btnNewButton_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "�б��Ѿ�Ϊ��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					int n = JOptionPane.showConfirmDialog(contentPane, "ȷ�������", "ȷ�϶Ի���", JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {						
						while(newTableModel.getRowCount() != 0){
							newTableModel.removeRow(newTableModel.getRowCount() - 1);
						}
						
						UpdateComboBoxItems1();
						toCreateBomViewMap.clear();
					}else{
						return;
					}
				}
			});
			btnNewButton_3.setBounds(236, 579, 93, 23);
			contentPane.add(btnNewButton_3);
			
			JButton btnNewButton_4 = new JButton("��䷽");
			btnNewButton_4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// ȡ�����ı༭״̬��Ϊ�˻�ȡ�����һ�����ڱ༭�ĵ�Ԫ���ֵ
					if (table_1.isEditing()) 
						table_1.getCellEditor().stopCellEditing();
					
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "�б�Ϊ�գ��޷�����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					ArrayList<TCComponentItemRevision> itemRevisionList = new ArrayList<TCComponentItemRevision>(); 
					@SuppressWarnings("unchecked")
					Vector<Vector<Object>> vector = newTableModel.getDataVector();
					for(Vector<Object> vector1 : vector){
						String item_id = vector1.get(1).toString();
						itemRevisionList.add(toCreateBomViewMap.get(item_id));
					}
					
					// ��ʼ������ͼ
					CreateBomView(itemRevisionList);
				}
			});
			btnNewButton_4.setBounds(395, 579, 93, 23);
			if(!ifModifiable){
				btnNewButton_4.setEnabled(false);
			}
			contentPane.add(btnNewButton_4);
			
			JButton btnNewButton_5 = new JButton("ȡ��");
			btnNewButton_5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnNewButton_5.setBounds(551, 579, 93, 23);
			contentPane.add(btnNewButton_5);
			
			// (JPanel�����ָ���)
			JPanel panel = new JPanel();
			panel.setBounds(18, 312, 846, 1);
			panel.setBorder(new LineBorder(Color.GRAY));
			contentPane.add(panel);
			
			// (JPanel�����ָ���)
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(Color.GRAY));
			panel_1.setBounds(18, 89, 846, 1);
			contentPane.add(panel_1);
		}


		// TC��������-------------------------------------------------------------------------------------------------------------------------- //
		
		private void CloseWindow(TCComponentBOMLine topBomLine) {

			// �رղ�����Bom View
			if (topBomLine != null) {
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				try {
					bomWindow.save();
					bomWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void CreateBomView(ArrayList<TCComponentItemRevision> itemRevisionList) {
			TCComponentBOMLine topBomLine = null;
			try
			{
				topBomLine = BomUtil.getTopBomLine(itemRev, Const.Formulator.MATERIALBOMNAME);
				if (topBomLine == null) {
					// û����ͼ��ȥ����
					topBomLine = BomUtil.setBOMViewForItemRev(itemRev);
				}else{
					if(topBomLine.hasChildren()){
						AIFComponentContext[] children = topBomLine.getChildren();
						for (AIFComponentContext context : children) {
							TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
							bomLine.cut();
						}
					}
				}
				
				// �����ȹر���ͼ�������´������BomLine�ᰴ��֮ǰ������(���ұ������)
				CloseWindow(topBomLine);
				topBomLine = BomUtil.getTopBomLine(itemRev, Const.Formulator.MATERIALBOMNAME);
				if(topBomLine == null){
					topBomLine = BomUtil.setBOMViewForItemRev(itemRev);
					
					if(topBomLine == null){
						JOptionPane.showMessageDialog(contentPane, "������ͼ��������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				
				for(TCComponentItemRevision itemRevision : itemRevisionList){
					topBomLine.add(itemRevision.getItem(), itemRevision, null, false);
				}
				
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				bomWindow.save();
				
				DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
				@SuppressWarnings("unchecked")
				Vector<Vector<Object>> vector = newTableModel.getDataVector();
				topBomLine.refresh();
				AIFComponentContext[] children = topBomLine.getChildren();
				for(int i = 0; i < children.length; i++){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
					bomLine.setProperties(
							new String[] { "U8_inventory", "U8_alternate", "U8_alternateitem", "U8_groupitem", "U8_minmaterial" }, 
							new String[] { 
									vector.get(i).get(4).toString(), 
									vector.get(i).get(5).toString(), 
									vector.get(i).get(6).toString(), 
									vector.get(i).get(7).toString(),
									vector.get(i).get(8).toString() });
				}
				
				CloseWindow(topBomLine);
				
				JOptionPane.showMessageDialog(contentPane, "�䷽�������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(contentPane, "�����䷽���̳�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				CloseWindow(topBomLine);
				e.printStackTrace();
			}
		}
		
		private TCComponentQuery InitializeQuery(String queryName){
			TCComponentQuery query = QueryUtil.getTCComponentQuery(queryName);
			if (query == null) {
				MessageBox.post("û���ҵ���ѯ��\n" + queryName + "\n����ϵ����Ա����", "��ʾ", MessageBox.INFORMATION);
			}			
			return query;
		}
		
		public TCComponent[] GetSearchResult(TCComponentQuery query, String[] propertyName, String[] values) throws Exception {
			TCComponent[] results = query.execute(propertyName, values);
			return results;
		}
		
		
		// ����������������-------------------------------------------------------------------------------------------------------------------------- //
		
		private void SetTableHeaderColor(Integer[] columnIndexs, final Color c) {			
			DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					JComponent comp = (JComponent) super .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					comp.setBackground(SystemColor.control);
//					comp.setBorder(BorderFactory.createLineBorder(c));
					comp.setBorder(BorderFactory.createEtchedBorder(c, c));
					return comp;
				}
			};
			cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			
			for(int index : columnIndexs){
				TableColumn column = table_1.getTableHeader().getColumnModel().getColumn(index);
				column.setHeaderRenderer(cellRenderer);
			}			
		}
		
		private void InitializeTextField() throws Exception {
			String formulatorName = itemRev.getProperty("object_name");
			String formulatorRevision = itemRev.getProperty("item_revision_id");
			textField.setText(formulatorName);
			textField_1.setText(formulatorRevision);
		}
		
		private void InitializeTableModels() throws Exception{
			// ��ʼ�� ��ѡԭ�ϱ� ����
			columnNames = new Vector<Object>();
			columnNames.add("ID");
			columnNames.add("ԭ����");
			columnNames.add("���Ӵ���");
			columnNames.add("��Ӧ��");
			columnNames.add("��λ�ɱ�(Ԫ/ǧ��)");
			columnNames.add("��׼��λ");
//			int columnCount = columnNames.size();
//			Vector<Vector<Object>> rowDatas = new Vector<Vector<Object>>();
//			for (int i = 0; i < 5; i++) {
//				Vector<Object> vector1 = new Vector<Object>();
//				vector1.add("");
//				for (int j = 1; j < columnCount; j++) {
//					vector1.add("" + i + j);
//				}
//				rowDatas.add(vector1);
//			}
//			DefaultTableModel newTableModel = new DefaultTableModel(rowDatas, columnNames) {
			DefaultTableModel newTableModel = new DefaultTableModel(null, columnNames) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table = new JTable();
			table.setModel(newTableModel);
			// ������0����(setMinWidth(0)����Ҫ������setMaxWidth(0)����֮ǰ�ųɹ�����ѡ����)
			table.getColumnModel().getColumn(0).setMinWidth(0);
			table.getColumnModel().getColumn(0).setMaxWidth(0);

			
			// ��ʼ�� �䷽�� ����
			columnNames_1 = new Vector<Object>();
			columnNames_1.add("�к�");
			columnNames_1.add("ID");
			columnNames_1.add("ԭ����");
			columnNames_1.add("���Ӵ���");
			columnNames_1.add("Ͷ����(ǧ��)");
			columnNames_1.add("ԭ���滻����");
			columnNames_1.add("����滻����");
			columnNames_1.add("����滻˵��");
			columnNames_1.add("С������");
//			int columnCount_1 = columnNames_1.size();
//			Vector<Vector<Object>> rowDatas_1 = new Vector<Vector<Object>>();
//			for (int i = 0; i < 8; i++) {
//				Vector<Object> vector1 = new Vector<Object>();
//				vector1.add("");
//				for (int j = 1; j < columnCount_1; j++) {
//					vector1.add("" + i + j);
//				}
//				rowDatas_1.add(vector1);
//			}			
//			DefaultTableModel newTableModel_1 = new DefaultTableModel(rowDatas_1, columnNames_1) {
			DefaultTableModel newTableModel_1 = new DefaultTableModel(null, columnNames_1) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					// ���������пɱ༭
					if (column == 4 || column == 5 || column == 6 || column == 7 || column == 8) {
						return true;
					} else {
						return false;
					}
				}

				@Override
				public Object getValueAt(int row, int column) {
					if (column == 0) {
						return row + 1;
						// return row;
					}
					return super.getValueAt(row, column);
				}
			};
			
			// ���ݲ�ͬ��ģʽ����Table
			if (modelType.equals("�༭ģʽ")) {
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.Formulator.MATERIALBOMNAME);
				AddRowsByTopBomLine(topBomLine, newTableModel_1, false);
			} else {
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.Formulator.MATERIALBOMNAME);
				AddRowsByTopBomLine(topBomLine, newTableModel_1, false);
				
				itemRev.refresh();
				TCComponent[] components = itemRev.getReferenceListProperty("U8_FormulaRel");
				for(TCComponent component : components){
					//String object_type = component.getProperty("object_type");
					String object_type = component.getType();
					// �䷽�汾---U8_FormulaRevision
					if(object_type.equals("U8_FormulaRevision")){
						TCComponentItemRevision itemRevision = (TCComponentItemRevision)component;
						TCComponentBOMLine topBomLine1 = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
						AddRowsByTopBomLine(topBomLine1, newTableModel_1, true);
					}
				}
			}
			
			table_1 = new JTable();
			table_1.setModel(newTableModel_1);
			SetTableHeaderColor(new Integer[] { 5 }, Color.GRAY);
			SetTableHeaderColor(new Integer[] { 6, 7, 8 }, Color.DARK_GRAY);
			
			// ������0�����
			table_1.getColumnModel().getColumn(0).setMaxWidth(40);
			table_1.getColumnModel().getColumn(1).setMaxWidth(70);
			
			// ������1����(setMinWidth(0)����Ҫ������setMaxWidth(0)����֮ǰ�ųɹ�����ѡ����)
//			table_1.getColumnModel().getColumn(1).setMinWidth(0);
//			table_1.getColumnModel().getColumn(1).setMaxWidth(0);
		}
		
		private void AddRowsByTopBomLine(TCComponentBOMLine topBomLine, DefaultTableModel newTableModel_1, boolean ifDeleteDuplicate) throws Exception{
			if (topBomLine != null) {
				if (topBomLine.hasChildren()) {
					try {
						AIFComponentContext[] children = topBomLine.getChildren();		
						for (AIFComponentContext context : children) {
							TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
							String[] properties = bomLine.getProperties(bomProperties);
							
							TCComponentItem item = bomLine.getItem();
							TCComponentItemRevision itemRevision = item.getLatestItemRevision();
							
							String item_id = properties[0];
							// ʹ�� id��Ͷ������������ȥ��
							String onlyMark = item_id + properties[2];
							
							if (ifDeleteDuplicate) {
								if (deleteDuplicateList.contains(onlyMark)) {
									continue;
								} else {
									deleteDuplicateList.add(onlyMark);
								}
							} else {
								if (!deleteDuplicateList.contains(onlyMark)) {
									deleteDuplicateList.add(onlyMark);
								}
							}
							
							if(!toCreateBomViewMap.containsKey(item_id)){
								toCreateBomViewMap.put(item_id, itemRevision);
							}
							
							newTableModel_1.addRow(new String[] {
									"",
									properties[0],
									properties[1],
									itemRevision.getProperty("u8_code"),
									properties[2],
									properties[3],
									properties[4],
									properties[5],
									properties[6] });
						}
					} catch (Exception e) {
						MessageBox.post("��ȡ�䷽�����Գ�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
						CloseWindow(topBomLine);
						e.printStackTrace();
						throw e;
					}
				}
				
				CloseWindow(topBomLine);
			}
		}
		
		// ���������һ�л���ɾ��һ�в����ĸ���
		private void UpdateComboBoxItems(boolean ifAdd){
			DefaultComboBoxModel<Integer> defaultComboBoxModel = (DefaultComboBoxModel<Integer>)comboBox.getModel();
			
			if(ifAdd){
				defaultComboBoxModel.addElement(defaultComboBoxModel.getSize() + 1);
			}else{
				int size = defaultComboBoxModel.getSize();
				if(size == 1){
					return;
				}else{
					defaultComboBoxModel.removeElementAt(size - 1);
				}
			}
			
//			comboBox.setSelectedItem(defaultComboBoxModel.getSize());
		}
		
		private void SetComboBoxSelectedItem(int index) {
			comboBox.setSelectedItem(index);
		}

		// ���������б�ĸ���
		private void UpdateComboBoxItems1(){
			DefaultComboBoxModel<Integer> defaultComboBoxModel = (DefaultComboBoxModel<Integer>)comboBox.getModel();
			defaultComboBoxModel.removeAllElements();
			defaultComboBoxModel.addElement(1);
			
			comboBox.setSelectedItem(defaultComboBoxModel.getSize());
		}
		
		private boolean InsertDataIntoTable(int index, String item_id) throws Exception {
			index = index - 1;
			
			DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
			TCComponent[] queryComponents = GetSearchResult(query1, new String[] { "����� ID" }, new String[] { item_id });
			if (queryComponents == null || queryComponents.length == 0) {
				JOptionPane.showMessageDialog(contentPane, "�Ҳ���IdΪ" + item_id + "��Item", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			TCComponentItem item = (TCComponentItem)queryComponents[0];
			TCComponentItemRevision itemRevision = item.getLatestItemRevision();
			toCreateBomViewMap.put(item_id, itemRevision);
			
			Vector<String> vector1 = new Vector<String>();
			vector1.add("");
			vector1.add(item.getProperty("item_id"));
			vector1.add(itemRevision.getProperty("object_name"));
			vector1.add(itemRevision.getProperty("u8_code"));
			vector1.add("");
			vector1.add("");
			vector1.add("");
			vector1.add("");
			vector1.add("");
			
			newTableModel.insertRow(index, vector1);
			return true;
		}
		
		private Integer[] GetComboBoxItems(){
			DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
			int rowCount = newTableModel.getRowCount();
			if(rowCount == 0){
				return new Integer[] { 1 };
			}
			
			ArrayList<Integer> returnList = new ArrayList<Integer>(); 
			for(int i = 0; i < rowCount; i++){
				returnList.add(i + 1);
			}
			// (���Ӧ�ö��һ�б�ʾ���뵽���)
			returnList.add(rowCount + 1);
			
			return (Integer[]) returnList.toArray(new Integer[returnList.size()]);
		}
		
		private void CreatePopupMenu() {  
			m_popupMenu = new JPopupMenu();

			JMenuItem delMenItem = new JMenuItem();
			delMenItem.setText("  ɾ��  ");
			delMenItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int row = table_1.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					newTableModel.removeRow(row);
					UpdateComboBoxItems(false);
				}
			});
			m_popupMenu.add(delMenItem);
			
			JMenuItem delMenItem1 = new JMenuItem();
			delMenItem1.setText("  ����  ");
			delMenItem1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int row = table_1.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					
					@SuppressWarnings("unchecked")
					Vector<Vector<Object>> vector = newTableModel.getDataVector();
					Vector<Object> obj = new Vector<Object>(vector.get(row));
					newTableModel.insertRow(row + 1, obj);
					UpdateComboBoxItems(true);
				}
			});
			m_popupMenu.add(delMenItem1);
			
//			JMenuItem delMenItem2 = new JMenuItem();
//			delMenItem2.setText("  ��ӡ����  ");
//			delMenItem2.addActionListener(new java.awt.event.ActionListener() {
//				public void actionPerformed(java.awt.event.ActionEvent evt) {
//					int row = table_1.getSelectedRow();
//					if (row == -1) {
//						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
//						return;
//					}
//					
//					System.out.println("����Ϊ�� " + row);
//				}
//			});
//			m_popupMenu.add(delMenItem2);
	    }
		
		// ��дϵͳ����dispose��Ϊ������frameΪnull
		public void dispose() {
			super.dispose();
			frame = null;
		}

//		@Override
	//  public void paint(Graphics g) {
//			super.paint(g);
//			g.setColor(Color.GRAY);//���õ�һ���ߵ���ɫ
//			g.drawLine(20, 120, 880, 120);//����һ���� ��(50,50) ����  (100,100)
//			g.drawLine(20, 341, 880, 341);//����һ���� ��(50,50) ����  (100,100)
//			//g.drawLine(450, 300, 450, 400);//����һ���� ��(50,50) ����  (100,100)
////	      g.setColor(Color.BLUE);
////	      g.drawLine(50, 50, 50, 150);//���ڶ����� ��(50,50) ����  (50,150)
////	      g.setColor(Color.GREEN);
////	      g.drawLine(50, 150, 100, 100);//���������� ��(50,150) ����  (100,100)
	//  }
		
		@SuppressWarnings("unused")
		private Rectangle GetWaitingUIRectangle(){
	        int offsetX = 20;
	        int offsetY = 100;
	        
	        Rectangle rectangle = GetRectangle();
	        int centerX = rectangle.x + offsetX;
	        int centerY = rectangle.y + offsetY;
	        Rectangle newRectangle = new Rectangle(centerX, centerY, 300, 220);
	        return newRectangle;
	    }
	    private Rectangle GetRectangle() {
	        return this.getBounds();
	    }
	}
	
	/***
	 * 
	 * @see name---ԭ����</br>
	 * code---���Ӵ���</br>
	 * provider---��Ӧ��</br>
	 * price---�ɱ���λ</br>
	 * unit---��׼��λ</br>
	 * 
	 */
	class MaterialBeanJr{
		String object_name = "";
		String u8_code = "";
		String u8_supplierinfo = "";
		String u8_price = "";
		String u8_uom = "";
		
		public MaterialBeanJr(String object_name, String u8_code, String u8_supplierinfo, String u8_price, String u8_uom){
			this.object_name = object_name;
			this.u8_code = u8_code;
			this.u8_supplierinfo = u8_supplierinfo;
			this.u8_price = u8_price;
			this.u8_uom = u8_uom;
		}
	}
	
	class CheckBomViewBean{
		boolean ifModifiable = true;
		boolean ifHasBom = true;
	}
}
