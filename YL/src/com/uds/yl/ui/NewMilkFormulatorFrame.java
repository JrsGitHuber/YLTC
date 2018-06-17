package com.uds.yl.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.INewMilkFormulatorService;
import com.uds.yl.service.impl.NewMilkFormulatorServiceImpl;
import com.uds.yl.ui.dragtreetable.MyDefaultTreeCellRenderer;
import com.uds.yl.ui.dragtreetable.MyGestureListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener.IndexTableRemoveListener;
import com.uds.yl.ui.dragtreetable.RemoveTargetListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener.RemoveNodeDragListener;
import com.uds.yl.ui.dragtreetable.Table2TableGestureListener.TableToTableNodeDragListener;
import com.uds.yl.ui.dragtreetable.Table2TableTargetListener;
import com.uds.yl.ui.dragtreetable.MyGestureListener.NodeDragListener;
import com.uds.yl.ui.dragtreetable.MyTargetListener.FormulatorUpdateListener;
import com.uds.yl.ui.dragtreetable.MyTargetListener;
import com.uds.yl.ui.dragtreetable.Table2TableGestureListener;
import com.uds.yl.ui.dragtreetable.Table2TableTargetListener.IndexTableInsertListener;


public class NewMilkFormulatorFrame extends JFrame implements NodeDragListener, FormulatorUpdateListener,
				TableToTableNodeDragListener, IndexTableInsertListener,
				RemoveNodeDragListener, IndexTableRemoveListener{
	
	private INewMilkFormulatorService service = new NewMilkFormulatorServiceImpl();//奶粉配方搭建器的服务类

	private JPanel contentPane;
	
	private JTree mFormulatorTree;//结构树
	private JTable mFormulatorTable;//展示整个配方信息的表格
	private JTable mMaterialTable;//原料信息表
	private JTable mIndexTable;//指标信息表
	private JTable mLableTable;//标签信息表
	
	private DefaultTreeModel mFormulatorTreeModel;//配方树的数据模型
	private DefaultTableModel mMaterialTableModel;//原料信息表的数据模型
	private DefaultTableModel mIndexTableModel;//指标信息表的数据模型
	private DefaultTableModel mLableTableModel;//标签信息表的数据模型
	private DefaultTableModel mFormulatorModel;//配方信息表中的数据模型
	
	private TreePath mRightPath;//右键节点的path
	
	private JPopupMenu mMenu ;//树的右击菜单
	private JMenuItem mDeleteMenuItem;//删除节点菜单
	private JMenuItem mInsertFormulatorMenuItem;//插入基粉菜单
	private JMenuItem mInsertNutritionMenuItem;//插入营养包菜单
	
	private JComboBox<String> mMaterialTypeCombox;//原料类型下拉框
	private JComboBox<String> mDryLossCombox;//干法损耗下拉框
	private JComboBox<String> mWetLossCombox;//湿法损耗下拉框
	private JComboBox<String> mDateLossCombox;//保质期损耗下拉框
	private JComboBox<String> mLossTypeCombox;//损耗类型的下拉框  
	
	private JButton mDryLossShowBtn;//查看干法损耗
	private JButton mWetLossShowBtn;//查看湿法损耗
	private JButton mDateLossShowBtn;//查看保质期损耗
	
	private JTextField waterEdt;//用来显示选中基粉节点的含水量
	
	private JButton mOkBtn;//确定
	private JButton mCancleBtn;//取消
	
	
	private NodeBean mRootNodeBean;//跟节点
	private List<TCComponentItemRevision> mDryLossItemRevList;//所有的干法损耗
	private TCComponentItemRevision mSelectedDryLossItemRev;//选中的干法损耗
	private List<TCComponentItemRevision> mWetLossItemRevList;//所有的湿法损耗
	private TCComponentItemRevision mSelectedWetLossItemRev;//选中的湿法损耗
	private List<TCComponentItemRevision> mDateLossItemRevList;//所有的保质期损耗
	private TCComponentItemRevision mSelectedDateLossItemRev;//选中的保质期损耗
	
	
	private NodeBean mSelectedNodeBean;//树结构中选中 原料中选中 和 整个配方中选中
	
	private List<NodeBean> mMaterialTableNodeBeanList;//原料 table 中的所有的原料的实体类
	private NodeBean mSelectedMaterialTableNodeBean;//原料 table选中的原料
	
	private List<NodeBean> mIndexTableNodeBeanList;//指标table 中的所有指标的实体类
	private NodeBean mSelectedIndexTableNodeBean;//指标table 中选中的指标的实体类
	
	private List<NodeBean> mFormulatorTableNodeBeanList;//配方table 中的所有的实体类
	private NodeBean mSelectedFormulatorTableNodeBean;//配方table 中选中的实体类
	
	private List<NodeBean> mLableTableNodeBeanList;//标签table 中的所有的实体类
	private NodeBean mSelectedLableTableNodeBean;//标签table 中的选中的实体类
	
	private ProgressBarDialog mProgressBarDialog=null;//进度条
	private MyGestureListener mGestureListener;//拖动监听器
	private MyTargetListener<NodeBean> mTargetListener;//拖动到目标上的监听器
	private Table2TableGestureListener mTable2TableGestureListener;
	private Table2TableTargetListener<NodeBean> mTable2TableTargetListener;
	private RemoveGestureListener mRemoveGestureListener;
	private RemoveTargetListener<NodeBean> mRemoveTargetListener;

	public NewMilkFormulatorFrame(NodeBean rootNodeBean) {
		
		this.mRootNodeBean = rootNodeBean;
		setResizable(false);
		setBounds(100, 10, 1421, 830);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(45, 20, 229, 274);
		contentPane.add(scrollPane);
		
		
		
		
		mFormulatorTree = new JTree(mRootNodeBean.node);
		scrollPane.setViewportView(mFormulatorTree);
		mFormulatorTree.setCellRenderer(new MyDefaultTreeCellRenderer());
		
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(20, 437, 282, 304);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 54, 262, 235);
		panel.add(scrollPane_1);
		
		mFormulatorTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==0 || ColIndex==1){
					return false;
				}
				return true;
			}
		};
		mFormulatorTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","原料名称", "投料量"
			}
		));
		scrollPane_1.setViewportView(mFormulatorTable);
		
		JLabel lblNewLabel = new JLabel("整个配方详细信息");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 15));
		lblNewLabel.setBounds(60, 10, 212, 34);
		panel.add(lblNewLabel);
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(737, 38, 642, 699);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel label = new JLabel("标签信息");
		label.setFont(new Font("宋体", Font.PLAIN, 15));
		label.setBounds(309, 10, 97, 34);
		panel_1.add(label);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 38, 622, 645);
		panel_1.add(scrollPane_2);
		
		mLableTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==4){//只能修改标签值
					return true;
				}
				return false;
			}
		};
		mLableTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","指标名称","国标值","内控值", "标签值","最小值","中间值","最大值"
			}
		));
		scrollPane_2.setViewportView(mLableTable);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(315, 20, 393, 274);
		contentPane.add(panel_3);
		panel_3.setLayout(null);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 62, 355, 201);
		panel_3.add(scrollPane_3);
		
		mMaterialTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				return false;
			}
		};
		mMaterialTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","名称","最小值","中间值","最大值"
			}
		));
		scrollPane_3.setViewportView(mMaterialTable);
		
		JLabel lblNewLabel_1 = new JLabel("原料类别：");
		lblNewLabel_1.setBounds(23, 24, 85, 15);
		panel_3.add(lblNewLabel_1);
		
		mMaterialTypeCombox = new JComboBox();
		mMaterialTypeCombox.setBounds(118, 21, 183, 21);
		panel_3.add(mMaterialTypeCombox);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(326, 437, 379, 304);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel label_1 = new JLabel("指标");
		label_1.setBounds(146, 10, 98, 18);
		label_1.setFont(new Font("宋体", Font.PLAIN, 15));
		panel_2.add(label_1);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(10, 38, 359, 249);
		panel_2.add(scrollPane_4);
		
		mIndexTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==0 || ColIndex==1){
					return false;
				}
				return true;
			}
			
		};
		mIndexTable.setModel(new DefaultTableModel(
			new Object[][] {
					{null, null},
			},
			new String[] {
				"ID","名称", "最小值","中间值","最大值"
			}
		));
		scrollPane_4.setViewportView(mIndexTable);
		
		JLabel label_2 = new JLabel("干法损耗：");
		label_2.setBounds(315, 316, 77, 15);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel("湿法损耗：");
		label_3.setBounds(315, 350, 77, 15);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("保质期损耗：");
		label_4.setBounds(315, 390, 83, 15);
		contentPane.add(label_4);
		
		mDryLossCombox = new JComboBox();
		mDryLossCombox.setBounds(420, 313, 144, 21);
		contentPane.add(mDryLossCombox);
		
		mWetLossCombox = new JComboBox();
		mWetLossCombox.setBounds(420, 347, 144, 21);
		contentPane.add(mWetLossCombox);
		
		mDateLossCombox = new JComboBox();
		mDateLossCombox.setBounds(420, 387, 144, 21);
		contentPane.add(mDateLossCombox);
		
		mDryLossShowBtn = new JButton("查看");
		mDryLossShowBtn.setBounds(598, 312, 93, 23);
		contentPane.add(mDryLossShowBtn);
		
		mWetLossShowBtn = new JButton("查看");
		mWetLossShowBtn.setBounds(598, 346, 93, 23);
		contentPane.add(mWetLossShowBtn);
		
		mDateLossShowBtn = new JButton("查看");
		mDateLossShowBtn.setBounds(598, 386, 93, 23);
		contentPane.add(mDateLossShowBtn);
		
		mOkBtn = new JButton("确定");
		mOkBtn.setBounds(268, 764, 93, 23);
		contentPane.add(mOkBtn);
		
		mCancleBtn = new JButton("取消");
		mCancleBtn.setBounds(788, 764, 93, 23);
		contentPane.add(mCancleBtn);
		
		JLabel label_5 = new JLabel("损耗类型：");
		label_5.setBounds(34, 347, 77, 15);
		contentPane.add(label_5);
		
		JComboBox lossTypeCombox = new JComboBox();
		lossTypeCombox.setBounds(130, 344, 144, 21);
		contentPane.add(lossTypeCombox);
		

		JLabel label_6 = new JLabel("基粉含水量：");
		label_6.setBounds(34, 390, 77, 15);
		contentPane.add(label_6);
		
		waterEdt = new JTextField();
		waterEdt.setBounds(133, 387, 115, 21);
		contentPane.add(waterEdt);
		waterEdt.setColumns(10);
		waterEdt.setEditable(false);
		
		
		
        //原料Table中拖动到树结构中
		mGestureListener = new MyGestureListener();
		mGestureListener.setNodeDragListener(this);
		//拖动到目标上监听器
		mTargetListener = new MyTargetListener<NodeBean>();
		mTargetListener.setFormulatorUpdateListener(this);
		
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mMaterialTable, DnDConstants.ACTION_COPY_OR_MOVE, mGestureListener );
    	new DropTarget(mFormulatorTree,mTargetListener);//监听 拖动到的目标
    	
    	
    	//标签表中的拖动数据作为数据源
    	mTable2TableGestureListener = new Table2TableGestureListener();
    	mTable2TableGestureListener.setNodeDragListener(this);
    	//指标表格作为拖动目的地
    	mTable2TableTargetListener = new Table2TableTargetListener<NodeBean>();
    	mTable2TableTargetListener.setIndexTableInsertListener(this);
    	
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mLableTable, DnDConstants.ACTION_COPY_OR_MOVE, mTable2TableGestureListener );
    	new DropTarget(mIndexTable,mTable2TableTargetListener);//监听 拖动到的目标
    	
    	//标签表中的拖动数据作为数据源
    	mRemoveGestureListener = new RemoveGestureListener();
    	mRemoveGestureListener.setRemoveNodeDragListener(this);
    	mRemoveGestureListener.setIndexTableRemoveListener(this);
    	//指标表格作为拖动目的地
    	mRemoveTargetListener = new RemoveTargetListener<NodeBean>();
