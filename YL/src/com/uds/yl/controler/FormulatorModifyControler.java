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


//配方搭建起新增版本
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
		
		// 首先检查是否有权限、是否有视图
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
				this.modelType = "新增模式";
			}
		} else {
			this.modelType = "编辑模式";
			MessageBox.post("界面中配方仅供查看\n如需修改，请先进行修订操作", "提示", MessageBox.INFORMATION);
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
					MessageBox.post("搭建器运行出错，请联系管理员查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
					e.printStackTrace();
				}
			}
		};
		ProgressBar progressBar = new ProgressBar("正在获取配方数据", thread, rectangleObj);
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
			if(is_modifiable.equals("否")){
				bean.ifModifiable = false;
			} else {
				bean.ifModifiable = true;
			}
		}else{
			// 如果没有视图，则需要检查版本是否可修改，因为后面需要在版本上新建视图
			bean.ifHasBom = false;
			String is_modifiable = itemRev.getProperty("is_modifiable");
			if(is_modifiable.equals("否")){
				bean.ifModifiable = false;
			} else {
				bean.ifModifiable = true;
			}
		}
		
		return bean;
	}

	class FormulaBomCreate extends JFrame {
		
		// 界面相关变量
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
		
		// 用于选择插入到配方表哪一行的bomboBox
		private JComboBox<Integer> comboBox;
		
		// TC相关变量
		private TCComponentQuery query;  // 用于查询器 U8_MaterialRevision
		private TCComponentQuery query1; // 用于查询器 零组件...
		private Map<String, TCComponentItemRevision> toCreateBomViewMap; // 用于暂存搭建BomView的Map
		private ArrayList<String> deleteDuplicateList; // 用于关系下配方BomLine去重操作
		
		public FormulaBomCreate() throws Exception {
			
			// 创建右键菜单先
			CreatePopupMenu();
			
			// 初始化查询器
			if(query == null){
				String groupName = UserInfoSingleFactory.getInstance().getTCSession().getGroup().toString();
				
				if(groupName.contains("酸奶")){
					query = InitializeQuery(QueryClassConst.U8_MATERIAL_REV_YG.getValue());
				}else if(groupName.contains("液奶")){
					query = InitializeQuery(QueryClassConst.U8_MATERIAL_REV_LM.getValue());
				}else{
					MessageBox.post("非酸奶/液奶用户登入，无法获取查询器", "提示", MessageBox.INFORMATION);
					throw new Exception("非酸奶/液奶用户登入，无法获取查询器");
				}
				
				// 本地TC测试时使用
//				query = InitializeQuery("U8_MaterialRevision");
				
				if(query == null){
					return;
				}
			}
			if(query1 == null){
				query1 = InitializeQuery("零组件...");
				if(query1 == null){
					return;
				}
			}
			
			// 初始化暂存搭建BomView的Map
			toCreateBomViewMap = new HashMap<String, TCComponentItemRevision>();
			deleteDuplicateList = new ArrayList<String>();
			
			// 初始化表格Model数据
			InitializeTableModels();			
			
			setAlwaysOnTop(true);
			setResizable(false);
			setType(Type.UTILITY);
			setTitle(" 组合配方构建器");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// 根据传来的Rectangle设置窗口位置
			int centerX = rectangleObj.x + rectangleObj.width / 2;
			int centerY = rectangleObj.y + rectangleObj.height / 2;
			setBounds(centerX - 450, centerY - 325, 900, 650);
			
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel label = new JLabel("配方/小料：");
			label.setBounds(25, 16, 70, 15);
			contentPane.add(label);
			
			textField = new JTextField();
			textField.setColumns(10);
			textField.setEditable(false);
			textField.setBounds(101, 13, 80, 21);
			contentPane.add(textField);		
			
			JLabel label_1 = new JLabel("版本：");
			label_1.setBounds(236, 16, 70, 15);
			contentPane.add(label_1);
			
			textField_1 = new JTextField();
			textField_1.setColumns(10);
			textField_1.setEditable(false);
			textField_1.setBounds(321, 13, 80, 21);
			contentPane.add(textField_1);
			
			// 初始化 配方/小料 和 版本
			InitializeTextField();
			
			JLabel label_2 = new JLabel("原料名：");
			label_2.setBounds(25, 58, 70, 15);
			contentPane.add(label_2);
			
			textField_2 = new JTextField();
			textField_2.setColumns(10);
			textField_2.setBounds(101, 55, 80, 21);
			contentPane.add(textField_2);
			
			JLabel label_3 = new JLabel("电子代码：");
			label_3.setBounds(236, 58, 70, 15);
			contentPane.add(label_3);
			
			textField_3 = new JTextField();
			textField_3.setColumns(10);
			textField_3.setBounds(321, 55, 80, 21);
			contentPane.add(textField_3);
			
			JLabel label_4 = new JLabel("供应商：");
			label_4.setBounds(454, 58, 70, 15);
			contentPane.add(label_4);
			
			textField_4 = new JTextField();
			textField_4.setColumns(10);
			textField_4.setEditable(false);
			textField_4.setBounds(531, 55, 80, 21);
			contentPane.add(textField_4);
			
			JButton btnNewButton = new JButton("搜索");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = textField_2.getText();
					String code = textField_3.getText();
					//String provider = textField_4.getText();

					if (name.equals("") && code.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "请输入至少一个条件进行查询", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					while(newTableModel.getRowCount() != 0){
						newTableModel.removeRow(newTableModel.getRowCount() - 1);
					}
					
					String keys = "";
					String valuse = "";
					// 查询的输入
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
						JOptionPane.showMessageDialog(contentPane, "查询过程出错，请联系管理员查看控制台输出的错误信息", "提示", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}finally{
						WaitingUI.CloseProcessBar();
					}
					if(components == null || components.length == 0){
						JOptionPane.showMessageDialog(contentPane, "没有查询结果", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if(components.length > 2000){
						JOptionPane.showMessageDialog(contentPane, "查询结果过多，请重新输入更加具体的条件查询", "提示", JOptionPane.INFORMATION_MESSAGE);
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
						
						// 默认选中第一行数据
						table.setRowSelectionInterval(0, 0);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "获取版本属性出错，请联系管理员查看控制台输出的错误信息", "提示", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}
				}
			});
			btnNewButton.setBounds(661, 54, 93, 23);
			contentPane.add(btnNewButton);
			
			JLabel label_5 = new JLabel("可选原料表：");
			label_5.setBounds(25, 98, 100, 15);
			contentPane.add(label_5);
			
			// 可选原料表其他设置
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBounds(25, 120, 845, 152);
			contentPane.add(scrollPane);
			
			JButton btnNewButton_1 = new JButton("清空列表");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "列表已经为空", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					while(newTableModel.getRowCount() != 0){
						newTableModel.removeRow(newTableModel.getRowCount() - 1);
					}
				}
			});
			btnNewButton_1.setBounds(236, 279, 93, 23);
			contentPane.add(btnNewButton_1);
			
			JLabel label_6 = new JLabel("插入到配方表第");
			label_6.setBounds(365, 283, 100, 15);
			contentPane.add(label_6);
			label_6.setVisible(false);
			
			Integer[] comboBoxItems = GetComboBoxItems();
			DefaultComboBoxModel<Integer> defaultComboBoxModel = new DefaultComboBoxModel<Integer>(comboBoxItems);
			
			comboBox = new JComboBox<Integer>();
			comboBox.setBounds(460, 279, 45, 23);
			comboBox.setModel(defaultComboBoxModel);
			// 默认选中comboBox的最后一个选项
			comboBox.setSelectedItem(comboBoxItems.length);
			contentPane.add(comboBox);
			comboBox.setVisible(false);
			
			JLabel label_7 = new JLabel("行");
			label_7.setBounds(515, 283, 54, 15);
			contentPane.add(label_7);
			 label_7.setVisible(false);
			
			JButton btnNewButton_2 = new JButton("插入");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中可选原料表中的行", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					// 判断是否已经存在该原料
					String item_id = table.getModel().getValueAt(row, 0).toString();
					// 20180330jiangren组合配方搭建器允许一个原料
//					javax.swing.table.TableModel tableModel = table_1.getModel();
//					int rowCount = tableModel.getRowCount();
//					for(int i = 0; i < rowCount; i++){
//						String id = tableModel.getValueAt(i, 1).toString();
//						if(id.equals(item_id)){
//							JOptionPane.showMessageDialog(contentPane, "配方表中已经存在该原料", "提示", JOptionPane.INFORMATION_MESSAGE);
//							return;
//						}
//					}
					
					// 20180412jiangren组合配方搭建器隐藏了下拉框 
//					int comboBoxItem = (int) comboBox.getSelectedItem();
					int comboBoxItem = table_1.getSelectedRow();
					if (comboBoxItem == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中配方表中的行", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					comboBoxItem = comboBoxItem + 2;
					
					try {
						boolean ifSuccess = InsertDataIntoTable(comboBoxItem, item_id);
						// 成功之后才去更新comboBox
						if(ifSuccess){
							UpdateComboBoxItems(true);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "插入到配方表出错，请联系管理员查看控制台输出的错误信息", "提示", JOptionPane.INFORMATION_MESSAGE);
						e1.printStackTrace();
						return;
					}
				}
			});
			btnNewButton_2.setBounds(551, 279, 93, 23);
			contentPane.add(btnNewButton_2);
			
			JLabel label_8 = new JLabel("配方表：");
			label_8.setBounds(25, 321, 100, 15);
			contentPane.add(label_8);
			
			// 配方表 其他设置
			table_1.setUI(new DragDropRowTableUI());
			table_1.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// （鼠标右键是BUTTON3）
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
						int focusedRowIndex = table_1.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						// 将表格所选项设为当前右键点击的行
						table_1.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
						// 弹出菜单
						m_popupMenu.show(table_1, e.getX(), e.getY());
						
						SetComboBoxSelectedItem(focusedRowIndex+1);
					}
					// （鼠标左键是BUTTON1）
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
			
			JButton btnNewButton_3 = new JButton("清空列表");
			btnNewButton_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "列表已经为空", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					int n = JOptionPane.showConfirmDialog(contentPane, "确认清空吗", "确认对话框", JOptionPane.YES_NO_OPTION);
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
			
			JButton btnNewButton_4 = new JButton("搭建配方");
			btnNewButton_4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 取消表格的编辑状态，为了获取到最后一个正在编辑的单元格的值
					if (table_1.isEditing()) 
						table_1.getCellEditor().stopCellEditing();
					
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					if(newTableModel.getRowCount() == 0){
						JOptionPane.showMessageDialog(contentPane, "列表为空，无法创建", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					ArrayList<TCComponentItemRevision> itemRevisionList = new ArrayList<TCComponentItemRevision>(); 
					@SuppressWarnings("unchecked")
					Vector<Vector<Object>> vector = newTableModel.getDataVector();
					for(Vector<Object> vector1 : vector){
						String item_id = vector1.get(1).toString();
						itemRevisionList.add(toCreateBomViewMap.get(item_id));
					}
					
					// 开始构造视图
					CreateBomView(itemRevisionList);
				}
			});
			btnNewButton_4.setBounds(395, 579, 93, 23);
			if(!ifModifiable){
				btnNewButton_4.setEnabled(false);
			}
			contentPane.add(btnNewButton_4);
			
			JButton btnNewButton_5 = new JButton("取消");
			btnNewButton_5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnNewButton_5.setBounds(551, 579, 93, 23);
			contentPane.add(btnNewButton_5);
			
			// (JPanel用作分割线)
			JPanel panel = new JPanel();
			panel.setBounds(18, 312, 846, 1);
			panel.setBorder(new LineBorder(Color.GRAY));
			contentPane.add(panel);
			
			// (JPanel用作分割线)
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(Color.GRAY));
			panel_1.setBounds(18, 89, 846, 1);
			contentPane.add(panel_1);
		}


		// TC函数部分-------------------------------------------------------------------------------------------------------------------------- //
		
		private void CloseWindow(TCComponentBOMLine topBomLine) {

			// 关闭并保存Bom View
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
					// 没有视图就去创建
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
				
				// 必须先关闭视图后再重新搭建，否则BomLine会按照之前的排序(查找编号属性)
				CloseWindow(topBomLine);
				topBomLine = BomUtil.getTopBomLine(itemRev, Const.Formulator.MATERIALBOMNAME);
				if(topBomLine == null){
					topBomLine = BomUtil.setBOMViewForItemRev(itemRev);
					
					if(topBomLine == null){
						JOptionPane.showMessageDialog(contentPane, "创建视图出错，请联系管理员查看控制台输出的错误信息", "提示", JOptionPane.INFORMATION_MESSAGE);
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
				
				JOptionPane.showMessageDialog(contentPane, "配方创建完成", "提示", JOptionPane.INFORMATION_MESSAGE);
				dispose();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(contentPane, "构造配方过程出错，请联系管理员查看控制台输出的错误信息", "提示", JOptionPane.INFORMATION_MESSAGE);
				CloseWindow(topBomLine);
				e.printStackTrace();
			}
		}
		
		private TCComponentQuery InitializeQuery(String queryName){
			TCComponentQuery query = QueryUtil.getTCComponentQuery(queryName);
			if (query == null) {
				MessageBox.post("没有找到查询器\n" + queryName + "\n请联系管理员配置", "提示", MessageBox.INFORMATION);
			}			
			return query;
		}
		
		public TCComponent[] GetSearchResult(TCComponentQuery query, String[] propertyName, String[] values) throws Exception {
			TCComponent[] results = query.execute(propertyName, values);
			return results;
		}
		
		
		// 界面其他函数部分-------------------------------------------------------------------------------------------------------------------------- //
		
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
			// 初始化 可选原料表 数据
			columnNames = new Vector<Object>();
			columnNames.add("ID");
			columnNames.add("原料名");
			columnNames.add("电子代码");
			columnNames.add("供应商");
			columnNames.add("单位成本(元/千克)");
			columnNames.add("基准单位");
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
			// 设置列0隐藏(setMinWidth(0)函数要设置在setMaxWidth(0)函数之前才成功隐藏选中列)
			table.getColumnModel().getColumn(0).setMinWidth(0);
			table.getColumnModel().getColumn(0).setMaxWidth(0);

			
			// 初始化 配方表 数据
			columnNames_1 = new Vector<Object>();
			columnNames_1.add("行号");
			columnNames_1.add("ID");
			columnNames_1.add("原料名");
			columnNames_1.add("电子代码");
			columnNames_1.add("投料量(千克)");
			columnNames_1.add("原料替换方案");
			columnNames_1.add("组合替换方案");
			columnNames_1.add("组合替换说明");
			columnNames_1.add("小料名称");
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
					// 设置以下列可编辑
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
			
			// 根据不同的模式构造Table
			if (modelType.equals("编辑模式")) {
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
					// 配方版本---U8_FormulaRevision
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
			
			// 设置列0最大宽度
			table_1.getColumnModel().getColumn(0).setMaxWidth(40);
			table_1.getColumnModel().getColumn(1).setMaxWidth(70);
			
			// 设置列1隐藏(setMinWidth(0)函数要设置在setMaxWidth(0)函数之前才成功隐藏选中列)
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
							// 使用 id和投料量两个属性去重
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
						MessageBox.post("获取配方行属性出错，请联系管理员查看控制台输出的错误信息", "提示", MessageBox.INFORMATION);
						CloseWindow(topBomLine);
						e.printStackTrace();
						throw e;
					}
				}
				
				CloseWindow(topBomLine);
			}
		}
		
		// 针对于增加一行或者删除一行操作的更新
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

		// 针对于清空列表的更新
		private void UpdateComboBoxItems1(){
			DefaultComboBoxModel<Integer> defaultComboBoxModel = (DefaultComboBoxModel<Integer>)comboBox.getModel();
			defaultComboBoxModel.removeAllElements();
			defaultComboBoxModel.addElement(1);
			
			comboBox.setSelectedItem(defaultComboBoxModel.getSize());
		}
		
		private boolean InsertDataIntoTable(int index, String item_id) throws Exception {
			index = index - 1;
			
			DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
			TCComponent[] queryComponents = GetSearchResult(query1, new String[] { "零组件 ID" }, new String[] { item_id });
			if (queryComponents == null || queryComponents.length == 0) {
				JOptionPane.showMessageDialog(contentPane, "找不到Id为" + item_id + "的Item", "提示", JOptionPane.INFORMATION_MESSAGE);
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
			// (最后应该多加一行表示插入到最后)
			returnList.add(rowCount + 1);
			
			return (Integer[]) returnList.toArray(new Integer[returnList.size()]);
		}
		
		private void CreatePopupMenu() {  
			m_popupMenu = new JPopupMenu();

			JMenuItem delMenItem = new JMenuItem();
			delMenItem.setText("  删除  ");
			delMenItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int row = table_1.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table_1.getModel();
					newTableModel.removeRow(row);
					UpdateComboBoxItems(false);
				}
			});
			m_popupMenu.add(delMenItem);
			
			JMenuItem delMenItem1 = new JMenuItem();
			delMenItem1.setText("  复制  ");
			delMenItem1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int row = table_1.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
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
//			delMenItem2.setText("  打印行数  ");
//			delMenItem2.addActionListener(new java.awt.event.ActionListener() {
//				public void actionPerformed(java.awt.event.ActionEvent evt) {
//					int row = table_1.getSelectedRow();
//					if (row == -1) {
//						JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
//						return;
//					}
//					
//					System.out.println("行数为： " + row);
//				}
//			});
//			m_popupMenu.add(delMenItem2);
	    }
		
		// 重写系统方法dispose，为了设置frame为null
		public void dispose() {
			super.dispose();
			frame = null;
		}

//		@Override
	//  public void paint(Graphics g) {
//			super.paint(g);
//			g.setColor(Color.GRAY);//设置第一条线的颜色
//			g.drawLine(20, 120, 880, 120);//画第一条线 点(50,50) 到点  (100,100)
//			g.drawLine(20, 341, 880, 341);//画第一条线 点(50,50) 到点  (100,100)
//			//g.drawLine(450, 300, 450, 400);//画第一条线 点(50,50) 到点  (100,100)
////	      g.setColor(Color.BLUE);
////	      g.drawLine(50, 50, 50, 150);//画第二条线 点(50,50) 到点  (50,150)
////	      g.setColor(Color.GREEN);
////	      g.drawLine(50, 150, 100, 100);//画第三条线 点(50,150) 到点  (100,100)
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
	 * @see name---原料名</br>
	 * code---电子代码</br>
	 * provider---供应商</br>
	 * price---成本单位</br>
	 * unit---基准单位</br>
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