//    	mRemoveTargetListener.setIndexTableRemoveListener(this);
    	
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mIndexTable, DnDConstants.ACTION_COPY_OR_MOVE, mRemoveGestureListener );
//    	new DropTarget(contentPane,mRemoveTargetListener);//监听 拖动到的目标
    	
        //初始化右键树弹出的菜单
    	initMenuAndTree();
        
        //初始化 原料Table  mMaterialTable
        initMaterialTable();
        
        //初始化指标table
        initIndexTable();
        
        //初始化整个配方table的信息
        initFormulatorTable();
        
        //初始化lableTable
        initLableTable();
        
        //初始化三个损耗 损耗类型Combox
        initLoss();
        
        //初始所有 table 的监听事件
        intiTableListener();
        
        //初始化功能按钮
        initBtnListener();
        
        
        
       
	}

	
	/**
	 * 初始化界面上的功能按钮
	 */
	private void initBtnListener() {
		//TODO:确定
		
		mCancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		//TODO: 查看按钮
		
	}


	/**
	 * 初始化四个 table 的监听事件
	 */
	private void intiTableListener() {
		// TODO 初始化 三个 table的监听事件
		
		//配方信息表
		mFormulatorModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				mSelectedFormulatorTableNodeBean = mFormulatorTableNodeBeanList.get(row);
				if(column==2){//投料量的话
					mSelectedFormulatorTableNodeBean.U8_inventory = mFormulatorModel.getValueAt(row, column)==null ? "" :mFormulatorModel.getValueAt(row, column).toString();
				}
			}
		});
		
		//指标信息表
		mIndexTableModel = (DefaultTableModel) mIndexTable.getModel(); 
		mIndexTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				if(mIndexTableNodeBeanList==null || mIndexTableNodeBeanList.size()<=0){//index比较特殊 可能只有一个空的东西
					return;
				}
				
				mSelectedIndexTableNodeBean = mIndexTableNodeBeanList.get(row);
				if(column==2){//最小值
					mSelectedIndexTableNodeBean.u8Down = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
				if(column==3){//中间值
					mSelectedIndexTableNodeBean.bl_quantity = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
				if(column==4){//最大值
					mSelectedIndexTableNodeBean.u8Up = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
			}
		});
		
		//标签信息表
		mLableTableModel = (DefaultTableModel) mLableTable.getModel(); 
		mLableTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				mSelectedLableTableNodeBean = mLableTableNodeBeanList.get(row);
				if(column==4){//标签值
					mSelectedLableTableNodeBean.u8Up = mLableTableModel.getValueAt(row, column)==null ? "" :mLableTableModel.getValueAt(row, column).toString();
				}
			}
		});
		
	}


	/**
	 *初始化损耗  以及损耗类别以及监听
	 */
	private void initLoss() {
		//TODO:
		
	}

	/**
	 * 初始化 lableTable中的信息
	 */
	private void initLableTable() {
		mLableTableModel = (DefaultTableModel) mLableTable.getModel();
		
		mLableTableNodeBeanList = service.getLableTableNodeBeanListByFormulatorRev(mRootNodeBean.nodeItemRev);
		
		//初始化表格
		for(NodeBean lableNodeBean : mLableTableNodeBeanList){
			mLableTableModel.addRow(new String[]{
					lableNodeBean.itemID,
					lableNodeBean.objectName,
					lableNodeBean.lawValue,
					lableNodeBean.standardValue,
					lableNodeBean.bl_quantity,
					lableNodeBean.minValue,
					lableNodeBean.middleValue,
					lableNodeBean.maxValue
			});
		}
	}

	/**
	 * 初始化整个配方 table
	 */
	private void initFormulatorTable() {
		mFormulatorModel = (DefaultTableModel) mFormulatorTable.getModel();
		
		mFormulatorTableNodeBeanList = service.getFormulatorTableNodeBeanListByRoot(mRootNodeBean);//获取配方表的基本信息
		
		//初始化表格
		for(NodeBean materialNodeBean : mFormulatorTableNodeBeanList){
			mFormulatorModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.U8_inventory
			});
		}
		
		//设置 mFormulatorTable 的选中行监听事件
		mFormulatorTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int selectRowIndex = mFormulatorTable.getSelectedRow();
				if(selectRowIndex == -1) return;
				
				NodeBean selectedMaterialNodeBean = mFormulatorTableNodeBeanList.get(selectRowIndex);
				mSelectedNodeBean = selectedMaterialNodeBean;
				
				updateIndexTable(selectedMaterialNodeBean);//更新指标表格
				
			}
		});
		
		
		
	}

	/**
	 * 初始化 指标table
	 * 
	 * 默认是原料table中的第一个
	 */
	private void initIndexTable() {
		//TODO:
	}

	/**
	 * 初始化 原料表格
	 */
	private void initMaterialTable() {
		//TODO:
		mMaterialTableModel = (DefaultTableModel) mMaterialTable.getModel();
		
		//初始化表格
		mMaterialTableNodeBeanList = service.getMaterialByType("type");
		for(NodeBean materialNodeBean : mMaterialTableNodeBeanList){
			mMaterialTableModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.bl_quantity
			});
		}
		
		//设置监听事件
		mMaterialTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int selectRowIndex = mMaterialTable.getSelectedRow();
				if(selectRowIndex == -1) return;
				
				NodeBean selectedMaterialNodeBean = mMaterialTableNodeBeanList.get(selectRowIndex);
				mSelectedNodeBean = selectedMaterialNodeBean;
				
				updateIndexTable(selectedMaterialNodeBean);//更新指标表格
			}
			
		});
		
		
	}
	

	/**
	 * 初始菜单和菜单的监听事件
	 */
	private void initMenuAndTree() {
		
		mFormulatorTreeModel = (DefaultTreeModel) mFormulatorTree.getModel();
		
		//TODO: 菜单的设计
		mMenu 						= new JPopupMenu();
		mDeleteMenuItem				= new JMenuItem("删除");
		mInsertFormulatorMenuItem 	= new JMenuItem("插入基粉");
		mInsertNutritionMenuItem	= new JMenuItem("插入营养包");
		
		
		//插入营养包的点击事件
		mInsertNutritionMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				final NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				//只有配方下能插入营养包
				if(!selectNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA)) return;
				
				InsertNutritionFrame insertNutritionFrame = new InsertNutritionFrame();
				insertNutritionFrame.setCallBack(new AbstractCallBack() {
					//接收到传递过来的营养包名称
					@Override
					public void setNutritionName(String nutritionName) {
						super.setNutritionName(nutritionName);
						//创建一个原料ITEM 作为营养包
						TCComponentItemRevision nutritionItemRev = service.createNutritionItemByName(nutritionName);
						
						NodeBean childNodeBean =service.initRootNode(nutritionItemRev); 	//nodeBean
						childNodeBean.nodeType = Const.NodeType.NODE_NUTRITION;//标注类型为营养包
						
						insertNodeIntoNode(selectNodeBean,childNodeBean);//插入
					}
				});
				insertNutritionFrame.setVisible(true);
			}
		});
		
		
		//插入基粉的点击事件
		mInsertFormulatorMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				final NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				//只有配方下能插入基粉
				if(!selectNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA)) return;
				
				InsertFormulatorFrame insertFormulatorFrame = new InsertFormulatorFrame();
				insertFormulatorFrame.setCallBack(new AbstractCallBack() {
					@Override
					public void setFormulatorName(String formulatorName, String waterValue) {
						super.setFormulatorName(formulatorName, waterValue);
						
						//创建一个原料ITEM 作为营养包
						TCComponentItemRevision formulatroItemRev = service.createFormulatorItemByName(formulatorName);
						try {//含水量写到法规中去 后续会在 bean中获取得到的
							formulatroItemRev.setProperty("u8_water", waterValue);
						} catch (TCException e) {
							e.printStackTrace();
						}
						
						NodeBean childNodeBean =service.initRootNode(formulatroItemRev); 	//nodeBean
						insertNodeIntoNode(selectNodeBean,childNodeBean);//插入
					}
				});
				insertFormulatorFrame.setVisible(true);
			}
		});
		
		//删除选中的节点
		mDeleteMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				if(selectNodeBean.parentNode==null) return;//根节点不能删除
				
				DefaultMutableTreeNode parentNode = selectNodeBean.parentNode;
				NodeBean parentNodeBean = (NodeBean) parentNode.getUserObject();
				
				
				service.removeChildFromParent(parentNodeBean, selectNodeBean);//从父节点的孩纸集合中移除
				mFormulatorTreeModel.removeNodeFromParent(selectNode);//移除tree中节点
			}
		});

		
		//添加菜单项
		mMenu.add(mDeleteMenuItem);
		mMenu.add(mInsertFormulatorMenuItem);
		mMenu.add(mInsertNutritionMenuItem);
		
		//树的右键点击事件弹出菜单
		mFormulatorTree.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == e.BUTTON3){//右击
						TreePath path = mFormulatorTree.getPathForLocation(e.getX(), e.getY());
						mRightPath = path;
						mFormulatorTree.setSelectionPath(path);
						System.out.println(path.toString());
						DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
						if(selectNode==null){
							return;
						}
						System.out.println(selectNode.toString());
						mMenu.show(mFormulatorTree, e.getX(), e.getY());
						
					}
					
					if(e.getButton() == e.BUTTON1){//左键单选
						//选中的是原料的话要展示 指标 信息在 ，mIndexTable中
						TreePath path = mFormulatorTree.getPathForLocation(e.getX(), e.getY());
						mFormulatorTree.setSelectionPath(path);
						DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
						if(selectNode==null) return;
						
						NodeBean selectedNodeBean = (NodeBean) selectNode.getUserObject();
						mSelectedNodeBean = selectedNodeBean;//保存在tree选中节点中
						
						if(selectedNodeBean.nodeType.equals(Const.NodeType.NODE_MATERIAL) ||
								selectedNodeBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)){//判断是否是原料或者营养包
							updateIndexTable(selectedNodeBean);
						}else if(selectedNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA) ||
								selectedNodeBean.nodeType.equals(Const.NodeType.NODE_BASE_FORMULATOR)){//选择的是配方或者基粉
							updateFormulatorTable(selectedNodeBean);
						}
					}
				}
		});
		
		
		
	}


	/* (non-Javadoc)
	 * 拖动事件的监听事件
	 */
	@Override
	public NodeBean getSelectedNodeBean(int position) {
		return mMaterialTableNodeBeanList.get(position);
	}


	/* (non-Javadoc)
	 * 添加原料成功后通知过来更新 配方表中的信息
	 */
	@Override
	public void notifyFormulatorTableUpdate() {
		updateFormulatorTable(mRootNodeBean);
	}
	
	/**
	 * 根据选中的节点信息 查看指标条目
	 */
	private void updateIndexTable(NodeBean selectedNodeBean){
		//清空表格内容 
		if(mIndexTableModel == null){
			mIndexTableModel = (DefaultTableModel) mIndexTable.getModel();
		}
		int rowCount = mIndexTableModel.getRowCount();
		for(int i=0; i<rowCount; i++){
			mIndexTableModel.removeRow(0);
		}
				
		//获取指标表的基本信息
		mIndexTableNodeBeanList = service.getIndexTableNodeBeanListBySelectedMaterialNode(selectedNodeBean);
		//初始化表格
		for(NodeBean indexNodeBean : mIndexTableNodeBeanList){
			mIndexTableModel.addRow(new String[]{
					indexNodeBean.itemID,
					indexNodeBean.objectName,
					indexNodeBean.u8Down,
					indexNodeBean.bl_quantity,
					indexNodeBean.u8Up
			});
		}
		
		//如果集合中的数据为空的话要给拖动事件一条活路
		if(mIndexTableNodeBeanList.size() == 0){
			mIndexTableModel.addRow(new String[]{
					"",
			});
		}
	}
	
	
	/**
	 * @param selectedNodeBean
	 * 更新 配方 table 中的信息
	 */
	private void updateFormulatorTable(NodeBean selectedNodeBean){
		//设置含水量
		waterEdt.setText(selectedNodeBean.u8Water);
		
		//清空表格
		if(mFormulatorModel == null){
			mFormulatorModel = (DefaultTableModel) mFormulatorTable.getModel();
		}
		int rowCount = mFormulatorModel.getRowCount();
		for(int i=0;i<rowCount;i++){
			mFormulatorModel.removeRow(0);
		}
		
		//获去配方表的基本信息
		mFormulatorTableNodeBeanList = service.getFormulatorTableNodeBeanListByRoot(selectedNodeBean);
		//初始化表格
		for(NodeBean materialNodeBean : mFormulatorTableNodeBeanList){
			mFormulatorModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.U8_inventory
			});
		}
	}
	
	/**
	 * 计算完数据后触发标签表中的数据更新 
	 */
	private void updateLabelTable(){
		//TODO: 计算完后要个更新这个表中对应条目的值
		//清空表格
		if (mLableTableModel == null) mLableTableModel = (DefaultTableModel) mLableTable.getModel();
		
		int rowCount = mLableTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			mLableTableModel.removeRow(0);
		}
		
		//赋值
		for (NodeBean lableNodeBean : mLableTableNodeBeanList) {
			mLableTableModel.addRow(new String[] { 
					lableNodeBean.itemID,
					lableNodeBean.objectName, 
					lableNodeBean.lawValue,
					lableNodeBean.standardValue, 
					lableNodeBean.bl_quantity,
					lableNodeBean.minValue, 
					lableNodeBean.middleValue,
					lableNodeBean.maxValue 
			});
		}
	}


	/* (non-Javadoc)
	 * 从标签项的表格中获取一个数据条目
	 */
	@Override
	public NodeBean getTable2TableSelectedNodeBean(int position) {
		return mLableTableNodeBeanList.get(position);
	}


	/* (non-Javadoc)
	 * 拖动标签中的指标 到 指标表格中
	 * 更新指标table中的数据 和树结构
	 */
	@Override
	public void notifyIndexTableInsert(NodeBean insertNodeBean) {
		//TODO：如果指标的父亲节点类型是 营养包类型就允许插入 否则不允许
		if(!mSelectedNodeBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)){//营养包类型的才插入
			return;
		}
		
		if(mSelectedNodeBean == null)	return;
		
		if(mSelectedNodeBean.childNodeBeans == null) mSelectedNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		
		if(mSelectedNodeBean.chidNodes == null) mSelectedNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		
		mSelectedNodeBean.childNodeBeans.add(0, insertNodeBean);
		mSelectedNodeBean.chidNodes.add(0,insertNodeBean.node);
		
		insertNodeBean.parentNode = mSelectedNodeBean.node;
		
		mFormulatorTreeModel.insertNodeInto(insertNodeBean.node, mSelectedNodeBean.node,0);

		updateIndexTable(mSelectedNodeBean);
		
		
	}



	
	/* (non-Javadoc)
	 * 通知指标table 移除一个条目
	 */
	@Override
	public void notifyIndexTableRemove(NodeBean removeNodeBean) {
		
		int position = -1;
		for(int i=0;i<mIndexTableNodeBeanList.size();i++){
			NodeBean indexNodeBean = mIndexTableNodeBeanList.get(i);
			if(removeNodeBean.itemID.equals(indexNodeBean.itemID)){
				position = i;
			}
		}
		
		mIndexTableNodeBeanList.remove(position);
		mSelectedNodeBean.chidNodes.remove(position);
		mSelectedNodeBean.childNodeBeans.remove(position);
		
		mFormulatorTreeModel.removeNodeFromParent(removeNodeBean.node);
		
		updateIndexTable(mSelectedNodeBean);
	}
	
	


	/* (non-Javadoc)
	 * 得到要移除指标 table 中的那个项
	 */
	@Override
	public NodeBean getRemoveSelectedNodeBean(int position) {
		if(mIndexTableNodeBeanList==null || mIndexTableNodeBeanList.size()<=0){
			return null;
		}
		return mIndexTableNodeBeanList.get(position);
	}
	
	
	/**
	 * @param parentNodeBean
	 * @param childNodeBean
	 * 将孩子节点插入到父亲节点下的第一个节点中
	 */
	public void insertNodeIntoNode(NodeBean parentNodeBean, NodeBean childNodeBean){
		DefaultMutableTreeNode parentNode = parentNodeBean.node;
		DefaultMutableTreeNode childNode = childNodeBean.node;
		
		childNodeBean.parentNode = parentNode;
		
		if(parentNodeBean.chidNodes == null) parentNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		parentNodeBean.chidNodes.add(0,childNode);
		if(parentNodeBean.childNodeBeans == null) parentNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		parentNodeBean.childNodeBeans.add(0,childNodeBean);
		
		mFormulatorTreeModel = (DefaultTreeModel) mFormulatorTree.getModel();
		mFormulatorTreeModel.insertNodeInto( childNode,parentNode, 0);//插入到目标节点的第一个
	}
	
	
	

}
